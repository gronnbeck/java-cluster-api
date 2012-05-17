package tasks;

import java.util.ArrayList;

public class TspHelpers {
    public static ArrayList<Integer> toArrayList(int[] array) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < array.length; i++){
            list.add(i,array[i]);
        }
        return list;
    }

    private static ArrayList<ArrayList<Integer>> permutations(
            ArrayList<Integer> prefix, ArrayList<Integer> suffix,
            ArrayList<ArrayList<Integer>> output) {
        if(prefix == null) prefix = new ArrayList<Integer>();
        if(output == null) output = new ArrayList<ArrayList<Integer>>();
        if(suffix.size() == 1) {
            ArrayList<Integer> newElement = new ArrayList<Integer>(prefix);
            newElement.addAll(suffix);
            output.add(newElement);
            return output;
        }
        for(int i = 0; i < suffix.size(); i++) {
            ArrayList<Integer> newPrefix = new ArrayList<Integer>(prefix);
            newPrefix.add(suffix.get(i));
            ArrayList<Integer> newSuffix = new ArrayList<Integer>(suffix);
            newSuffix.remove(i);
            permutations(newPrefix,newSuffix,output);
        }
        return output;
    }

    /**
     * Calculates the distance between two points on the Euclidean plane
     * @param city1 (x,y) coordinates for point 1
     * @param city2 (x,y) coordinates for point 2
     * @return the distance (double) between the two points
     */
    public static double distance(double[] city1, double[] city2) {
        return Math.sqrt(Math.pow(city2[0] - city1[0], 2) +
                Math.pow(city2[1] - city1[1], 2));
    }

    /**
     * Gets the total travel distance between the coordinates given following the given path
     * @param coordinates for the cities on the Euclidean plane
     * @param path the TSP path we are measuring
     * @return the total distance of the TSP path
     */
    public static double totalDistance(double[][] coordinates, ArrayList<Integer> path)  {
        ArrayList<Integer> temp = (ArrayList<Integer>) path.clone();
        int start = temp.get(0);
        int prev = start;
        temp.remove(0);
        double totalDistance = 0;
        for (int city : temp) {
            totalDistance += distance(coordinates[prev], coordinates[city]);
            prev = city;
        }
        totalDistance += distance(coordinates[prev], coordinates[start]);
        return totalDistance;
    }

    /**
     * Calculate all permutations for a given list of integers
     * @param prefix a list of integers which is always the first integers of all the permutations
     * @param list a list we of integers we want to permute
     * @return an ArrayList containing all the permutations.
     */
    public static ArrayList<ArrayList<Integer>> permutations(ArrayList<Integer> prefix,
                                                      ArrayList<Integer> list) {
        return permutations(prefix, list, null);
    }
}
