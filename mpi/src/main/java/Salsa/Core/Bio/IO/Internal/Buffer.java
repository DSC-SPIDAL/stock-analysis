package Salsa.Core.Bio.IO.Internal;

import Salsa.Core.*;
import Salsa.Core.Bio.IO.*;

//-----------------------------------------------------------------------------------
// Buffer
//-----------------------------------------------------------------------------------
public class Buffer
{
	// This Buffer supports the following cases:
	// 1) seekable stream (file)
	//    a) whole stream in buffer
	//    b) part of stream in buffer
	// 2) non seekable stream (network, console)

	public static final int EOF = Character.MAX_VALUE + 1;
	private static final int MIN_BUFFER_LENGTH = 1024; // 1KB
	private static final int MAX_BUFFER_LENGTH = MIN_BUFFER_LENGTH * 64; // 64KB
	private boolean isUserStream; // was the stream opened by the user?
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: private byte[] buf;
	private byte[] buf; // input buffer
	private int bufLen; // length of buffer
	private int bufPos; // current position in buffer
	private int bufStart; // position of first byte in buffer relative to input stream
	private int fileLen; // length of input stream (may change if the stream is no file)
	private Stream stream; // input stream (seekable)

	public Buffer(Stream s, boolean isUserStream)
	{
		stream = s;
		this.isUserStream = isUserStream;

		if (stream.CanSeek)
		{
			fileLen = (int) stream.getLength();
			bufLen = Math.min(fileLen, MAX_BUFFER_LENGTH);
			bufStart = Integer.MAX_VALUE; // nothing in the buffer so far
		}
		else
		{
			fileLen = bufLen = bufStart = 0;
		}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: buf = new byte[(bufLen > 0) ? bufLen : MIN_BUFFER_LENGTH];
		buf = new byte[(bufLen > 0) ? bufLen : MIN_BUFFER_LENGTH];
		if (fileLen > 0) // setup buffer to position 0 (start)
		{
			setPos(0);
		}
		else // index 0 is already after the file, thus Pos = 0 is invalid
		{
			bufPos = 0;
		}
		if (bufLen == fileLen && stream.CanSeek)
		{
			Close();
		}
	}

	protected Buffer(Buffer b)
	{
		// called in UTF8Buffer constructor
		buf = b.buf;
		bufStart = b.bufStart;
		bufLen = b.bufLen;
		fileLen = b.fileLen;
		bufPos = b.bufPos;
		stream = b.stream;
		// keep destructor from closing the stream
		b.stream = null;
		isUserStream = b.isUserStream;
	}

	public final int getPos()
	{
		return bufPos + bufStart;
	}
	public final void setPos(int value)
	{
		if (value >= fileLen && stream != null && !stream.CanSeek)
		{
				// Wanted position is after buffer and the stream
				// is not seek-able e.g. network or console,
				// thus we have to read the stream manually till
				// the wanted position is in sight.
			while (value >= fileLen && ReadNextStreamChunk() > 0);
		}

		if (value < 0 || value > fileLen)
		{
			throw new FatalError("buffer out of bounds access, position: " + value);
		}

		if (value >= bufStart && value < bufStart + bufLen)
		{
				// already in buffer
			bufPos = value - bufStart;
		}
		else if (stream != null)
		{
				// must be swapped in
			stream.Seek(value, SeekOrigin.Begin);
			bufLen = stream.Read(buf, 0, buf.length);
			bufStart = value;
			bufPos = 0;
		}
		else
		{
				// set the position to the end of the file, Pos will return fileLen.
			bufPos = fileLen - bufStart;
		}
	}

	protected void finalize() throws Throwable
	{
		Close();
	}

	protected final void Close()
	{
		if (!isUserStream && stream != null)
		{
			stream.Close();
			stream = null;
		}
	}

	public int Read()
	{
		if (bufPos < bufLen)
		{
			return buf[bufPos++];
		}
		else if (getPos() < fileLen)
		{
			setPos(getPos()); // shift buffer start to Pos
			return buf[bufPos++];
		}
		else if (stream != null && !stream.CanSeek && ReadNextStreamChunk() > 0)
		{
			return buf[bufPos++];
		}
		else
		{
			return EOF;
		}
	}

	public final int Peek()
	{
		int curPos = getPos();
		int ch = Read();
		setPos(curPos);
		return ch;
	}

	public final String GetString(int beg, int end)
	{
		int len = end - beg;
		char[] buf = new char[len];
		int oldPos = getPos();
		setPos(beg);
		for (int i = 0; i < len; i++)
		{
			buf[i] = (char) Read();
		}
		setPos(oldPos);
		return new String(buf);
	}

	// Read the next chunk of bytes from the stream, increases the buffer
	// if needed and updates the fields fileLen and bufLen.
	// Returns the number of bytes read.
	private int ReadNextStreamChunk()
	{
		int free = buf.length - bufLen;
		if (free == 0)
		{
			// in the case of a growing input stream
			// we can neither seek in the stream, nor can we
			// foresee the maximum length, thus we must adapt
			// the buffer size on demand.
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: var newBuf = new byte[bufLen*2];
			byte[] newBuf = new byte[bufLen * 2];
			System.arraycopy(buf, 0, newBuf, 0, bufLen);
			buf = newBuf;
			free = bufLen;
		}
		int read = stream.Read(buf, bufLen, free);
		if (read > 0)
		{
			fileLen = bufLen = (bufLen + read);
			return read;
		}
		// end of stream reached
		return 0;
	}
}