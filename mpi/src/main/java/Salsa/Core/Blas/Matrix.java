package Salsa.Core.Blas;

import Salsa.Core.*;

public class Matrix<T> implements Serializable
{
	private int _colCount;
	private T[][] _elements;
	private int _rowCount;

	public Matrix(T[][] values)
	{
		_elements = values;
		tangible.RefObject<Integer> tempRef__rowCount = new tangible.RefObject<Integer>(_rowCount);
		tangible.RefObject<Integer> tempRef__colCount = new tangible.RefObject<Integer>(_colCount);
		GetRowColumnCount(_elements, tempRef__rowCount, tempRef__colCount);
		_rowCount = tempRef__rowCount.argValue;
		_colCount = tempRef__colCount.argValue;
	}

	public Matrix(int rowCount, int columnCount)
	{
		_rowCount = rowCount;
		_colCount = columnCount;
		_elements = CreateElements(_rowCount, _colCount);
	}

	public final T getItem(int rowIndex, int columnIndex)
	{
		return _elements[rowIndex][columnIndex];
	}
	public final void setItem(int rowIndex, int columnIndex, T value)
	{
		_elements[rowIndex][columnIndex] = value;
	}

	public final int getRowCount()
	{
		return _rowCount;
	}

	public final int getColumnCount()
	{
		return _colCount;
	}

	public final boolean getIsSquare()
	{
		return (_rowCount == _colCount);
	}

	public final T[][] getElements()
	{
		return _elements;
	}

	public final Matrix<T> Transpose()
	{
		T[][] leftElements = CreateElements(_colCount, _rowCount);

		for (int i = 0; i < _rowCount; i++)
		{
			for (int j = 0; j < _colCount; j++)
			{
				leftElements[j][i] = _elements[i][j];
			}
		}

		return new this.Matrix<T>(leftElements);
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

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < Math.min(80, _rowCount); i++)
		{
			for (int j = 0; j < Math.min(80, _colCount); j++)
			{
				if (j > 0)
				{
					sb.append(",");
				}

				sb.append(String.format("%1$s", _elements[i][j]));
			}

			sb.append("\r\n");
		}

		return sb.toString();
	}

//C# TO JAVA CONVERTER TODO TASK: The following operator overload is not converted by C# to Java Converter:
	public static implicit operator T[][] (Matrix<T> matrix)
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

	public final T[] GetRowValues(int rowIndex)
	{
		T[] values = new T[_colCount];
		System.arraycopy(_elements[rowIndex], 0, values, 0, _colCount);
		return values;
	}

	public final void SetRowValues(int rowIndex, T[] values)
	{
		System.arraycopy(values, 0, _elements[rowIndex], 0, values.length);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Column Operations

	public final T[] GetColumnValues(int columnIndex)
	{
		T[] leftElements = new T[_rowCount];

		for (int i = 0; i < _rowCount; i++)
		{
			leftElements[i] = _elements[i][columnIndex];
		}

		return leftElements;
	}

	public final void SetColumnValues(int columnIndex, T[] values)
	{
		for (int i = 0; i < values.length; i++)
		{
			_elements[i][columnIndex] = values[i];
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Block Operations

	public final T[][] GetBlockValues(Block block)
	{
		return GetBlockValues(block.RowRange, block.ColumnRange);
	}

	public final T[][] GetBlockValues(Range rowRange, Range columnRange)
	{
		return GetBlockValues(rowRange.StartIndex, rowRange.EndIndex, columnRange.StartIndex, columnRange.EndIndex);
	}

	public final T[][] GetBlockValues(int rowStartIndex, int rowEndIndex, int columnStartIndex, int columnEndIndex)
	{
		T[][] leftElements = CreateElements(rowEndIndex - rowStartIndex + 1, columnEndIndex - columnStartIndex + 1);

		for (int i = rowStartIndex; i <= rowEndIndex; i++)
		{
			for (int j = columnStartIndex; j <= columnEndIndex; j++)
			{
				leftElements[i - rowStartIndex][j - columnStartIndex] = _elements[i][j];
			}
		}


		return leftElements;
	}

	public final void SetBlockValues(Block block, T[][] values)
	{
		SetBlockValues(block.RowRange, block.ColumnRange, values);
	}

	public final void SetBlockValues(Range rowRange, Range columnRange, T[][] values)
	{
		SetBlockValues(rowRange.StartIndex, rowRange.EndIndex, columnRange.StartIndex, columnRange.EndIndex, values);
	}

	public final void SetBlockValues(int rowStartIndex, int rowEndIndex, int columnStartIndex, int columnEndIndex, T[][] values)
	{
		for (int i = rowStartIndex; i <= rowEndIndex; i++)
		{
			for (int j = columnStartIndex; j <= columnEndIndex; j++)
			{
				_elements[i][j] = values[i - rowStartIndex][j - columnStartIndex];
			}
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}