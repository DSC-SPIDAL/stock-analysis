import mpi.MPI;
import mpi.MPIException;
import mpi.MpiOps;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * We need two types of weights. First is for rotations and the second is for damds
 */
public class WeightCalculator {
    private final boolean sharedInput;
    private String vectorFolder;
    private String distFolder;
    private boolean normalize;
    private double dmax;
    private double dmin;
    private boolean mpi = false;
    private boolean simple = true;
    private MpiOps mpiOps;

    public WeightCalculator(String vectorFolder, String distFolder, boolean normalize, boolean mpi, boolean simple, boolean sharedInput) {
        this.vectorFolder = vectorFolder;
        this.distFolder = distFolder;
        this.normalize = normalize;
        this.simple = simple;
        this.mpi = mpi;
        this.sharedInput = sharedInput;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("d", true, "Distance matrix folder");
        options.addOption("n", false, "normalize");
        options.addOption("m", false, "mpi");
        options.addOption("s", false, "True: gen simple list, False: Gen matrix");
        options.addOption("sh", false, "Shared file system");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            boolean _normalize = cmd.hasOption("n");
            boolean mpi = cmd.hasOption("m");
            boolean simple = cmd.hasOption("s");
            boolean sharedInput = cmd.hasOption("sh");
            if (mpi) {
                MPI.Init(args);
            }
            WeightCalculator program = new WeightCalculator(_vectorFile, _distFile, _normalize, mpi, simple, sharedInput);
            program.process();
            if (mpi) {
                MPI.Finalize();
            }
        } catch (MPIException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static int INC = 3000;

    private void process() {
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
                    if (!simple) {
                        processFile(f);
                    } else {
                        processSimpleFile(f);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void processSimpleFile(File fileEntry) {
        WriterWrapper writer;
        if (fileEntry.isDirectory()) {
            return;
        }

        String outFileName = distFolder + "/" + fileEntry.getName();
        System.out.println("Calculator vector file: " + fileEntry.getAbsolutePath() + " Output: " + outFileName);
        writer = new WriterWrapper(outFileName, false);

        int startIndex = 0;
        int endIndex = -1;

        List<VectorPoint> vectors;
        do {
            startIndex = endIndex + 1;
            endIndex = startIndex + INC - 1;

            vectors = Utils.readVectors(fileEntry, startIndex, endIndex);
            if (vectors.size() == 0) {
                break;
            }
            // write the vectors to file
            for (int i = 0; i < vectors.size(); i++) {
                double weight = vectors.get(i).getTotalCap();
                writer.write(weight);
                writer.line();
            }
        } while (true);
        writer.close();
    }

    private void processFile(File fileEntry) {
        WriterWrapper writer = null;
        if (fileEntry.isDirectory()) {
            return;
        }

        String outFileName = distFolder + "/" + fileEntry.getName();
        System.out.println("Calculator vector file: " + fileEntry.getAbsolutePath() + " Output: " + outFileName);
        writer = new WriterWrapper(outFileName, false);

        int lineCount = countLines(fileEntry);

        // initialize the double arrays for this block
        double values[][] = new double[INC][];
        for (int i = 0; i < values.length; i++) {
            values[i] = new double[lineCount];
        }

        int startIndex = 0;
        int endIndex = -1;

        List<VectorPoint> vectors;
        do {
            startIndex = endIndex + 1;
            endIndex = startIndex + INC - 1;

            int readStartIndex = 0;
            int readEndIndex = INC - 1;

            vectors = Utils.readVectors(fileEntry, startIndex, endIndex);
            if (vectors.size() == 0) {
                break;
            }

            // System.out.println("Processing block: " + startIndex + " : " + endIndex);
            // now start from the begining and go through the whole file
            List<VectorPoint> secondVectors;
            do {
                System.out.println("Reading second block: " + readStartIndex + " : " + readEndIndex);
                if (readStartIndex != startIndex) {
                    secondVectors = Utils.readVectors(fileEntry, readStartIndex, readEndIndex);
                } else {
                    secondVectors = vectors;
                }

                if (secondVectors.size() == 0) {
                    break;
                }

                for (int i = 0; i < secondVectors.size(); i++) {
                    VectorPoint sv = secondVectors.get(i);
                    for (int j = 0; j < vectors.size(); j++) {
                        VectorPoint fv = vectors.get(j);
                        double cor = sv.weight(fv);
                        if (cor > dmax) {
                            dmax = cor;
                        }

                        if (cor < dmin) {
                            dmin = cor;
                        }
                        values[j][readStartIndex + i] = Math.max(dmax * .05, Math.pow(cor, .25));
                    }
                }
                readStartIndex = readEndIndex + 1;
                readEndIndex = readStartIndex + INC - 1;
            } while (true);

            double max = Double.MIN_VALUE;
            for (int i = 0; i < values.length; i++) {
                double[] row = values[i];
                for (int j = 0; j < row.length; j++) {
                    values[i][j] = Math.max(dmax * .05, Math.pow(values[i][j], .25));
                    if (values[i][j] > max) {
                        max = values[i][j];
                    }
                }
            }
            // write the vectors to file
            for (int i = 0; i < vectors.size(); i++) {
                double[] row = values[i];
                for (double value : row) {
                    short val = (short) ((normalize ? value / max : value) * Short.MAX_VALUE);
                    writer.write(val);
                }
                //writer.line();
            }
        } while (true);
        if (writer != null) {
            writer.close();
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
