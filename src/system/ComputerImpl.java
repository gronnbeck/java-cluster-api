package system;


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;

import api.*;

public class ComputerImpl extends UnicastRemoteObject implements Computer {

    private Space space;
    private Shared shared;
	public ComputerImpl(Space space) throws RemoteException {
		super();
        this.space = space;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Result execute(Task task) throws RemoteException {
        ((TaskImpl)task).setComputer(this);
        return task.execute();
	}

	@Override
	public void stop() throws RemoteException {
		System.exit(0);		
	}

	public static void main(String[] args) {
		try {

            if (args.length == 0) {
                System.out.println("Argument missing");
                System.exit(0);
            }
            String url = args[0];
            int port = 8888;
            if (args.length == 2) {
                port = Integer.parseInt(args[1]);
            }


			String urlString = "rmi://"+url+":"+port+"/"+Space.SERVICE_NAME;
			System.out.println("Connecting to " + url + ":" + port + ". ");
			//Registry registry = LocateRegistry.getRegistry(url,port);
			Space space = (Space) Naming.lookup(urlString);  //Dette er vel ikke riktig!?
            ComputerImpl computer = new ComputerImpl(space);
            space.register(computer);
			System.out.println("Computer successfully registered!");
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}



	}

	@Override
	public synchronized Object getShared() throws RemoteException {
		return shared;
	}

    private synchronized boolean checkAndSetSharedThreadSafe(Shared shared) throws RemoteException {
        if (shared.isNewerThan(this.shared)) {
            this.shared = shared;
            return true;
        }
        return false;
    }

	@Override
	public  void setShared(Shared proposedShared) throws RemoteException {
		if (checkAndSetSharedThreadSafe(proposedShared))	{
		    space.setShared( shared );
		}
		
	}

    @Override
    public Space getSpace() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSpace(Space space) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
