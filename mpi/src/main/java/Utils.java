import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public static Record parseFile(BufferedReader reader) throws FileNotFoundException {
        String myLine = null;
        try {
            while ((myLine = reader.readLine()) != null) {
                String[] array = myLine.split(" ");
                if (array.length >= 3) {
                    int permNo = Integer.parseInt(array[0]);
                    Date date = formatter.parse(array[1]);
                    double price = Double.parseDouble(array[2]);
                    return new Record(price, permNo, date, array[1]);
                }
            }
        } catch (IOException | ParseException | NumberFormatException e) {
            throw new RuntimeException("Failed to read content from file", e);
        }
        return null;
    }

    public static Date parseDateString(String date) {
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date", e);
        }
    }

    public static String dateToString(Date date) {
        return formatter.format(date);
    }
}
