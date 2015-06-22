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

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: internal enum Directions : byte
public enum Directions 
{
	STOP(0),
	LEFT(1),
	DIAGONAL(2),
	UP(3);

	private int intValue;
	private static java.util.HashMap<Integer, Directions> mappings;
	private static java.util.HashMap<Integer, Directions> getMappings()
	{
		if (mappings == null)
		{
			synchronized (Directions.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, Directions>();
				}
			}
		}
		return mappings;
	}

	private Directions(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static Directions forValue(int value)
	{
		return getMappings().get(value);
	}
}