package msc.fall2015.stock.kmeans.hbase.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Methods for parsing the CSV file
 */
public class FileUtils {
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    /**
     * Parse the given line and create a record
     * @param line the line containing the stock data
     * @return a record
     */
    public static Record parseLine(String line) {
        String myLine = null;
        try {
            String[] array = myLine.trim().split(",");
            if (array.length >= 3) {
                int permNo = Integer.parseInt(array[0]);
                Date date = FileUtils.formatter.parse(array[1]);
                String stringSymbol = array[2];
                if (array.length >= 7) {
                    double price = -1;
                    if (!array[5].equals("")) {
                        price = Double.parseDouble(array[5]);
                    }

                    double factorToAdjPrice = 0;
                    if (!"".equals(array[4].trim())) {
                        factorToAdjPrice = Double.parseDouble(array[4]);
                    }

                    double factorToAdjVolume = 0;
                    if (!"".equals(array[3].trim())) {
                        factorToAdjVolume = Double.parseDouble(array[3]);
                    }

                    int volume = 0;
                    if (!array[6].equals("")) {
                        volume = Integer.parseInt(array[6]);
                    }

                    return new Record(price, permNo, date, array[1], stringSymbol, volume, factorToAdjPrice, factorToAdjVolume);
                } else {
                    return new Record(-1, permNo, date, array[1], stringSymbol, 0, 0, 0);
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException("Failed to read content from file", e);
        }
        return null;
    }
}
