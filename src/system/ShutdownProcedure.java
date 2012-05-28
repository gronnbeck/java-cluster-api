package system;

import java.rmi.RemoteException;

import api.Space;

public class ShutdownProcedure extends Thread{
	
	private Space space;
	
	public ShutdownProcedure(Space space) {
		this.space = space;
	}
	
	public void run() {
		System.out.println("Shutting down..");
		try {
			space.stop();
		} catch (RemoteException e) {}
	}
}