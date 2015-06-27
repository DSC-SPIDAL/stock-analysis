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
        double sum = 0 ;
        for (int i = 0; i < numbers.length; i++) {
            sum += numbers[i];
        }
        return sum / numbers.length;
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
