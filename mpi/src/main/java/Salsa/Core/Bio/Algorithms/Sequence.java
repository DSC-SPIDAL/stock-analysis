package Salsa.Core.Bio.Algorithms;

import Salsa.Core.Bio.IO.*;
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


public class Sequence implements IFastaRecord, Serializable
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	private String _label = "";
	private String _residues = "";

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/**  Constructor
	*/
	public Sequence()
	{
	}

	/**  Constructor
	*/
	public Sequence(String label, String sequence)
	{
		_label = label;
		_residues = sequence;
	}

	public final char getItem(int residueIndex)
	{
		return _residues.charAt(residueIndex);
	}

	public final int getLength()
	{
		return _residues.length();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region IFastaRecord Members

	/**  Returns the sequence label
	*/
	public final String getLabel()
	{
		return _label;
	}
	public final void setLabel(String value)
	{
		_label = value;
	}

	/**  Gets or sets the amino acid sequence
	*/
	public final String getResidues()
	{
		return _residues;
	}
	public final void setResidues(String value)
	{
		_residues = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/**  Returns a subsequence
	 @param index start index 
	 @param length length of subsequence 
	 @return  subsequence 
	*/
	public final String Subsequence(int index, int length)
	{
		return _residues.substring(index, index + (index + length) - (index));
	}
}