import java.io.*;
import java.util.*;

public class VectorGenerator {
    private final String inFile;
    private final String outFile;
    private Map<Integer, VectorPoint> currentPoints = new HashMap<Integer, VectorPoint>();

    private int days;

    private Date startDate;

    public VectorGenerator(String inFile, String outFile, String startDate, int days) {
        this.days = days;
        this.inFile = inFile;
        this.outFile = outFile;
        this.startDate = Utils.parseDateString(startDate);
    }

    public void process() {
        List<String> dates = getDates(inFile, startDate);
        processFile(inFile, dates, outFile);
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
            Record record = null;
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
                datesForSymbol.add(record.getDateString());
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

    public void processFile(String inFile, List<String> dates, String outFile) {
        BufferedWriter bufWriter = null;
        BufferedReader bufRead = null;
        try {
            FileReader input = new FileReader(inFile);
            FileOutputStream fos = new FileOutputStream(new File(outFile));
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            bufRead = new BufferedReader(input);
            Record record = null;
            int size = dates.size();
            while ((record = Utils.parseFile(bufRead)) != null) {
                // check weather we are interested in this record
                if (dates.contains(record.getDateString())) {
                    int key = record.getSymbol();
                    // check weather we already have the key
                    VectorPoint point = currentPoints.get(key);
                    if (point == null) {
                        point = new VectorPoint(key, size);
                        currentPoints.put(key, point);
                    }
                    point.add(dates.indexOf(record.getDateString()), record.getPrice());

                    // now lets check weather this point is full
                    if (point.isFull()) {
                        String sv = point.serialize();
                        bufWriter.write(sv);
                        bufWriter.newLine();
                    }
                }
            }
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
     *  We need to do some sanity checks in case some of the stocks doesn't have complete data for the date range
     */
    private void sanityCheck() {

    }
}
