import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 * A vector read from the file. It has a key to identify and a list of numbers
 */


public class VectorPoint {
    int key;
    double []numbers;
    int elements;

    public VectorPoint(int key, int size) {
        this.key = key;
        this.numbers = new double[size];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = -1;
        }
        elements = 0;
    }

    public VectorPoint(int key, double[] numbers) {
        this.key = key;
        this.numbers = numbers;
        this.elements = 0;
    }

    public double correlation(VectorPoint vc) {
        double []xs = vc.numbers;
        double []ys = this.numbers;

//        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
//        double cor = pearsonsCorrelation.correlation(xs, ys);
//        return (1 - cor) / 2;

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

    public void add(double number) {
        numbers[elements] = number;
        elements++;
    }

    public boolean isFull() {
        for (double n : numbers) {
            if (n == -1) {
                return false;
            }
        }
        return true;
    }

    public int noOfElements() {
        return elements;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder(Integer.toString(key)).append(" ");
        for (int i = 0; i < elements; i++) {
            sb.append(Double.toString(numbers[i])).append(" ");
        }
        return sb.toString();
    }
}
