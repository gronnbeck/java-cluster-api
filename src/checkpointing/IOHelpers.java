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

    public static void write(String file, Serializable object) {
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fOut);
            objectOutputStream.writeObject(object);
        } catch (FileNotFoundException e) {
            System.out.println("Trying to write file to a file that doesn't exist");
        } catch (IOException e) {
            System.out.println("IOException when trying to write an object " + object.toString());
        }
    }

    public static Serializable read(String file) {
        try {
            FileInputStream fIn = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fIn);
            return (Serializable) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File you were trying to read from doesn't exist");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
