import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class VectorGenerator {

    private Map<Integer, VectorPoint> currentPoints = new HashMap<Integer, VectorPoint>();

    public void processFile(String file, List<String> dates) {
        FileReader input = null;
        try {
            input = new FileReader(file);
            BufferedReader bufRead = new BufferedReader(input);
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
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open the file");
        }
    }
}
