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
        double []ys;
        final int start = 1;
        double alpha10 = 0.1;
        double alpha10Day = alpha10 / 250;
        double alpha20 = 0.2;
        double alpha20Day = alpha20 / 250;
        if (this.getKey() < 10) {
            ys = new double[elements];

            double bd = 1.0;
            double ed = 0.0;

            if (this.getKey() == 1) {
                for (int i = 0; i < ys.length; i++) {
                    ed = (1 + alpha10Day) * bd;
                    //System.out.println(ed);
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (this.getKey() == 2) {
                for (int i = 0; i < ys.length; i++) {
                    ed = (1 + alpha20Day) * bd;
                    //System.out.println(ed);
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (this.getKey() == 3) {
                for (int i = 0; i < ys.length; i++) {
                    ed = bd / (1 + alpha10Day);
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (this.getKey() == 4) {
//                StringBuilder sb = new StringBuilder("20 Dec YS: ");
                for (int i = 0; i < ys.length; i++) {
                    ed = bd / (1 + alpha20Day);
                    bd = ed;
                    ys[i] = ed;
//                    sb.append(ed).append(" ");
                }
//                System.out.println(sb);
            }
            this.numbers = ys;
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
        if (vp.getKey() == 0) return 0;

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
            return 10 * Math.abs(Math.log(Math.abs(regression.getSlope())));
        }
        return 0;
    }

    public double change() {
        if (elements >= 2) {
                double v = Utils.lastNonZero(numbers) / Utils.firstNonZero(numbers);
                if (v > maxChange) {
                    maxChange = v;
                }
                if (v < minChange) {
                    minChange = v;
                }
                if (v > 10) v = 10;
                if (v < .1) v = .1;
                return v;
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
        final int start = 1;
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

        double point1Delta = 0.01/250;
        double point2Delta = 0.02/250;
        double decreasePoint1Delta = (1.0 - 1.0/1.01)/250;
        double decreasePoint2Delta = (1.0- 1.0/1.02)/250;

        double alpha10 = 0.1;
        double alpha10Day = alpha10 / 250;
        double alpha20 = 0.2;
        double alpha20Day = alpha20 / 250;

        int noOfElements = elements >  1 ? elements : 100;
        if (vc.getKey() < 10) {
            if (this.getKey() < 10) {
                xs = new double[noOfElements];
            } else {
                xs = new double[ys.length];
            }

            double bd = 1.0;
            double ed = 0.0;

            if (vc.getKey() == 0) {
                for (int i = 0; i < xs.length; i++) {
                    if (i % 2 == 0) {
                        xs[i] = start + point1Delta;
                    } else {
                        xs[i] = start - point1Delta;
                    }
                }
            }
            else if (vc.getKey() == 1) {
                for (int i = 0; i < xs.length; i++) {
                    ed = (1 + alpha10Day) * bd;
                    //System.out.println(ed);
                    bd = ed;
                    xs[i] = ed;
                }
            }
            else if (vc.getKey() == 2) {
                for (int i = 0; i < xs.length; i++) {
                    ed = (1 + alpha20Day) * bd;
                    //System.out.println(ed);
                    bd = ed;
                    xs[i] = ed;
                }
            }
            else if (vc.getKey() == 3) {
                for (int i = 0; i < xs.length; i++) {
                    ed = bd / (1 + alpha10Day);
                    bd = ed;
                    xs[i] = ed;
                }
            }
            else if (vc.getKey() == 4) {
                //StringBuilder sb = new StringBuilder("20 Dec XS: ");
                for (int i = 0; i < xs.length; i++) {
                    ed = bd / (1 + alpha20Day);
                    bd = ed;
                    xs[i] = ed;
                    //sb.append(ed).append(" ");
                }
//                System.out.println(sb);
            }
        }

        if (this.getKey() < 10) {
            if (vc.getKey() < 10) {
                ys = new double[noOfElements];
            } else {
                ys = new double[xs.length];
            }

            double bd = 1.0;
            double ed = 0.0;

            if (this.getKey() == 0) {
                for (int i = 0; i < ys.length; i++) {
                    if (i % 2 == 0) {
                        ys[i] = start + point1Delta;
                    } else {
                        ys[i] = start - point1Delta;
                    }
                }
            }

            if (this.getKey() == 1) {
                for (int i = 0; i < ys.length; i++) {
                    ed = (1 + alpha10Day) * bd;
                    //System.out.println(ed);
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (this.getKey() == 2) {
                for (int i = 0; i < ys.length; i++) {
                    ed = (1 + alpha20Day) * bd;
                    //System.out.println(ed);
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (this.getKey() == 3) {
                for (int i = 0; i < ys.length; i++) {
                    ed = bd / (1 + alpha10Day);
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (this.getKey() == 4) {
//                StringBuilder sb = new StringBuilder("20 Dec YS: ");
                for (int i = 0; i < ys.length; i++) {
                    ed = bd / (1 + alpha20Day);
                    bd = ed;
                    ys[i] = ed;
//                    sb.append(ed).append(" ");
                }
//                System.out.println(sb);
            }
        }

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        try {
            double correlation = pearsonsCorrelation.correlation(xs, ys);
            return correlation;
        } catch (Exception e) {
            System.out.println(xs.length + " " + ys.length);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

//        if (vc.getKey() < 10 || this.getKey() < 10) {
//            System.out.println(correlation);
//        }

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
//        double x[] = {100, 101, 102, 103, 104, 105, 106, 107};
//        double y[] = {100, 102, 104, 106, 108, 110, 112, 114};
        double x[] = {100, 110, 120, 130, 140, 150, 160, 170};
        double y[] = {100, 150, 200, 250, 300, 350, 400, 450};

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

        double alpha = 0.1;
        double alphaDay = alpha / 250;
        double bd = 1;
        double ed = 0;
        for (int i = 0; i < 2800; i++) {
            ed = (1 + alphaDay) * bd;
            //System.out.println(ed);
            bd = ed;
        }

        bd = 1;
        for (int i = 0; i < 2800; i++) {
            ed = bd / (1 + alphaDay);
            System.out.println(ed);
            bd = ed;
        }
    }
}
