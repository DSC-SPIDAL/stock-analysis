package Salsa.Core.Blas;

import Salsa.Core.*;

public class PartialMatrixTextReader
{
	private int _colCount;
	private char _delimiter;
	private int _globalColStartIndex;
	private int _globalRowStartIndex;
	private int _rowCount;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	public PartialMatrixTextReader(Range globalRowRange, Range globalColumnRange)
	{
		this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex, ',');
	}

	public PartialMatrixTextReader(Range globalRowRange, int globalColumnStartIndex, int globalColumnEndIndex)
	{
		this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnStartIndex, globalColumnEndIndex, ',');
	}

	public PartialMatrixTextReader(int globalRowStartIndex, int globalRowEndIndex, Range globalColumnRange)
	{
		this(globalRowStartIndex, globalRowEndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex, ',');
	}

	public PartialMatrixTextReader(Range globalRowRange, Range globalColumnRange, char delimiter)
	{
		this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex, delimiter);
	}

	public PartialMatrixTextReader(Range globalRowRange, int globalColumnStartIndex, int globalColumnEndIndex, char delimiter)
	{
		this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnStartIndex, globalColumnEndIndex, delimiter);
	}

	public PartialMatrixTextReader(int globalRowStartIndex, int globalRowEndIndex, Range globalColumnRange, char delimiter)
	{
		this(globalRowStartIndex, globalRowEndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex, delimiter);
	}

	public PartialMatrixTextReader(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex, char delimiter)
	{
		_globalRowStartIndex = globalRowStartIndex;
		_globalColStartIndex = globalColumnStartIndex;
		_rowCount = globalRowEndIndex - globalRowStartIndex + 1;
		_colCount = globalColumnEndIndex - globalColumnStartIndex + 1;
		_delimiter = delimiter;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	public final PartialMatrix<Short> ReadInt16(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			return ReadInt16(reader);
		}
		finally
		{
			reader.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public PartialMatrix<ushort> ReadUInt16(string fileName)
	public final PartialMatrix<Short> ReadUInt16(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			return ReadUInt16(reader);
		}
		finally
		{
			reader.dispose();
		}
	}

	public final PartialMatrix<Integer> ReadInt32(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			return ReadInt32(reader);
		}
		finally
		{
			reader.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public PartialMatrix<uint> ReadUInt32(string fileName)
	public final PartialMatrix<Integer> ReadUInt32(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			return ReadUInt32(reader);
		}
		finally
		{
			reader.dispose();
		}
	}

	public final PartialMatrix<Float> ReadSingle(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			return ReadSingle(reader);
		}
		finally
		{
			reader.dispose();
		}
	}

	public final PartialMatrix<Double> ReadDouble(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			return ReadDouble(reader);
		}
		finally
		{
			reader.dispose();
		}
	}

	public final PartialMatrix<Short> ReadInt16(StreamReader reader)
	{
		short[][] elements = PartialMatrix<Short>.CreateElements(_rowCount, _colCount);

		int row = 0;

		// Read the Body
		while (reader.EndOfStream == false)
		{
			String[] fields = reader.ReadLine().split(java.util.regex.Pattern.quote(new char[] {_delimiter}.toString()), -1);

			if (fields.length != _colCount)
			{
				throw new InvalidDataException("Unexpected number of columns on line " + row);
			}

			for (int col = 0; col < fields.length; col++)
			{
				elements[row][col] = Short.parseShort(fields[col].trim());
			}

			row++;
		}

		if (row != _rowCount)
		{
			throw new InvalidDataException("Unexpected number of rows in file");
		}

		return new PartialMatrix<Short>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public PartialMatrix<ushort> ReadUInt16(StreamReader reader)
	public final PartialMatrix<Short> ReadUInt16(StreamReader reader)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort[][] elements = PartialMatrix<ushort>.CreateElements(_rowCount, _colCount);
		short[][] elements = PartialMatrix<Short>.CreateElements(_rowCount, _colCount);

		int row = 0;

		// Read the Body
		while (reader.EndOfStream == false)
		{
			String[] fields = reader.ReadLine().split(java.util.regex.Pattern.quote(new char[] {_delimiter}.toString()), -1);

			if (fields.length != _colCount)
			{
				throw new InvalidDataException("Unexpected number of columns on line " + row);
			}

			for (int col = 0; col < fields.length; col++)
			{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: elements[row][col] = Convert.ToUInt16(fields[col].Trim());
				elements[row][col] = Short.parseShort(fields[col].trim());
			}

			row++;
		}

		if (row != _rowCount)
		{
			throw new InvalidDataException("Unexpected number of rows in file");
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new PartialMatrix<ushort>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
		return new PartialMatrix<Short>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

	public final PartialMatrix<Integer> ReadInt32(StreamReader reader)
	{
		int[][] elements = PartialMatrix<Integer>.CreateElements(_rowCount, _colCount);

		int row = 0;

		// Read the Body
		while (reader.EndOfStream == false)
		{
			String[] fields = reader.ReadLine().split(java.util.regex.Pattern.quote(new char[] {_delimiter}.toString()), -1);

			if (fields.length != _colCount)
			{
				throw new InvalidDataException("Unexpected number of columns on line " + row);
			}

			for (int col = 0; col < fields.length; col++)
			{
				elements[row][col] = Integer.parseInt(fields[col].trim());
			}

			row++;
		}

		if (row != _rowCount)
		{
			throw new InvalidDataException("Unexpected number of rows in file");
		}

		return new PartialMatrix<Integer>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public PartialMatrix<uint> ReadUInt32(StreamReader reader)
	public final PartialMatrix<Integer> ReadUInt32(StreamReader reader)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint[][] elements = PartialMatrix<uint>.CreateElements(_rowCount, _colCount);
		int[][] elements = PartialMatrix<Integer>.CreateElements(_rowCount, _colCount);

		int row = 0;

		// Read the Body
		while (reader.EndOfStream == false)
		{
			String[] fields = reader.ReadLine().split(java.util.regex.Pattern.quote(new char[] {_delimiter}.toString()), -1);

			if (fields.length != _colCount)
			{
				throw new InvalidDataException("Unexpected number of columns on line " + row);
			}

			for (int col = 0; col < fields.length; col++)
			{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: elements[row][col] = Convert.ToUInt32(fields[col].Trim());
				elements[row][col] = Integer.parseInt(fields[col].trim());
			}

			row++;
		}

		if (row != _rowCount)
		{
			throw new InvalidDataException("Unexpected number of rows in file");
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new PartialMatrix<uint>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
		return new PartialMatrix<Integer>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

	public final PartialMatrix<Float> ReadSingle(StreamReader reader)
	{
		float[][] elements = PartialMatrix<Float>.CreateElements(_rowCount, _colCount);

		int row = 0;

		// Read the Body
		while (reader.EndOfStream == false)
		{
			String[] fields = reader.ReadLine().split(java.util.regex.Pattern.quote(new char[] {_delimiter}.toString()), -1);

			if (fields.length != _colCount)
			{
				throw new InvalidDataException("Unexpected number of columns on line " + row);
			}

			for (int col = 0; col < fields.length; col++)
			{
				elements[row][col] = Float.parseFloat(fields[col].trim());
			}

			row++;
		}

		if (row != _rowCount)
		{
			throw new InvalidDataException("Unexpected number of rows in file");
		}

		return new PartialMatrix<Float>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

	public final PartialMatrix<Double> ReadDouble(StreamReader reader)
	{
		double[][] elements = PartialMatrix<Double>.CreateElements(_rowCount, _colCount);

		int row = 0;

		// Read the Body
		while (reader.EndOfStream == false)
		{
			String[] fields = reader.ReadLine().split(java.util.regex.Pattern.quote(new char[] {_delimiter}.toString()), -1);

			if (fields.length != _colCount)
			{
				throw new InvalidDataException("Unexpected number of columns on line " + row);
			}

			for (int col = 0; col < fields.length; col++)
			{
				elements[row][col] = Double.parseDouble(fields[col].trim());
			}

			row++;
		}

		if (row != _rowCount)
		{
			throw new InvalidDataException("Unexpected number of rows in file");
		}

		return new PartialMatrix<Double>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}
}