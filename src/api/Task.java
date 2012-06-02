package api;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Task<T> extends Serializable, TaskEventHandler {
    /**
     * Executes the given task
     * @return The tasks result
     */
    Result<?> execute();

    /**
     * Returns the tasks identifier
     * @return an ID as String
     */
    String getTaskIdentifier();
    
    /**
     * 
     * @return true if the task has been cached
     */
    
    boolean getCached();
    
    /**
     * Sets the tasks isCached attribute.
     * This value is set to true if the task has been cached at a computer
     * 
     */
    void setCached(boolean b);

    /**
     * Sets the computer associated with this task.
     * 
     * @param computer
     */
    void setComputer(Computer computer);
    
    /**
     * If the task is considered very simple, the task can be executed on space. 
     * A task is considered very simple if the time it takes to solve it is less than or equal to the time it takes to marshal/unmarshal + latency.
     * @return true if the task is marked as simple
     */
    
    boolean isSimple();

    /**
     * Sets the task id
     * @param identifier The id represented as a String. The id must be unique.
     */
    void setTaskIdentifier(String identifier);
    
    

    /**
     * If a client wants to use branch & bound on the system. They need add a jobId reference to the shared object.
     * The id should be unique, if else, the system may return the wrong shared object.
     * @return a unique job id as a String
     */
    String getJobId();

    /**
     * advanced:
     * Sets the job id for this task. This method is used by the framework.
     * The method is available for developers who needs to modify the jobId for a task. However, it is not recommended to edit it.
     * @param jobId The job id of a task.
     */
    void setJobId(String jobId);


    /**
     * The Owner of a task is the Space where a task is waiting for that task to be executed.
     * This method should only be called locally.
     * @param ownerId the space where a task is waiting for this task to be executed
     */
    void setOwnerId(String ownerId);

    /**
     * This method returns the owner (a space) of the task, should only be called locally
     * @return If it has an owner a Space is returned, else it will return null
     */
    String getOwnerId();

    /**
     * Returns the shared object for this type of task.
     * @return
     */
    Shared getShared() throws RemoteException;

    /**
     * Sets the shared object of this task.
     * This value is used to prune the search tree.
     * @param shared
     */
    void setShared(Shared<?> shared) throws RemoteException;

    /**
     * Every ContinuationTask (ct) must be returned as a ContinuationResult.
     * When a ct is passed through this method all its dependencies such as job/task/owner id
     * is handeled automatically
     * @param continuationTask The task which should be past as a new tak
     * @return A ContinuationResult containing the corresponding continuation task.
     */
    Result createContinuationResult(ContinuationTask continuationTask);

    /**
     * Every result from a task should be passed through this filter. This filter adds the needed
     * job/task/owner ids.
     * @param result A result that a task wants to return
     * @return a result wrapped with the right job/task/owner ids
     */
    Result createResult(Result result);

    /**
     * Set the priorty of a task. A task with higher priority will be executed first
     * @param priority the priority level of a task, the higher the better
     */
    void setPriority(int priority);

    /**
     * Returns the priority of a Task
     * @return the priority of a task as an int
     */
    int getPriority();


}
