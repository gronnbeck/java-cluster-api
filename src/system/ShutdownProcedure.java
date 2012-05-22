package system;

import java.rmi.RemoteException;

import api.Computer;
import api.Space;

/**
 * Thread that takes care of a graceful shutdown.
 * Invoked when the program is killed.
 */
public class ShutdownProcedure extends Thread{
	
	private Object o;
	
	public ShutdownProcedure(Object o) {
		this.o = o;
	}
	
	@Override
	public void run() {
		
		System.out.println("Shutdown procedure invoked..");
		
		try {
			if(o instanceof Space) {
				((Space)o).stop();
			}
			else if (o instanceof Computer) {
				((Computer)o).stop();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			System.err.println("Failed to shut down gracefully");
		}
		System.exit(0);
	}
}