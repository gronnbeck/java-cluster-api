package client;

import api.*;
import tasks.DoubleShared;
import tasks.Pair;
import tasks.TspHelpers;
import tasks.TspTask;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.util.ArrayList;

public class Tsp {


    private static class TaskEventReceiver extends Thread {

        private Space space;
        private String jobid;

        TaskEventReceiver(Space space, String jobid) {
            this.space = space;
            this.jobid = jobid;
        }

        public void run () {
            double bestCost = Double.MAX_VALUE;
            while (true) {
                try {
                    TaskEvent taskEvent = space.nextEvent(jobid);
                    ArrayList<Integer> pathAsList = (ArrayList<Integer>) taskEvent.getValue();
                    pathAsList.add(0,0);


                    double currentCost = TspHelpers.totalDistance(coord, pathAsList);
                    if (currentCost > bestCost) {
                        continue;
                    }
                    bestCost = currentCost;
                    int[] path = new int[pathAsList.size()];
                    for (int i = 0; i < path.length; i++)
                        path[i] = pathAsList.get(i);

                    System.out.println("new possibly optimal path: " + currentCost );

                    JLabel euclideanTspLabel = displayEuclideanTspTaskReturnValue(coord, path);
                    container.removeAll();
                    container.add( new JScrollPane( euclideanTspLabel ), BorderLayout.EAST );
                    frame.pack();

                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

    }


    private static Double findUpperBound(double[][] cities) {
        ArrayList<Integer> notVisited = new ArrayList<Integer>();
        for (int i = 0; i < cities.length; i++) {
            notVisited.add(i);
        }
        double cost = 0;
        int currentCity = notVisited.get(0);
        notVisited.remove(0);
        while (notVisited.size() > 0) {

            double minDistance = Double.MAX_VALUE;
            Integer minCity = -1;
            for (int city : notVisited) {
                double currentDistance = TspHelpers.distance(cities[city], cities[currentCity]);
                if (currentDistance < minDistance) {
                    minDistance = currentDistance;
                    minCity = city;
                }
            }
            cost += minDistance;

            currentCity = minCity;
            notVisited.remove(minCity);
        }
        return cost + TspHelpers.distance(cities[currentCity], cities[0]);
    }

    static JFrame frame;
    static Container container;
    static double[][] coord =
            {
                    {1,1},
                    {1,2},
                    {1,3},
                    {1,4},
                    {2,1},
                    {2,2},
                    {2,3},
                    {2,4},
                    {3,1},
                    {3,2},
                    {3,3},
                    {3,4},
                    {4,1},
                    {4,2}, //14
                    {4,3},
        		{4,4},
        		{5,1},
        		{5,2},
//        		{5,3},
//        		{5,4},
//                {6,1},
//                {6,2},
//                {6,3},
//                {6,4},
            };


    public static void main(String[] args) throws Exception {
        if (args.length == 0) return;

        int port = 8888;
        String url = args[0];
        Registry registry = LocateRegistry.getRegistry(url, port);

        Space space = (Space) registry.lookup(Space.SERVICE_NAME);


        frame = new JFrame( "Result Visualizations" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        container = frame.getContentPane();
        container.setLayout( new BorderLayout() );
        frame.pack();
        frame.setVisible( true );




        

        Task tspTask = new TspTask(coord);

        Double upperBound = findUpperBound(coord);

        space.setShared(new DoubleShared(upperBound, tspTask.getJobId()));

        System.out.println("Upperbound: " + upperBound);
        long runTime = System.currentTimeMillis();


        space.publishTask(tspTask);

        TaskEventReceiver taskEventReceiver = new TaskEventReceiver(space,tspTask.getJobId());
        taskEventReceiver.start();

        Result result = space.getResult(tspTask.getJobId());

        System.out.println("Client run time: " + (System.currentTimeMillis() - runTime));
        ArrayList<Integer> pathAsList = ((Pair<Double, ArrayList<Integer>>)result.getTaskReturnValue()).getRight();
        System.out.println("Path: " + pathAsList);
        System.out.println("Cost: " + ((Pair<Double, ArrayList<Integer>>)result.getTaskReturnValue()).getLeft());

        int[] path = new int[pathAsList.size()];
        for (int i = 0; i < path.length; i++)
            path[i] = pathAsList.get(i);

        JLabel euclideanTspLabel = displayEuclideanTspTaskReturnValue(coord, path);
        container.add(new JScrollPane(euclideanTspLabel), BorderLayout.EAST);
        frame.pack();


    }

    private static JLabel displayEuclideanTspTaskReturnValue( double[][] cities, int[] tour )
    {
        int N_PIXELS = 250;

        // display the graph graphically, as it were
        // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
        double minX = cities[0][0], maxX = cities[0][0];
        double minY = cities[0][1], maxY = cities[0][1];
        for ( int i = 0; i < cities.length; i++ )
        {
            if ( cities[i][0] < minX ) minX = cities[i][0];
            if ( cities[i][0] > maxX ) maxX = cities[i][0];
            if ( cities[i][1] < minY ) minY = cities[i][1];
            if ( cities[i][1] > maxY ) maxY = cities[i][1];
        }

        // scale points to fit in unit square
        double side = Math.max( maxX - minX, maxY - minY );
        double[][] scaledCities = new double[cities.length][2];
        for ( int i = 0; i < cities.length; i++ )
        {
            scaledCities[i][0] = ( cities[i][0] - minX ) / side;
            scaledCities[i][1] = ( cities[i][1] - minY ) / side;
        }

        Image image = new BufferedImage( N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB );
        Graphics graphics = image.getGraphics();

        int margin = 10;
        int field = N_PIXELS - 2*margin;

        // draw edges
        graphics.setColor( Color.BLUE );
        int x1, y1, x2, y2;
        int city1 = tour[0], city2;
        x1 = margin + (int) ( scaledCities[city1][0]*field );
        y1 = margin + (int) ( scaledCities[city1][1]*field );
        for ( int i = 1; i < tour.length; i++ )
        {
            city2 = tour[i];
            x2 = margin + (int) ( scaledCities[city2][0]*field );
            y2 = margin + (int) ( scaledCities[city2][1]*field );
            graphics.drawLine( x1, y1, x2, y2 );
            x1 = x2;
            y1 = y2;
        }

        city2 = tour[0];
        x2 = margin + (int) ( scaledCities[city2][0]*field );
        y2 = margin + (int) ( scaledCities[city2][1]*field );
        graphics.drawLine( x1, y1, x2, y2 );



        // draw vertices
        int VERTEX_DIAMETER = 6;
        graphics.setColor( Color.RED );
        for ( int i = 0; i < cities.length; i++ )
        {
            int x = margin + (int) ( scaledCities[i][0]*field );
            int y = margin + (int) ( scaledCities[i][1]*field );
            graphics.fillOval( x - VERTEX_DIAMETER/2,
                    y - VERTEX_DIAMETER/2,
                    VERTEX_DIAMETER, VERTEX_DIAMETER);
        }
        ImageIcon imageIcon = new ImageIcon( image );
        return new JLabel( imageIcon );
    }

}
