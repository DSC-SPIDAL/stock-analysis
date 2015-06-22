package Salsa.Core.Blas;

import Salsa.Core.*;

import java.io.Serializable;

public class PartialMatrix<T> implements Serializable
{
	private int _colCount;
	private T[][] _elements;
	private int _globalColStartIndex;
	private int _globalRowStartIndex;
	private int _rowCount;

	public PartialMatrix(Range globalRowRange, Range globalColumnRange)
	{
		this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
	}

	public PartialMatrix(Range globalRowRange, int globalColumnStartIndex, int globalColumnEndIndex)
	{
		this(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnStartIndex, globalColumnEndIndex);
	}

	public PartialMatrix(int globalRowStartIndex, int globalRowEndIndex, Range globalColumnRange)
	{
		this(globalRowStartIndex, globalRowEndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
	}

	/** 
	 Creates a Partial Matrix
	 
	 @param rowStartIndex Inclusive row starting index
	 @param rowEndIndex Inclusive row ending index
	 @param columnStartIndex Inclusive column starting index
	 @param columnEndIndex Inclusive column ending index
	*/
	public PartialMatrix(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex)
	{
		_globalRowStartIndex = globalRowStartIndex;
		_globalColStartIndex = globalColumnStartIndex;
		_rowCount = globalRowEndIndex - globalRowStartIndex + 1;
		_colCount = globalColumnEndIndex - globalColumnStartIndex + 1;
		_elements = CreateElements(_rowCount, _colCount);
	}

	public PartialMatrix(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex, T[][] values)
	{
		_globalRowStartIndex = globalRowStartIndex;
		_globalColStartIndex = globalColumnStartIndex;
		_rowCount = globalRowEndIndex - globalRowStartIndex + 1;
		_colCount = globalColumnEndIndex - globalColumnStartIndex + 1;
		_elements = values;
	}

	public final T getItem(int globalRowIndex, int globalColumnIndex)
	{
		return _elements[globalRowIndex - _globalRowStartIndex][globalColumnIndex - _globalColStartIndex];
	}
	public final void setItem(int globalRowIndex, int globalColumnIndex, T value)
	{
		_elements[globalRowIndex - _globalRowStartIndex][globalColumnIndex - _globalColStartIndex] = value;
	}

	public final int getRowCount()
	{
		return _rowCount;
	}

	public final int getColumnCount()
	{
		return _colCount;
	}

	public final int getGlobalRowStartIndex()
	{
		return _globalRowStartIndex;
	}

	public final int getGlobalRowEndIndex()
	{
		return _globalRowStartIndex + _rowCount - 1;
	}

	public final int getGlobalColumnStartIndex()
	{
		return _globalColStartIndex;
	}

	public final int getGlobalColumnEndIndex()
	{
		return _globalColStartIndex + _colCount - 1;
	}

	public final boolean getIsSquare()
	{
		return (_rowCount == _colCount);
	}

	public final T[][] getElements()
	{
		return _elements;
	}

	public final PartialMatrix<T> Transpose()
	{
		T[][] leftElements = CreateElements(_colCount, _rowCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				leftElements[j][i] = _elements[i][j];
			}
		}

		return new this.PartialMatrix<T>(getGlobalColumnStartIndex(), getGlobalColumnEndIndex(), getGlobalRowStartIndex(), getGlobalRowEndIndex(), leftElements);
	}

	public final void SetAllValues(T value)
	{
		for (int i = 0; i < getRowCount(); i++)
		{
			for (int j = 0; j < getColumnCount(); j++)
			{
				_elements[i][j] = value;
			}
		}
	}

	public final void CopyTo(Matrix<T> matrix)
	{
		matrix.SetBlockValues(getGlobalRowStartIndex(), getGlobalRowEndIndex(), getGlobalColumnStartIndex(), getGlobalColumnEndIndex(), _elements);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < Math.min(20, _rowCount); i++)
		{
			for (int j = 0; j < Math.min(20, _colCount); j++)
			{
				if (j > 0)
				{
					sb.append(", ");
				}

				sb.append(String.format("%1$4s", _elements[i][j]));

				if (i == 20 - 1)
				{
					sb.append("...");
				}
			}

			sb.append("\r\n");
		}

		return sb.toString();
	}

