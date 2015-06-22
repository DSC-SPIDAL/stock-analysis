package Salsa.Core.Blas;

import Salsa.Core.*;

public class PartialMatrixBinaryReader
{
	private int _colCount;
	private int _globalColStartIndex;
	private int _globalRowStartIndex;
	private int _rowCount;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	public PartialMatrixBinaryReader(Range globalRowRange, Range globalColumnRange)
	{
		this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
	}

	public PartialMatrixBinaryReader(Range globalRowRange, int globalColumnStartIndex, int globalColumnEndIndex)
	{
		this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnStartIndex, globalColumnEndIndex);
	}

	public PartialMatrixBinaryReader(int globalRowStartIndex, int globalRowEndIndex, Range globalColumnRange)
	{
		this(globalRowStartIndex, globalRowEndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
	}

	public PartialMatrixBinaryReader(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex)
	{
		_globalRowStartIndex = globalRowStartIndex;
		_globalColStartIndex = globalColumnStartIndex;
		_rowCount = globalRowEndIndex - globalRowStartIndex + 1;
		_colCount = globalColumnEndIndex - globalColumnStartIndex + 1;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	public final PartialMatrix<Short> ReadInt16(String fileName)
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
//ORIGINAL LINE: public PartialMatrix<ushort> ReadUInt16(string fileName)
	public final PartialMatrix<Short> ReadUInt16(String fileName)
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

	public final PartialMatrix<Integer> ReadInt32(String fileName)
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
//ORIGINAL LINE: public PartialMatrix<uint> ReadUInt32(string fileName)
	public final PartialMatrix<Integer> ReadUInt32(String fileName)
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

	public final PartialMatrix<Float> ReadSingle(String fileName)
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

	public final PartialMatrix<Double> ReadDouble(String fileName)
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

	public final PartialMatrix<Short> ReadInt16(BinaryReader reader)
	{
		short[][] elements = PartialMatrix<Short>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadInt16();
			}
		}

		return new PartialMatrix<Short>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public PartialMatrix<ushort> ReadUInt16(BinaryReader reader)
	public final PartialMatrix<Short> ReadUInt16(BinaryReader reader)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort[][] elements = PartialMatrix<ushort>.CreateElements(_rowCount, _colCount);
		short[][] elements = PartialMatrix<Short>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadUInt16();
			}
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new PartialMatrix<ushort>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
		return new PartialMatrix<Short>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

	public final PartialMatrix<Integer> ReadInt32(BinaryReader reader)
	{
		int[][] elements = PartialMatrix<Integer>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadInt32();
			}
		}

		return new PartialMatrix<Integer>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public PartialMatrix<uint> ReadUInt32(BinaryReader reader)
	public final PartialMatrix<Integer> ReadUInt32(BinaryReader reader)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint[][] elements = PartialMatrix<uint>.CreateElements(_rowCount, _colCount);
		int[][] elements = PartialMatrix<Integer>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadUInt32();
			}
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return new PartialMatrix<uint>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
		return new PartialMatrix<Integer>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

	public final PartialMatrix<Float> ReadSingle(BinaryReader reader)
	{
		float[][] elements = PartialMatrix<Float>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadSingle();
			}
		}

		return new PartialMatrix<Float>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}

	public final PartialMatrix<Double> ReadDouble(BinaryReader reader)
	{
		double[][] elements = PartialMatrix<Double>.CreateElements(_rowCount, _colCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				elements[i][j] = reader.ReadDouble();
			}
		}

		return new PartialMatrix<Double>(_globalRowStartIndex, _globalRowStartIndex + _rowCount - 1, _globalColStartIndex, _globalColStartIndex + _colCount - 1, elements);
	}
}