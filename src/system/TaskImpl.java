package system;

import api.*;

import java.rmi.RemoteException;

public abstract class TaskImpl implements Task {
	private Computer computer;
	private boolean isCached;

	abstract public Result execute();
 	public  Object getShared() throws RemoteException { return computer.getShared(); }
	protected  void setShared( Shared shared ) throws RemoteException { computer.setShared( shared ); }
	public  void  setComputer( Computer computer ) { this.computer = computer; }
	public  void setCached(boolean bol){this.isCached = bol;}
	public boolean getCached(){return this.isCached;}

}
