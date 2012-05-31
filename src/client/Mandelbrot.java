package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import api.Result;
import tasks.*;
import api.Space;

public class Mandelbrot {
	
	private static int [][] count;
	private static int iteration_limit = 4096;
	private static int N_PIXELS = 1024;
	
	  private static JLabel displayMandelbrotSetTaskReturnValue( int[][] counts )
	    {
	        Image image = new BufferedImage( N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB );
	        Graphics graphics = image.getGraphics();
	        for ( int i = 0; i < counts.length; i++ )
	            for ( int j = 0; j < counts.length; j++ )
	            {
	                graphics.setColor( getColor( counts[i][j],j ) );
	                graphics.fillRect(j, counts.length - 1 - i, 1, 1);
	            }
	        ImageIcon imageIcon = new ImageIcon( image );
	        return new JLabel( imageIcon );
	    }
	
	    private static Color getColor( int i,int y){
	        if ( i == iteration_limit )
	            return Color.BLACK;
	      return new Color(20+i);
	    
	    }

	
	
	 public static void main(String[] args) throws Exception {
		 
		 if (args.length == 0) return;
		 int port = 8888;
		 String url = args[0];
		 Registry registry = LocateRegistry.getRegistry(url, port);

         Space space = (Space) registry.lookup(Space.SERVICE_NAME);
		 
		 
		 System.out.println("Starting mandelbrot!");
		 double lowerX = -0.7510975859375;
		 double lowerY = 0.1315680625;
		 double edge  = 0.01611;
		 int n = 1024;
		 String id = "0";
		 //Start time - CLIENT
		 
		 MandelTask MandelTask = new MandelTask(id,lowerX,lowerY,edge,n,iteration_limit);
		 long jobExecTime = System.nanoTime();
         space.publishTask(MandelTask);
         Result res = space.getResult(MandelTask.getJobId());
         jobExecTime = System.nanoTime() - jobExecTime;
		 count = (int[][])res.getTaskReturnValue();
		 //End time - CLIENT
		 
		 System.out.println("Mandelbrot finished!");
		 System.out.println("Runtime: " + res.getTaskRunTime()/1000000+"ms");
		 System.out.println("Client time: " + (jobExecTime)/1000000 +"ms");
		
		 
		 JLabel mandelbrotLabel = displayMandelbrotSetTaskReturnValue(count);
         JFrame frame = new JFrame( "Result Visualizations" );
         frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
         Container container = frame.getContentPane();
         container.add( new JScrollPane( mandelbrotLabel ), BorderLayout.WEST );
         frame.pack();
         frame.setVisible( true );
         
		 
	 }

}
