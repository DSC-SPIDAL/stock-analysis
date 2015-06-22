package Salsa.Core.Blas;

import Salsa.Core.*;

public class MatrixTextReader
{
	private int _colCount;
	private char _delimiter;
	private int _rowCount;

	public MatrixTextReader(int rowCount, int colCount)
	{
		this(rowCount, colCount, ',');
	}

	public MatrixTextReader(int rowCount, int colCount, char delimiter)
	{
		_rowCount = rowCount;
		_colCount = colCount;
		_delimiter = delimiter;
	}

	public final Matrix<Short> ReadInt16(String fileName)
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
//ORIGINAL LINE: public Matrix<ushort> ReadUInt16(string fileName)
	public final Matrix<Short> ReadUInt16(String fileName)
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

	public final Matrix<Integer> ReadInt32(String fileName)
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
//ORIGINAL LINE: public Matrix<uint> ReadUInt32(string fileName)
	public final Matrix<Integer> ReadUInt32(String fileName)
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

	public final Matrix<Float> ReadSingle(String fileName)
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

	public final Matrix<Double> ReadDouble(String fileName)
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

	public final Matrix<Short> ReadInt16(StreamReader reader)
	{
		short[][] elements = Matrix<Short>.CreateElements(_rowCount, _colCount);

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

		return new Matrix<Short>(elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<ushort> ReadUInt16(StreamReader reader)
	public final Matrix<Short> ReadUInt16(StreamReader reader)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort[][] elements = Matrix<ushort>.CreateElements(_rowCount, _colCount);
		short[][] elements = Matrix<Short>.CreateElements(_rowCount, _colCount);

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
//ORIGINAL LINE: return new Matrix<ushort>(elements);
		return new Matrix<Short>(elements);
	}

	public final Matrix<Integer> ReadInt32(StreamReader reader)
	{
		int[][] elements = Matrix<Integer>.CreateElements(_rowCount, _colCount);

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

		return new Matrix<Integer>(elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<uint> ReadUInt32(StreamReader reader)
	public final Matrix<Integer> ReadUInt32(StreamReader reader)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint[][] elements = Matrix<uint>.CreateElements(_rowCount, _colCount);
		int[][] elements = Matrix<Integer>.CreateElements(_rowCount, _colCount);

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
//ORIGINAL LINE: return new Matrix<uint>(elements);
		return new Matrix<Integer>(elements);
	}

	public final Matrix<Float> ReadSingle(StreamReader reader)
	{
		float[][] elements = Matrix<Float>.CreateElements(_rowCount, _colCount);

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

		return new Matrix<Float>(elements);
	}

	public final Matrix<Double> ReadDouble(StreamReader reader)
	{
		double[][] elements = Matrix<Double>.CreateElements(_rowCount, _colCount);

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

		return new Matrix<Double>(elements);
	}

	public final Matrix<Short> ReadInt16(String fileName, int rowStartIndex, int rowCount)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			return ReadInt16(reader, rowStartIndex, rowCount);
		}
		finally
		{
			reader.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<ushort> ReadUInt16(string fileName, int rowStartIndex, int rowCount)
	public final Matrix<Short> ReadUInt16(String fileName, int rowStartIndex, int rowCount)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var reader = new StreamReader(fileName))
		StreamReader reader = new StreamReader(fileName);
		try
		{
			return ReadUInt16(reader, rowStartIndex, rowCount);
		}
		finally
		{
			reader.dispose();
		}
	}

	public final Matrix<Short> ReadInt16(StreamReader reader, int rowStartIndex, int rowCount)
	{
		short[][] elements = Matrix<Short>.CreateElements(_rowCount, _colCount);
		char[] separators = new char[] {_delimiter};

		int row = 0;

		while (reader.EndOfStream == false)
		{
			String line = reader.ReadLine();

			if (row >= rowStartIndex + rowCount)
			{
				break;
			}
			else if (row >= rowStartIndex)
			{
				String[] fields = line.split(java.util.regex.Pattern.quote(separators.toString()), -1);

				if (fields.length != _colCount)
				{
					throw new InvalidDataException("Unexpected number of columns on line " + row);
				}

				for (int col = 0; col < fields.length; col++)
				{
					elements[row][col] = Short.parseShort(fields[col].trim());
				}
			}

			row++;
		}

		return new Matrix<Short>(elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<ushort> ReadUInt16(StreamReader reader, int rowStartIndex, int rowCount)
	public final Matrix<Short> ReadUInt16(StreamReader reader, int rowStartIndex, int rowCount)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort[][] elements = Matrix<ushort>.CreateElements(_rowCount, _colCount);
		short[][] elements = Matrix<Short>.CreateElements(_rowCount, _colCount);
		char[] separators = new char[] {_delimiter};

		int row = 0;

		while (reader.EndOfStream == false)
		{
			String line = reader.ReadLine();

			if (row >= rowStartIndex + rowCount)
			{
				break;
			}
			else if (row >= rowStartIndex)
			{
				String[] fields = line.split(java.util.regex.Pattern.quote(separators.toString()), -1);

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
			}

			row++;
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new Matrix<ushort>(elements);
		return new Matrix<Short>(elements);
	}
}