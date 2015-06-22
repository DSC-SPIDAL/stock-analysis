package Salsa.Core.Bio.Algorithms.SmithWaterman;

import Salsa.Core.*;
import Salsa.Core.Bio.Algorithms.*;

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


/**  An implementation of the Smith-Waterman algorithm with
 Gotoh's improvement for biological local pairwise sequence alignment. 
*/
public class SmithWatermanGotoh
{
	private AlignmentType _alignmentType = AlignmentType.values()[0];
	private float _gapExtensionPenalty;
	private float _gapOpenPenalty;
	private ScoringMatrix _scoringMatrix;

	public SmithWatermanGotoh(AlignmentType alignmentType)
	{
		_alignmentType = alignmentType;
	}

	/**  Aligns two sequences by Smith-Waterman algorithm
	 @param s1 sequene #1 
	 @param s2 sequene #2 
	 @param matrix scoring matrix 
	 @param o open gap penalty 
	 @param e extend gap penalty 
	 @return  alignment object contains the two aligned sequences, 
	 the alignment score and alignment statistics
	 {@link Sequence}
	 {@link Matrix}
	*/
	public final PairwiseAlignment Align(Sequence s1, Sequence s2, ScoringMatrix matrix, float gapOpenPenalty, float gapExtensionPenalty)
	{
		if (s1 == null)
		{
			System.out.println("Error: S1 is null");
			throw new SmithWatermanGotohException("S1 is null");
		}

		if (s2 == null)
		{
			System.out.println("Error: S2 is null");
			throw new SmithWatermanGotohException("S2 is null");
		}

		int m = s1.getLength() + 1;
		int n = s2.getLength() + 1;

		Directions[] pointers = new Directions[m * n];

		// Initializes the boundaries of the traceback matrix to STOP.
		for (int i = 0, k = 0; i < m; i++, k += n)
		{
			pointers[k] = Directions.STOP;
		}

		for (int j = 1; j < n; j++)
		{
			pointers[j] = Directions.STOP;
		}

		short[] sizesOfVerticalGaps = new short[m * n];
		short[] sizesOfHorizontalGaps = new short[m * n];

		for (int i = 0, k = 0; i < m; i++, k += n)
		{
			for (int j = 0; j < n; j++)
			{
				sizesOfVerticalGaps[k + j] = sizesOfHorizontalGaps[k + j] = 1;
			}
		}

		_scoringMatrix = matrix;
		_gapOpenPenalty = gapOpenPenalty;
		_gapExtensionPenalty = gapExtensionPenalty;


		Cell cell = Construct(s1, s2, pointers, sizesOfVerticalGaps, sizesOfHorizontalGaps);
		PairwiseAlignment alignment = Traceback(s1.getResidues(), s2.getResidues(), pointers, cell, sizesOfVerticalGaps, sizesOfHorizontalGaps);

		alignment.setName1(s1.getLabel());
		alignment.setName2(s2.getLabel());
		alignment.setGapOpenPenalty(_gapOpenPenalty);
		alignment.setGapExtendPenalty(_gapExtensionPenalty);
		alignment.setMatrix(matrix.getName());

		return alignment;
	}

	/**  Constructs directions matrix for the traceback 
	 @param s1 sequence #1 
	 @param s2 sequence #2 
	 @param matrix scoring matrix 
	 @param o open gap penalty 
	 @param e extend gap penalty 
	 @return  The cell where the traceback starts. 
	*/
	private Cell Construct(Sequence s1, Sequence s2, Directions[] pointers, short[] sizesOfVerticalGaps, short[] sizesOfHorizontalGaps)
	{
		int m = s1.getLength() + 1;
		int n = s2.getLength() + 1;

		float f; // score of alignment x1...xi to y1...yi if xi aligns to yi
		float[] g = new float[n]; // score if xi aligns to a gap after yi
		float h; // score if yi aligns to a gap after xi
		float[] v = new float[n]; // best score of alignment x1...xi to y1...yi
		float vDiagonal;

		g[0] = Float.NEGATIVE_INFINITY;
		h = Float.NEGATIVE_INFINITY;
		v[0] = 0;

		for (int j = 1; j < n; j++)
		{
			g[j] = Float.NEGATIVE_INFINITY;
			v[j] = 0;
		}

		float[][] scores = _scoringMatrix.getScores();
		float similarityScore, g1, g2, h1, h2;


		Cell cell = new Cell();

		for (int i = 1, k = n; i < m; i++, k += n)
		{
			h = Float.NEGATIVE_INFINITY;
			vDiagonal = v[0];

			for (int j = 1, l = k + 1; j < n; j++, l++)
			{
				similarityScore = scores[s1.getItem(i - 1)][s2.getItem(j - 1)];

				// Fill the matrices
				f = vDiagonal + similarityScore;

				g1 = g[j] - _gapExtensionPenalty;
				g2 = v[j] - _gapOpenPenalty;

				if (g1 > g2)
				{
					// Gap extension penalty
					g[j] = g1;
					sizesOfVerticalGaps[l] = (short)(sizesOfVerticalGaps[l - n] + 1);
				}
				else
				{
					// Gap open penalty
					g[j] = g2;
				}

				h1 = h - _gapExtensionPenalty;
				h2 = v[j - 1] - _gapOpenPenalty;

				if (h1 > h2)
				{
					// Gap extension penalty
					h = h1;
					sizesOfHorizontalGaps[l] = (short)(sizesOfHorizontalGaps[l - 1] + 1);
				}
				else
				{
					// Gap open penalty
					h = h2;
				}

				vDiagonal = v[j];
				v[j] = Max(f, g[j], h, 0);

				// Determine the traceback direction
				if (v[j] == 0)
				{
					pointers[l] = Directions.STOP;
				}
				else if (v[j] == f)
				{
					pointers[l] = Directions.DIAGONAL;
				}
				else if (v[j] == g[j])
				{
					pointers[l] = Directions.UP;
				}
				else
				{
					pointers[l] = Directions.LEFT;
				}

				// Set the traceback start at the current cell i, j and score
				if (v[j] > cell.getScore())
				{
					cell.Set(i, j, v[j]);
				}
			}
		}

		return cell;
	}

