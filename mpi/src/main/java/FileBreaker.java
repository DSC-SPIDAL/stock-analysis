import mpi.MPI;
import mpi.MPIException;
import mpi.MpiOps;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

/**
 * Break up the file in to sub files with the dates that we want.
 * We will use the sub-files to parallely do the vector generation
 */
public class FileBreaker {
    private final String inFile;
    private final String outDir;

    private int mode;

    private boolean mpi;
    private MpiOps mpiOps;
    private Date startDate;
    private Date endDate;

    TreeMap<String, List<Date>> dates = new TreeMap<String, List<Date>>();

    private enum DateCheckType {
        MONTH,
        YEAR,
        CONT_YEAR,
        DAY
    }

    public FileBreaker(String inFile, String outFile, String startDate, int mode, String endDate, boolean mpi) {
        this.mode = mode;
        this.inFile = inFile;
        this.outDir = outFile;
        this.startDate = Utils.parseDateString(startDate);
        this.endDate = Utils.parseDateString(endDate);
        this.mpi = mpi;
    }

    public void process() {
        TreeMap<String, List<Date>> dates = Utils.genDates(this.startDate, this.endDate, mode);
        Map<Integer, String> s = new HashMap<Integer, String>();
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
        printDates();
        processFile(inFile);
    }

    private void printDates() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Date>> e : dates.entrySet()) {
            sb.append(e.getKey()).append(": ");
            for (Date d : e.getValue()) {
                sb.append(Utils.getMonthString(d)).append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    private Set<String> getDatesForThisRecord(Record r) {
        Set<String> files = new HashSet<String>();
        for (Map.Entry<String, List<Date>> entry : dates.entrySet()) {
            if (mode <= 4) {
                for (Date d : entry.getValue()) {
                    if (check(d, r.getDate(), DateCheckType.MONTH)) {
                        files.add(entry.getKey());
                    }
                }
            } else if (mode >= 5) {
                Date start = entry.getValue().get(0);
                Date end = entry.getValue().get(1);
                if (check(start, end, r.getDate(), DateCheckType.DAY)) {
                    files.add(entry.getKey());
                }
            }
        }
        return files;
    }

    /**
     * Process a stock file and generate vectors for a month or year period
     */
    private void processFile(String inFile) {
        Map<String, List<Record>> records = new HashMap<String, List<Record>>();
        Map<String, BufferedWriter> writers = new HashMap<String, BufferedWriter>();
        CleanMetric metric = new CleanMetric();
        for (String s : dates.keySet()) {
            String outFile = outDir + "/" + s + ".csv";
            FileOutputStream fos;
            BufferedWriter bufWriter;
            try {
                fos = new FileOutputStream(new File(outFile));
                bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
                writers.put(s, bufWriter);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to create writer", e);
            }
        }
        int splitCount = 0;

        BufferedReader bufRead = null;
        try {
            int totalCount = 0;
            FileReader input = new FileReader(inFile);
            bufRead = new BufferedReader(input);
            Record record;
            int count = 0;
            while ((record = Utils.parseFile(bufRead, metric, true)) != null) {
                if (record.getFactorToAdjPrice() > 0) {
                    splitCount++;
                }
                totalCount++;
                Set<String> files = getDatesForThisRecord(record);
                for (String f : files) {
                    List<Record> l = records.get(f);
                    if (l == null) {
                        l = new ArrayList<Record>();
                        records.put(f, l);
                    }
                    l.add(record);
                    count++;
                }

                if (count >= 1000000) {
                    count = 0;
                    System.out.println("Total count: " + totalCount);
                    for (Map.Entry<String, List<Record>> e : records.entrySet()) {
                        BufferedWriter w = writers.get(e.getKey());
                        for (Record r : e.getValue()) {
                            w.write(r.serialize());
                            w.newLine();
                        }
                        e.getValue().clear();
                    }
                }
            }

            for (Map.Entry<String, List<Record>> e : records.entrySet()) {
                BufferedWriter w = writers.get(e.getKey());
                for (Record r : e.getValue()) {
                    w.write(r.serialize());
                    w.newLine();
                }
                e.getValue().clear();
            }

            System.out.println("Split count for file: " + inFile + " = " + splitCount);
            System.out.println("Clean metric for file: " + metric.serialize());
        } catch (IOException e) {
            throw new RuntimeException("Failed to open the file", e);
        } finally {
            try {
                for (BufferedWriter bf : writers.values()) {
                    bf.close();
                }
                if (bufRead != null) {
                    bufRead.close();
                }
            } catch (IOException ignore) {
            }
        }
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

    private boolean check(Date start, Date end, Date compare, DateCheckType check) {
        if (compare == null) {
            System.out.println("Comapre null*****************");
        }
        return (compare.equals(start) || compare.after(start)) && compare.before(end);
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", true, "Input file");
        options.addOption("o", true, "Output directory");
        options.addOption("s", true, "Start date");
        options.addOption("e", true, "End date");
        options.addOption("m", false, "mpi");
        options.addOption("d", true, "Mode, 1 - month, 2 year, 3 whole, 4 continous year");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String input = cmd.getOptionValue("i");
            String output = cmd.getOptionValue("o");
            String date = cmd.getOptionValue("s");
            String end = cmd.getOptionValue("e");
            String days = cmd.getOptionValue("d");
            boolean mpi = cmd.hasOption("m");
            if (mpi) {
                MPI.Init(args);
            }
            FileBreaker vg = new FileBreaker(input, output, date, Integer.parseInt(days), end, mpi);
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
