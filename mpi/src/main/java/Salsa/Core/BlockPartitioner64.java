package Salsa.Core;

public final class BlockPartitioner64
{
	public static Block64[][] Partition(long rowCount, long columnCount, long numRowBlocks, long numColumnBlocks)
	{
		return Partition(0, 0, rowCount, columnCount, numRowBlocks, numColumnBlocks);
	}

	public static Block64[][] Partition(long rowStartIndex, long columnStartIndex, long rowCount, long columnCount, long numRowBlocks, long numColumnBlocks)
	{
		Range64[] rowRanges = RangePartitioner64.Partition(rowStartIndex, rowCount, numRowBlocks);
		Range64[] colRanges = RangePartitioner64.Partition(columnStartIndex, columnCount, numColumnBlocks);
		Block64[][] result = new Block64[(int) numRowBlocks][];

		for (int i = 0; i < rowRanges.length; i++)
		{
			result[i] = new Block64[(int) numColumnBlocks];

			for (int j = 0; j < colRanges.length; j++)
			{
				result[i][j] = new Block64(rowRanges[i], colRanges[j]);
			}
		}

		return result;
	}

	public static Block64[][] PartitionByLength(long rowCount, long columnCount, long maxRowPartitionLength, long maxColumnPartitionLength)
	{
		return PartitionByLength(0, 0, rowCount, columnCount, maxRowPartitionLength, maxColumnPartitionLength);
	}

	public static Block64[][] PartitionByLength(long rowStartIndex, long columnStartIndex, long rowCount, long columnCount, long maxRowPartitionLength, long maxColumnPartitionLength)
	{
		Range64[] rowRanges = RangePartitioner64.PartitionByLength(rowStartIndex, rowCount, maxRowPartitionLength);
		Range64[] colRanges = RangePartitioner64.PartitionByLength(columnStartIndex, columnCount, maxColumnPartitionLength);
		Block64[][] result = new Block64[rowRanges.length][];

		for (int i = 0; i < rowRanges.length; i++)
		{
			result[i] = new Block64[colRanges.length];

			for (int j = 0; j < colRanges.length; j++)
			{
				result[i][j] = new Block64(rowRanges[i], colRanges[j]);
			}
		}

		return result;
	}
}