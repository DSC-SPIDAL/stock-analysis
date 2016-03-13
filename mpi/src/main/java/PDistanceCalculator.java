import mpi.MPI;
import mpi.MPIException;
import mpi.MpiOps;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PDistanceCalculator {
    private String vectorFolder;
    private String distFolder;
    private boolean mpi = false;
    private MpiOps mpiOps;
    private int distanceType;
    private boolean sharedInput = false;
    private int threads;
    private BlockingQueue<Work> workQueue = new ArrayBlockingQueue<Work>(64);
    private boolean run = true;

    public PDistanceCalculator(String vectorFolder, String distFolder, boolean mpi, int distanceType, boolean sharedInput, int threads) {
        this.vectorFolder = vectorFolder;
        this.distFolder = distFolder;
        this.mpi = mpi;
        this.distanceType = distanceType;
        this.sharedInput = sharedInput;
        this.threads = threads;
    }

    public static void main(String[] args) throws InterruptedException {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("d", true, "Distance matrix folder");
        options.addOption("n", false, "normalize");
        options.addOption("m", false, "mpi");
        options.addOption("t", true, "distance type");
        options.addOption("s", false, "shared input directory");
        options.addOption(Utils.createOption("f", true, "Single calc", false));
        options.addOption(Utils.createOption("tn", true, "Threads", false));

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            boolean _normalize = cmd.hasOption("n");
            boolean mpi = cmd.hasOption("m");
            int distanceType = Integer.parseInt(cmd.getOptionValue("t"));
            boolean sharedInput = cmd.hasOption("s");
            String singleFile = cmd.getOptionValue("f");
            int threads = Integer.parseInt(cmd.getOptionValue("tn"));

            if (singleFile == null) {
                String print = "vector: " + _vectorFile + " ,distance matrix folder: "
                        + _distFile + " ,normalize: "
                        + _normalize + " ,mpi: " + mpi
                        + " ,distance type: " + distanceType
                        + " ,shared input: " + sharedInput;
                System.out.println(print);
                if (mpi) {
                    MPI.Init(args);
                }
                PDistanceCalculator program = new PDistanceCalculator(_vectorFile, _distFile, mpi, distanceType, sharedInput, threads);
                program.process();
                if (mpi) {
                    MPI.Finalize();
                }
            } else {
                PDistanceCalculator program = new PDistanceCalculator(_vectorFile, _distFile, mpi, distanceType, sharedInput, threads);
                program.processFile(new File(singleFile));
            }
        } catch (MPIException | ParseException e) {
            e.printStackTrace();
            System.out.println(options.toString());
        }
    }

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

            List<File> files = new ArrayList<>();

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

            // start the threads
            for (int i = 0; i < threads; i++) {
                Thread t = new Thread(new PartitionWorker(distanceType));
                t.start();
            }

            for (File file : files) {
                processFile(file);
            }

            run = false;
            System.out.println("Distance calculator finished...");
        } catch (MPIException e) {
            throw new RuntimeException("Failed to communicate");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void processFile(File fileEntry) throws InterruptedException {
        long start = System.currentTimeMillis();
        WriterWrapper writer;
        if (fileEntry.isDirectory()) {
            return;
        }

        String outFileName = distFolder + "/" + fileEntry.getName();
        System.out.println("Calculator vector file: " + fileEntry.getAbsolutePath() + " Output: " + outFileName);

        writer = new WriterWrapper(outFileName, false);
        List<VectorPoint> vectors = Utils.readVectors(fileEntry, 0, Integer.MAX_VALUE);
        int lineCount = vectors.size();

        // initialize the double arrays for this block
        double values[][] = new double[lineCount][];
        for (int i = 0; i < values.length; i++) {
            values[i] = new double[lineCount];
        }

        List<Double> localMaxValues = new ArrayList<>();
        CountDownLatch doneSignal = new CountDownLatch(threads);
        // assign values to the workers
        assignWorks(lineCount, localMaxValues, vectors, values, doneSignal);
        // barrier, wait until the workers finish
        doneSignal.await();

        double globalMax = Double.MIN_VALUE;
        for (Double localMaxValue : localMaxValues) {
            if (localMaxValue > globalMax) {
                globalMax = localMaxValue;
            }
        }

        // now go through the values and copy the diagonal to next
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                if (j > i) {
                    values[i][j] = values[j][i];
                }
            }
        }

        // now write the output
        // write the vectors to file
        for (int i = 0; i < vectors.size(); i++) {
            for (int j = 0; j < values[i].length; j++) {
                double doubleValue = values[i][j]/globalMax;
                if (doubleValue < 0) {
                    throw new RuntimeException("Invalid distance");
                } else if (doubleValue > 1) {
                    throw new RuntimeException("Invalid distance");
                }
                short shortValue = (short) (doubleValue * Short.MAX_VALUE);
                writer.writeShort(shortValue);
            }
            writer.line();
        }
        writer.close();
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) / 1000);
    }

    private void assignWorks(int lineCount, List<Double> localMaxValues, List<VectorPoint> vectorPoints,
                             double[][] values, CountDownLatch signal) throws InterruptedException {
        // assign values to the workers
        int cellsPerWorker = (lineCount * lineCount / 2) / threads;
        int currentCellCount = 0;
        int currentStart = 0;
        for (int i = 0; i < lineCount; i++) {
            if (currentCellCount > cellsPerWorker) {
                Work work = new Work(currentStart, i, localMaxValues, vectorPoints, values, signal);
                workQueue.put(work);

                System.out.println(work + " cell count: " + currentCellCount);
                currentCellCount = 0;
                currentStart = i;
            }
            currentCellCount += (i + 1);
        }

        if (currentStart < lineCount) {
            Work work = new Work(currentStart, lineCount, localMaxValues, vectorPoints, values, signal);
            workQueue.put(work);
            System.out.println(work + " cell count: " + currentCellCount);
        }
    }

    private class Work {
        int startRow;
        int endRow;
        private List<Double> localMaxValues;
        private List<VectorPoint> vectorPoints;
        private double[][] values;
        private CountDownLatch signal;

        public Work(int startRow, int endRow, List<Double> localMaxValues, List<VectorPoint> vectorPoints, double[][] values, CountDownLatch signal) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.localMaxValues = localMaxValues;
            this.vectorPoints = vectorPoints;
            this.values = values;
            this.signal = signal;
        }

        @Override
        public String toString() {
            return "Work{" +
                    "startRow=" + startRow +
                    ", endRow=" + endRow + " diff: " + (endRow -  startRow) + '}';
        }
    }


    private class PartitionWorker implements Runnable {
        private int type;

        public PartitionWorker(int type) {
            this.type = type;
        }

        @Override
        public void run() {
            try {
                while (run) {
                    Work work = workQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (work == null) continue;

                    System.out.println("Running worker: " + work);
                    List<VectorPoint> vectorPoints = work.vectorPoints;
                    double [][]values = work.values;

                    double max = Double.MIN_VALUE;
                    double val;
                    for (int i = work.startRow; i < work.endRow; i++) {
                        VectorPoint rowVec = vectorPoints.get(i);
                        for (int j = 0; j <= i; j++) {
                            VectorPoint colVec = vectorPoints.get(j);
                            val = rowVec.correlation(colVec, type);
                            if (val > max) {
                                max = val;
                            }
                            values[i][j] = val;
                        }
                    }
                    work.localMaxValues.add(max);
                    work.signal.countDown();
                }
                System.out.println("Worker done...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
