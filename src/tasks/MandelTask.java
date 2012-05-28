package tasks;

import java.io.Serializable;
import java.util.ArrayList;

import system.TaskImpl;

import api.Result;
import api.Task;

public class MandelTask extends TaskImpl implements Serializable{
	
	private double lowerX;
	private double lowerY;
	private double edge;
	private int n;
	private int iteration_limit;
	private String id;
	private boolean simple;

    /**
     * Computes the fractal "Mandelbrot set" for the given parameters.
     *
     * @param lowerX X Coordinate for the lower left corner
     * @param lowerY Y Coordinate for the lower left corner
     * @param edge How long the edges in our square is
     * @param n The number of pixels to divide the edge into
     * @param iteration_limit Maximum number of iterations before we conclude that
     *                        it is most likely in the mandelbrot set
     *
     */
	public MandelTask(String id,double lowerX, double lowerY, double edge, int n,int iteration_limit) {
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.edge = edge;
		this.n = n;
		this.id = id;
		this.iteration_limit = iteration_limit;
		simple = false;
	}

    /**
     * Executes the Mandelbrot task and puts the result into a Space.
     * Description of the execution: int [][]  An array with one element for each subsquare/pixel, the number of iterations for this point.
     *         This can be used to visualize the mandelbrot set. Assign a color to a given pixel based on how many
     *         iterations this pixel had.
     */
	public Result execute() {
        long timeBefore = System.nanoTime();
		if (n==256) {
		    int  [][]count = new int[n][n];
		    double x,y,xtemp;
		    double sx=lowerX,sy=lowerY;
		    double step = (double)edge/n;
		    for (int i = 0; i <n; i++) {
		        for (int j = 0; j < n; j++) {
		            x=0;
		            y=0;
		            sx+=step;
		            int iteration =0;
		            while ( x*x + y*y < 4  &&  iteration < iteration_limit){
		                xtemp = x*x - y*y + sx;
		                y = 2*x*y + sy;
		                x = xtemp;
		                iteration++;
		            }
		            count[i][j]=iteration;
		        }
		        sx=lowerX;
		        sy+=step;
		    }
		return new MandelResult(count, id);
		
		}//END-IF
		//Split into 4 new subtasks!
		int newN = n/2;
		double newEdge = edge/2;
		
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		String id1 = id.concat(".0");
		Task sub1 = new MandelTask(id1, lowerX, lowerY, newEdge,newN, iteration_limit);
		String id2 = id.concat(".1");
		Task sub2 = new MandelTask(id2, lowerX+newEdge, lowerY, newEdge, newN, iteration_limit);
		String id3 = id.concat(".2");
		Task sub3 = new MandelTask(id3, lowerX, lowerY+newEdge, newEdge, newN, iteration_limit);
		String id4 = id.concat(".3");
		Task sub4 = new MandelTask(id4, lowerX+newEdge, lowerY+newEdge, newEdge, newN, iteration_limit);
		tasks.add(sub1);tasks.add(sub2);tasks.add(sub3);tasks.add(sub4);

		
		return createContinuationResult(new MandelContin(tasks));
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
