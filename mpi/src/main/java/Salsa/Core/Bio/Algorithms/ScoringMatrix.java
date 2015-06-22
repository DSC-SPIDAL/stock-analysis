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


public class ScoringMatrix implements Serializable
{
	private static final char COMMENT_STARTER = '#';
	private static final int SIZE = 127;
	private static String[] _matrixNames;
	private String _matrixName;
	private float[][] _scores;

	static
	{
		_matrixNames = new String[] {"BLOSUM100", "BLOSUM30", "BLOSUM35", "BLOSUM40", "BLOSUM45", "BLOSUM50", "BLOSUM55", "BLOSUM60", "BLOSUM62", "BLOSUM65", "BLOSUM70", "BLOSUM75", "BLOSUM80", "BLOSUM85", "BLOSUM90", "BLOSUMN", "DAYHOFF", "EDNAFULL", "GONNET", "IDENTITY", "MATCH", "NUC44", "PAM10", "PAM100", "PAM110", "PAM120", "PAM130", "PAM140", "PAM150", "PAM160", "PAM170", "PAM180", "PAM190", "PAM20", "PAM200", "PAM210", "PAM220", "PAM230", "PAM240", "PAM250", "PAM260", "PAM270", "PAM280", "PAM290", "PAM30", "PAM300", "PAM310", "PAM320", "PAM330", "PAM340", "PAM350", "PAM360", "PAM370", "PAM380", "PAM390", "PAM40", "PAM400", "PAM410", "PAM420", "PAM430", "PAM440", "PAM450", "PAM460", "PAM470", "PAM480", "PAM490", "PAM50", "PAM500", "PAM60", "PAM70", "PAM80", "PAM90", "TEST1"};
	}

	public ScoringMatrix(String matrixName, float[][] scores)
	{
		_matrixName = matrixName;
		_scores = scores;
	}

	public final String getName()
	{
		return _matrixName;
	}

	public final float[][] getScores()
	{
		return _scores;
	}

	public static String[] getMatrixNames()
	{
		return _matrixNames;
	}

	public final float GetScore(char a, char b)
	{
		return _scores[a][b];
	}

	public static ScoringMatrix Load(String matrixName)
	{
		char[] acids = new char[SIZE];

		// Initialize the acids array to null values (ascii = 0)
		for (int i = 0; i < SIZE; i++)
		{
			acids[i] = (char)(0);
		}

		float[][] scores = new float[SIZE][SIZE];

//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("Salsa.Core.Bio.Algorithms.Matrices." + matrixName))
		Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("Salsa.Core.Bio.Algorithms.Matrices." + matrixName);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var reader = new StreamReader(stream))
			StreamReader reader = new StreamReader(stream);
			try
			{
				// Skip the comment lines
				String line;

				while ((line = reader.ReadLine()) != null && line.trim().charAt(0) == COMMENT_STARTER)
				{
					;
				}

				// Read the headers line (the letters of the acids)
				ScoringMatrixTokenizer tokenizer = new ScoringMatrixTokenizer(line.trim());

				for (int j = 0; tokenizer.HasMoreTokens(); j++)
				{
					acids[j] = tokenizer.NextToken().charAt(0);
				}

				// Read the scores
				while ((line = reader.ReadLine()) != null)
				{
					tokenizer = new ScoringMatrixTokenizer(line.trim());
					char acid = tokenizer.NextToken().charAt(0);

					for (int i = 0; i < SIZE; i++)
					{
						if (acids[i] != 0)
						{
							scores[acid][acids[i]] = Float.parseFloat(tokenizer.NextToken());
						}
					}
				}
			}
			finally
			{
				reader.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}

		return new ScoringMatrix(matrixName, scores);
	}
}