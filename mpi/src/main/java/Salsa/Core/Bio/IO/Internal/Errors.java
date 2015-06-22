package Salsa.Core.Bio.IO.Internal;

import Salsa.Core.*;
import Salsa.Core.Bio.IO.*;

// end Parser

public class Errors
{
	public int count = 0; // number of errors detected
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	public TextWriter errorStream = Console.Out; // error messages go to this stream

	public final void SynErr(int line, int col, int n)
	{
		String s;
		switch (n)
		{
			case 0:
				s = "EOF expected";
				break;
			case 1:
				s = "string expected";
				break;
			case 2:
				s = "\">\" expected";
				break;
			case 3:
				s = "\"\\r\" expected";
				break;
			case 4:
				s = "\"\\n\" expected";
				break;
			case 5:
				s = "??? expected";
				break;

			default:
				s = "error " + n;
				break;
		}
		errorStream.WriteLine(errMsgFormat, line, col, s);
		count++;
	}

	public final void SemErr(int line, int col, String s)
	{
		errorStream.WriteLine(errMsgFormat, line, col, s);
		count++;
	}

	public final void SemErr(String s)
	{
		errorStream.WriteLine(s);
		count++;
	}

	public final void Warning(int line, int col, String s)
	{
		errorStream.WriteLine(errMsgFormat, line, col, s);
	}

	public final void Warning(String s)
	{
		errorStream.WriteLine(s);
	}
}