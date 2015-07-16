import java.io.*;

public class WriterWrapper {
    DataOutputStream dataOutputStream = null;
    PrintWriter writer = null;
    boolean print = true;

    public WriterWrapper(String fileName, boolean print) {
        this.print = print;
        try {
            if (print) {
                writer = new PrintWriter(new FileWriter(fileName));
            } else {
                dataOutputStream = new DataOutputStream(new FileOutputStream(fileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(short val) {
        try {
            if (!print) {
                dataOutputStream.writeShort(val);
            } else {
                writer.write(val + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(double val) {
        try {
            if (!print) {
                dataOutputStream.writeDouble(val);
            } else {
                writer.write(val + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void line() {
        if (print) {
            writer.write("\n");
        }
    }

    public void close() {
        try {
            if (print) {
                writer.close();
            } else {
                dataOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
