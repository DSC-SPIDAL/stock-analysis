import mpi.MPI;
import mpi.MPIException;
import mpi.MpiOps;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PVectorGenerator {
    private final String inFolder;
    private final String outFolder;
    private Map<Integer, VectorPoint> currentPoints = new HashMap<Integer, VectorPoint>();
    private int days;
    private boolean mpi = false;
    private MpiOps mpiOps;

    private enum DateCheckType {
        MONTH,
        YEAR,
        CONT_YEAR,
    }

    public PVectorGenerator(String inFile, String outFile, int days, boolean mpi) {
        this.days = days;
        this.inFolder = inFile;
        this.outFolder = outFile;
        this.mpi = mpi;
    }

    public void process() {
        System.out.println("starting vector generator...");
        File inFolder = new File(this.inFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder");
            return;
        }

        // create the out directory
        Utils.createDirectory(outFolder);

        int rank = 0;
        int size = 0;
        int filesPerProcess = 0;
        try {
            if (mpi) {
                mpiOps = new MpiOps();
                rank = mpiOps.getRank();
                size = mpiOps.getSize();
                filesPerProcess = inFolder.listFiles().length / size;
            }
            BlockingQueue<File> files = new LinkedBlockingQueue<File>();

            for (int i = 0; i < inFolder.listFiles().length; i++) {
                File fileEntry = inFolder.listFiles()[i];
                try {
                    if (mpi) {
                        files.put(fileEntry);
                    } else {
                        files.put(fileEntry);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
            System.out.println("Vector generator finished...");
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

    private void printExistingVectors() {
        for (Map.Entry<Integer, VectorPoint> e : currentPoints.entrySet()) {
            System.out.println(e.getValue().serialize());
        }
    }

    private void printDates(List dates) {
        StringBuilder sb = new StringBuilder("");
        for (Object s : dates) {
            sb.append(s.toString()).append(" ,");
        }
        System.out.println(sb.toString());
    }

    /**
     * Process a stock file and generate vectors for a month or year period
     */
    private void processFile(File inFile) {
        BufferedWriter bufWriter = null;
        BufferedReader bufRead = null;
        int size = -1;
        vectorCounter = 0;
        String outFileName = outFolder + "/" + inFile.getName();
        try {
            FileReader input = new FileReader(inFile);
            FileOutputStream fos = new FileOutputStream(new File(outFileName));
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            bufRead = new BufferedReader(input);
            Record record;
            int count = 0;
            int fullCount = 0;
            double totalCap = 0;
            int capCount  = 0;
            while ((record = Utils.parseFile(bufRead)) != null) {
                count++;
                int key = record.getSymbol();
                // check weather we already have the vector seen
                VectorPoint point = currentPoints.get(key);
                if (point == null) {
                    point = new VectorPoint(key, days);
                    currentPoints.put(key, point);
                }
                if (!point.isFull()) {
                    point.add(record.getPrice());
                    point.addCap(record.getVolume() * record.getPrice());
                } else {
                    System.out.println("Point full cannot add more....");
                }
                if (point.noOfElements() == size) {
                    fullCount++;
                }
                // sort the already seen symbols and determine how many days are there in this period
                // we take the highest number as the number of days
                if (currentPoints.size() > 1000 && size == -1) {
                    List<Integer> pointSizes = new ArrayList<Integer>();
                    for (VectorPoint v : currentPoints.values()) {
                        pointSizes.add(v.noOfElements());
                    }
                    size = mostCommon(pointSizes);
                    System.out.println("Number of stocks per period: " + size);
                }

                // now write the current vectors, also make sure we have the size determined correctly
                if (currentPoints.size() > 1000 && size != -1 && fullCount > 750) {
                    System.out.println("Processed: " + count);
                    totalCap += writeVectors(bufWriter, size, outFileName);
                    capCount++;
                    fullCount = 0;
                }
            }

            System.out.println("Size: " + size);
            // write the rest of the vectors in the map after finish reading the file
            totalCap += writeVectors(bufWriter, size, outFileName);
            capCount++;

            // write the constant vector at the end
            //VectorPoint v = new VectorPoint(0, new double[]{0});
            //v.addCap(totalCap / (10 * capCount));
            //bufWriter.write(v.serialize());

            System.out.println("Total stocks: " + vectorCounter + " bad stocks: " + currentPoints.size());
            currentPoints.clear();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open the file");
        } finally {
            try {
                if (bufWriter != null) {
                    bufWriter.close();
                }
                if (bufRead != null) {
                    bufRead.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<T, Integer>();
        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }
        Map.Entry<T, Integer> max = null;
        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }
        return max.getKey();
    }

    int vectorCounter = 0;
    /**
     * Write the current vector to file
     * @param bufWriter stream
     * @param size
     * @throws IOException
     */
    private double writeVectors(BufferedWriter bufWriter, int size, String outFileName) throws IOException {
        double capSum = 0;
        int count = 0;
        for(Iterator<Map.Entry<Integer, VectorPoint>> it = currentPoints.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, VectorPoint> entry = it.next();
            VectorPoint v = entry.getValue();

            if (v.noOfElements() == size) {
                String sv = v.serialize();
                if (!v.isValid()) {
                    System.out.println("Vector not valid: " + outFileName + ", " + v.serialize());
                    it.remove();
                    continue;
                }
                // if many points are missing, this can return null
                if (sv != null) {
                    capSum += v.getTotalCap();
                    count++;
                    bufWriter.write(sv);
                    bufWriter.newLine();
                    // remove it from map
                    vectorCounter++;
                } else {
                    System.out.println("Missing points: " + outFileName + ", " + v.serialize());
                }
                it.remove();
            }
        }
        return capSum / count;
    }

    private boolean check(Date data1, Date date2, DateCheckType check) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(data1);
        cal2.setTime(date2);
        if (check == DateCheckType.MONTH) {
            if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                return true;
            }
        } else if (check == DateCheckType.YEAR) {
            if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", true, "Input file");
        options.addOption("o", true, "Output file");
        options.addOption("d", true, "Number of days");
        options.addOption("m", false, "MPI");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String input = cmd.getOptionValue("i");
            String output = cmd.getOptionValue("o");
            String days = cmd.getOptionValue("d");
            boolean mpi = cmd.hasOption("m");
            if (mpi) {
                MPI.Init(args);
            }
            PVectorGenerator vg = new PVectorGenerator(input, output, Integer.parseInt(days), mpi);
            vg.process();
            if (mpi) {
                MPI.Finalize();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (MPIException e) {
            e.printStackTrace();
        }
    }
}
