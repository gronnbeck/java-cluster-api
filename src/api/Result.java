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

    String getJobId();
}

