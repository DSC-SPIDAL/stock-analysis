package edu.indiana.soic.ts.utils;

public class VectorPoint {
    int key;
    double []numbers;
    int elements;
    /** the totalCap of a stock for this period */
    double totalCap = 0.0;
    double factor = 1.0;
    String symbol;
    boolean constantVector = false;

    public VectorPoint(int key, String symbol, double[] numbers, double totalCap) {
        this.key = key;
        this.symbol = symbol;
        this.numbers = numbers;
        this.totalCap = totalCap;
    }

    public VectorPoint(int key, String symbol, int size, boolean indexed) {
        this.key = key;
        this.symbol = symbol;
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
        double alpha10 = 0.1;
        double alpha10Day = alpha10 / 250;
        double alpha20 = 0.2;
        double alpha20Day = alpha20 / 250;
        if (this.getKey() < 10) {
            ys = new double[elements];

            double bd = 1.0;
            double ed;

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
                for (int i = 0; i < ys.length; i++) {
                    ed = bd / (1 + alpha20Day);
                    bd = ed;
                    ys[i] = ed;
                }
            }
            this.numbers = ys;
        }
    }

    public String getSymbol() {
        return symbol;
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

    public void setTotalCap(double totalCap) {
        this.totalCap = totalCap;
    }

    public void setElements(int elements) {
        this.elements = elements;
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
            return  totalCap * 10;
        }
        return totalCap;
    }

    public boolean add(double number, int index) {
        if (numbers[index] != -1) return false;
        numbers[index] =number;
        return true;
    }

    public boolean add(double number, double factorToAdjPrice, double factoToAdjVolume, CleanMetric metric, int index) {
        if (numbers[index] != -1) return false;
        if (factorToAdjPrice > 0) {
            if (Math.abs(factorToAdjPrice - factoToAdjVolume) < .0001) {
                factor = factor * (factorToAdjPrice + 1);
                metric.properSplitData++;
            } else {
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
                metric.properSplitData++;
            } else {
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
        StringBuilder sb = new StringBuilder().append(symbol).append(',').append(Double.toString(marketCap)).append(",");
        for (int i = 0; i < elements-1; i++) {
            sb.append(Double.toString(numbers[i])).append(",");
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
}
