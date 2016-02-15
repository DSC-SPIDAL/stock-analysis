package edu.indiana.soic.ts.utils;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.text.DecimalFormat;

public class Point {
    int index;
    double x, y, z;
    int clazz;
    String symbol;
    DecimalFormat four = new DecimalFormat("#0.0000");

    public Point(int index, double x, double y, double z, int clazz, String symbol) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.z = z;
        this.clazz = clazz;
        this.symbol = symbol;
    }

    public double distance(Point p) {
        EuclideanDistance distance = new EuclideanDistance();
        return distance.compute(new double[]{p.x, p.y, p.z}, new double[]{x, y, z});
    }

    public int getIndex() {
        return index;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getClazz() {
        return clazz;
    }

    public String getSymbol() {
        return symbol;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(index)).append("\t");
        sb.append(four.format(x)).append("\t").append(four.format(y)).append("\t").append(four.format(z)).append("\t");
        sb.append(Integer.toString(clazz));
        return sb.toString();
    }

    public void setClazz(int clazz) {
        this.clazz = clazz;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
