package tasks;

import api.*;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

import system.ContinuationResult;
import system.PruneResult;
import system.TaskImpl;

public class TspTask extends TaskImpl implements Serializable  {

    private static int levels = 2;
    private int tspBaseCase;
    private double[][] coordinates;
    private ArrayList<Integer> cities;
    private ArrayList<Integer> prefix;
    private String id;
    private ArrayList<Integer> path;
    private double cost;
    private int currentCity;
    private Lowerbound<Double> lowerbound;
    private boolean simple;


	/**
     * Instantiates a Task for solving a Euclidean TSP problem
     * @param coordinates A two dimensional double array which contains the coordinates
     *                    (in the euclidean space) of the cities. This set of coordinates
     *                    are used to calculate the shortest possible path for the TSP problem.
     */
    public TspTask(double[][] coordinates) {
        this.tspBaseCase = coordinates.length - levels;
        this.coordinates = coordinates;
        cities = new ArrayList<Integer>();
        for (int i = 1; i < coordinates.length; i++){
            cities.add(i);
        }
        id = this.toString() + UUID.randomUUID().toString();
        this.cost = 0;
        this.currentCity = 0;
        path = new ArrayList<Integer>();
        path.add(0);
        lowerbound = new TspLowerBound(coordinates, path); // For now a pretty stupid lowerbound method
        simple = false;
    }



    protected TspTask(double[][] coordinates, ArrayList<Integer> citiesNotVisited, int currentCity, double cost,
                      ArrayList<Integer> path) {
        this.tspBaseCase = coordinates.length - levels;
        this.coordinates = coordinates;
        cities = citiesNotVisited;
        this.path = path;
        id = this.toString() + UUID.randomUUID().toString();
        this.cost = cost;
        this.currentCity = currentCity;
        this.path.add(currentCity);
        lowerbound = new TspLowerBound(coordinates, this.path); // For now a pretty stupid lowerbound method
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
      //  System.out.println(path.size());
      //  System.out.println(lowerbound.getLowerBound() + " > " + getSharedValue());
        if (lowerbound.getLowerBound() > getSharedValue()) {
            return new PruneResult(id);
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

             return new TspResult(minPath, minCost, id);
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
        return new ContinuationResult(new TspContin(currentCity, subtasks, id));

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
                    setShared(new DoubleShared(cost));
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
    public String getTaskIdentifier() {
        return id;
    }



	@Override
	public boolean isSimple() {
		return this.simple;
	}


}