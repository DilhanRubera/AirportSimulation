package edu.curtin.saed.assignment1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * This is demonstration code intended for you to modify. Currently, it sets up a rudimentary
 * JavaFX GUI with the basic elements required for the assignment.
 *
 * (There is an equivalent Swing version of this, which you can use if you have trouble getting
 * JavaFX as a whole to work.)
 *
 * You will need to use the GridArea object, and create various GridAreaIcon objects, to represent
 * the on-screen map.
 *
 * Use the startBtn, endBtn, statusText and textArea objects for the other input/output required by
 * the assignment specification.
 *
 * Break this up into multiple methods and/or classes if it seems appropriate. Promote some of the
 * local variables to fields if needed.
 */
public class App extends Application
{
    private int nA =10; //Number of Airports
    private int nP=10; //Number of planes of each airport

    private Map<Integer, GridAreaIcon> planeIcons = new HashMap<>(); //A map of all the planes and their icons
    private Map<Integer, Airport> airports = new HashMap<>(); //A map of all the airports

    private GridArea area = new GridArea(15, 15); //Grid area

    private static TextArea textArea = new TextArea(); //Text area
    private List<Thread> producerThreads = new ArrayList<>(); //List to store the Flight Request Producer threads
    private List<Thread> consumerThreads = new ArrayList<>(); //List to store the Flight Request Consumer threads

    public static int flightNumber=0;  //Variable to store number of completed flights
    public static int planesInFlight=0; //Variable to store number of planes in flight
    public static int planesServicing=0; //Variable to store number of planes being serviced

    //Label to display planes in flight
    private static Label planesInFlightStatusText =new Label("Planes in flight: "+getPlanesInFlight()+" ");

    //Label to display planes being serviced
    private static Label planesServicingStatusText =new Label("Planes undergoing service: "+getPlanesServicing()+" ");

    //Label to display total number of completed flights
    private static Label totalFlightsStatusText = new Label("Total Completed Flights: " + getFlightNumber()+" ");


    public static void main(String[] args)
    {
        launch();
    }

    @Override
    public void start(Stage stage)
    {

        // Set up the main "top-down" display area. This is an example only, and you should
        // change this to set it up as you require.


        // area.setGridLines(false); // If desired
        area.setStyle("-fx-background-color: #006000;");

        //Initializing the airports
        int planeId=1;
        for(int i=0;i<nA;i++){
            double x = Math.random()*9;
            double y =Math.random()*9;
            Airport airport = new Airport(i,x,y,nP);
            airports.put(i, airport);
            area.getIcons().add(new GridAreaIcon(x, y, 0.0, 1.0,
                    App.class.getClassLoader().getResourceAsStream("airport.png"),
                    "Airport " + i));

            //Initializing the planes of each airport
            for(int j=0;j<nP;j++) {
                Plane plane = new Plane(planeId, x, y);
                GridAreaIcon planeIcon = new GridAreaIcon(plane.getX(), plane.getY(), 0.0, 1.0,
                        App.class.getClassLoader().getResourceAsStream("plane.png"),
                        "Plane " + plane.getPlaneId());
                area.getIcons().add(planeIcon);
                planeIcons.put(planeId, planeIcon);
                airport.getPlaneQueue().add(plane.getPlaneId());
                planeId++;
            }

            }


        // Set up other key parts of the user interface. You'll also want to adjust this.

        var startBtn = new Button("Start");
        var endBtn = new Button("End");

        startBtn.setOnAction((event) ->
        {
            System.out.println("Start button pressed");
            //Starting the application
            start();
        });
        endBtn.setOnAction((event) ->
        {
            System.out.println("End button pressed");
            //Ending the application
            stopThreads();
        });
        stage.setOnCloseRequest((event) ->
        {
            System.out.println("Close button pressed");
        });


      //  var textArea = new TextArea();
        textArea.appendText("Sidebar\n");
        textArea.appendText("Text\n");


        // Below is basically just the GUI "plumbing" (connecting things together).

        var toolbar = new ToolBar();
        toolbar.getItems().addAll(startBtn, endBtn, new Separator(),planesInFlightStatusText,planesServicingStatusText, totalFlightsStatusText);

        var splitPane = new SplitPane();
        splitPane.getItems().addAll(area, textArea);
        splitPane.setDividerPositions(0.75);

        stage.setTitle("Air Traffic Simulator");
        var contentPane = new BorderPane();
        contentPane.setTop(toolbar);
        contentPane.setCenter(splitPane);

        var scene = new Scene(contentPane, 1200, 1000);
        stage.setScene(scene);
        stage.show();

    }

    //Method to initialize and start all threads
    public void start(){

        for (int i=0;i<nA;i++){

            Airport airport = airports.get(i);

            //Initializing Flight Request Producers
            FlightRequestProducer flightRequestProducer = new FlightRequestProducer(airport,nA);

            //Initializing Flight Request Consumers
            FlightRequestConsumer flightRequestConsumer= new FlightRequestConsumer(airport,flightRequestProducer,area,planeIcons,airports);

            String threadName= "Airport Thread no: "+(i+1);

            //Creating a thread for a Flight Request Producer
            Thread flightRequestProducingThread = new Thread(flightRequestProducer,threadName);
            producerThreads.add(flightRequestProducingThread); //Adding thread to thread list

            //Starting the flight request producer thread
            flightRequestProducingThread.start();

            //Creating a thread for a Flight Request Consumer
            Thread flightRequestConsumingThread =  new Thread(flightRequestConsumer);
            consumerThreads.add(flightRequestConsumingThread); //Adding thread to thread list
            flightRequestConsumingThread.start(); //Starting the flight request consumer thread

        }
    }

    //Method to stop all threads by interrupting them
    public void stopThreads(){

        //Interrupting flight request producer thread
        for (Thread producerThread : producerThreads) {
            producerThread.interrupt();
        }

        //Interrupting flight request consumer thread
        for (Thread consumerThread : consumerThreads) {
            consumerThread.interrupt();
        }
    }


    //Method to display text
    public static void displayText(String text){
        textArea.appendText(text+"\n");
    }

    //Method to update labels
    public static void updateLabels() {
        planesInFlightStatusText.setText("Planes in flight: " + getPlanesInFlight());
        planesServicingStatusText.setText("Planes undergoing service: " + getPlanesServicing());
        totalFlightsStatusText.setText("Total Completed Flights: " + getFlightNumber());
    }

    //Getters and setters
    public static int getFlightNumber() {
        return flightNumber;
    }

    public static void setFlightNumber(int flightNumber) {
        App.flightNumber = flightNumber;
    }

    public static int getPlanesInFlight() {
        return planesInFlight;
    }

    public static void setPlanesInFlight(int planesInFlight) {
        App.planesInFlight = planesInFlight;
    }

    public static int getPlanesServicing() {
        return planesServicing;
    }

    public static void setPlanesServicing(int planesServicing) {
        App.planesServicing = planesServicing;
    }


    }





