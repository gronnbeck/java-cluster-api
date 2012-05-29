package checkpointing;

import java.io.*;
import java.util.ArrayList;

public class Persistor {

    private ArrayList<Serializable> objectList;
    private String file;

    public Persistor(String file) {
        objectList = new ArrayList<Serializable>();
        this.file = file;
    }

    public void write() {
        String tmpFile = file + ".tmp";
        IOHelpers.write(tmpFile, objectList);
        IOHelpers.renameFile(tmpFile, file);
    }

    public void read() {
        objectList = (ArrayList<Serializable>) IOHelpers.read(file);
    }

    public void add(Serializable object) {
        objectList.add(object);
    }

    public ArrayList<Serializable> getObjectList() {
        return (ArrayList<Serializable>) objectList.clone();
    }

}
