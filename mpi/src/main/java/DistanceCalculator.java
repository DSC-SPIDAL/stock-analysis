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

    public DistanceCalculator(String vectorFolder, String distFolder, boolean normalize, boolean mpi, int distanceType, boolean sharedInput) {
        this.vectorFolder = vectorFolder;
        this.distFolder = distFolder;
        this.normalize = normalize;
        this.mpi = mpi;
        this.distanceType = distanceType;
        this.sharedInput = sharedInput;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("d", true, "Distance matrix folder");
        options.addOption("n", false, "normalize");
        options.addOption("m", false, "mpi");
        options.addOption("t", true, "distance type");
        options.addOption("s", false, "shared input directory");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            boolean _normalize = cmd.hasOption("n");
            boolean mpi = cmd.hasOption("m");
            int distanceType = Integer.parseInt(cmd.getOptionValue("t"));
            boolean sharedInput = cmd.hasOption("s");
            String print = "vector: " + _vectorFile + " ,distance matrix folder: "
                    + _distFile + " ,normalize: "
                    + _normalize + " ,mpi: " + mpi
                    + " ,distance type: " + distanceType
                    + " ,shared input: " + sharedInput;
            System.out.println(print);
            if (mpi) {
                MPI.Init(args);
            }
            DistanceCalculator program = new DistanceCalculator(_vectorFile, _distFile, _normalize, mpi, distanceType, sharedInput);
            program.process();
            if (mpi) {
                MPI.Finalize();
            }
        } catch (MPIException | ParseException e) {
            e.printStackTrace();
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
        writer = new WriterWrapper(outFileName, false);
        //WriterWrapper smallWriter = new WriterWrapper(smallOutFileName, true);
        // +1 to accomodate constant sctock
        int lineCount = countLines(fileEntry);

        // initialize the double arrays for this block
        double values[][] = new double[INC][];
        double cachedValues[][] = new double[INC][];
        for (int i = 0; i < values.length; i++) {
            values[i] = new double[lineCount];
            cachedValues[i] = new double[lineCount];
        }

        for (int i = 0; i < cachedValues.length; i++) {
            for (int j = 0; j < cachedValues[i].length; j++) {
                cachedValues[i][j] = -1;
            }
        }
        int []histogram = new int[100];
        double []chanegHisto = new double[100];

        double dmax = Double.MIN_VALUE;
        double dmin = Double.MAX_VALUE;

        int startIndex = 0;
        int endIndex = -1;

        List<VectorPoint> vectors;

//        do {
            startIndex = endIndex + 1;
            endIndex = startIndex + INC - 1;

            int readStartIndex = 0;
            int readEndIndex = INC - 1;

            vectors = Utils.readVectors(fileEntry, startIndex, endIndex);
//            if (vectors.size() == 0) {
//                break;
//            }

            // System.out.println("Processing block: " + startIndex + " : " + endIndex);
            // now start from the begining and go through the whole file
            List<VectorPoint> secondVectors = vectors;
                System.out.println("Reading second block: " + readStartIndex + " : " + readEndIndex + " read size: " + secondVectors.size());
                for (int i = 0; i < secondVectors.size(); i++) {
                    VectorPoint sv = secondVectors.get(i);
                    double v = VectorPoint.vectorLength(1, sv);
                    for (int z = 0; z < 100; z++) {
                        if (v < (z + 1) * .1) {
                            chanegHisto[z]++;
                            break;
                        }
                    }
                    for (int j = 0; j < vectors.size(); j++) {
                        VectorPoint fv = vectors.get(j);
                        double cor = 0;
                        // assume i,j is eqaul to j,i
                        if (cachedValues[readStartIndex + i][j] == -1) {
                            cor = sv.correlation(fv, distanceType);
                        } else {
                            cor = cachedValues[readStartIndex + i][j];
                        }

                        if (cor > dmax) {
                            dmax = cor;
                        }

                        if (cor < dmin) {
                            dmin = cor;
                        }
                        values[j][readStartIndex + i] = cor;
                        cachedValues[j][readStartIndex + i] = cor;
                    }
                }
                readStartIndex = readEndIndex + 1;
                readEndIndex = readStartIndex + INC - 1;
            System.out.println("MAX distance is: " + dmax + " MIN Distance is: " + dmin);
            // write the vectors to file
            for (int i = 0; i < vectors.size(); i++) {
                for (int j = 0; j < values[i].length; j++) {
                    double doubleValue = values[i][j]/dmax;
                    for (int k = 0; k < 100; k++) {
                        if (doubleValue < (k + 1.0) / 100) {
                            histogram[k]++;
                            break;
                        }
                    }
                    if (doubleValue < 0) {
                        System.out.println("*********************************ERROR, invalid distance*************************************");
                        throw new RuntimeException("Invalid distance");
                    } else if (doubleValue > 1) {
                        System.out.println("*********************************ERROR, invalid distance*************************************");
                        throw new RuntimeException("Invalid distance");
                    }
                    short shortValue = (short) (doubleValue * Short.MAX_VALUE);
                    writer.writeShort(shortValue);
                }
                writer.line();
            }
//        } while (true);
        if (writer != null) {
            writer.close();
        }
        System.out.println("MAX: " + VectorPoint.maxChange + " MIN: " + VectorPoint.minChange);
        System.out.println("Distance histo");
        for (int i = 0; i < 100; i++) {
            System.out.print(histogram[i] + ", ");
        }
        System.out.println();

        System.out.println("Ratio histo");
        for (int i = 0; i < 100; i++) {
            System.out.print(chanegHisto[i] + ", ");
        }
        System.out.println();

//        if (smallWriter != null) {
//            smallWriter.close();
//        }
        System.out.println(dmax);
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
