package tasks;

import api.Lowerbound;

import java.util.ArrayList;

public class TspNNLowerBound implements Lowerbound {

    private double [][] coordinates;
    private ArrayList<Integer> path;

    public TspNNLowerBound(double[][] coordinates, ArrayList<Integer> path) {
        this.coordinates = coordinates;
        this.path = path;
    }


    @Override
    public Double getLowerBound() {
        double cost = 0;
        ArrayList<Integer> clone = (ArrayList<Integer>) path.clone();
        Integer pre = clone.remove(0);


        double min = 0;
        for (double[] coordinate : coordinates) {
            double c = TspHelpers.distance(coordinates[pre], coordinate);
            if (c < min) {
                min = c;
            }
        }
        cost += min;

        for (Integer cur : clone) {
            cost += TspHelpers.distance(coordinates[pre], coordinates[cur]);
            pre = cur;
        }

        min = 0;
        for (double[] coordinate : coordinates) {
            double c = TspHelpers.distance(coordinates[pre], coordinate);
            if (c < min) {
                min = c;
            }
        }
        cost += min;


        double nCost = 0;
        for (Integer i = 0; i < coordinates.length; i++) {
            if (path.contains(i))  continue;

            double m1 = Double.MAX_VALUE;
            double m2 = Double.MAX_VALUE;
            for (int j = 0; j < coordinates.length; j++) {
                if (i == j) continue;
                double c = TspHelpers.distance(coordinates[i], coordinates[j]);
                if (c < m1) {
                    m2 = m1;
                    m1 = c;
                }
                else if (c < m2) {
                    m2 = c;
                }
            }
            nCost += m1 + m2;
        }

        return cost + nCost/2;
    }


    public static void main(String[] args) {
        double[][] coord =
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
                        {5,3},
                        {5,4},
//                {6,1},
//                {6,2},
//                {6,3},
//                {6,4},
                };

        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(0);
        path.add(18);


        TspNNLowerBound tlb = new TspNNLowerBound(coord, path);
        System.out.println(tlb.getLowerBound());
    }
}
