package Salsa.Core;

public final class RangePartitioner64
{
	public static Range64[] Partition(long length, long numPartitions)
	{
		return Partition(0, length, numPartitions);
	}

	public static Range64[] Partition(Range64 range, long numPartitions)
	{
		return Partition(range.StartIndex, range.getLength(), numPartitions);
	}

	public static Range64[] Partition(long startIndex, long length, long numPartitions)
	{
		if (numPartitions < 1)
		{
			throw new IllegalArgumentException("count Partitioning requires numPartitions to be greater than zero.");
		}

		if (length < 1)
		{
			throw new IllegalArgumentException("length Partitioning requires length to be greater than zero.");
		}

		if (length < numPartitions)
		{
			throw new UnsupportedOperationException("Partitioning cannot be performed when length is less than numPartitions requested.");
		}

		Range64[] ranges = new Range64[(int) numPartitions];
		long chunksize = length / numPartitions;
		long remainder = length % numPartitions;

		for (int i = 0; i < numPartitions; i++)
		{
			if (remainder > 0)
			{
				ranges[i] = new Range64(startIndex, startIndex + chunksize);
				startIndex += chunksize + 1;
				remainder--;
			}
			else
			{
				ranges[i] = new Range64(startIndex, startIndex + chunksize - 1);
				startIndex += chunksize;
			}
		}

		return ranges;
	}

	public static Range64[] PartitionByLength(long length, long maxPartitionLength)
	{
		return PartitionByLength(0, length, maxPartitionLength);
	}

	public static Range64[] PartitionByLength(Range64 range, long maxPartitionLength)
	{
		return PartitionByLength(range.StartIndex, range.getLength(), maxPartitionLength);
	}

	public static Range64[] PartitionByLength(long startIndex, long length, long maxPartitionLength)
	{
		if (maxPartitionLength < 1)
		{
			throw new IllegalArgumentException("maxPartitionLength Partitioning requires the maxPartitionLength to be greater than zero.");
		}
		if (length < 1)
		{
			throw new IllegalArgumentException("length Partitioning requires the length to be greater than zero.");
		}
		if (length < maxPartitionLength)
		{
			throw new IllegalArgumentException("length Partitioning requires the length to be greater than maxPartitionLength.");
		}

		long rangeCount = length / maxPartitionLength;
		long lastRangeLength = length % maxPartitionLength;

		if (lastRangeLength > 0)
		{
			rangeCount++;
		}
		else
		{
			lastRangeLength = maxPartitionLength;
		}

		Range64[] ranges = new Range64[(int) rangeCount];

		for (int i = 0; i < rangeCount; i++)
		{
			long start = i * maxPartitionLength;
			long end = (i == rangeCount - 1 ? start + lastRangeLength - 1 : start + maxPartitionLength - 1);
			ranges[i] = new Range64(start, end);
			start += maxPartitionLength;
		}

		return ranges;
	}
}