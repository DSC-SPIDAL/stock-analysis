import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.Random;

/**
 * A vector read from the file. It has a key to identify and a list of numbers
 */


public class VectorPoint {
    int key;
    double []numbers;
    //double []factorToAjdPrices;
    //double []factorToAjdVolume;
    int elements;
    /** the totalCap of a stock for this period */
    double totalCap = 0.0;

    double factor = 1.0;

    static double maxChange = Double.MIN_VALUE;
    static double minChange = Double.MAX_VALUE;

    boolean constantVector = false;

    public static final double CONST_DISTANCE = .5;

    public VectorPoint(int key, int size) {
        this(key, size, false);
    }

    public VectorPoint(int key, int size, boolean indexed) {
        this.key = key;
        this.numbers = new double[size];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = -1;
        }
        if (indexed) {
            elements = size;
        } else {
            elements = 0;
        }
    }

    public void setConstantVector(boolean constantVector) {
        this.constantVector = constantVector;
    }

    public int getKey() {
        return key;
    }

    public double[] getNumbers() {
        return numbers;
    }

    public boolean isConstantVector() {
        if (key < 10) {
            return true;
        }
        return false;
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
        return vc.getTotalCap() * this.getTotalCap();
    }

    public double getTotalCap() {
        if (constantVector) {
            return  totalCap * Configuration.getInstance().weightAdjustForConstant;
        }
        return totalCap;
    }

    public double correlation(VectorPoint vc, int type) {
        double []xs = vc.numbers;
        double []ys = this.numbers;
        if (!constantVector && !vc.isConstantVector() || type >= 5) {
            if (type == 0) {
                double cor = correlation(vc);
                if (Double.isNaN(cor)) {
                    String s = serialize();
                    String s2 = vc.serialize();
//                if (isValid() && vc.isValid()) {
//                    System.out.println("Errrrrrrrrrrrrrrrrrrrrrrrrr");
                    System.out.println("Not a number..............................................");
                    if (!cleanVector(new CleanMetric())) System.out.println("Not valid");
                    System.out.println(s);
                    if (!vc.cleanVector(new CleanMetric())) System.out.println("Not valid");
                    System.out.println(s2);
                    System.out.println("NAN");
                }
                return cor;
            } else if (type == 1) {
                EuclideanDistance distance = new EuclideanDistance();
                return distance.compute(xs, ys);
            } else if (type == 2) {
                return modCorrelation(xs, ys);
            } else if (type == 3) {
                double[] x = StatUtils.normalize(xs);
                double y[] = StatUtils.normalize(ys);
                EuclideanDistance distance = new EuclideanDistance();
                return distance.compute(x, y);
            } else if (type == 4) {
                double c = correlation(vc);
                return c * c;
            } else if (type == 5) {
                double c = corr(vc);
                double l1 = vectorLength(1, this);
                double l2 = vectorLength(1, vc);
                double sqrt = Math.sqrt(Math.pow(l1, 2) + Math.pow(l2, 2) - 2 * l1 * l2 * c);
                if (sqrt == 0) {
                    //System.out.println("Sqrt = = 0");
                } else if (sqrt > 100000) {
                    System.out.println("Sqrt = " + sqrt);
                }
                return sqrt;
            } else if (type == 6) {
                double c = corr(vc);
                double l1 = vectorLength(2, this);
                double l2 = vectorLength(2, vc);
                return Math.sqrt(Math.pow(l1, 2) + Math.pow(l2, 2) - 2 * l1 * l2 * c);
            } else if (type == 7) {
                double c = corr(vc);
                double l1 = vectorLength(3, this);
                double l2 = vectorLength(3, vc);
                return Math.sqrt(Math.pow(l1, 2) + Math.pow(l2, 2) - 2 * l1 * l2 * c);
            } else if (type == 8) {
                double c = corr(vc);
                double l1 = 1;
                double l2 = 1;
                return Math.sqrt(Math.pow(l1, 2) + Math.pow(l2, 2) - 2 * l1 * l2 * c);
            }
        } else {
            return CONST_DISTANCE;
        }
        return 0;
    }

    public static double vectorLength(int type, VectorPoint vp) {
        if (vp.isConstantVector()) return 0;

        double change = vp.change();
        if (type == 1) {
            double pow = 10 * Math.abs(Math.log(change));
//            if (pow == 0) {
//                System.out.println("0 for: " + change);
//            } else if (pow > 10000) {
//                System.out.println("Infinity");
//            }
            return pow;
        } else if (type == 2) {
            return 10 * (change > 1 ? (change - 1) : (1 / change) - 1);
        } else if (type == 3) {
            SimpleRegression regression = new SimpleRegression();
            for (int i = 0; i < vp.elements; i++) {
                regression.addData(i, vp.numbers[i]);
            }
            return regression.getSlope();
        }
        return 0;
    }

    public double change() {
        if (elements >= 2) {
            if (numbers[0] > 0) {
                double v = numbers[elements - 1] / numbers[0];
                if (v > maxChange) {
                    maxChange = v;
                }
                if (v < minChange) {
                    minChange = v;
                }
                if (v > 3) v = 3;
                if (v < .1) v = .1;

//                double v1 = .02 * Math.abs(Math.exp(v));
//                if (v1 <= 0) {
//                    System.out.println("0");
//                }
                return v;
            }
        }
        return 1;
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


    public double correlation(VectorPoint vc) {
        if (vc.isConstantVector() || constantVector) return 0;

        double []xs = vc.numbers;
        double []ys = this.numbers;

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        double cor = pearsonsCorrelation.correlation(xs, ys);
        return (1 - (1 + cor) / 2);
    }

    public double corr(VectorPoint vc) {
        final int start = 100;
        double []xs;
        double []ys;
        ys = this.numbers;
        xs = vc.numbers;

        if (vc.getKey() == this.getKey()) {
            // both are same so return 1
            return 1;
        }

        if (vc.getKey() == 0 || this.getKey() == 0) {
            return 0;
        }

        if (vc.getKey() < 10) {
            if (this.getKey() < 10) {
                xs = new double[100];
            } else {
                xs = new double[ys.length];
            }
            double point1Delta = .1/250;
            double point2Delta = .2/250;
            double decreasePoint1Delta = .1/250;
            double decreasePoint2Delta = .2/250;
            if (vc.getKey() == 1) {
                for (int i = 0; i < xs.length; i++) {
                    xs[i] = start + i * point1Delta;
                }
            }
            if (vc.getKey() == 2) {
                for (int i = 0; i < xs.length; i++) {
                    xs[i] = start + i * point2Delta;
                }
            }
            if (vc.getKey() == 3) {
                for (int i = 0; i < xs.length; i++) {
                    xs[i] = start - i * decreasePoint1Delta;
                }
            }
            if (vc.getKey() == 4) {
                for (int i = 0; i < xs.length; i++) {
                    xs[i] = start - i * decreasePoint2Delta;
                }
            }
        }

        if (this.getKey() < 10) {
            if (vc.getKey() < 10) {
                ys = new double[100];
            } else {
                ys = new double[xs.length];
            }
            double point1Delta = 0.1/250;
            double point2Delta = 0.2/250;
            double decreasePoint1Delta = 0.1/250;
            double decreasePoint2Delta = 0.2/250;

            if (this.getKey() == 1) {
                for (int i = 0; i < ys.length; i++) {
                    ys[i] = start + i * point1Delta;
                }
            }
            if (this.getKey() == 2) {
                for (int i = 0; i < ys.length; i++) {
                    ys[i] = start + i * point2Delta;
                }
            }
            if (this.getKey() == 3) {
                for (int i = 0; i < ys.length; i++) {
                    ys[i] = start - i * decreasePoint1Delta;
                }
            }
            if (this.getKey() == 4) {
                for (int i = 0; i < ys.length; i++) {
                    ys[i] = start - i * decreasePoint2Delta;
                }
            }
        }

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        return pearsonsCorrelation.correlation(xs, ys);
    }

    public boolean add(double number, double factorToAdjPrice, double factoToAdjVolume, CleanMetric metric, int index) {
        if (numbers[index] != -1) return false;
        if (factorToAdjPrice > 0) {
            if (Math.abs(factorToAdjPrice - factoToAdjVolume) < .0001) {
                factor = factor * (factorToAdjPrice + 1);
                //System.out.println("New factor: " + key + " = " + factor);
                metric.properSplitData++;
            } else {
                //System.out.println("Pirce != Volume not adjusting: " + key + " = " + factor);
                metric.nonProperSplitData++;
            }
        }
        numbers[index] = factor * number;
        return true;
    }

    public void add(double number, double factorToAdjPrice, double factoToAdjVolume, CleanMetric metric) {
        if (factorToAdjPrice > 0) {
            if (Math.abs(factorToAdjPrice - factoToAdjVolume) < .0001) {
                factor = factor * (factorToAdjPrice + 1);
                //System.out.println("New factor: " + key + " = " + factor);
                metric.properSplitData++;
            } else {
                //System.out.println("Pirce != Volume not adjusting: " + key + " = " + factor);
                metric.nonProperSplitData++;
            }
        }
        numbers[elements] = factor * number;
        elements++;
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
        int missingCount = 0;
        for (int i = 0; i < elements; i++) {
            sb.append(Double.toString(numbers[i])).append(" ");
        }
        return sb.toString();
    }

    /**
     * Check weather this vector is a valid one
     * @return true if the vector is cleaned
     */
    public boolean cleanVector(CleanMetric metric) {
        if (constantVector) return true;
        // for now lets just check weather this has same values, if so this is not a valid vector
        if (elements <= 0) return false;
        double first = numbers[0];
        int missingCount = 0;
        double previousVal = 0;
        boolean change = false;
        for (int i = 0; i < elements; i++) {
            double n = numbers[i];
            if (numbers[i] < 0) {
                if (numbers[i] == -1) {
                    missingCount++;
                    numbers[i] = previousVal;
                } else {
                    numbers[i] = numbers[i] * -1;
                }
            }
            previousVal = numbers[i];
            if (Math.abs(n - first) > .0001) {
                change = true;
            }
        }

        if (missingCount > (elements * .05)) {
            metric.missingValues++;
//            for (int i = 0; i < elements; i++) {
//                System.out.print(numbers[i] + " ");
//            }
            return false;
        }

        if (!change) {
            for (int i = 0; i < elements; i++) {
                System.out.print(numbers[i] + " ");
            }
            metric.constantStock++;
        }

        return change;
    }

    public static void main(String[] args) {
        double x[] = {1, 2, 3, 4, 5, 6, 7, 8};
//        double y[] = {2, 4, 8, 16, 32, 64, 128, 256};
//        double y[] = {1, 2, 3, 4, 5, 6, 7, 8};
        double y[] = {1, 4, 6, 8, 10, 12, 14, 16};

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        double aa = pearsonsCorrelation.correlation(x, y);
        System.out.println(aa);

        VectorPoint vc = new VectorPoint(1, 10);
//        double correlation = vc.correlation(x, y);
        //System.out.println((1 - correlation) / 2);
        double x1 = vc.modCorrelation(x, y);
//        System.out.println(x1);

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
//        System.out.println(System.currentTimeMillis() - t);

        VectorPoint p = new VectorPoint(1, new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0});
//        System.out.println(p.cleanVector(new CleanMetric()));

        VectorPoint c0 = new VectorPoint(0, 100);
        VectorPoint c1 = new VectorPoint(1, 100);
        VectorPoint c2 = new VectorPoint(2, 100);
        VectorPoint c3 = new VectorPoint(3, 100);
        VectorPoint c4 = new VectorPoint(4, 100);

//        System.out.println(c0.corr(c1));
//        System.out.println(c1.corr(c2));
        System.out.println(c3.corr(c4));
//        System.out.println(c3.corr(c4));
    }
}
