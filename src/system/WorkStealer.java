package system;

import api.Computer;
import api.Task;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

class WorkStealer implements Runnable {

    private int next;
    private Computer computer;
    private int backOff;
    private final int MAX_BACKOFF_VALUE = 2000;
    private boolean print;

    public WorkStealer(Computer computer) {
        this.computer = computer;
        this.next = 0;
        this.backOff = 2;
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
                if (!computer.want2Steal()) {
                    //System.out.println("Have enough tasks. Don't want 2 steal");
                    updateBackoff();
                    continue;
                }

                Computer computer = selectComputer();
                if (print) System.out.print("Trying to steal a task: ("+ computer.getTaskQ().size() +") ");
                if (computer.canSteal()) {
                    Task task = null;

                    try {
                        task = computer.stealTask();
                    } catch (IndexOutOfBoundsException e) { }

                    this.computer.addTask(task);
                    resetBackOff();

                    if (print) System.out.println("Success");
                } else {
                    if (print) System.out.println("Failed");
                    updateBackoff();
                }
            } catch (RemoteException ignore) {}


            // TODO: Handle if a computer is faulty

        } while (true);
    }
}
