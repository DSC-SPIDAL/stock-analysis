import java.io.*;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

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
                    if (array.length >= 5) {
                        double price = -1;
                        if (!array[3].equals("")) {
                            price = Double.parseDouble(array[3]);
                            if (price < 0) {
                                price *= -1;
                            }
                        }
                        int volume = 0;
                        if (!array[4].equals("")) {
                            volume = Integer.parseInt(array[4]);
                        }
                        return new Record(price, permNo, date, array[1], stringSymbol, volume);
                    } else {
                        return new Record(-1, permNo, date, array[1], stringSymbol, 0);
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
                theDir.mkdirs();
            } catch (SecurityException se) {
                //handle it
            }
        }
    }

    private static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if (inputChannel != null) {
                inputChannel.close();
            }
            if (outputChannel != null) {
                outputChannel.close();
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

    public static Date addYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
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
        return cal.get(Calendar.YEAR) + "_" + (cal.get(Calendar.MONTH) + 1);
    }

    public static String getYearString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR) + "";
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
                        double cap = Double.parseDouble(parts[1]);

                        int vectorLength = parts.length - 2;
                        double[] numbers = new double[vectorLength];
                        for (int i = 2; i < parts.length; i++) {
                            numbers[i - 2] = Double.parseDouble(parts[i]);
                        }
                        VectorPoint p = new VectorPoint(key, numbers);
                        p.addCap(cap);
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

    public static List<Integer> readVectorKeys(File file) {
        List<Integer> keys = new ArrayList<Integer>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                String parts[] = line.trim().split(" ");
                if (parts.length > 0 && !(parts.length == 1 && parts[0].equals(""))) {
                    int key = Integer.parseInt(parts[0]);
                    keys.add(key);
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
        return keys;
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
        String []splits = line.split("\",\"");
        return new SectorRecord(splits[5].replaceAll("^\"|\"$", ""), splits[0].replaceAll("^\"|\"$", ""));
    }

    public static Bin readBin(String line) {
        String []parts = line.split(",");
        double start = Double.parseDouble(parts[1]);
        double end = Double.parseDouble(parts[2]);
        Bin bin = new Bin();
        for (int i = 3; i < parts.length; i++) {
            bin.symbols.add(parts[i]);
        }
        return bin;
    }

    // first read the original stock file and load the mapping from permno to stock symbol
    public static Map<Integer, String> loadMapping(String inFile) {
        System.out.println("Reading original stock file: " + inFile);
        BufferedReader bufRead = null;
        Map<Integer, String> maps = new HashMap<Integer, String>();
        try {
            FileReader input = new FileReader(inFile);
            bufRead = new BufferedReader(input);

            Record record;
            while ((record = Utils.parseFile(bufRead)) != null) {
                maps.put(record.getSymbol(), record.getSymbolString());
            }
            System.out.println("No of stocks: " + maps.size());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open file");
        }
        return maps;
    }
}
