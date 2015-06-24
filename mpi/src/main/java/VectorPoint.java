/**
 * A vector read from the file. It has a key to identify and a list of numbers
 */

public class VectorPoint {
    String key;
    double []numbers;

    public VectorPoint(String key, double[] numbers) {
        this.key = key;
        this.numbers = numbers;
    }

    public double correlation(VectorPoint vc) {
        double sum = 0 ;
        for (int i = 0; i < numbers.length; i++) {
            sum += numbers[i];
        }
        return sum / numbers.length;
    }
}
