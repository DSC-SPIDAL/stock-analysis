package edu.indiana.soic.ts.utils;

import java.util.Date;

public class Record {
    private double price;
    private int symbol;
    private Date date;
    private String dateString;
    private String symbolString;
    private int volume;
    private double factorToAdjPrice = 0;
    private double factorToAdjVolume = 0;

    public Record(double price, int symbol, Date date, String dateString, String symbolString, int volume, double factorToAdjPrice, double factorToAdjVolume) {
        setValues(price, symbol, date, dateString, symbolString, volume, factorToAdjPrice, factorToAdjVolume);
    }

    private void setValues(double price, int symbol, Date date, String dateString, String symbolString, int volume, double factorToAdjPrice, double factorToAdjVolume) {
        this.price = price;
        this.symbol = symbol;
        this.date = date;
        this.dateString = dateString;
        this.volume = volume;
        this.symbolString = symbolString;
        if (factorToAdjPrice > 0) {
            this.factorToAdjPrice = factorToAdjPrice;
        }
        if (factorToAdjVolume > 0) {
            this.factorToAdjVolume = factorToAdjVolume;
        }
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

    public double getFactorToAdjPrice() {
        return factorToAdjPrice;
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

    public double getFactorToAdjVolume() {
        return factorToAdjVolume;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.symbol).append(",");
        // sb.append(Utils.formatter.format(this.date)).append(",");
        sb.append(symbolString).append(",");
        if (factorToAdjVolume > 0) {
            sb.append(factorToAdjVolume).append(",");
            System.out.println("Writing factor to volume ============================= " + symbolString + " " + factorToAdjPrice);
        } else {
            sb.append(",");
        }

        if (factorToAdjPrice > 0) {
            System.out.println("Writing factor to price ============================= " + symbolString + " " + factorToAdjPrice);
            sb.append(factorToAdjPrice).append(",");
        } else {
            sb.append(",");
        }
        sb.append(price).append(",");
        sb.append(volume);
        return sb.toString();
    }
}
