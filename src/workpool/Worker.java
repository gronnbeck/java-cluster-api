package workpool;


import api.Result;
import api.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Worker implements Runnable {

    private BlockingQueue<Task> taskQ;
    private Worker2Workpool workpool;

    public Worker(Worker2Workpool workpool) {
        this.workpool = workpool;
        taskQ =  new LinkedBlockingDeque<Task>(1);
        workpool.registerWorker(this);
        System.out.println("Worker ready");
    }

    public void setTask(Task task) {
        try {
            taskQ.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Task next() throws InterruptedException {
       return taskQ.take();
    }

    @Override
    public void run() {
        // Find a way to return results later
        while (true) {
            try {
                Task task = next();
                long timeBefore = System.nanoTime();
                Result result = task.execute();
                result.setTaskRunTime(System.nanoTime() - timeBefore);
                workpool.returnResult(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
               workpool.addReadyWorker(this);
            }
       }

    }
}