	/**  Returns the alignment of two sequences based on the passed array of pointers
	 @param s1 sequence #1 
	 @param s2 sequence #2 
	 @param m scoring matrix 
	 @param cell The cell where the traceback starts. 
	 @return  <see cref="Alignment"/> with the two aligned sequences and alignment score. 
	 {@link Cell}
	 {@link Alignment}
	*/
	private PairwiseAlignment Traceback(String s1, String s2, Directions[] pointers, Cell cell, short[] sizesOfVerticalGaps, short[] sizesOfHorizontalGaps)
	{
		int n = s2.length() + 1;

		PairwiseAlignment alignment = new PairwiseAlignment();
		alignment.setScore(cell.getScore());

		float[][] scores = _scoringMatrix.getScores();

		int maxlen = s1.length() + s2.length(); // maximum length after the aligned sequences

		char[] reversed1 = new char[maxlen]; // reversed sequence #1
		char[] reversed2 = new char[maxlen]; // reversed sequence #2
		char[] reversed3 = new char[maxlen]; // reversed markup


		int len1 = 0; // length of sequence #1 after alignment
		int len2 = 0; // length of sequence #2 after alignment
		int len3 = 0; // length of the markup line

		int identity = 0; // count of identitcal pairs
		int similarity = 0; // count of similar pairs
		int gaps = 0; // count of gaps

		char c1, c2;

		int i = cell.getRow(); // traceback start row
		int j = cell.getColumn(); // traceback start col
		int k = i * n;

		boolean stillGoing = true; // traceback flag: true -> continue & false -> stop

		while (stillGoing)
		{
			switch (pointers[k + j])
			{
				case UP:
					for (int l = 0, len = sizesOfVerticalGaps[k + j]; l < len; l++)
					{
						reversed1[len1++] = s1.charAt(--i);
						reversed2[len2++] = PairwiseAlignment.GAP;
						reversed3[len3++] = Markups.GAP;
						k -= n;
						gaps++;
					}
					break;

				case DIAGONAL:
					c1 = s1.charAt(--i);
					c2 = s2.charAt(--j);
					k -= n;
					reversed1[len1++] = c1;
					reversed2[len2++] = c2;

					if (c1 == c2)
					{
						reversed3[len3++] = Markups.IDENTITY;
						identity++;
						similarity++;
					}
					else if (scores[c1][c2] > 0)
					{
						reversed3[len3++] = Markups.SIMILARITY;
						similarity++;
					}
					else
					{
						reversed3[len3++] = Markups.MISMATCH;
					}
					break;

				case LEFT:
					for (int l = 0, len = sizesOfHorizontalGaps[k + j]; l < len; l++)
					{
						reversed1[len1++] = PairwiseAlignment.GAP;
						reversed2[len2++] = s2.charAt(--j);
						reversed3[len3++] = Markups.GAP;
						gaps++;
					}
					break;

				case STOP:
					stillGoing = false;
					break;
			}
		}


		alignment.setMatrix(_scoringMatrix.getName());
		alignment.setGaps(gaps);
		alignment.setGapOpenPenalty(_gapOpenPenalty);
		alignment.setGapExtendPenalty(_gapExtensionPenalty);
		alignment.setScore(cell.getScore());
		alignment.setSequence1(Reverse(reversed1, len1));
		alignment.setMarkupLine(Reverse(reversed3, len3));
		alignment.setSequence2(Reverse(reversed2, len2));
		alignment.setStart1(i);
		alignment.setStart2(j);
		alignment.setIdentity(identity);
		alignment.setSimilarity(similarity);

		return alignment;
	}

	/**  
	 Returns the maximum of 4 float numbers.
	*/
	private static float Max(float a, float b, float c, float d)
	{
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	/**  
	 Reverses an array of chars
	*/
	private static char[] Reverse(char[] a, int len)
	{
		// TODO: replace this method by System.Array.Reverse
		char[] b = new char[len];

		for (int i = len - 1, j = 0; i >= 0; i--, j++)
		{
			b[j] = a[i];
		}
		return b;
	}
}