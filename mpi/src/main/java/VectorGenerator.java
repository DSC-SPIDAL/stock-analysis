import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class VectorGenerator {
    private final String inFile;
    private final String outFile;
    private Map<Integer, VectorPoint> currentPoints = new HashMap<Integer, VectorPoint>();

    private int days;

    private Date startDate;
    private Date endDate;

    private enum DateCheckType {
        MONTH,
        YEAR
    }

    public VectorGenerator(String inFile, String outFile, String startDate, int days, String endDate) {
        this.days = days;
        this.inFile = inFile;
        this.outFile = outFile;
        this.startDate = Utils.parseDateString(startDate);
        this.endDate = Utils.parseDateString(endDate);
    }

    public void process() {
        Date currentDate = startDate;
        if (days > 30) {
            while (!check(currentDate, endDate, DateCheckType.YEAR)) {
                System.out.println("Processing: " + Utils.getYearString(currentDate));
                processFile(inFile, currentDate, outFile + "/" + Utils.getYearString(currentDate) + ".csv");
                currentDate = Utils.addYear(currentDate);
                currentPoints.clear();
            }
        } else {
            while (!check(currentDate, endDate, DateCheckType.MONTH)) {
                System.out.println("Processing: " + Utils.getMonthString(currentDate));
                processFile(inFile, currentDate, outFile + "/" + Utils.getMonthString(currentDate) + ".csv");
                currentDate = Utils.addMonth(currentDate);
                currentPoints.clear();
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

    public List<String> getDates(String inFile, Date startDate) {
        // first lets go through 10 stocks to figure out the correct dates
        // we assume the records appear sorted according to date
        BufferedReader bufRead = null;
        Map<Integer, List<String>> initialDates = new HashMap<Integer, List<String>>();
        boolean done = false;
        try {
            FileReader input = new FileReader(inFile);
            bufRead = new BufferedReader(input);
            Record record;
            while ((record = Utils.parseFile(bufRead)) != null && !done) {
                // check weather this date is greater than what we need
                if (startDate.after(record.getDate())) {
                    continue;
                }
                // check weather we are interested in this record
                List<String> datesForSymbol = initialDates.get(record.getSymbol());
                if (datesForSymbol == null) {
                    // check how many symbols we have
                    if (initialDates.keySet().size() == 10) {
                        continue;
                    }
                    datesForSymbol = new ArrayList<>();
                    initialDates.put(record.getSymbol(), datesForSymbol);
                }
                // now add these dates
                if (datesForSymbol.size() < days) {
                    datesForSymbol.add(record.getDateString());
                }
                // check weather we have a complete set
                if (initialDates.keySet().size() == 10) {
                    boolean complete = true;
                    for (Map.Entry<Integer, List<String>> entry : initialDates.entrySet()) {
                        if (entry.getValue().size() < days) {
                            complete = false;
                        }
                    }
                    if (complete) {
                        done = true;
                    }
                }
            }
            // for now lets return the first list as dates
            for (Map.Entry<Integer, List<String>> entry : initialDates.entrySet()) {
                return entry.getValue();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open the file");
        } finally {
            try {
                if (bufRead != null) {
                    bufRead.close();
                }
            } catch (IOException ignore) {
            }
        }
        return null;
    }

    /**
     * Process a stock file and generate vectors for a month or year period
     */
    private void processFile(String inFile, Date date, String outFile) {
        BufferedWriter bufWriter = null;
        BufferedReader bufRead = null;
        int size = -1;
        try {
            FileReader input = new FileReader(inFile);
            FileOutputStream fos = new FileOutputStream(new File(outFile));
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            bufRead = new BufferedReader(input);
            Record record;
            while ((record = Utils.parseFile(bufRead)) != null) {
                // check weather we are interested in this record
                boolean check;
                if (days > 30) {
                    check = check(date, record.getDate(), DateCheckType.YEAR);
                } else {
                    check = check(date, record.getDate(), DateCheckType.MONTH);
                }

                // if we are interested in this record
                if (check) {
                    int key = record.getSymbol();
                    // check weather we already have the vector seen
                    VectorPoint point = currentPoints.get(key);
                    if (point == null) {
                        point = new VectorPoint(key, days);
                        currentPoints.put(key, point);
                    }
                    point.add(record.getPrice());

                    // sort the already seen symbols and determine how many days are there in this period
                    // we take the highest number as the number of days
                    if (currentPoints.size() > 1000 && size == -1) {
                        List<Integer> pointSizes = new ArrayList<Integer>();
                        for (VectorPoint v : currentPoints.values()) {
                            pointSizes.add(v.noOfElements());
                        }
                        Collections.sort(pointSizes);
                        size = pointSizes.get(pointSizes.size() - 1);
                        // printDates(pointSizes);
                        System.out.println("Number of stocks per period: " + size);
                    }

                    // now write the current vectors, also make sure we have the size determined correctly
                    if (currentPoints.size() > 1000 && size != -1) {
                        writeVectors(bufWriter, size);
                    }
                }
            }

            System.out.println("Size: " + size);
            // write the rest of the vectors in the map after finish reading the file
            writeVectors(bufWriter, size);
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

    /**
     * Write the current vector to file
     * @param bufWriter stream
     * @param size
     * @throws IOException
     */
    private void writeVectors(BufferedWriter bufWriter, int size) throws IOException {
        for(Iterator<Map.Entry<Integer, VectorPoint>> it = currentPoints.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, VectorPoint> entry = it.next();
            VectorPoint v = entry.getValue();
            if (v.noOfElements() == size) {
                String sv = v.serialize();
                // if many points are missing, this can return null
                if (sv != null) {
                    bufWriter.write(sv);
                    bufWriter.newLine();
                    // remove it from map
                    it.remove();
                }
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
        options.addOption("o", true, "Output file");
        options.addOption("s", true, "Start date");
        options.addOption("e", true, "End date");
        options.addOption("d", true, "Number of days");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String input = cmd.getOptionValue("i");
            String output = cmd.getOptionValue("o");
            String date = cmd.getOptionValue("s");
            String end = cmd.getOptionValue("e");
            String days = cmd.getOptionValue("d");

            VectorGenerator vg = new VectorGenerator(input, output, date, Integer.parseInt(days), end);
            vg.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
