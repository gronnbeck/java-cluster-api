package system;

import java.rmi.RemoteException;

import api.Computer;
import api.Result;
import api.Space;
import api.Task;

public class SpaceComputer  extends ComputerProxy implements Runnable{
	
	public SpaceComputer(Computer comp, Space space) throws RemoteException {
		super (comp,space);
		//Thread t = new Thread(this);
		//t.start();
		
	}
	
	@Override
	public void run(){
		do{
			try {
                Task task = space.takeSimpleTask();
                try {
                	Result r = execute(task);
                    space.putResult(r);
                } catch (RemoteException e) {
                    System.out.println("WTF!?");
					return;          // exit thread
                }
            } catch (InterruptedException e) {
                e.printStackTrace();            // don't know how we shall handle this one, yet...
            } catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } while(true);
	}
	
}
