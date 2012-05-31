package system;

import java.rmi.RemoteException;

import api.Space;
import api.SpaceCoordinator;

public class ClientInfoThread implements Runnable{
	
	
	Space spaceProvider;
	
	public ClientInfoThread(Space spaceProvider) {
		this.spaceProvider = spaceProvider;
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public String getInfo() {
		
		String info = null;
		try {
			info = spaceProvider.getInfo().toString();
		} catch (RemoteException e) {
			System.err.println("RemoteException: Could not get info");
		}
		return info;
	}
	
	public void run() {
		while (true) {
			System.out.print("\r");
			System.out.print(getInfo());
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
