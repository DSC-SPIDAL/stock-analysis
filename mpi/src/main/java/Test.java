import java.text.ParseException;
import java.util.Date;

public class Test {
    public static void main(String[] args) {
        int a[] = {1, 2, 3, 4, 5};
        int b[] = {1, 2, 3, 4, 5};

        double vals[][] = new double[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                vals[i][j] = a[i] * b[j];
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                vals[i][j] = Math.max(.05 * 50, Math.pow(vals[i][j], .25));
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(vals[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();

        vals = new double[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                vals[i][j] = Math.max(.05 * 5, Math.pow(b[j], .25)) * Math.max(.05 * 5, Math.pow(a[i], .25));
            }
        }

//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 5; j++) {
//                vals[i][j] = Math.max(.05 * 50, Math.pow(vals[i][j], .25));
//            }
//        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(vals[i][j] + " ");
            }
            System.out.println();
        }

        try {
            Date d1 = Utils.formatter.parse("20040101");
            Date d2 = Utils.formatter.parse("20040101");
            System.out.println(d1.equals(d2));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
