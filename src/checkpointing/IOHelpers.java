package checkpointing;

import java.io.*;

public class IOHelpers {
    public static boolean renameFile(String file, String toFile) {
        File toBeRenamed = new File(file);
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            System.out.println("File does not exist: " + file);
            return false;
        }
        File newFile = new File(toFile);

        return toBeRenamed.renameTo(newFile);
    }

    public static void write(String file, Serializable object) throws FileNotFoundException, IOException {
        FileOutputStream fOut = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fOut);
        objectOutputStream.writeObject(object);
    }

    public static Serializable read(String file) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fIn = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fIn);
        return (Serializable) objectInputStream.readObject();

    }
}
