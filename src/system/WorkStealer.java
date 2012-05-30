package system;

import api.Computer;
import api.Task;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

class WorkStealer implements Runnable {

    private int next;
    private List<Computer> otherComputers;
    private List<Task> tasks;
    private Computer computer;
    private int backOff;
    private final int MAX_BACKOFF_VALUE = 2000;

    public WorkStealer(Computer computer, List<Computer> otherComputers, List<Task> tasks) {
        this.computer = computer;
        this.otherComputers = otherComputers;
        this.tasks = tasks;
        this.next = 0;
        this.backOff = 2;
    }

    private boolean hasComputers() throws RemoteException {
        return otherComputers.size() > 0;
    }

    private Computer selectComputer() throws RemoteException {
        Random random = new Random();
        next = random.nextInt(otherComputers.size());
        return otherComputers.get(next);
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
                //System.out.print("Trying to steal a task: ");
                if (computer.canSteal()) {
                    Task task = computer.stealTask();
                    this.tasks.add(task);
                    resetBackOff();
                   // System.out.println("Success");
                } else {
                   // System.out.println("Failed");
                    updateBackoff();
                }
            } catch (RemoteException ignore) {}


            // TODO: Handle if a computer is faulty

        } while (true);
    }
}
