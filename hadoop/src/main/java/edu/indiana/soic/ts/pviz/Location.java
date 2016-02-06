package edu.indiana.soic.ts.pviz;

import javax.xml.bind.annotation.XmlAttribute;

public class Location {
    private double x;
    private double y;
    private double z;

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location() {
    }

    @XmlAttribute
    public void setX(double x) {
        this.x = x;
    }

    @XmlAttribute
    public void setY(double y) {
        this.y = y;
    }

    @XmlAttribute
    public void setZ(double z) {
        this.z = z;
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
}
