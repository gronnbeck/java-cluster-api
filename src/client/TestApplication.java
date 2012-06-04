package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JFrame;

import test.TestTask;

import api.Space;

public class TestApplication {

    static JFrame frame;
    static Container container;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		 if (args.length == 0) return;

	        int port = 8888;
	        String url = args[0];
	        Registry registry = LocateRegistry.getRegistry(url, port);

	        Space space = (Space) registry.lookup(Space.SERVICE_NAME);

//	        frame = new JFrame("Testapplication");
//	        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//	        container = frame.getContentPane();
//	        container.setLayout( new BorderLayout() );
//	        frame.pack();
//	        frame.setVisible( true );
//	        
	        TestTask task = new TestTask(1, 10);
	        System.out.println("Publishing task to space..");
	        space.publishTask(task);
	        
	        System.out.println("Waiting for result..");
	        space.getResult(task.getJobId());
	        
	        System.out.println("Result returned..\nTest application done.. ");

	}

}
