
import edu.indiana.soic.spidal.common.BinaryReader2D;
import edu.indiana.soic.spidal.common.Range;

import java.io.*;
import java.nio.ByteOrder;

public class DistanceReader {
    static String file = "/home/supun/dev/projects/spidal/data/2004_2014_AUG_30/distances/2004_1_2005_1.csv";
//    static String file = "/home/supun/dev/projects/spidal/data/2004_2014_AUG_30/distances/static.csv";
    static int size = 6435;
//    static int size = 1000;

    public static void main(String[] args) {
//        long t = System.nanoTime();
//        writeFile();
        readByDistanceReader();
        //System.out.println((System.nanoTime() - t) * 1.0 / 1e6 + "ms");
//        readManually();
    }

    private static void readByDistanceReader() {
        short[][] distance = BinaryReader2D.readRowRange(file, new Range(0, size-1),
                size,
                ByteOrder.BIG_ENDIAN,
                true, 1.0);
        double cut = .3;
        long count = 0;
        long count2 = 0;
        long total = 0;
        for (int i = 0; i < size; ++i){
            for (int j = 0; j < size; ++j){
                double d = (distance[i][j] * 1.0) / Short.MAX_VALUE;

                if (d >= cut) continue;

                if (d == 0) {
                    count += 1;
                } else {
                    count2 += 1;
                }

                total++;
                //System.out.println(d);

            }
        }

        System.out.println(count);
        System.out.println(count2);
        System.out.println(total);
    }

    public static void readManually(){
        int size = 6435;
        try(DataInputStream dis = new DataInputStream(new FileInputStream(new File(file)))){
            double cut = 0.03;
            long count = 0;
            for (int i = 0; i < size; ++i){
                for (int j = 0; j < size; ++j){
                    double d = dis.readShort() * 1.0 / Short.MAX_VALUE;
                    if (d >= cut) continue;
                    ++count;
                }
            }

            System.out.println(count);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile() {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    short k = (short) (0.1 * Short.MAX_VALUE);
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
