
public class MatrixReader {
    private String file;
    private int cols;
    int rows;

    short [][]values = new short[rows][cols];

    public MatrixReader(String file, int cols, int rows) {
        this.file = file;
        this.cols = cols;
        this.rows = rows;
    }

    public double read(int row, int col) {
        return ((double)values[row][col])/ Short.MAX_VALUE;
    }
}
