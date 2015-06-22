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

/**  
 A cell in a similarity matrix, to hold row, column and score.
*/
public final class Cell
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private int column;
	private int row;
	private float score;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	public Cell()
	{
		row = 0;
		column = 0;
		score = Float.NEGATIVE_INFINITY;
	}

	public int getColumn()
	{
		return column;
	}
	public void setColumn(int value)
	{
		column = value;
	}

	public int getRow()
	{
		return row;
	}
	public void setRow(int value)
	{
		row = value;
	}

	public float getScore()
	{
		return score;
	}
	public void setScore(float value)
	{
		score = value;
	}

	public void Set(int row, int column, float score)
	{
		this.row = row;
		this.column = column;
		this.score = score;
	}
}