package Salsa.Core;

import java.io.Serializable;

/**
 Represents a block within a 2D array
*/
public class Block implements Serializable
{
	public Range ColumnRange;
	public Range RowRange;
	private int _columnBlockNumber;
	private int _rowBlockNumber;

	/** 
	 Initializes a new instance of the <see cref="BlockPartition"/> class.
	 
	 @param rowRange The row range.
	 @param colRange The col range.
	*/
	public Block(Range rowRange, Range colRange)
	{
		RowRange = rowRange;
		ColumnRange = colRange;
//        System.out.println("Block rowrange: " + rowRange + " column range: " + colRange);
    }

	private boolean privateIsDiagonal;
	public final boolean getIsDiagonal()
	{
		return privateIsDiagonal;
	}
	public final void setIsDiagonal(boolean value)
	{
		privateIsDiagonal = value;
	}

	public final int getRowBlockNumber()
	{
		return _rowBlockNumber;
	}
	public final void setRowBlockNumber(int value)
	{
		_rowBlockNumber = value;
	}

	public final int getColumnBlockNumber()
	{
		return _columnBlockNumber;
	}
	public final void setColumnBlockNumber(int value)
	{
		_columnBlockNumber = value;
	}

	public final void SetIndex(int rowBlockNumber, int columnBlockNumber)
	{
		_rowBlockNumber = rowBlockNumber;
		_columnBlockNumber = columnBlockNumber;
	}

	/** 
	 Transposes the row and column ranges
	 
	 @return 
	*/
	public final Block Transpose()
	{
		Block b = new Block(ColumnRange, RowRange);
		return b;
	}

	@Override
	public String toString()
	{
		return String.format("[%1$s %2$s]", RowRange, ColumnRange);
	}
}