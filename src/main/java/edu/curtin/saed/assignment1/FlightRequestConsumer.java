package edu.curtin.saed.assignment1;


import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("PMD.CloseResource")
public class FlightRequestConsumer implements  Runnable {

    private Airport airport; //Variable to store instance of airport
    private  FlightRequestProducer producer; //Variable to store the FlightRequestProducer instance
    private GridArea area; //Variable to store grid area
    private Map<Integer, GridAreaIcon> planeIcons; //Variable to store and access the plane icons
   private Map<Integer, Airport> airports; //Variable to store and access the map of airports

    public FlightRequestConsumer(Airport airport, FlightRequestProducer producer,GridArea area,Map<Integer, GridAreaIcon> planeIcons,Map<Integer, Airport> airports) {

        this.airport = airport;
        this.producer=producer;
        this.area=area;
        this.planeIcons=planeIcons;
        this.airports=airports;
    }


    @Override
    public void run() {

        Integer destinationAirport=-1;
        int sendingPlaneId=-1;

        //Declaring the thread pool to move multiple planes simultaneously  to their destinations
        ExecutorService executorServices= Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());

        try{

        //Run while the thread is not interrupted
        while (!Thread.currentThread().isInterrupted()) {
            try {

                //Getting a destination airport from the blocking queue
                destinationAirport = producer.getNextFlightDestination();
            } catch (InterruptedException e) {

                //Interrupting the current thread if there's an interrupt that's thrown
                Thread.currentThread().interrupt();
            }

            try {
                //Getting an available plane from the blocking queue
                sendingPlaneId = producer.getAvailablePlaneId();

            } catch (InterruptedException e) {
                //Interrupting the current thread if there's an interrupt that's thrown
                Thread.currentThread().interrupt();
            }

            //Check if the current thread is not interrupted
            if(!Thread.currentThread().isInterrupted()){
                //Check if the destination airport and the plane id is not equal -1
            if ((destinationAirport != -1) && (sendingPlaneId != -1)) {
                //Using a thread from the thread pool to move the plane to the airport
                executorServices.execute(moveToAirportTask(airport.getAirportId(), sendingPlaneId, destinationAirport, area, planeIcons, airports));
            }}

        }

        }
        finally {
            // Shutting down the thread pool when the program is interrupted
                executorServices.shutdownNow();
            }
    }

    //Runnable task used to move plane to a new airport
    private Runnable moveToAirportTask(int currentAirportId, int planeId, int airportId, GridArea area, Map<Integer, GridAreaIcon> planeIcons,Map<Integer, Airport> airports) {
        return () -> {
            boolean running = true;
            //Incrementing the number of planes in flight
            App.setPlanesInFlight(App.getPlanesInFlight()+1);

            //Updating the labels
            Platform.runLater(() -> {
                        App.updateLabels();
                        App.displayText("Plane: " + planeId + " is taking off from airport: " + currentAirportId );
                    }
            );
            while (running) {

                //Getting the plane icon from the map of plane icons
                GridAreaIcon planeIcon = planeIcons.get(planeId);

                //Getting the destination airport from the map of airports
                Airport destination = airports.get(airportId);


                if (planeIcon != null && destination != null) {
                    //Get initial airports x and y coordinates
                    double initialAirportX = planeIcon.getX();
                    double initialAirportY = planeIcon.getY();

                    //Get destination airports x and y coordinates
                    double destinationAirportX = destination.getX();
                    double destinationAirportY = destination.getY();

                    //Calculating the angle of the plane and the destination airport
                    double angle = Math.toDegrees(Math.atan2(destinationAirportY - initialAirportY, destinationAirportX - initialAirportX)) + 90;
                    //Setting the angle of the plane to the destination
                    planeIcon.setRotation(angle);

                    //Using pythagorean theorem to calculate the distance between the two points
                    double distance = Math.sqrt(Math.pow(destinationAirportX - initialAirportX, 2) + Math.pow(destinationAirportY - initialAirportY, 2));

                    //Multiplying the distance by 4 for smoother movement
                    double steps = 4*distance;

                    //Determine the total number of steps in the x direction
                    double stepX = (destinationAirportX - initialAirportX) / steps;

                    //Determine the total number of steps in the y direction
                    double stepY = (destinationAirportY - initialAirportY) / steps;

                        try {
                            for (int i = 1; i <= steps; i++) {
                                //Incrementing the planes position
                                double newX = initialAirportX + stepX * i;
                                double newY = initialAirportY + stepY * i;

                                //Displaying the planes new position
                                Platform.runLater(() -> {
                                    planeIcon.setPosition(newX, newY);
                                    area.requestLayout();
                                });

                                //Delay the thread to stimulate movement
                                Thread.sleep(100);
                            }
                            //Once the plane reaches the airport, set the planes coordinate's as the airport coordinates
                            Platform.runLater(() -> {
                                planeIcon.setPosition(destinationAirportX, destinationAirportY);

                                //Update the total completed flights
                                App.setFlightNumber(App.getFlightNumber()+1);
                                //Decrement the number of flights in flight
                                App.setPlanesInFlight(App.getPlanesInFlight()-1);

                                //Update labels and display the text
                                App.updateLabels();
                                area.requestLayout();
                                App.displayText("Plane: "+planeId+" arrived at its destination: "+destination.getAirportId());

                            });

                            //Service the plane at the destination airport
                            getServicingTask(destination,planeId).run();


                        } catch (InterruptedException e) {
                            //Interrupt the current thread if an interrupt is caught
                            Thread.currentThread().interrupt();

                        }

                        area.requestLayout();

                }

                //Set running as false to break out of the loop
                running = false;

            }
        };
    }

    //Method to simulate the plane being serviced
   private Runnable getServicingTask(Airport airport,int planeId){
        return ()->{
            //Increment the number of planes getting serviced
            App.setPlanesServicing(App.getPlanesServicing()+1);
            //Update the text
            Platform.runLater(() ->{ App.updateLabels();
                App.displayText("Servicing starting for plane: "+planeId+" at airport: "+airport.getAirportId());
            });
            try {
                //Run the servicing process
                Process proc = Runtime.getRuntime().exec(
                        new String[]{"comms/bin/saed_plane_service.bat",
                                String.valueOf(airport.getAirportId()),
                                String.valueOf(planeId)});


                BufferedReader br = proc.inputReader();
               // String line;
                while (( br.readLine())!=null) {

                    //If the current thread is interrupted destroy the process and return
                    if (Thread.currentThread().isInterrupted()) {
                        proc.destroy();
                        throw new InterruptedException();
                    }


                    //System.out.println(line);
                    //Updating the number of planes being serviced
                    App.setPlanesServicing(App.getPlanesServicing()-1);
                    //Update the labels
                    Platform.runLater(() -> {App.updateLabels();
                        App.displayText("Servicing done for plane: "+planeId+" at Airport: "+airport.getAirportId());

                    });
                    //Put the plane back in the queue
                    airport.getPlaneQueue().put(planeId);

                }
            } catch (InterruptedException e){
                //If an Interrupted Exception is caught interrupt the current thread
                Thread.currentThread().interrupt();

            }
            catch (IOException e) {
                System.out.println("Error during servicing process");
            }
        };
    }


}

