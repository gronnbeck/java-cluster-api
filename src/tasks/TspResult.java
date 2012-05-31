package tasks;

import java.util.ArrayList;
import system.ResultImpl;

public class TspResult extends ResultImpl {

    private double cost;
    private ArrayList<Integer> path;

    public TspResult(ArrayList<Integer> path, double cost) {
        this.path = path;
        this.cost = cost;
    }

    /**
     *
     * @return A pair of the cost (double) and a path represented as an array list
     */
    public Pair<Double, ArrayList<Integer>> getTaskReturnValue() {
        return new Pair<Double, ArrayList<Integer>>(cost, path);
    }

}
