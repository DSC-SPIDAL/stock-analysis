import java.util.Date;

public class Record {
    private double price;
    private int symbol;
    private Date date;
    private String dateString;
    private String symbolString;

    public Record(double price, int symbol, Date date, String dateString, String symbolString) {
        this.price = price;
        this.symbol = symbol;
        this.date = date;
        this.dateString = dateString;
        this.symbolString = symbolString;
    }

    public Record(double price, int symbol, Date date, String dateString) {
        this.price = price;
        this.symbol = symbol;
        this.date = date;
        this.dateString = dateString;
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
}
