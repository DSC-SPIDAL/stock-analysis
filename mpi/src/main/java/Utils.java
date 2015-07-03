import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public static Record parseFile(BufferedReader reader) throws FileNotFoundException {
        String myLine = null;
        try {
            while ((myLine = reader.readLine()) != null) {
                String[] array = myLine.trim().split(",");
                if (array.length >= 3) {
                    int permNo = Integer.parseInt(array[0]);
                    Date date = formatter.parse(array[1]);
                    String stringSymbol = array[2];
                    if (array.length >= 4) {
                        double price = Double.parseDouble(array[3]);
                        if (price < 0) {
                            price *= -1;
                        }
                        return new Record(price, permNo, date, array[1], stringSymbol);
                    } else {
                        return new Record(-1, permNo, date, array[1], stringSymbol);
                    }
                }
            }
        } catch (IOException | ParseException | NumberFormatException e) {
            throw new RuntimeException("Failed to read content from file", e);
        }
        return null;
    }

    public static void createDirectory(String directoryName) {
        File theDir = new File(directoryName);
        if (!theDir.exists()) {
            System.out.println("creating directory: " + directoryName);
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }
    }

    public static Date parseDateString(String date) {
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date", e);
        }
    }

    public static Date addMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    public static String getMonthString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.MONTH);
    }

    public static String dateToString(Date date) {
        return formatter.format(date);
    }

    public static List<VectorPoint> readVectors(File file, int startIndex, int endIndex) {
        List<VectorPoint> vecs = new ArrayList<VectorPoint>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            int readCount = 0;
            while ((line = br.readLine()) != null) {
                if (count >= startIndex) {
                    readCount++;
                    // process the line.
                    String parts[] = line.trim().split(" ");
                    if (parts.length > 0 && !(parts.length == 1 && parts[0].equals(""))) {
                        int key = Integer.parseInt(parts[0]);
                        int vectorLength = parts.length - 1;
                        double[] numbers = new double[vectorLength];
                        if (vectorLength != parts.length - 1) {
                            throw new RuntimeException("The number of points in file " + (parts.length - 1) +
                                    " is not equal to the expected value: " + vectorLength);
                        }

                        for (int i = 1; i < parts.length; i++) {
                            numbers[i - 1] = Double.parseDouble(parts[i]);
                        }
                        VectorPoint p = new VectorPoint(key, numbers);
                        vecs.add(p);
                    }

                }
                count++;
                // we stop
                if (readCount > endIndex - startIndex) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignore) {
                }
            }
        }
        return vecs;
    }

    public static Point readPoint(String line) {
        String []splits = line.split("\t");

        int i = Integer.parseInt(splits[0]);
        double x = Double.parseDouble(splits[1]);
        double y = Double.parseDouble(splits[2]);
        double z = Double.parseDouble(splits[3]);
        int clazz = Integer.parseInt(splits[4]);

        return new Point(i, x, y, z, clazz);
    }

    public static SectorRecord readSectorRecord(String line) {
        String []splits = line.split(",");
        return new SectorRecord(splits[5].replaceAll("^\"|\"$", ""), splits[0].replaceAll("^\"|\"$", ""));
    }
}
