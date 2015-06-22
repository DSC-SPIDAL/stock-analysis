package Salsa.Core.Bio.Algorithms;

import Salsa.Core.*;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#region NAligner Copyright

/*
 * NAligner
 * C# port of JAligner API, http://jaligner.sourceforge.net
 * 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#endregion


/**  Holds the output of a pairwise sequences alignment. 
*/
public class PairwiseAlignment implements Serializable
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	/**  Gap character
	*/
	public static final char GAP = '-';

	/**  Default name for sequence #1
	*/
	private static final String DEFAULT_SEQUENCE1_NAME = "swg_1";

	/**  Default name for sequence #2
	*/
	private static final String DEFAULT_SEQUENCE2_NAME = "swg_2";

	/**  Gap extend cost
	*/
	private float extend;

	/**  Count of gap locations
	*/
	private int gaps;

	/**  Count of identical locations
	*/
	private int identity;

	/**  Markup line
	*/
	private char[] markupLine;

	/**  Scoring matrix
	*/
	private String matrix;

	/**  Name of sequence #1
	*/
	private String name1;

	/**  Name of sequence #2
	*/
	private String name2;

	/**  Gap open cost
	*/
	private float open;

	/**  Alignment score
	*/
	private float score;

	/**  Aligned sequence #1
	*/
	private char[] sequence1;

	/**  Aligned sequence #2
	*/
	private char[] sequence2;

	/**  Count of similar locations
	*/
	private int similarity;

	/**  Alignment start location in sequence #1
	*/
	private int start1;

	/**  Alignment start location in sequence #2
	*/
	private int start2;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** @return  Returns the matrix. 
	*/
	public final String getMatrix()
	{
		return matrix;
	}
	public final void setMatrix(String value)
	{
		matrix = value;
	}

	/** @return  Returns the name1. 
	*/
	public final String getName1()
	{
		return name1 == null || name1.length() == 0 ? DEFAULT_SEQUENCE1_NAME : name1;
	}
	public final void setName1(String value)
	{
		name1 = value;
	}

	/** @return  Returns the name2. 
	*/
	public final String getName2()
	{
		return name2 == null || name2.length() == 0 ? DEFAULT_SEQUENCE2_NAME : name2;
	}

	public final void setName2(String value)
	{
		name2 = value;
	}

	/** @return  Returns the open. 
	*/
	public final float getGapOpenPenalty()
	{
		return open;
	}
	public final void setGapOpenPenalty(float value)
	{
		open = value;
	}

	/** @return  Returns the extend. 
	*/
	public final float getGapExtendPenalty()
	{
		return extend;
	}
	public final void setGapExtendPenalty(float value)
	{
		extend = value;
	}

	/** @return  Returns the score. 
	*/
	public final float getScore()
	{
		return score;
	}
	public final void setScore(float value)
	{
		score = value;
	}

	/** @return  Returns the sequence1. 
	*/
	public final char[] getSequence1()
	{
		return sequence1;
	}
	public final void setSequence1(char[] value)
	{
		sequence1 = value;
	}

	/** @return  Returns the sequence2. 
	*/
	public final char[] getSequence2()
	{
		return sequence2;
	}
	public final void setSequence2(char[] value)
	{
		sequence2 = value;
	}

	/** @return  Returns the start1. 
	*/
	public final int getStart1()
	{
		return start1;
	}
	public final void setStart1(int value)
	{
		start1 = value;
	}

	/** @return  Returns the start2. 
	*/
	public final int getStart2()
	{
		return start2;
	}
	public final void setStart2(int value)
	{
		start2 = value;
	}

	/** @return  Returns the gaps. 
	*/
	public final int getGaps()
	{
		return gaps;
	}
	public final void setGaps(int value)
	{
		gaps = value;
	}

	/** @return  Returns the markupLine. 
	*/
	public final char[] getMarkupLine()
	{
		return markupLine;
	}
	public final void setMarkupLine(char[] value)
	{
		markupLine = value;
	}

	/** @return  Returns the identity. 
	*/
	public final int getIdentity()
	{
		return identity;
	}
	public final void setIdentity(int value)
	{
		identity = value;
	}

	/** @return  Returns the similarity. 
	*/
	public final int getSimilarity()
	{
		return similarity;
	}
	public final void setSimilarity(int value)
	{
		similarity = value;
	}

	public final float getPercentIdentity()
	{
		return (getIdentity() / (getSequence1().length * 1.0f));
	}

	public final float getPercentSimilarity()
	{
		return (getSimilarity() / (getSequence1().length * 1.0f));
	}

	public final float getPercentGaps()
	{
		return (getGaps() / (getSequence1().length * 1.0f));
	}

	/**  Returns a summary for alignment
	*/
	public final String getSummary()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("Sequence #1: " + getName1());
		buffer.append("\r\n");
		buffer.append("Sequence #2: " + getName2());
		buffer.append("\r\n");
		buffer.append("Matrix: " + getMatrix());
		buffer.append("\r\n");
		buffer.append("Gap open: " + open);
		buffer.append("\r\n");
		buffer.append("Gap extend: " + extend);
		buffer.append("\r\n");
		buffer.append("Start1: " + start1);
		buffer.append("\r\n");
		buffer.append("Start2: " + start2);
		buffer.append("\r\n");
		buffer.append("Length: " + getSequence1().length);
		buffer.append("\r\n");
		buffer.append(String.format("Identity: %1$s/%2$s (%3$s) (%4$s)", identity, getSequence1().length, String.format("%0.5f", getPercentIdentity()), String.format("%0.5f", 1.0f - getPercentIdentity())));
		buffer.append("\r\n");
		buffer.append(String.format("Similarity: %1$s/%2$s (%3$s) (%4$s)", similarity, getSequence1().length, String.format("%0.5f", getPercentSimilarity()), String.format("%0.5f", 1.0f - getPercentSimilarity())));
		buffer.append("\r\n");
		buffer.append(String.format("JunkesCantor: %1$s", String.format("%0.5f", ComputeJukesCantorDistance())));
		buffer.append("\r\n");
		buffer.append(String.format("Kimera2: %1$s", String.format("%0.5f", ComputeKimuraDistance())));
		buffer.append("\r\n");
		buffer.append("Gaps: " + gaps + "/" + getSequence1().length + " (" + String.format("%0.5f", getPercentGaps()) + ")");
		buffer.append("\r\n");
		buffer.append("Score: " + String.format("%0.2f", score));
		buffer.append("\r\n");
		buffer.append("Score/Length: " + String.format("%0.3f", score / (sequence1.length * 1.0)));
		buffer.append("\r\n");
		buffer.append(String.format(">Name=%1$s Start=%2$s", getName1(), getStart1()));
		buffer.append("\r\n");
		buffer.append(getSequence1());
		buffer.append("\r\n");
		buffer.append(String.format(">Name=%1$s Start=%2$s", getName2(), getStart2()));
		buffer.append("\r\n");
		buffer.append(getSequence2());
		buffer.append("\r\n");
		buffer.append(getMarkupLine());
		buffer.append("\r\n");
		return buffer.toString();
	}

	public final double ComputeKimuraDistance()
	{
		double length = 0;
		double gapCount = 0;
		double transitionCount = 0; // P = A -> G | G -> A | C -> T | T -> C
		double transversionCount = 0; // Q = A -> C | A -> T | C -> A | C -> G | T -> A  | T -> G | G -> T | G -> C

		for (int i = 0; i < sequence1.length; i++)
		{
			length++;
			char nt1 = getSequence1()[i]; //nucleotide 1
			char nt2 = getSequence2()[i]; //nucelotide 2

			if (nt1 != nt2)
			{
				// Don't consider gaps at all in this computation;
				if (nt1 == GAP || nt2 == GAP)
				{
					gapCount++;
				}
				else if ((nt1 == 'A' && nt2 == 'G') || (nt1 == 'G' && nt2 == 'A') || (nt1 == 'C' && nt2 == 'T') || (nt1 == 'T' && nt2 == 'C'))
				{
					transitionCount++;
				}
				else
				{
					transversionCount++;
				}
			}
		}

		double P = transitionCount / (length - gapCount);
		double Q = transversionCount / (length - gapCount);

		double artificialDistance = 10;
		if (1.0 - (2.0 * P + Q) <= Double.MIN_VALUE)
		{
			PrintArtificialDistanceAlignments(getSequence1(), getSequence2(), artificialDistance, "Kimura2");
			return artificialDistance;
		}
		if (1.0 - (2.0 * Q) <= Double.MIN_VALUE)
		{
			PrintArtificialDistanceAlignments(getSequence1(), getSequence2(), artificialDistance, "Kimura2");
			return artificialDistance;
		}

		return (-0.5 * Math.log(1.0 - 2.0 * P - Q) - 0.25 * Math.log(1.0 - 2.0 * Q));
	}

	private static void PrintArtificialDistanceAlignments(char[] alignedSeqA, char[] alignedSeqB, double aDistance, String distanceType)
	{
		System.out.println("*******************************************");
		System.out.println(alignedSeqB);
		System.out.println(alignedSeqB);
		System.out.println("Artificial " + distanceType + "Distance: " + aDistance);
		System.out.println("*******************************************");
	}

	public final double ComputeJukesCantorDistance()
	{
		double length = 0;
		double gapCount = 0;
		double differenceCount = 0;

		for (int i = 0; i < sequence1.length; i++)
		{
			length++;
			char nt1 = getSequence1()[i]; //nucleotide 1
			char nt2 = getSequence2()[i]; //nucelotide 2

			if (nt1 != nt2)
			{
				// Don't consider gaps at all in this computation;
				if (nt1 == GAP || nt2 == GAP)
				{
					gapCount++;
				}
				else
				{
					differenceCount++;
				}
			}
		}

		double d = 1.0 - ((4.0 / 3.0) * (differenceCount / (length - gapCount)));

		if (d <= Double.MIN_VALUE)
		{
			throw new RuntimeException("Jukes and Cantor Distance Undefined - Log(Zero)");
		}

		return (-0.75 * Math.log(d));
	}
}