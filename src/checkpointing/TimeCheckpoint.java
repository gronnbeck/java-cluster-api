package checkpointing;

import java.io.IOException;

public class TimeCheckpoint<T> extends Thread {

    private long time;
    private String file;
    private Persistor persistor;
    private Recoverable recoverable;

    public TimeCheckpoint(Recoverable recoverable, int min, String file) {
        this.recoverable = recoverable;
        time = 1000*60*min;
        this.file = file;
    }


    private boolean next() {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void run() {
        do {
            if (!recoverable.stateChanged()) continue;
            persistor = new Persistor(file);
            checkpointing.State state = recoverable.getState();

            try {
                persistor.write(state);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Persisted SpaceImpl's State");
        } while(next());
    }
}
