package checkpointing;

import java.io.Serializable;
import java.util.Collection;

public class TimeCheckpoint<T> extends Thread {

    private long time;
    private String file;
    private Persistor persistor;
    private boolean changed;
    public TimeCheckpoint(String file, int min) {
        time = 1000*60*min;
        changed = false;
    }


    private void setState(Collection<T> collection) {
        for (T object : collection) {
            if (!(object instanceof Serializable)) throw new IllegalArgumentException("Cannot serialize the following object: "+ object);
            persistor.add((Serializable)object);
        }
        changed = true;
    }

    private void sleep() {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        do {
            sleep();

            if (!changed) continue;
            persistor = new Persistor(file);
            persistor.write();
            changed = false;
            System.out.println("Persisted SpaceImpl's State");
        } while(true);
    }
}
