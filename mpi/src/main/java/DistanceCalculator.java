import mpi.MPI;
import mpi.MPIException;
import mpi.MpiOps;
import org.apache.commons.cli.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DistanceCalculator {
    private String vectorFolder;
    private String distFolder;
    private boolean normalize;
    private boolean mpi = false;
    private MpiOps mpiOps;
    private int distanceType;
    private boolean sharedInput = false;
    private String originalStockFile;
    private Map<Integer, String> permNoToSymbol = new HashMap<Integer, String>();
    public static final double CONST_DISTANCE = 0.5;

    public DistanceCalculator(String vectorFolder, String distFolder, boolean normalize, boolean mpi, int distanceType, boolean sharedInput, String originalStockFile) {
        this.vectorFolder = vectorFolder;
        this.distFolder = distFolder;
        this.normalize = normalize;
        this.mpi = mpi;
        this.distanceType = distanceType;
        this.sharedInput = sharedInput;
        this.originalStockFile = originalStockFile;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("d", true, "Distance matrix folder");
        options.addOption("n", false, "normalize");
        options.addOption("m", false, "mpi");
        options.addOption("t", true, "distance type");
        options.addOption("s", false, "shared input directory");
        options.addOption("o", true, "Original stocks file");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            boolean _normalize = cmd.hasOption("n");
            boolean mpi = cmd.hasOption("m");
            int distanceType = Integer.parseInt(cmd.getOptionValue("t"));
            boolean sharedInput = cmd.hasOption("s");
            String originalStockFile = cmd.getOptionValue("o");
            String print = "vector: " + _vectorFile + " ,distance matrix folder: "
                    + _distFile + " ,normalize: "
                    + _normalize + " ,mpi: " + mpi
                    + " ,distance type: " + distanceType
                    + " ,shared input: " + sharedInput;
            System.out.println(print);
            if (mpi) {
                MPI.Init(args);
            }
            DistanceCalculator program = new DistanceCalculator(_vectorFile, _distFile, _normalize, mpi, distanceType, sharedInput, originalStockFile);
            program.process();
            if (mpi) {
                MPI.Finalize();
            }
        } catch (MPIException | ParseException e) {
            System.out.println(options.toString());
        }
    }

    private static int INC = 7000;

    private void process() {
        System.out.println("Starting Distance calculator...");
        File inFolder = new File(vectorFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder: " + vectorFolder);
            return;
        }

        // create the out directory
        Utils.createDirectory(distFolder);

        int rank = 0;
        int size = 0;
        try {
            if (mpi) {
                mpiOps = new MpiOps();
                rank = mpiOps.getRank();
                size = mpiOps.getSize();
            }

            BlockingQueue<File> files = new LinkedBlockingQueue<File>();

            List<File> list = new ArrayList<File>();
            Collections.addAll(list, inFolder.listFiles());
            Collections.sort(list);
            if (mpi && sharedInput) {
                Iterator<File> datesItr = list.iterator();
                int i = 0;
                while (datesItr.hasNext()) {
                    File next = datesItr.next();
                    if (i == rank) {
                        files.add(next);
                    }
                    i++;
                    if (i == size) {
                        i = 0;
                    }
                }
            } else {
                files.addAll(list);
            }

            if (originalStockFile != null && !originalStockFile.equals("")) {
                if (new File(originalStockFile).exists()) {
                    permNoToSymbol = Utils.loadMapping(originalStockFile);
                }
            }

            List<Thread> threads = new ArrayList<Thread>();
            // start 4 threads
            for (int i = 0; i < 1; i++) {
                Thread t = new Thread(new Worker(files));
                t.start();
                threads.add(t);
            }

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Distance calculator finished...");
        } catch (MPIException e) {
            throw new RuntimeException("Failed to communicate");
        }
    }



    private class Worker implements Runnable {
        private BlockingQueue<File> queue;

        private Worker(BlockingQueue<File> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                try {
                    File f = queue.take();
                    processFile(f);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void processFile(File fileEntry) {
        WriterWrapper writer = null;
        if (fileEntry.isDirectory()) {
            return;
        }

        String outFileName = distFolder + "/" + fileEntry.getName();
        String smallValDir = distFolder + "/small";
        String smallOutFileName = smallValDir + "/" + fileEntry.getName();

        System.out.println("Calculator vector file: " + fileEntry.getAbsolutePath() + " Output: " + outFileName);
        //File smallDirFile = new File(smallValDir);
        //smallDirFile.mkdirs();
        writer = new WriterWrapper(outFileName, true);
        //WriterWrapper smallWriter = new WriterWrapper(smallOutFileName, true);
        // +1 to accomodate constant sctock
        int lineCount = countLines(fileEntry) + 1;

        // initialize the double arrays for this block
        double values[][] = new double[INC][];
        for (int i = 0; i < values.length; i++) {
            values[i] = new double[lineCount];
            values[i][lineCount - 1] = .5;
        }
        double dmax = Double.MIN_VALUE;
        double dmin = Double.MAX_VALUE;

        int startIndex = 0;
        int endIndex = -1;

        List<VectorPoint> vectors;
        long count = 0;
        long count2 = 0;
        long count3 = 0;
        long count4 = 0;
        do {
            startIndex = endIndex + 1;
            endIndex = startIndex + INC - 1;

            int readStartIndex = 0;
            int readEndIndex = INC - 1;

            vectors = Utils.readVectors(fileEntry, startIndex, endIndex);
            if (vectors.size() == 0) {
                writeConstantVector(writer, lineCount);
                break;
            }

            // System.out.println("Processing block: " + startIndex + " : " + endIndex);
            // now start from the begining and go through the whole file
            List<VectorPoint> secondVectors;
            do {

                if (readStartIndex != startIndex) {
                    secondVectors = Utils.readVectors(fileEntry, readStartIndex, readEndIndex);
                } else {
                    secondVectors = vectors;
                }

                if (secondVectors.size() == 0) {
                    break;
                }
                System.out.println("Reading second block: " + readStartIndex + " : " + readEndIndex + " read size: " + secondVectors.size());
                for (int i = 0; i < secondVectors.size(); i++) {
                    VectorPoint sv = secondVectors.get(i);
                    for (int j = 0; j < vectors.size(); j++) {
                        VectorPoint fv = vectors.get(j);
                        double cor = sv.correlation(fv, distanceType);
                        if (cor < 0.03) {
                            String sym1 = permNoToSymbol.get(fv.getKey());
                            String sym2 = permNoToSymbol.get(sv.getKey());
                            if (sym1 != null && sym2 != null) {
                                //smallWriter.write(sym1 + "," + sym2 + " :" + cor);
                            }
                            // count++;
                        }
                        if (cor > dmax) {
                            dmax = cor;
                        }

                        if (cor < dmin) {
                            dmin = cor;
                        }
                        values[j][readStartIndex + i] = cor;
                    }
                }
                readStartIndex = readEndIndex + 1;
                readEndIndex = readStartIndex + INC - 1;
            } while (true);

            // write the vectors to file
            for (int i = 0; i < vectors.size(); i++) {
                for (int j = 0; j < values[i].length; j++) {
                    double doubleValue = values[i][j];

                    short shortValue = (short) (doubleValue * Short.MAX_VALUE);
                    if (shortValue < 3277) {
                        count2++;
                        if (doubleValue > .1) {
                        } else {
                            count++;
                        }
                    }
                    if (doubleValue < .1) {
                        count3++;
                        if (shortValue > 3277) {
                            count4++;
                        }
                    }
                    writer.writeShort((short) shortValue);
                }
                writer.line();
            }
            System.out.println("count " + count);
            System.out.println("count2 " + count2);
            System.out.println("count3 " + count3);
            System.out.println("count4 " + count4);
        } while (true);
        if (writer != null) {
            writer.close();
        }
//        if (smallWriter != null) {
//            smallWriter.close();
//        }
        System.out.println(dmax);
    }

    private void writeConstantVector(WriterWrapper writer, int length) {
        for (int i = 0; i < length; i++) {
            short shortValue = (short) (CONST_DISTANCE * Short.MAX_VALUE);
            writer.writeShort(shortValue);
        }
    }

    private int countLines(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                count++;
            }
            return count;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file");
        }
    }


}
