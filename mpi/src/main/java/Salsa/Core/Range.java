package Salsa.Core;

import java.io.Serializable;

/**
 Represents a range within an 1D array.
*/
public final class Range implements Serializable {
	/** 
	 The inclusive ending index of the BlockPartition.
	 
	 <value>The rangeEnd index.</value>
	*/
	public int EndIndex;

	/** 
	 The total length of the BlockPartition.
	 
	 <value>The length.</value>
	*/
	public int Length;

	/** 
	 The inclusive starting index of the BlockPartition.
	 
	 <value>The rangeStart index.</value>
	*/
	public int StartIndex;

    /**
     * Initializes a new instance of the <see cref="BlockPartition"/> class.
     *
     * @param start The starting index of the Range.
     * @param end   The ending index of the Range.
     */
    public Range(int start, int end) {
        StartIndex = start;
        EndIndex = end;
        Length = end - start + 1;
    }

    private String privateStartSeqName;

    public String getStartSeqName() {
        return privateStartSeqName;
    }

    public void setStartSeqName(String value) {
        privateStartSeqName = value;
    }

    private String privateEndSeqName;

    public String getEndSeqName() {
        return privateEndSeqName;
    }

    public void setEndSeqName(String value) {
        privateEndSeqName = value;
    }

    public boolean Contains(int index) {
        return (index >= StartIndex && index <= EndIndex);
    }

    /**
     * Returns the fully qualified type name of this instance.
     *
     * @return A <see cref="T:System.String"/> containing a fully qualified type name.
     */
    @Override
    public String toString() {
        return String.format("(%1$s:%2$s)", (new Integer(StartIndex)).toString(), (new Integer(EndIndex)).toString());
    }

    /**
     * Returns true if there's an intersection of this range with the given range
     *
     * @param range The range to see if an intersection exists
     * @return True if an intersection exists or false otherwise
     */
    public boolean IntersectsWith(Range range) {
        Range lengthiest = range.Length >= Length ? range : this;
        Range other = range == lengthiest ? this : range;
        return lengthiest.Contains(other.StartIndex) || lengthiest.Contains(other.EndIndex);
    }

    /**
     * Gets the intersecting range assuming an intersection exists. Use <code>IntersectsWith(Range range)</code>
     * to check for an existing intersection
     *
     * @param range The range to intersect with
     * @return The intersection with the given range
     */
    public Range GetIntersectionWith(Range range) {
        int start = range.StartIndex >= StartIndex ? range.StartIndex : StartIndex;
        int end = range.EndIndex <= EndIndex ? range.EndIndex : EndIndex;
        if (start <= end) {
            return new Range(start, end);
        }
        throw new RuntimeException(String.format("Given range [%1$s, %2$s] does not intersect with this range [%3$s, %4$s]",
                range.StartIndex, range.EndIndex, StartIndex, EndIndex));
    }
}