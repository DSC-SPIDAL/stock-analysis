
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DistanceReader {
    private MatrixReader _matrixReader;
    private java.util.List<Point> _pointsTable = new java.util.ArrayList<Point>();
    private boolean _readPoints;

    public DistanceReader(String file, int cols, int rows, boolean readPoints) {
        _readPoints = readPoints;
        if (readPoints) {
            try {
                readPoints(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            _matrixReader = new MatrixReader(file, cols, rows);
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

    public final double ReadDistanceFromMatrix(int row, int col) {
        if (_readPoints) {
            throw new RuntimeException("DistanceReader is instantiated to read from points file, but called the matrix file read");
        }
        return _matrixReader.read(row, col);
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
