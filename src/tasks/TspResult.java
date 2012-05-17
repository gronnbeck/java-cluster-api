package tasks;

import api.Result;

import java.util.ArrayList;

public class TspResult implements Result {

    private long runTime;
    private double cost;
    private String id;
    private ArrayList<Integer> path;

    public TspResult(ArrayList<Integer> path, double cost, String id) {
        this.path = path;
        this.cost = cost;
        this.id = id;
    }

    /**
     *
     * @return A pair of the cost (double) and a path represented as an array list
     */
    public Pair<Double, ArrayList<Integer>> getTaskReturnValue() {
        return new Pair<Double, ArrayList<Integer>>(cost, path);
    }

    /**
     * @param elapsed_time set how much time that elapsed for a task to complete the TSP task (in ns)
     */
    public void setTaskRunTime(long elapsed_time) {
        runTime = elapsed_time;
    }

    @Override
    public long getTaskRunTime() {
        return runTime;
    }

    @Override
    public String getTaskIdentifier() {
        return id;
    }
}
