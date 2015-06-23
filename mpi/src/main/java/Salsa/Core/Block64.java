package Salsa.Core;

import java.io.Serializable;

/**
 Represents a block within a 2D array
*/
public class Block64 implements Serializable
{
	public Range64 ColumnRange;
	public Range64 RowRange;

	public Block64(Range64 rowRange, Range64 colRange)
	{
		RowRange = rowRange;
		ColumnRange = colRange;
	}

	/** 
	 Transposes the row and column ranges
	 
	 @return 
	*/
	public final Block64 Transpose()
	{
		Block64 b = new Block64(ColumnRange, RowRange);
		return b;
	}

	@Override
	public String toString()
	{
		return String.format("[%1$s %2$s]", RowRange, ColumnRange);
	}
}