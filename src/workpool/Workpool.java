package workpool;

import api.Task;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Workpool extends Thread implements Worker2Workpool {

    ArrayList<Worker> workers;
    BlockingQueue<Worker> readyWorkers;
    BlockingQueue<Task> taskQ;

    public Workpool() {
        workers = new ArrayList<Worker>();
        readyWorkers = new LinkedBlockingQueue<Worker>();
        taskQ = new LinkedBlockingQueue<Task>();
    }

    @Override
    public synchronized void addReadyWorker(Worker worker) {
        if (!workers.contains(worker)) {
            throw new IllegalArgumentException("Should not be able to add a worker not " +
                    "belonging to this workpool");
        }
        try {
            readyWorkers.put(worker);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void putTask(Task task) throws InterruptedException {
        taskQ.put(task);
    }


    private void initWorkers(){
        for (Worker worker : workers) {
            Thread t = new Thread(worker);
            t.start();
            addReadyWorker(worker);
        }
    }

    @Override
    public void run() {
        initWorkers();
        System.out.println("Started");
        while (true) {
            try {
                Task next = taskQ.take();
                Worker worker = readyWorkers.take();
                worker.setTask(next);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
