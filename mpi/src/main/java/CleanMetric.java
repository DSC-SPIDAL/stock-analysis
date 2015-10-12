/**
 * Keep track of bad data points
 */
public class CleanMetric {
    public int negativeCount;
    public int missingValues;
    public int properSplitData;
    public int nonProperSplitData;
    public int constantStock;

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("Negative: ").append(negativeCount).append(" ");
        sb.append("MissingValues: ").append(missingValues).append(" ");
        sb.append("ProperSplitData: ").append(properSplitData).append(" ");
        sb.append("NonProperSplitData: ").append(nonProperSplitData).append(" ");
        sb.append("ConstantStock: ").append(constantStock).append(" ");

        return sb.toString();
    }
}
