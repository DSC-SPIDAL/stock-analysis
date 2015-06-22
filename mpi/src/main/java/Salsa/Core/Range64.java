package Salsa.Core;

/** 
 Represents a range within an 1D array.
*/
public final class Range64 implements Serializable
{
	/** 
	 The inclusive ending index of the BlockPartition.
	 
	 <value>The rangeEnd index.</value>
	*/
	public long EndIndex;

	/** 
	 The total length of the BlockPartition.
	 
	 <value>The length.</value>
	*/
	public long Length;

	/** 
	 The inclusive starting index of the BlockPartition.
	 
	 <value>The rangeStart index.</value>
	*/
	public long StartIndex;

	/** 
	 Initializes a new instance of the <see cref="BlockPartition"/> class.
	 
	 @param rangeStart The starting index of the Range.
	 @param rangeEnd The ending index of the Range.
	*/
	public Range64(long start, long end)
	{
		StartIndex = start;
		EndIndex = end;
		Length = end - start + 1L;
	}

	public boolean Contains(long index)
	{
		return (index >= StartIndex && index <= EndIndex);
	}

	/** 
	 Returns the fully qualified type name of this instance.
	 
	 @return 
	 A <see cref="T:System.String"/> containing a fully qualified type name.
	 
	*/
	@Override
	public String toString()
	{
		return String.format("(%1$s, %2$s)", (new Long(StartIndex)).toString(), (new Long(EndIndex)).toString());
	}
}