package edu.curtin.saed.assignment1;

import java.io.BufferedReader;
import java.io.IOException;

public class FlightRequestProducer implements Runnable {

private Airport airport; //Instance of the airport
private int noOfAirports; //Variable to hold number of airports

    public FlightRequestProducer (Airport airport, int noOfAirports)
    {
    this.airport = airport;
    this.noOfAirports=noOfAirports;
    }

    @Override
    public void run() {

        //Running the process to get flight request details for an airport
        try {
            Process proc = Runtime.getRuntime().exec(
                    new String[]{"comms/bin/saed_flight_requests.bat",
                            String.valueOf(noOfAirports),
                            String.valueOf(airport.getAirportId())});

            boolean running = true;
            try(BufferedReader br = proc.inputReader()){
            while (running) {
                String line = br.readLine();
                try {
                    int destinationAirport = Integer.parseInt(line);

                    //Putting the new destination in the destination queue
                    airport.getDestinationQueue().put(destinationAirport);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;

                }
            }
        } catch (  IOException e) {

            System.out.println("Error reading destination id from process");

        }}catch (IOException e){
            System.out.println("Error running process");

        }
    }

    //Method to get next flight destination from blocking queue
    //Blocking method
    public int getNextFlightDestination() throws InterruptedException{
        return airport.getDestinationQueue().take();
    }

    //Method to get next available plane id from blocking queue
    //Blocking method
    public int getAvailablePlaneId() throws  InterruptedException{
        return airport.getPlaneQueue().take();
    }

}

