package system;

import api.*;

import java.rmi.RemoteException;
import java.util.UUID;

public abstract class TaskImpl implements Task {

    private String taskId;
    private String jobId;
	private Computer computer;
	private boolean isCached;

    public TaskImpl() {
        UUID uuid = UUID.randomUUID();
        this.jobId = uuid.toString();
        this.taskId = this.toString() +  uuid.toString();
    }

    @Override
    public void setJobId(String jobId) { this.jobId = jobId; }

    @Override
    public String getJobId() { return jobId; }

    @Override
    public String getTaskIdentifier() { return taskId; }

    @Override
    public void setTaskIdentifier(String id) { this.taskId = id;}

	abstract public Result<?> execute();
 	public  Shared getShared() throws RemoteException { return computer.getShared(jobId); }
	protected  void setShared( Shared<?> shared ) throws RemoteException { computer.setShared( shared ); }
	public  void  setComputer( Computer computer ) { this.computer = computer; }
	public  void setCached(boolean bol){this.isCached = bol;}
	public boolean getCached(){return this.isCached;}

    public ContinuationResult createContinuationResult(ContinuationTask continuationTask) {
        continuationTask.setTaskIdentifier(getTaskIdentifier());
        for (Task task : continuationTask.getTasks()) {
            task.setJobId(this.jobId);
        }
        return new ContinuationResult(continuationTask);
    }

}
