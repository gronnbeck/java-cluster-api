package system;

import api.Computer;
import api.Task;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

/**
* Created with IntelliJ IDEA.
* User: gronnbeck
* Date: 5/29/12
* Time: 7:14 PM
* To change this template use File | Settings | File Templates.
*/
class WorkStealer implements Runnable {

    private int next;
    private List<Computer> computers;
    private List<Task> tasks;
    private ComputerProxy computerProxy;

    public WorkStealer(ComputerProxy computerProxy, List<Computer> computers, List<Task> tasks) {
        this.computerProxy = computerProxy;
        this.computers = computers;
        this.tasks = tasks;
        this.next = 0;
        this.backoff = 2;
    }

    private boolean hasComputers() throws RemoteException {
        return computers.size() > 0;
    }

    private Computer selectComputer() throws RemoteException {
        Random random = new Random();
        next = random.nextInt(computers.size());
        return computers.get(next);
    }

    private int backoff;
    private final int MAX_BACKOFF_VALUE = 2000;
    private void updateBackoff() {
        if (backoff < MAX_BACKOFF_VALUE) backoff = backoff*backoff;
        if (backoff > MAX_BACKOFF_VALUE) backoff = MAX_BACKOFF_VALUE;
    }
    private int getBackoff() {
        return backoff;
    }
    private  void resetBackoff() {
        backoff = 2;
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(getBackoff());
            } catch (InterruptedException e) { e.printStackTrace(); }

            try {
                if (!hasComputers()) {
                    updateBackoff();
                    continue;
                }
                if (!computerProxy.want2Steal()) {
                    //System.out.println("Have enough tasks. Don't want 2 steal");
                    updateBackoff();
                    continue;
                }
                Computer computer = selectComputer();
                //System.out.print("Trying to steal a task: ");
                if (computer.canSteal()) {
                    Task task = computer.stealTask();
                    this.tasks.add(task);
                    resetBackoff();
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
