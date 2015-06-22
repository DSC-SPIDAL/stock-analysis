import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {
    public static ArrayList<Record> parseFile(String file, int start, int end) throws FileNotFoundException {
        FileReader input = new FileReader(file);
        BufferedReader bufRead = new BufferedReader(input);
        String myLine = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        ArrayList<Record> records = new ArrayList<Record>();
        try {
            while ((myLine = bufRead.readLine()) != null) {
                String[] array = myLine.split(" ");
                if (array.length >= 3) {
                    int permNo = Integer.parseInt(array[0]);
                    Date date = formatter.parse(array[1]);
                    double price = Double.parseDouble(array[2]);
                    Record r = new Record(price, permNo, date);
                    records.add(r);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return records;
    }
}
