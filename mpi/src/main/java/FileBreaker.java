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
    }

    public FileBreaker(String inFile, String outFile, String startDate, int days, String endDate, boolean mpi) {
        this.mode = days;
        this.inFile = inFile;
        this.outDir = outFile;
        this.startDate = Utils.parseDateString(startDate);
        this.endDate = Utils.parseDateString(endDate);
        this.mpi = mpi;
    }

    public void process() {
        TreeMap<String, List<Date>> dates = genDates(this.startDate, this.endDate, mode);
        Map<Integer, String> s = new HashMap<Integer, String>();
        if (mpi) {
            try {
                mpiOps = new MpiOps();
                int rank = mpiOps.getRank();
                int size = mpiOps.getSize();
                int devide = dates.size() / size;
                Iterator<String> datesItr = dates.keySet().iterator();
                int i = 0;
                while (datesItr.hasNext()) {
                    if (i == rank) {
                        String next = datesItr.next();
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

    private TreeMap<String, List<Date>> genDates(Date startDate, Date endDate, int mode) {
        TreeMap<String, List<Date>> dates = new TreeMap<String, List<Date>>();
        Date currentDate = startDate;
        if (mode == 1) {
            // month data
            while (currentDate.before(endDate)) {
                List<Date> d = new ArrayList<Date>();
                d.add(currentDate);
                dates.put(Utils.getMonthString(currentDate), d);
                currentDate = Utils.addMonth(currentDate);
            }
        } else if (mode == 2) {
            while (currentDate.before(endDate)) {
                String startName = Utils.getMonthString(currentDate);
                Date tempDate = currentDate;
                List<Date> d = new ArrayList<Date>();
                for (int i = 0; i < 12; i++) {
                    d.add(tempDate);
                    tempDate = Utils.addMonth(tempDate);
                }
                currentDate = tempDate;
                String endDateName = Utils.getMonthString(tempDate);
                dates.put(startName + "_" + endDateName, d);
            }
        } else if (mode == 3) {
            List<Date> d = new ArrayList<Date>();
            while (currentDate.before(endDate)) {
                d.add(currentDate);
                currentDate = Utils.addMonth(currentDate);
            }
            dates.put(Utils.getMonthString(startDate) + "_" + Utils.getMonthString(endDate), d);
        } else if (mode == 4) {
            while (currentDate.before(endDate)) {
                String startName = Utils.getMonthString(currentDate);
                Date tempDate = currentDate;
                List<Date> d = new ArrayList<Date>();
                for (int i = 0; i < 12; i++) {
                    d.add(tempDate);
                    tempDate = Utils.addMonth(tempDate);
                }
                currentDate = Utils.addMonth(currentDate);
                String endDateName = Utils.getMonthString(tempDate);
                dates.put(startName + "_" + endDateName, d);
                if (!tempDate.before(endDate)) {
                    break;
                }
            }
        }
        return dates;
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
            for (Date d : entry.getValue()) {
                if (check(d, r.getDate(), DateCheckType.MONTH)) {
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


        BufferedReader bufRead = null;
        try {
            int totalCount = 0;
            FileReader input = new FileReader(inFile);
            bufRead = new BufferedReader(input);
            Record record;
            int count = 0;
            while ((record = Utils.parseFile(bufRead)) != null) {
                totalCount++;
                Set<String> files = getDatesForThisRecord(record);
                for (String f : files) {
                    List l = records.get(f);
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
