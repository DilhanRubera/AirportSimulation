package edu.curtin.saed.assignment1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Airport {

    private int airportId; //Storing airport id
    private String airportName; //Storing airport name
    private double x; //Storing airports x position
    private double y; //Storing airports y position

    //Blocking queues to share resources and communicate between the producer and consumer threads

    //Sharing an airports list of destination airports
    //The airport should send planes to these destination airports
    private BlockingQueue<Integer> destinationQueue;

    //Used to share an airports list of available planes
    private BlockingQueue<Integer> planeQueue;

    public Airport(int airportId,double x, double y ,int noOfPlanes){
       this.airportId =airportId;
       this.x =x;
       this.y =y;
       destinationQueue =new ArrayBlockingQueue<>(noOfPlanes);
        planeQueue= new ArrayBlockingQueue<>(noOfPlanes);

    }

    //Getters and setters
    public int getAirportId() {
        return airportId;
    }

    public void setAirportId(int airportId) {
        this.airportId = airportId;
    }
    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
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

    public BlockingQueue<Integer> getDestinationQueue(){
        return destinationQueue;
    }

    public BlockingQueue<Integer> getPlaneQueue(){
        return planeQueue;
    }


}
