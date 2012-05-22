package system;

import api.*;

import java.rmi.RemoteException;
import java.util.UUID;

public abstract class TaskImpl implements Task {

	private static final long serialVersionUID = -2084915820807754528L;
	private String id;
	private Computer computer;
	private boolean isCached;
	
	private Integer priority;

    public TaskImpl() {
        UUID uuid = UUID.randomUUID();
        this.id = this.toString() +  uuid.toString();
    }

    @Override
    public String getTaskIdentifier() { return id; }
    public void setTaskIdentifier(String id) { this.id = id;}

	abstract public Result<?> execute();
 	public  Object getShared() throws RemoteException { return computer.getShared(); }
	protected  void setShared( Shared<?> shared ) throws RemoteException { computer.setShared( shared ); }
	public  void  setComputer( Computer computer ) { this.computer = computer; }
	public  void setCached(boolean bol){this.isCached = bol;}
	public boolean getCached(){return this.isCached;}
	
	public int getPriority() {
		if (this.priority == null) {
			return 0;
		}
		return this.priority; 
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}

}
