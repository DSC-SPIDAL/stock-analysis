
public class Point {
    int index;
    double x, y, z;
    int clazz;

    public Point(int index, double x, double y, double z, int clazz) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.z = z;
        this.clazz = clazz;
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
}
