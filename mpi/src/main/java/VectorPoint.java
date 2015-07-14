import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.Random;

/**
 * A vector read from the file. It has a key to identify and a list of numbers
 */


public class VectorPoint {
    int key;
    double []numbers;
    int elements;
    /** the totalCap of a stock for this period */
    double totalCap = 0.0;

    public VectorPoint(int key, int size) {
        this.key = key;
        this.numbers = new double[size];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = -1;
        }
        elements = 0;
    }

    public int getKey() {
        return key;
    }

    public double[] getNumbers() {
        return numbers;
    }

    public int getElements() {
        return elements;
    }

    public VectorPoint(int key, double[] numbers) {
        this.key = key;
        this.numbers = numbers;
        this.elements = numbers.length;
    }

    public void addCap(double cap) {
        this.totalCap += cap;
    }

    public double weight(VectorPoint vc) {
        return vc.getTotalCap() * this.totalCap;
    }

    public double getTotalCap() {
        return totalCap;
    }

    public double correlation(VectorPoint vc, int type) {
        double []xs = vc.numbers;
        double []ys = this.numbers;
        if (type == 0) {
            return correlation(vc);
        } else if (type == 1) {
            EuclideanDistance distance = new EuclideanDistance();
            return distance.compute(xs, ys);
        } else if (type == 2) {
            return modCorrelation(xs, ys);
        } else if (type == 3) {
            double []x = StatUtils.normalize(xs);
            double y[] = StatUtils.normalize(ys);
            EuclideanDistance distance = new EuclideanDistance();
            return distance.compute(x, y);
        }
        return 0;
    }

    public double modCorrelation(double []xs, double []ys) {
        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;

        int n = xs.length;

        for(int i = 0; i < n; ++i) {
            double x = xs[i];
            double y = ys[i];

            sx += x;
            sy += y;
        }

        double sum = 0;
        for (int i = 0; i < n; i++) {
            double x = xs[i];
            double y = ys[i];
             sum += Math.abs(n * x / sx - n * y / sy);
        }

        return sum / n;
    }

    public double correlation(double []xs, double []ys) {
        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;

        int n = xs.length;

        for(int i = 0; i < n; ++i) {
            double x = xs[i];
            double y = ys[i];

            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
        }

        // covariation
        double cov = sxy / n - sx * sy / n / n;
        // standard error of x
        double sigmax = Math.sqrt(sxx / n -  sx * sx / n / n);
        // standard error of y
        double sigmay = Math.sqrt(syy / n -  sy * sy / n / n);
        // correlation is just a normalized covariation
        return (1 -cov / (sigmax * sigmay)) /2;
    }

    public double correlation(VectorPoint vc) {
        double []xs = vc.numbers;
        double []ys = this.numbers;

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        double cor = pearsonsCorrelation.correlation(xs, ys);
        return (1 - (1 + cor) / 2) * (1 - (1 + cor) / 2);
    }

    public void add(double number) {
        numbers[elements] = number;
        elements++;
    }

    public boolean isFull() {
        return elements == numbers.length - 1;
    }

    public int noOfElements() {
        return elements;
    }

    public String serialize() {
        double marketCap = this.totalCap / this.elements;
        StringBuilder sb = new StringBuilder(Integer.toString(key)).append(" ").append(Double.toString(marketCap)).append(" ");
        double previousVal = 0;
        int missingCount = 0;
        for (int i = 0; i < elements; i++) {
            if (numbers[i] == -1) {
                missingCount++;
                numbers[i] = previousVal;
            }
            sb.append(Double.toString(numbers[i])).append(" ");
            previousVal = numbers[i];
        }
        // if missing count is greater than 5% print and  ignore
        if (missingCount > (elements * 0.025)) {
            System.out.println("Missing count: " + missingCount);
            return null;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        double x[] = {1, 2, 3, 4, 5, 6, 7, 8};
//        double y[] = {2, 4, 8, 16, 32, 64, 128, 256};
//        double y[] = {1, 2, 3, 4, 5, 6, 7, 8};
        double y[] = {8, 7, 6, 5, 3, 2, 1, 0};

        VectorPoint vc = new VectorPoint(1, 10);
        double correlation = vc.correlation(x, y);
        System.out.println((1 - correlation) / 2);
        double x1 = vc.modCorrelation(x, y);
        System.out.println(x1);

        double a[] = new double[2048];
        double b[] = new double[2048];
        Random rand = new Random();
        for (int i = 0; i < 2048; i++) {
            a[i] = rand.nextDouble();
            b[i] = rand.nextDouble();
        }

        long t = System.currentTimeMillis();
        double []z = StatUtils.normalize(a);
        double k[] = StatUtils.normalize(b);
        EuclideanDistance distance = new EuclideanDistance();
        distance.compute(z, k);
        System.out.println(System.currentTimeMillis() - t);
    }
}
