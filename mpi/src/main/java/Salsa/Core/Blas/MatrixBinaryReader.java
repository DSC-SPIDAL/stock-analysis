package Salsa.Core.Blas;

import Salsa.Core.*;

public class MatrixBinaryReader
{
	private int _colCount;
	private int _rowCount;

	public MatrixBinaryReader(int rowCount, int colCount)
	{
		_rowCount = rowCount;
		_colCount = colCount;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Read matrix through global row and column counts as set via constructor

	public final Matrix<Short> ReadInt16(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open))
		Stream stream = File.Open(fileName, FileMode.Open);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadInt16(reader);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<ushort> ReadUInt16(string fileName)
	public final Matrix<Short> ReadUInt16(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open))
		Stream stream = File.Open(fileName, FileMode.Open);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadUInt16(reader);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final Matrix<Integer> ReadInt32(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open))
		Stream stream = File.Open(fileName, FileMode.Open);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadInt32(reader);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<uint> ReadUInt32(string fileName)
	public final Matrix<Integer> ReadUInt32(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open))
		Stream stream = File.Open(fileName, FileMode.Open);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadUInt32(reader);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final Matrix<Float> ReadSingle(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open))
		Stream stream = File.Open(fileName, FileMode.Open);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadSingle(reader);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final Matrix<Double> ReadDouble(String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open))
		Stream stream = File.Open(fileName, FileMode.Open);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadDouble(reader);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final Matrix<Short> ReadInt16(BinaryReader reader)
	{
		short[][] elements = Matrix<Short>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadInt16();
			}
		}

		return new Matrix<Short>(elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<ushort> ReadUInt16(BinaryReader reader)
	public final Matrix<Short> ReadUInt16(BinaryReader reader)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort[][] elements = Matrix<ushort>.CreateElements(_rowCount, _colCount);
		short[][] elements = Matrix<Short>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadUInt16();
			}
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new Matrix<ushort>(elements);
		return new Matrix<Short>(elements);
	}

	public final Matrix<Integer> ReadInt32(BinaryReader reader)
	{
		int[][] elements = Matrix<Integer>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadInt32();
			}
		}

		return new Matrix<Integer>(elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<uint> ReadUInt32(BinaryReader reader)
	public final Matrix<Integer> ReadUInt32(BinaryReader reader)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint[][] elements = Matrix<uint>.CreateElements(_rowCount, _colCount);
		int[][] elements = Matrix<Integer>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadUInt32();
			}
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new Matrix<uint>(elements);
		return new Matrix<Integer>(elements);
	}

	public final Matrix<Float> ReadSingle(BinaryReader reader)
	{
		float[][] elements = Matrix<Float>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadSingle();
			}
		}

		return new Matrix<Float>(elements);
	}

	public final Matrix<Double> ReadDouble(BinaryReader reader)
	{
		double[][] elements = Matrix<Double>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadDouble();
			}
		}

		return new Matrix<Double>(elements);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Read matrix from a set of files with consecutive numbering

	/** 
	 Reads a matrix of Int16 from the set of files in the given path with the given prefix and
	 indices lying between startIndex and endIndex inclusively.
	 
	 @param path The local path where partial matrix files are stored
	 @param prefix The name prefix of the files
	 @param ext File extension of the partial matrix files
	 @param startIndex The start index of file names to read
	 @param endIndex The end index (inclusively) of file names to read
	 @return 
	*/
	public final Matrix<Short> ReadInt16(String path, String prefix, String ext, int startIndex, int endIndex, Range[] ranges)
	{
		short[][] elements = Matrix<Short>.CreateElements(_rowCount, _colCount);
		String fileName;
		int row = 0;
		for (int i = startIndex; i <= endIndex; i++)
		{
			fileName = String.format("%1$s_%2$s%3$s", prefix, i, ext);
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (Stream stream = File.Open(Path.Combine(path, fileName), FileMode.Open, FileAccess.Read, FileShare.Read))
			Stream stream = File.Open(Path.Combine(path, fileName), FileMode.Open, FileAccess.Read, FileShare.Read);
			try
			{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//				using (var reader = new BinaryReader(stream))
				BinaryReader reader = new BinaryReader(stream);
				try
				{
					for (int j = ranges[i].StartIndex; j <= ranges[i].EndIndex; j++)
					{
						for (int k = 0; k < _colCount; k++)
						{
							elements[row][k] = reader.ReadInt16();
						}
						row++;
					}
				}
				finally
				{
					reader.dispose();
				}
			}
			finally
			{
				stream.dispose();
			}
		}
		return new Matrix<Short>(elements);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Read matrix only for a given set of rows starting from a particular index

	public final Matrix<Short> ReadInt16(String fileName, int startRowIndex, int rowCount)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read))
		Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadInt16(reader, startRowIndex, rowCount);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<ushort> ReadUInt16(string fileName, int startRowIndex, int rowCount)
	public final Matrix<Short> ReadUInt16(String fileName, int startRowIndex, int rowCount)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read))
		Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadUInt16(reader, startRowIndex, rowCount);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final Matrix<Integer> ReadInt32(String fileName, int startRowIndex, int rowCount)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read))
		Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadInt32(reader, startRowIndex, rowCount);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<uint> ReadUInt32(string fileName, int startRowIndex, int rowCount)
	public final Matrix<Integer> ReadUInt32(String fileName, int startRowIndex, int rowCount)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read))
		Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadUInt32(reader, startRowIndex, rowCount);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final Matrix<Float> ReadSingle(String fileName, int startRowIndex, int rowCount)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read))
		Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadSingle(reader, startRowIndex, rowCount);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final Matrix<Double> ReadDouble(String fileName, int startRowIndex, int rowCount)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read))
		Stream stream = File.Open(fileName, FileMode.Open, FileAccess.Read, FileShare.Read);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new BinaryReader(stream))
			BinaryReader reader = new BinaryReader(stream);
			try
			{
				return ReadDouble(reader, startRowIndex, rowCount);
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final Matrix<Short> ReadInt16(BinaryReader reader, int startRowIndex, int rowCount)
	{
		short[][] elements = Matrix<Short>.CreateElements(rowCount, _colCount);

		long offset = startRowIndex;
		offset *= _colCount;
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		offset *= sizeof(short);

		reader.BaseStream.Seek(offset, SeekOrigin.Begin);

		for (int i = 0; i < rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadInt16();
			}
		}

		return new Matrix<Short>(elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<ushort> ReadUInt16(BinaryReader reader, int startRowIndex, int rowCount)
	public final Matrix<Short> ReadUInt16(BinaryReader reader, int startRowIndex, int rowCount)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort[][] elements = Matrix<ushort>.CreateElements(rowCount, _colCount);
		short[][] elements = Matrix<Short>.CreateElements(rowCount, _colCount);

		long offset = startRowIndex;
		offset *= _colCount;
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: offset *= sizeof(ushort);
		offset *= sizeof(short);
		reader.BaseStream.Seek(offset, SeekOrigin.Begin);

		for (int i = 0; i < rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadUInt16();
			}
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new Matrix<ushort>(elements);
		return new Matrix<Short>(elements);
	}

	public final Matrix<Integer> ReadInt32(BinaryReader reader, int startRowIndex, int rowCount)
	{
		int[][] elements = Matrix<Integer>.CreateElements(rowCount, _colCount);

		long offset = startRowIndex;
		offset *= _colCount;
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		offset *= sizeof(int);
		reader.BaseStream.Seek(offset, SeekOrigin.Begin);

		for (int i = 0; i < rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadInt32();
			}
		}

		return new Matrix<Integer>(elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public Matrix<uint> ReadUInt32(BinaryReader reader, int startRowIndex, int rowCount)
	public final Matrix<Integer> ReadUInt32(BinaryReader reader, int startRowIndex, int rowCount)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint[][] elements = Matrix<uint>.CreateElements(rowCount, _colCount);
		int[][] elements = Matrix<Integer>.CreateElements(rowCount, _colCount);

		long offset = startRowIndex;
		offset *= _colCount;
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: offset *= sizeof(uint);
		offset *= sizeof(int);
		reader.BaseStream.Seek(offset, SeekOrigin.Begin);

		for (int i = 0; i < rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadUInt32();
			}
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new Matrix<uint>(elements);
		return new Matrix<Integer>(elements);
	}

	public final Matrix<Float> ReadSingle(BinaryReader reader, int startRowIndex, int rowCount)
	{
		float[][] elements = Matrix<Float>.CreateElements(rowCount, _colCount);

		long offset = startRowIndex;
		offset *= _colCount;
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		offset *= sizeof(float);
		reader.BaseStream.Seek(offset, SeekOrigin.Begin);

		for (int i = 0; i < rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadSingle();
			}
		}

		return new Matrix<Float>(elements);
	}

	public final Matrix<Double> ReadDouble(BinaryReader reader, int startRowIndex, int rowCount)
	{
		double[][] elements = Matrix<Double>.CreateElements(rowCount, _colCount);

		long offset = startRowIndex;
		offset *= _colCount;
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to 'sizeof':
		offset *= sizeof(double);
		reader.BaseStream.Seek(offset, SeekOrigin.Begin);

		for (int i = 0; i < rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadDouble();
			}
		}

		return new Matrix<Double>(elements);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}