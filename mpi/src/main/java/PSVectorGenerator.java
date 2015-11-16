import mpi.MPI;
import mpi.MPIException;
import mpi.MpiOps;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class PSVectorGenerator {
    private final String inFolder;
    private final String outFolder;
    private Map<Integer, VectorPoint> currentPoints = new HashMap<Integer, VectorPoint>();
    private int days;
    private boolean mpi = false;
    private MpiOps mpiOps;
    private Date startDate;
    private Date endDate;
    private int mode;
    TreeMap<String, List<Date>> dates = new TreeMap<String, List<Date>>();

    private Map<String, CleanMetric> metrics = new HashMap<String, CleanMetric>();

    public PSVectorGenerator(String inFile, String outFile, int days, boolean mpi, String startDate, String endDate, int mode) {
        this.days = days;
        this.inFolder = inFile;
        this.outFolder = outFile;
        this.mpi = mpi;
        this.startDate = Utils.parseDateString(startDate);
        this.endDate = Utils.parseDateString(endDate);
        this.mode = mode;
    }

    public void process() {
        System.out.println("starting vector generator...");
        File inFolder = new File(this.inFolder);
        TreeMap<String, List<Date>> dates = Utils.genDates(this.startDate, this.endDate, this.mode);
        // create the out directory
        Utils.createDirectory(outFolder);

        int filesPerProcess = 0;
        if (mpi) {
            try {
                mpiOps = new MpiOps();
                int rank = mpiOps.getRank();
                int size = mpiOps.getSize();
                Iterator<String> datesItr = dates.keySet().iterator();
                int i = 0;
                while (datesItr.hasNext()) {
                    String next = datesItr.next();
                    if (i == rank) {
                        this.dates.put(next, dates.get(next));
                    }
                    i++;
                    if (i == size) {
                        i = 0;
                    }
                }
            } catch (MPIException e) {
                e.printStackTrace();
            }
        } else {
            this.dates = dates;
        }

        for (Map.Entry<String, List<Date>> ed : dates.entrySet()) {
            Date start = ed.getValue().get(0);
            Date end = ed.getValue().get(1);
            processFile(inFolder, start, end, ed.getKey());
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
    private void processFile(File inFile, Date startDate, Date endDate, String outFile) {
        BufferedWriter bufWriter = null;
        BufferedReader bufRead = null;
        System.out.println("Calc: " + outFile + Utils.formatter.format(startDate) + ":" + Utils.formatter.format(endDate));
        int size = -1;
        vectorCounter = 0;
        String outFileName = outFolder + "/" + outFile + ".csv";
        int capCount = 0;
        CleanMetric metric = this.metrics.get(outFileName);
        if (metric == null) {
            metric = new CleanMetric();
            this.metrics.put(outFileName, metric);
        }
        try {
            FileReader input = new FileReader(inFile);
            FileOutputStream fos = new FileOutputStream(new File(outFileName));
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            bufRead = new BufferedReader(input);
            Record record;
            int count = 0;
            int fullCount = 0;
            double totalCap = 0;
            int splitCount = 0;
            while ((record = Utils.parseFile(bufRead, null, false)) != null) {
                // not a record we are interested in
                if (!check(startDate, endDate, record.getDate())) {
                    continue;
                }
                count++;
                int key = record.getSymbol();
                if (record.getFactorToAdjPrice() > 0) {
                    splitCount++;
                }
                // check weather we already have the vector seen
                VectorPoint point = currentPoints.get(key);
                if (point == null) {
                    point = new VectorPoint(key, days);
                    currentPoints.put(key, point);
                }
                if (!point.isFull()) {
                    point.add(record.getPrice(), record.getFactorToAdjPrice(), record.getFactorToAdjVolume(), metric);
                    point.addCap(record.getVolume() * record.getPrice());
                } else {
                    System.out.println("Point full cannot add more....");
                }
                if (point.noOfElements() == size) {
                    fullCount++;
                }
                // sort the already seen symbols and determine how many days are there in this period
                // we take the highest number as the number of days
                if (currentPoints.size() > 2000 && size == -1) {
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
                    totalCap += writeVectors(bufWriter, size, metric);
                    capCount++;
                    fullCount = 0;
                }
            }

            System.out.println("Size: " + size);
            System.out.println("Split count: " + inFile.getName() + " = " + splitCount);
            // write the rest of the vectors in the map after finish reading the file
            totalCap += writeVectors(bufWriter, size, metric);
            capCount++;

//            write the constant vector at the end
            VectorPoint v = new VectorPoint(0, new double[]{0});
            v.addCap(totalCap);
            bufWriter.write(v.serialize());

            System.out.println("Total stocks: " + vectorCounter + " bad stocks: " + currentPoints.size());
            metric.stocksWithIncorrectDays = currentPoints.size();
            System.out.println("Metrics for file: " + outFileName + " " + metric.serialize());
            currentPoints.clear();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open the file", e);
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
    private double writeVectors(BufferedWriter bufWriter, int size, CleanMetric metric) throws IOException {
        double capSum = 0;
        int count = 0;
        for(Iterator<Map.Entry<Integer, VectorPoint>> it = currentPoints.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, VectorPoint> entry = it.next();
            VectorPoint v = entry.getValue();
            if (v.noOfElements() == size) {
                metric.totalStocks++;

                if (!v.isValid(metric)) {
                    // System.out.println("Vector not valid: " + outFileName + ", " + v.serialize());
                    metric.invalidStocks++;
                    it.remove();
                    continue;
                }
                String sv = v.serialize();

                // if many points are missing, this can return null
                if (sv != null) {
                    capSum += v.getTotalCap();
                    count++;
                    bufWriter.write(sv);
                    bufWriter.newLine();
                    // remove it from map
                    vectorCounter++;
                } else {
                    metric.invalidStocks++;
                }
                it.remove();
            } else {
                metric.lenghtWrong++;
            }
        }
        return capSum;
    }

    private boolean check(Date start, Date end, Date compare) {
        if (compare == null) {
            System.out.println("Comapre null*****************");
        }
        return (compare.equals(start) || compare.after(start)) && compare.before(end);
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", true, "Input file");
        options.addOption("o", true, "Output file");
        options.addOption("d", true, "Number of days");
        options.addOption("m", false, "MPI");
        options.addOption("s", true, "Start date");
        options.addOption("e", true, "End date");
        options.addOption("md", true, "mode");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String input = cmd.getOptionValue("i");
            String output = cmd.getOptionValue("o");
            String days = cmd.getOptionValue("d");
            boolean mpi = cmd.hasOption("m");
            String mode = cmd.getOptionValue("md");
            String start = cmd.getOptionValue("s");
            String end = cmd.getOptionValue("e");
            if (mpi) {
                MPI.Init(args);
            }
            PSVectorGenerator vg = new PSVectorGenerator(input, output, Integer.parseInt(days), mpi, start, end, Integer.parseInt(mode));
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
