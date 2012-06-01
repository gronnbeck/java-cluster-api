package tasks;

import api.*;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

import system.PruneResult;
import system.TaskImpl;

public class TspTask extends TaskImpl implements Serializable  {


    // TODO: make thos configureable... Buggy...
    private static int levels = 2;
    private int tspBaseCase;
    private double[][] coordinates;
    private ArrayList<Integer> cities;
    private ArrayList<Integer> prefix;
    private ArrayList<Integer> path;
    private double cost;
    private int currentCity;
    private Lowerbound<Double> lowerbound;
    private boolean simple;


    private static ArrayList<Integer> initPath(int i) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(i);
        return path;
    }

    private static ArrayList<Integer> initCities(double[][] coordinates) {
        ArrayList<Integer> cities = new ArrayList<Integer>();
        for (int i = 1; i < coordinates.length; i++){
            cities.add(i);
        }
        return cities;
    }

	/**
     * Instantiates a Task for solving a Euclidean TSP problem
     * @param coordinates A two dimensional double array which contains the coordinates
     *                    (in the euclidean space) of the cities. This set of coordinates
     *                    are used to calculate the shortest possible path for the TSP problem.
     */
    public TspTask(double[][] coordinates) {
        this(coordinates, initCities(coordinates), 0, 0, initPath(0));
        this.tspBaseCase = coordinates.length - levels;
        lowerbound = new TspLowerBound(coordinates, initPath(0)); // For now a pretty stupid lowerbound method
    }



    protected TspTask(double[][] coordinates, ArrayList<Integer> citiesNotVisited, int currentCity, double cost,
                      ArrayList<Integer> path) {
        super();
        this.tspBaseCase = coordinates.length - levels;
        this.coordinates = coordinates;
        cities = citiesNotVisited;
        this.path = path;
        this.cost = cost;
        this.currentCity = currentCity;
        this.path.add(currentCity);
        lowerbound = new TspLowerBound(coordinates, this.path); // For now a pretty stupid lowerbound method
        simple = false;
    }


    private double pathCost(ArrayList<Integer> path){
        double cost = 0;
        ArrayList<Integer> pathClone = (ArrayList<Integer>) path.clone();
        int prevCity = pathClone.get(0);
        pathClone.remove(0);
        for (int city : pathClone) {
            cost += TspHelpers.distance(coordinates[city], coordinates[prevCity]);
            prevCity = city;
        }
        return this.cost + cost + TspHelpers.distance(coordinates[prevCity], coordinates[0]);
    }


    private void registerResult(Task2Space task2Space, Result result) {
        try {
            task2Space.putResult(result);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Double getSharedValue() {
        DoubleShared shared = null;
        try {
            shared = (DoubleShared) getShared();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return shared.getValue();
    }


    /**
     * Finds a solution for the tsp problem
     * @return The minimum path for this particular problem
     *         in terms of distance, in the Euclidean space.
     *         The path is represented as an integer array.
     */
    public Result execute() {
        if (lowerbound.getLowerBound() > getSharedValue()) {
            Result pr = createResult(new PruneResult());
            return pr;
        }

         if (cities.size() < tspBaseCase) {
             ArrayList<Integer> prefix = new ArrayList<Integer>();
             prefix.add(currentCity);
             ArrayList<Integer> minPath = null;
             double minCost = Double.MAX_VALUE;
             for (ArrayList<Integer> path : possiblePaths(prefix, cities)) {
                 if (minPath == null) {
                     minPath = path;
                     minCost = pathCost(path);
                     continue;
                 }
                 if (pathCost(path) < minCost) {
                     minPath = path;
                     minCost = pathCost(path);
                 }
             }

             return createResult(new TspResult(minPath, minCost));
         }

        ArrayList<Task> subtasks = new ArrayList<Task>();
        for (Integer city : cities) {
            double newCost = TspHelpers.distance(coordinates[city], coordinates[currentCity]) + cost;
            ArrayList<Integer> citiesNotVisited = (ArrayList<Integer>) cities.clone();
            citiesNotVisited.remove(city);
            Task task = new TspTask(coordinates, citiesNotVisited, city, newCost, (ArrayList) path.clone());
            subtasks.add(task);
        }

        // TODO return a special case Result instead where the task will continue
        return createContinuationResult(new TspContin(currentCity, subtasks));

    }

    private ArrayList<ArrayList<Integer>> possiblePaths(ArrayList<Integer> prefix, ArrayList<Integer> suffix) {
        return _possiblePaths(prefix,suffix,null);
    }

    private double currentlyBestCost = Double.MAX_VALUE;
    private ArrayList<ArrayList<Integer>> _possiblePaths(ArrayList<Integer> prefix,
                                                         ArrayList<Integer> suffix,
                                                         ArrayList<ArrayList<Integer>> output) {
        if(prefix == null) prefix = new ArrayList<Integer>();
        if(output == null) output = new ArrayList<ArrayList<Integer>>();
        if(suffix.size() == 1) {
            ArrayList<Integer> newElement = new ArrayList<Integer>(prefix);
            newElement.addAll(suffix);
            double cost = TspHelpers.totalDistance(coordinates, newElement);
            try {
                if (cost <= getSharedValue() && cost < currentlyBestCost) {

                    if (output.size() > 0)
                        output.remove(0);

                    output.add(newElement);
                    currentlyBestCost = cost;

                    try {
                        ArrayList<Integer> subresult = new ArrayList<Integer>(newElement);
                        for (int i = 0; i < path.size(); i++)
                            subresult.add(i, path.get(i));

                        propagateTaskEvent(createTaskEvent("new tsp result", subresult));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    setShared(new DoubleShared(cost, getJobId()));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return output;
        }

        for(int i = 0; i < suffix.size(); i++) {
            ArrayList<Integer> newPrefix = new ArrayList<Integer>(prefix); newPrefix.add(suffix.get(i));
            ArrayList<Integer> newSuffix = new ArrayList<Integer>(suffix); newSuffix.remove(i);

            double cost = getLowerboundCost(coordinates, prefix);

            if (cost > getSharedValue() || cost >= currentlyBestCost) {
                continue;
            }
            _possiblePaths(newPrefix, newSuffix, output);
        }
        return output;
    }

    private double getLowerboundCost(double[][] coordinates, ArrayList<Integer> prefix) {
        return new TspLowerBound(coordinates, prefix).getLowerBound();
    }



	@Override
	public boolean isSimple() {
		return this.simple;
	}


}