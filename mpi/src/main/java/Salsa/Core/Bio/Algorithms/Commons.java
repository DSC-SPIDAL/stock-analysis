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


/**  Global constants and varilables  
*/
public abstract class Commons
{
	/**  Current release version of JAligner
	*/
	private static final String CURRENT_RELEASE = "0.9";

	/**  Default home directory
	*/
	private static final String DEFAULT_USER_DIRECTORY = ".";

	/**  Default file separator
	*/
	private static final String DEFAULT_FILE_SEPARATOR = "/";

	/**  Default line separator
	*/
	private static final String DEFAULT_LINE_SEPARATOR = "\r\n";

	/**  User home directory
	*/
	private static final String userDirectory = DEFAULT_USER_DIRECTORY;

	/**  Line separator
	*/
	private static final String fileSeparator = DEFAULT_FILE_SEPARATOR;

	/**  Line separator
	*/
	private static final String lineSeparator = DEFAULT_LINE_SEPARATOR;

	static
	{
	{
			try
			{
				userDirectory = Environment.CurrentDirectory;
			}
			catch (RuntimeException e)
			{
				System.err.println("Failed getting user current directory: " + e);
			}
		}

		{
			try
			{
				fileSeparator = java.io.File.separatorChar.toString();
			}
			catch (RuntimeException e)
			{
				System.err.println("Failed getting system file separator: " + e);
			}
		}

		{
			try
			{
				lineSeparator = Environment.NewLine;
			}
			catch (RuntimeException e)
			{
				System.err.println("Failed getting system line separator: " + e);
			}
		}
	}

	/**  Returns system file separator.
	 @return  file separator 
	*/
	public static String getFileSeparator()
	{
		return fileSeparator;
	}

	/**  Returns system line separator.
	 @return  line separator 
	*/
	public static String getLineSeparator()
	{
		return lineSeparator;
	}

	/**  Returns user's current directory.
	 @return  user's current directory 
	*/
	public static String getUserDirectory()
	{
		return userDirectory;
	}

	/**  Returns the current release version of JAligner
	*/
	public static String getCurrentRelease()
	{
		return CURRENT_RELEASE;
	}
}