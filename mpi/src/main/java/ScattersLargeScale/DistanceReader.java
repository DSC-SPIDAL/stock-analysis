package ScattersLargeScale;

import Common.*;

public class DistanceReader
{
	private MatrixReader _matrixReader;
	private java.util.List<Point> _pointsTable = new java.util.ArrayList<Point>();
	private boolean _readPoints;
	private MatrixType _matrixType = MatrixType.values()[0];

	public DistanceReader(String file, MatrixType matrixType, int cols, boolean readPoints)
	{
		_readPoints = readPoints;
		_matrixType = matrixType;
		if (readPoints)
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new SimplePointsReader(file))
			SimplePointsReader reader = new SimplePointsReader(file);
			try
			{
				while (!reader.getEndOfStream())
				{
					Point p = reader.ReadPoint();
					_pointsTable.add(p);
				}
			}
			finally
			{
				reader.dispose();
			}
		}
		else
		{
			_matrixReader = new MatrixReader(file, matrixType, cols);
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public byte[] ReadDistanceFromMatrix(int row, int col)
	public final byte[] ReadDistanceFromMatrix(int row, int col)
	{
		if (_readPoints)
		{
			throw new RuntimeException("DistanceReader is instantiated to read from points file, but called the matrix file read");
		}
		return _matrixReader.Read(row, col);
	}

	public final double ReadDistanceFromPointsFile(int row, int col)
	{
		if (!_readPoints)
		{
			throw new RuntimeException("DistanceReader is instantiated to read from matrix file, but called the points file read");
		}
		Point rowPoint = _pointsTable.get(row);
		Point colPoint = _pointsTable.get(col);
		return rowPoint.DistanceTo(colPoint);
	}


	public final void Dispose()
	{
		if (!_readPoints && _matrixReader != null)
		{
			_matrixReader.Dispose();
		}
	}
}