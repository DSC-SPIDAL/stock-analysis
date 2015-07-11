import java.util.Date;

public class Record {
    private double price;
    private int symbol;
    private Date date;
    private String dateString;
    private String symbolString;
    private int volume;

    public Record(double price, int symbol, Date date, String dateString, String symbolString, int volume) {
        this.price = price;
        this.symbol = symbol;
        this.date = date;
        this.dateString = dateString;
        this.volume = volume;
        this.symbolString = symbolString;
    }

    public Record(double price, int symbol, Date date, String dateString) {
        this.price = price;
        this.symbol = symbol;
        this.date = date;
        this.dateString = dateString;
    }

    public int getVolume() {
        return volume;
    }

    public String getSymbolString() {
        return symbolString;
    }

    public double getPrice() {
        return price;
    }

    public int getSymbol() {
        return symbol;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        return dateString;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.symbol).append(",");
        sb.append(Utils.formatter.format(this.date)).append(",");
        sb.append(symbolString).append(",");
        sb.append(price).append(",");
        sb.append(volume);
        return sb.toString();
    }
}
