package Salsa.Core.Blas;

import Salsa.Core.*;

public class MatrixBinaryInt16RowReader implements IDisposable, Iterable<MatrixRow<Short>>
{
	private int _colCount;
	private int _rowCount;
	private BinaryReader _reader;

	public MatrixBinaryInt16RowReader(String fileName, int rowCount, int colCount)
	{
		_rowCount = rowCount;
		_colCount = colCount;
		_reader = new BinaryReader(File.OpenRead(fileName));
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region IDisposable Members

	public final void Dispose()
	{
		Dispose(true);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region IEnumerable<MatrixRow<short>> Members

	public final java.util.Iterator<MatrixRow<Short>> GetEnumerator()
	{
		for (int i = 0; i < _rowCount; i++)
		{
			short[] values = new short[_colCount];

			for (int j = 0; j < _colCount; j++)
			{
				values[j] = _reader.ReadInt16();
			}

//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
			yield return new MatrixRow<Short>(i, values);
		}
	}

	public final java.util.Iterator GetEnumerator()
	{
		return GetEnumerator();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	public void Close()
	{
		Dispose(true);
	}

	protected void Dispose(boolean disposing)
	{
		if (disposing)
		{
			if (_reader != null)
			{
				_reader.Dispose();
			}
		}

		_reader = null;
	}
}