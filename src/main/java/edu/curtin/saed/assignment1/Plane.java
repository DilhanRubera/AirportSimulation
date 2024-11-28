package edu.curtin.saed.assignment1;

public class Plane {
    private int planeId; //Storing plane id
    private double x; //Storing planes x location
    private double y; //Storing planes y location

    public Plane (int planeId, double x, double y){
        this.planeId =planeId;
        this.x=x;
        this.y=y;
    }

    //Getters and setters
    public int getPlaneId() {
        return planeId;
    }

    public void setPlaneId(int planeId) {
        this.planeId = planeId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


}
