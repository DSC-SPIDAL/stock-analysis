package edu.indiana.soic.ts.streaming.dataflow.utils;

import java.io.Serializable;

public class VectorPoint implements Serializable{
    private static final long serialVersionUID = 3915269597399355994L;

    int key;

    double []numbers;

    double totalCap = 0.0;

    String symbol;

    public VectorPoint(int key,  String symbol, double[] numbers, double totalCap) {
        this.key = key;
        this.numbers = numbers;
        this.totalCap = totalCap;
        this.symbol = symbol;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public double[] getNumbers() {
        return numbers;
    }

    public void setNumbers(double[] numbers) {
        this.numbers = numbers;
    }

    public double getTotalCap() {
        return totalCap;
    }

    public void setTotalCap(double totalCap) {
        this.totalCap = totalCap;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
