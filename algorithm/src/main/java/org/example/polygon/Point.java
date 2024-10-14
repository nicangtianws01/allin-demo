package org.example.polygon;


public class Point {
    private double x;
    private double y;

    public double x() {
        return x;
    }

    public Point setX(double x) {
        this.x = x;
        return this;
    }

    public double y() {
        return y;
    }

    public Point setY(double y) {
        this.y = y;
        return this;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
