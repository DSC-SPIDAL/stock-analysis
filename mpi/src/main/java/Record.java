import java.util.Date;

public class Record {
    private double price;
    private int symbol;
    private Date date;

    public Record(double price, int symbol, Date date) {
        this.price = price;
        this.symbol = symbol;
        this.date = date;
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
}
