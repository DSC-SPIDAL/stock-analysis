
import edu.indiana.soic.spidal.common.BinaryReader2D;
import edu.indiana.soic.spidal.common.Range;

import java.io.*;
import java.nio.ByteOrder;
import java.util.Random;

public class DistanceReader {
    static String file = "/home/supun/dev/projects/dsspidal/data/2004_2014_AUG_30/preproc/distances/2004_1_2005_1.csv";
    static String secondFile = "/home/supun/dev/projects/dsspidal/data/2004_2014_AUG_30/preproc/distances/text2.csv";
//    static String file = "/home/supun/dev/projects/dsspidal/data/2004_2014_AUG_30/preproc/distances/static.csv";
    static int size = 6435;
//    static int size = 1000;
    static double cut = .1;
    static double cut2 = 3276.7;

    public static void main(String[] args) {
        double a = .02;
        short b = 5;
        short c = (short) (a * Short.MAX_VALUE);
        double d = ((double) c) / (double)Short.MAX_VALUE;
        System.out.println(d);

//        writeFile();
//        readByDistanceReader();
        test2();
//        test();
//        readManually();
        //readWrite();
    }

    public static void test() {
        Random random = new Random();
        long count = 0;
        long count2 = 0;
        for (int i = 0; i < 6435 * 6435; i++) {
            double value = random.nextDouble();
            if (value < 0.1) {
                count++;
            }
            short val = (short) (value * Short.MAX_VALUE);
            if (val < 3276.7) {
                count2++;
            }
        }
        System.out.println(count);
        System.out.println(count2);
    }

    public static void test2() {
        long count = 0;
        long count2 = 0;
        long count3 = 0;
        long count4 = 0;
        Random random = new Random();
        double values[][] = new double[6435][];
        for (int i = 0; i < 6435; i++) {
            values[i] = new double[6435];
            for (int j = 0; j < 6435; j++) {
                values[i][j] = random.nextDouble();
            }
        }

        for (int i = 0; i < 6435; i++) {
            for (int j = 0; j < 6435; j++) {
                double doubleValue = values[i][j];
                int shortValue = (int) (doubleValue * Short.MAX_VALUE);
                if (shortValue < 3277) {
                    count2++;
                    if (doubleValue > .1) {
                        System.out.println("value double : " + doubleValue);
                        System.out.println("value short : " + shortValue);
                    } else {
                        count++;
                    }
                }
                if (doubleValue < .1) {
                    count3++;
                    if (shortValue > 3277) {
                        count4++;
                    }
                }
            }
        }
        System.out.println("count " + count);
        System.out.println("count2 " + count2);
        System.out.println("count3 " + count3);
        System.out.println("count4 " + count4);
    }


    private static void readByDistanceReader() {
        short[][] distance = Utils.readRowRange(file, new Range(0, size-1),
                size,
                ByteOrder.BIG_ENDIAN);
        long count = 0;
        long count2 = 0;
        long total = 0;
        long total2 = 0;
        for (int i = 0; i < size; ++i){
            for (int j = 0; j < size; ++j){
                short i1 = distance[i][j];
                double d = (i1 * 1.0) / Short.MAX_VALUE;

                if (i1 < cut2) {
                    total2++;
                }
                if (d < cut) {
                    total++;
                }
            }
        }

        System.out.println(total);
        System.out.println(total2);
    }

    public static void readManually(){
        int size = 6435;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(new File(file)))) {
            long count = 0;
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    double d = dis.readShort() * 1.0 / Short.MAX_VALUE;
                    if (d >= cut) continue;
                    ++count;
                }
            }
            System.out.println(count);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readWrite() {
        short[][] distance = BinaryReader2D.readRowRange(file, new Range(0, size-1),
                size,
                ByteOrder.BIG_ENDIAN,
                true, 1.0);
        writeFile(secondFile, distance);
    }

    public static void writeFile(String file, short [][]values) {
        WriterWrapper writerWrapper = new WriterWrapper(file, true);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                writerWrapper.writeShort(values[i][j]);
            }
            writerWrapper.line();
        }
        writerWrapper.close();
    }

    public static void writeFile() {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    short k = (short) (0.01 * Short.MAX_VALUE);
                    dataOutputStream.writeShort(k);
                }
            }
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