//C# TO JAVA CONVERTER TODO TASK: The following operator overload is not converted by C# to Java Converter:
	public static implicit operator T[][] (PartialMatrix<T> matrix)
	{
		return matrix.getElements();
	}

	public static T[][] CreateElements(int rowCount, int columnCount)
	{
		T[][] elements = new T[rowCount][];

		for (int i = 0; i < rowCount; i++)
		{
			elements[i] = new T[columnCount];
		}

		return elements;
	}

	public static void GetRowColumnCount(T[][] data, tangible.RefObject<Integer> rows, tangible.RefObject<Integer> columns)
	{
		rows.argValue = data.length;
		columns.argValue = (rows.argValue == 0) ? 0 : data[0].length;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Row Operations

	public final T[] GetRowValues(int globalRowIndex)
	{
		globalRowIndex = globalRowIndex - _globalRowStartIndex;
		T[] values = new T[_colCount];
		System.arraycopy(_elements[globalRowIndex], 0, values, 0, _colCount);
		return values;
	}

	public final void SetRowValues(int globalRowIndex, T[] values)
	{
		globalRowIndex = globalRowIndex - _globalRowStartIndex;
		System.arraycopy(values, 0, _elements[globalRowIndex], 0, values.length);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Column Operations

	public final T[] GetColumnValues(int globalColumnIndex)
	{
		globalColumnIndex = globalColumnIndex - _globalColStartIndex;
		T[] leftElements = new T[_rowCount];

		for (int i = 0; i < _rowCount; i++)
		{
			leftElements[i] = _elements[i][globalColumnIndex];
		}

		return leftElements;
	}

	public final void SetColumnValues(int globalColumnIndex, T[] values)
	{
		globalColumnIndex = globalColumnIndex - _globalColStartIndex;
		for (int i = 0; i < values.length; i++)
		{
			_elements[i][globalColumnIndex] = values[i];
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Block Operations

	public final T[][] GetBlockValues(Block globalBlock)
	{
		return GetBlockValues(globalBlock.RowRange, globalBlock.ColumnRange);
	}

	public final T[][] GetBlockValues(Range globalRowRange, Range globalColumnRange)
	{
		return GetBlockValues(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
	}

	public final T[][] GetBlockValues(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex)
	{
		globalRowStartIndex = globalRowStartIndex - _globalRowStartIndex;
		globalRowEndIndex = globalRowEndIndex - _globalRowStartIndex;
		globalColumnStartIndex = globalColumnStartIndex - _globalColStartIndex;
		globalColumnEndIndex = globalColumnEndIndex - _globalColStartIndex;

		T[][] leftElements = CreateElements(globalRowEndIndex - globalRowStartIndex + 1, globalColumnEndIndex - globalColumnStartIndex + 1);

		for (int i = globalRowStartIndex; i <= globalRowEndIndex; i++)
		{
			for (int j = globalColumnStartIndex; j <= globalColumnEndIndex; j++)
			{
				leftElements[i - globalRowStartIndex][j - globalColumnStartIndex] = _elements[i][j];
			}
		}


		return leftElements;
	}


	/** 
	 Gets the block at the given coordinate but transforms the block before returning it.  This is an optimization
	 method.
	 
	 @param block The block to retrieve
	 @return 
	*/
	public final T[][] GetTransposedBlock(Block globalBlock)
	{
		return GetTransposedBlock(globalBlock.RowRange, globalBlock.ColumnRange);
	}

	/** 
	 Gets the block at the given coordinate but transforms the block before returning it.  This is an optimization
	 method.
	 
	 @param rowRange The range specifiying the rows to retrieve
	 @param columnRange The range specifiying the columns to retrieve
	 @return 
	*/
	public final T[][] GetTransposedBlock(Range globalRowRange, Range globalColumnRange)
	{
		return GetTransposedBlock(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex);
	}

	/** 
	 Gets the block at the given coordinate but transforms the block before returning it.  This is an optimization
	 method.
	 
	 @param rowStartIndex Inclusive starting row index.
	 @param rowEndIndex Inclusive ending row index.
	 @param columnStartIndex Inclusive starting column index.
	 @param columnEndIndex Inclusive ending column index.
	 @return 
	*/
	public final T[][] GetTransposedBlock(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex)
	{
		globalRowStartIndex = globalRowStartIndex - _globalRowStartIndex;
		globalRowEndIndex = globalRowEndIndex - _globalRowStartIndex;
		globalColumnStartIndex = globalColumnStartIndex - _globalColStartIndex;
		globalColumnEndIndex = globalColumnEndIndex - _globalColStartIndex;

		T[][] leftElements = CreateElements(globalColumnEndIndex - globalColumnStartIndex + 1, globalRowEndIndex - globalRowStartIndex + 1);

		for (int i = globalRowStartIndex; i <= globalRowEndIndex; i++)
		{
			for (int j = globalColumnStartIndex; j <= globalColumnEndIndex; j++)
			{
				leftElements[j - globalColumnStartIndex][i - globalRowStartIndex] = _elements[i][j];
			}
		}

		return leftElements;
	}

	public final void SetBlockValues(Block globalBlock, T[][] blockValues)
	{
		SetBlockValues(globalBlock.RowRange, globalBlock.ColumnRange, blockValues);
	}

	public final void SetBlockValues(Range globalRowRange, Range globalColumnRange, T[][] blockValues)
	{
		SetBlockValues(globalRowRange.StartIndex, globalRowRange.EndIndex, globalColumnRange.StartIndex, globalColumnRange.EndIndex, blockValues);
	}

	public final void SetBlockValues(int globalRowStartIndex, int globalRowEndIndex, int globalColumnStartIndex, int globalColumnEndIndex, T[][] blockValues)
	{
		globalRowStartIndex = globalRowStartIndex - _globalRowStartIndex;
		globalRowEndIndex = globalRowEndIndex - _globalRowStartIndex;
		globalColumnStartIndex = globalColumnStartIndex - _globalColStartIndex;
		globalColumnEndIndex = globalColumnEndIndex - _globalColStartIndex;

		for (int i = globalRowStartIndex; i <= globalRowEndIndex; i++)
		{
			for (int j = globalColumnStartIndex; j <= globalColumnEndIndex; j++)
			{
				try
				{
					_elements[i][j] = blockValues[i - globalRowStartIndex][j - globalColumnStartIndex];
				}
				catch (RuntimeException ex)
				{
					System.out.printf("%1$s,%2$s", i, j, "\r\n");
					throw (ex);
				}
			}
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}