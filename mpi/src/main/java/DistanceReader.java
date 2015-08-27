import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DistanceReader {
    private MatrixReader _matrixReader;
    private java.util.List<Point> _pointsTable = new java.util.ArrayList<Point>();
    private boolean _readPoints;
    private MatrixType _matrixType = MatrixType.values()[0];

    public DistanceReader(String file, MatrixType matrixType, int cols, boolean readPoints) {
        _readPoints = readPoints;
        _matrixType = matrixType;
        if (readPoints) {
            try {
                readPoints(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            _matrixReader = new MatrixReader(file, matrixType, cols);
        }
    }

    private void readPoints(String file) throws IOException {
        FileReader input = new FileReader(file);
        BufferedReader bufRead = new BufferedReader(input);
        String inputLine;
        int index = 0;
        while ((inputLine = bufRead.readLine()) != null) {
            Point p = Utils.readPoint(inputLine);
            _pointsTable.add(p);
        }
    }

    public final byte[] ReadDistanceFromMatrix(int row, int col) {
        if (_readPoints) {
            throw new RuntimeException("DistanceReader is instantiated to read from points file, but called the matrix file read");
        }
        return _matrixReader.Read(row, col);
    }

    public final double ReadDistanceFromPointsFile(int row, int col) {
        if (!_readPoints) {
            throw new RuntimeException("DistanceReader is instantiated to read from matrix file, but called the points file read");
        }
        Point rowPoint = _pointsTable.get(row);
        Point colPoint = _pointsTable.get(col);
        return rowPoint.distance(colPoint);
    }
}