import edu.indiana.soic.spidal.common.BinaryReader2D;
import edu.indiana.soic.spidal.common.Range;

import java.nio.ByteOrder;

public class MatrixReader {
    private String file;
    private int cols;
    int rows;

    short [][]values;

    public MatrixReader(String file, int cols, int rows) {
        this.file = file;
        this.cols = cols;
        this.rows = rows;
        readValues();
    }

    private void readValues() {
        values = BinaryReader2D.readRowRange(file, new Range(0, rows - 1), cols, ByteOrder.BIG_ENDIAN, false, 1.0);
    }

    public double read(int row, int col) {
        return ((double)values[row][col])/ Short.MAX_VALUE;
    }
}
