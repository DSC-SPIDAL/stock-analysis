package Salsa.Core.Blas;

import Salsa.Core.*;

public class MatrixRow<T>
{
	private int _rowIndex;
	private T[] _values;

	public MatrixRow(int rowIndex, T[] values)
	{
		_rowIndex = rowIndex;
		_values = values;
	}

	public final int getRowIndex()
	{
		return _rowIndex;
	}

	public final T getItem(int colIndex)
	{
		return _values[colIndex];
	}
}