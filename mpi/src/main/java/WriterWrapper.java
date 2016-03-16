import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class WriterWrapper {
    DataOutputStream dataOutputStream = null;
    PrintWriter writer = null;
    boolean append = false;
    boolean print = true;
    FileChannel wChannel;
    boolean nio;

    public WriterWrapper(String fileName, boolean print) {
        this(fileName, print, false);
    }

    public WriterWrapper(String fileName, boolean print, boolean nio) {
        this.print = print;
        this.nio = nio;
        try {
            if (print) {
                writer = new PrintWriter(new FileWriter(fileName));
            } else if (!nio) {
                dataOutputStream = new DataOutputStream(new FileOutputStream(fileName));
            } else {
                wChannel = new FileOutputStream(new File(fileName), false).getChannel();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeShort(short val) {
        try {
            if (!print) {
                dataOutputStream.writeShort(val);
            } else {
                writer.write(val + " ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public void write(ByteBuffer buffer) {
        try {
            wChannel.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String line) {
        if (print) {
            writer.println(line);
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
            } else if (!nio){
                dataOutputStream.close();
            } else {
                wChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
