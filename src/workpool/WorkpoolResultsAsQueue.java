package workpool;

import api.Result;
import api.Task;

public interface WorkpoolResultsAsQueue {

    void putTask(Task task) throws InterruptedException;

    Result takeResult() throws InterruptedException;
}
