package Salsa.Core.Bio.IO.Internal;

import Salsa.Core.*;
import Salsa.Core.Bio.IO.*;

//-----------------------------------------------------------------------------------
// Scanner
//-----------------------------------------------------------------------------------
public class Scanner
{
	private static final char EOL = '\n';
	private static final int eofSym = 0; // pdt
	private static final int maxT = 5;
	private static final int noSym = 5;
	private static java.util.Hashtable start; // maps first token character to start state


	public Buffer buffer; // scanner buffer

	private int ch; // current input character
	private int col; // column number of current character
	private int line; // line number of current character
	private int oldEols; // EOLs that appeared in a comment;
	private int pos; // byte position of current character
	private Token pt; // current peek token
	private Token t; // current token

	private int tlen; // length of current token
	private Token tokens; // list of tokens already peeked (first token is a dummy)
	private char[] tval = new char[128]; // text of current token

	static
	{
		start = new java.util.Hashtable(128);
		for (int i = 0; i <= 9; ++i)
		{
			start.put(i, 1);
		}
		for (int i = 11; i <= 12; ++i)
		{
			start.put(i, 1);
		}
		for (int i = 14; i <= 61; ++i)
		{
			start.put(i, 1);
		}
		for (int i = 63; i <= 65535; ++i)
		{
			start.put(i, 1);
		}
		start.put(62, 2);
		start.put(13, 3);
		start.put(10, 4);
		start.put(Buffer.EOF, -1);
	}

	public Scanner(String fileName)
	{
		try
		{
			Stream stream = new FileStream(fileName, FileMode.Open, FileAccess.Read, FileShare.Read);
			buffer = new Buffer(stream, false);
			Init();
		}
		catch (IOException e)
		{
			throw new FatalError("Cannot open file " + fileName);
		}
	}

	public Scanner(Stream s)
	{
		buffer = new Buffer(s, true);
		Init();
	}

	private void Init()
	{
		pos = -1;
		line = 1;
		col = 0;
		oldEols = 0;
		NextCh();
		if (ch == 0xEF)
		{
			// check optional byte order mark for UTF-8
			NextCh();
			int ch1 = ch;
			NextCh();
			int ch2 = ch;
			if (ch1 != 0xBB || ch2 != 0xBF)
			{
				throw new FatalError(String.format("illegal byte order mark: EF %1$2X %2$2X", ch1, ch2));
			}
			buffer = new UTF8Buffer(buffer);
			col = 0;
			NextCh();
		}
		pt = tokens = new Token(); // first token is a dummy
	}

	private void NextCh()
	{
		if (oldEols > 0)
		{
			ch = EOL;
			oldEols--;
		}
		else
		{
			pos = buffer.getPos();
			ch = buffer.Read();
			col++;
			// replace isolated '\r' by '\n' in order to make
			// eol handling uniform across Windows, Unix and Mac
			if (ch == '\r' && buffer.Peek() != '\n')
			{
				ch = EOL;
			}
			if (ch == EOL)
			{
				line++;
				col = 0;
			}
		}
	}

	private void AddCh()
	{
		if (tlen >= tval.length)
		{
			char[] newBuf = new char[2 * tval.length];
			System.arraycopy(tval, 0, newBuf, 0, tval.length);
			tval = newBuf;
		}
		if (ch != Buffer.EOF)
		{
			tval[tlen++] = (char) ch;
			NextCh();
		}
	}

	private void CheckLiteral()
	{
//C# TO JAVA CONVERTER NOTE: The following 'switch' operated on a string member and was converted to Java 'if-else' logic:
//		switch (t.val)
	{
		}
	}

	private Token NextToken()
	{
		while (ch == ' ' || false)
		{
			NextCh();
		}

		t = new Token();
		t.pos = pos;
		t.col = col;
		t.line = line;
		int state;
		if (start.containsKey(ch))
		{
			state = (int) start.get(ch);
		}
		else
		{
			state = 0;
		}
		tlen = 0;
		AddCh();

		switch (state)
		{
			case -1:
			{
					t.kind = eofSym;
					break;
			} // NextCh already done
			case 0:
			{
					t.kind = noSym;
					break;
			} // NextCh already done
			case 1:
				if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '=' || ch >= '?' && ch <= 65535)
				{
					AddCh();
//C# TO JAVA CONVERTER TODO TASK: There is no 'goto' in Java:
					goto case 1;
				}
				else
				{
					t.kind = 1;
					break;
				}
			case 2:
			{
					t.kind = 2;
					break;
			}
			case 3:
			{
					t.kind = 3;
					break;
			}
			case 4:
			{
					t.kind = 4;
					break;
			}
		}
		t.val = new String(tval, 0, tlen);
		return t;
	}

	// get the next token (possibly a token already seen during peeking)
	public final Token Scan()
	{
		if (tokens.next == null)
		{
			return NextToken();
		}
		else
		{
			pt = tokens = tokens.next;
			return tokens;
		}
	}

	// peek for the next token, ignore pragmas
	public final Token Peek()
	{
		do
		{
			if (pt.next == null)
			{
				pt.next = NextToken();
			}
			pt = pt.next;
		} while (pt.kind > maxT); // skip pragmas

		return pt;
	}

	// make sure that peeking starts at the current scan position
	public final void ResetPeek()
	{
		pt = tokens;
	}
}
// end Scanner