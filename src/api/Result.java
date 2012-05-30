package api;

import java.io.Serializable;

public interface Result<T> extends Serializable {
    /**
     * Fetch the result value of an executed Task
     * @return The result returned by executing a task
     */
    T getTaskReturnValue();

    /**
     * Log the run time of an executed task
     * @param elapsed_time how much time that elapsed for a task to complete. In ns
     */
    void setTaskRunTime(long elapsed_time);

    /**
     * Returns the logged run time
     * @return The run time in ns as a long
     */
    long getTaskRunTime();

    /**
     * Returns the id of the task this result belongs to
     * @return an integer representing the task this result belongs to
     */
    String getTaskIdentifier();

    /**
     * The Owner of a result is the Space where a task is waiting for this result to be returned
     * @param space the Space where a task is waiting for this result to be returned
     */
    void setOwner(Space space);

    /**
     * This method returns the owner (a space) of the resykt
     * @return If it has an owner a Space is returned, else it will return null
     */
    Space getOwner();

    /**
     * If a client wants to use branch & bound on the system. They need add a jobId reference to the shared object.
     * The id should be unique, if else, the system may return the wrong shared object.
     * @return a unique job id as a String
     */
    String getJobId();
}

