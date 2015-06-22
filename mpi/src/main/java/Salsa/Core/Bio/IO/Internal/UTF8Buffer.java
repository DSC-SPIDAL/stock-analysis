package Salsa.Core.Bio.IO.Internal;

import Salsa.Core.*;
import Salsa.Core.Bio.IO.*;

//-----------------------------------------------------------------------------------
// UTF8Buffer
//-----------------------------------------------------------------------------------
public class UTF8Buffer extends Buffer
{
	public UTF8Buffer(Buffer b)
	{
		super(b);
	}

	@Override
	public int Read()
	{
		int ch;
		do
		{
			ch = super.Read();
			// until we find a utf8 start (0xxxxxxx or 11xxxxxx)
		} while ((ch >= 128) && ((ch & 0xC0) != 0xC0) && (ch != EOF));
		if (ch < 128 || ch == EOF)
		{
			// nothing to do, first 127 chars are the same in ascii and utf8
			// 0xxxxxxx or end of file character
		}
		else if ((ch & 0xF0) == 0xF0)
		{
			// 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
			int c1 = ch & 0x07;
			ch = super.Read();
			int c2 = ch & 0x3F;
			ch = super.Read();
			int c3 = ch & 0x3F;
			ch = super.Read();
			int c4 = ch & 0x3F;
			ch = (((((c1 << 6) | c2) << 6) | c3) << 6) | c4;
		}
		else if ((ch & 0xE0) == 0xE0)
		{
			// 1110xxxx 10xxxxxx 10xxxxxx
			int c1 = ch & 0x0F;
			ch = super.Read();
			int c2 = ch & 0x3F;
			ch = super.Read();
			int c3 = ch & 0x3F;
			ch = (((c1 << 6) | c2) << 6) | c3;
		}
		else if ((ch & 0xC0) == 0xC0)
		{
			// 110xxxxx 10xxxxxx
			int c1 = ch & 0x1F;
			ch = super.Read();
			int c2 = ch & 0x3F;
			ch = (c1 << 6) | c2;
		}
		return ch;
	}
}