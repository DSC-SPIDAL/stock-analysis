package msc.fall2015.stock.kmeans.hbase.utils;

public class CleanMetric {
    public int negativeCount;
    public int missingValues;
    public int properSplitData;
    public int nonProperSplitData;
    public int constantStock;
    public int totalStocks;
    public int invalidStocks;
    public int stocksWithIncorrectDays;
    public int lenghtWrong;
    public int dupRecords;
    public int writtenStocks;

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("Negative: ").append(negativeCount).append(" ");
        sb.append("MissingValues: ").append(missingValues).append(" ");
        sb.append("ProperSplitData: ").append(properSplitData).append(" ");
        sb.append("NonProperSplitData: ").append(nonProperSplitData).append(" ");
        sb.append("ConstantStock: ").append(constantStock).append(" ");
        sb.append("InvalidStock: ").append(invalidStocks).append(" ");
        sb.append("TotalStock: ").append(totalStocks).append(" ");
        sb.append("IncorrectDaysStocks: ").append(stocksWithIncorrectDays).append(" ");
        sb.append("Lenght: ").append(lenghtWrong).append(" ");
        sb.append("Duprecords exceeded: ").append(dupRecords).append(" ");
        sb.append("Written stocks: ").append(writtenStocks).append(" ");
        return sb.toString();
    }
}
