package tasks;

import java.util.ArrayList;

import api.Lowerbound;

public class TspLowerBound implements Lowerbound<Double> {
    private double[][] coordinates;
    private ArrayList<Integer> path;

	
	public TspLowerBound(double[][] coordinates, ArrayList<Integer> path){
        this.coordinates = coordinates;
        this.path = (ArrayList<Integer>) path.clone();
    }
	
    public static double totalDistance(double[][] coordinates, ArrayList<Integer> path)  {
        ArrayList<Integer> temp = (ArrayList<Integer>) path.clone();
        int start = temp.get(0);
        int prev = start;
        temp.remove(0);
        double totalDistance = 0;
        for (int city : temp) {
            totalDistance += TspHelpers.distance(coordinates[prev], coordinates[city]);
            prev = city;
        }
        return totalDistance;
    }

	@Override
	public Double getLowerBound() {
		//Comute a minal spanning tree.
		double[][] distance= new double[coordinates.length][coordinates.length];
		ArrayList<Integer> notVisited = new ArrayList<Integer>();
		for (int i = 0; i < distance.length; i++) {
			notVisited.add(i);
			
		}
		
		Integer currentCity = -1;
		double cost = totalDistance(coordinates, path);
		
		for (int i = 0; i < distance.length; i++) {
			for (int j = 0; j < distance.length; j++) {
				distance[i][j] = TspHelpers.distance(coordinates[i],coordinates[j]);
			}
		}
		
		
		currentCity = path.get(path.size() - 1);
		notVisited.remove(currentCity);
		path.remove(currentCity);
		for (Integer city : path) {
			for (int i = 0; i < distance.length; i++) {
				distance[city][i] = Double.MAX_VALUE;
				distance[i][city] = Double.MAX_VALUE;
			}
			notVisited.remove(city);
		}
		
		while(notVisited.size() > 0){
			double lowest = Double.MAX_VALUE;
			Integer index =-1;
			for (Integer city : notVisited) {
				if (distance[currentCity][city] < lowest) {
					lowest = distance[currentCity][city];
					index = city;
				}
			}
			currentCity = index;
			
			notVisited.remove(index);
			cost += lowest;
		}
		return cost;
		
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
                };
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(0);
        path.add(1);
        path.add(4);
        path.add(6);
        path.add(19);
        path.add(2);
        path.add(18);
        long time = System.currentTimeMillis();
        TspLowerBound t = new TspLowerBound(coord, path);
        System.out.println(t.getLowerBound());
        System.out.println(System.currentTimeMillis() - time);


    }

}
