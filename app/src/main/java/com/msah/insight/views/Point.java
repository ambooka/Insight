package com.msah.insight.views;


public class Point {
    public int x;
    public int y;

    // Required default constructor for Firebase serialization / deserialization
    private Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
