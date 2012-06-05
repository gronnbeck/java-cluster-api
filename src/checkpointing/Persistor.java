package checkpointing;

import java.io.*;

public class Persistor {

    private State state;
    private String file;

    public Persistor(String file) {
        this.file = file;
    }

    public void write(State state) throws IOException {
        String tmpFile = file + ".tmp";
        IOHelpers.write(tmpFile, state);
        IOHelpers.renameFile(tmpFile, file);
    }

    public State read() throws ClassNotFoundException, IOException {
        return (State) IOHelpers.read(file);
    }
}
