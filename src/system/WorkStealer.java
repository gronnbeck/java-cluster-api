package system;

import api.Computer;
import api.Task;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class WorkStealer implements Runnable {

    private int next;
    private final Computer computer;
    private int backOff;
    private final int MAX_BACKOFF_VALUE = 2000;
    private boolean print;
    private boolean running;

    public WorkStealer(Computer computer) {
        this.computer = computer;
        this.next = 0;
        this.backOff = 2;
        this.running = true;
    }

    public WorkStealer(Computer computer, boolean print) {
        this(computer);
        this.print = print;
    }

    private boolean hasComputers() throws RemoteException {
        return computer.getComputers().size() > 0;
    }

    private Computer selectComputer() throws RemoteException {
        Random random = new Random();
        next = random.nextInt(computer.getComputers().size());
        return computer.getComputers().get(next);
    }

    private void updateBackoff() {
        if (backOff < MAX_BACKOFF_VALUE) backOff = backOff * backOff;
        if (backOff > MAX_BACKOFF_VALUE) backOff = MAX_BACKOFF_VALUE;
    }

    private int getBackOff() {
        return backOff;
    }

    private  void resetBackOff() {
        backOff = 2;
    }

    private BlockingQueue<Boolean> hasStopped;
    public void stop() {
        hasStopped = new LinkedBlockingQueue<Boolean>();
        running = false;
        try {
            hasStopped.take();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("A Workstealer has been shutodwn");
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(getBackOff());
            } catch (InterruptedException e) { e.printStackTrace(); }

            try {
                if (!hasComputers()) {
                    updateBackoff();
                    continue;
                }
                if (!this.computer.want2Steal()) {
                    updateBackoff();
                    continue;
                }

                Computer computer = selectComputer();
                if (print) System.out.print("Trying to steal a task: ("+ computer.getTaskQSize() +") ");
                if (computer.canSteal()) {
                    Task<?> task = null;

                    while(computer.canSteal() && running) {
                    	System.out.println(computer.getTaskQSize());
                        try {
                            task = computer.stealTask();
                            if (task == null) break;
                            this.computer.addTask(task);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                    resetBackOff();

                    if (print) System.out.println("Success");
                } else {
                    if (print) System.out.println("Failed");
                    updateBackoff();
                }
            } catch (RemoteException ignore) {}


            // TODO: Handle if a computer is faulty

        } while (running);
        System.out.println("out of while loop");

        try {
            hasStopped.put(true);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
