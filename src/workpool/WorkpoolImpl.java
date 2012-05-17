package workpool;

import api.Result;
import api.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkpoolImpl extends Workpool implements WorkpoolResultsAsQueue {

    BlockingQueue<Result> resultQ;

    public WorkpoolImpl(int numWorkers) {
        super();
        resultQ = new LinkedBlockingQueue<Result>();
        taskQ = new LinkedBlockingQueue<Task>(numWorkers);
        for (int i = 1; i <= numWorkers; i++) {
            new Worker(this);
        }
    }

    @Override
    public void returnResult(Result result) {
        try {
            resultQ.put(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void registerWorker(Worker worker) {
        workers.add(worker);
    }
    @Override
    public Result takeResult() throws InterruptedException {
        return resultQ.take();
    }
}
