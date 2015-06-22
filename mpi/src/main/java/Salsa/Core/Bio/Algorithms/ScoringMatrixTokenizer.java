package Salsa.Core.Bio.Algorithms;

import Salsa.Core.*;

/** The class performs token processing in strings
 Class create by the Java Language Conversion Assistant.
*/
//C# TO JAVA CONVERTER TODO TASK: The interface type was changed to the closest equivalent Java type, but the methods implemented will need adjustment:
public class ScoringMatrixTokenizer implements java.util.Iterator
{
	/** Char representation of the String to tokenize.
	*/
	private char[] chars;

	/** Include demiliters in the results.
	*/
	private boolean includeDelims;

	/** Position over the string
	*/
	private long currentPos;

	//The tokenizer uses the default delimiter set: the space character, the tab character, the newline character, and the carriage-return character and the form-feed character
	private String delimiters = " \t\n\r\f";

	/** 
	 Initializes a new class instance with a specified string to process
	 
	 @param source String to tokenize
	*/
	public ScoringMatrixTokenizer(String source)
	{
		chars = source.toCharArray();
	}

	/** 
	 Initializes a new class instance with a specified string to process
	 and the specified token delimiters to use
	 
	 @param source String to tokenize
	 @param delimiters String containing the delimiters
	*/
	public ScoringMatrixTokenizer(String source, String delimiters)
	{
		this(source);
		this.delimiters = delimiters;
	}


	/** 
	 Initializes a new class instance with a specified string to process, the specified token 
	 delimiters to use, and whether the delimiters must be included in the results.
	 
	 @param source String to tokenize
	 @param delimiters String containing the delimiters
	 @param includeDelims Determines if delimiters are included in the results.
	*/
	public ScoringMatrixTokenizer(String source, String delimiters, boolean includeDelims)
	{
		this(source, delimiters);
		this.includeDelims = includeDelims;
	}


	/** 
	 Remaining tokens count
	*/
	public final int getCount()
	{
			//keeping the current pos
		long pos = currentPos;
		int i = 0;

		try
		{
			while (true)
			{
				NextToken();
				i++;
			}
		}
		catch (IllegalArgumentException e)
		{
			currentPos = pos;
			return i;
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region IEnumerator Members

	/** 
	  Performs the same action as NextToken.
	*/
	public final Object getCurrent()
	{
		return NextToken();
	}

	/** 
	  Performs the same action as HasMoreTokens.
	 
	 @return True or false, depending if there are more tokens
	*/
	public final boolean MoveNext()
	{
		return HasMoreTokens();
	}

	/** 
	 Does nothing.
	*/
	public final void Reset()
	{
		;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** 
	 Returns the next token from the token list
	 
	 @return The string value of the token
	*/
	public final String NextToken()
	{
		return NextToken(delimiters);
	}

	/** 
	 Returns the next token from the source string, using the provided
	 token delimiters
	 
	 @param delimiters String containing the delimiters to use
	 @return The string value of the token
	*/
	public final String NextToken(String delimiters)
	{
		//According to documentation, the usage of the received delimiters should be temporary (only for this call).
		//However, it seems it is not true, so the following line is necessary.
		this.delimiters = delimiters;

		//at the end 
		if (currentPos == chars.length)
		{
			throw new IllegalArgumentException();
		}
			//if over a delimiter and delimiters must be returned
		else if ((Array.indexOf(delimiters.toCharArray(), chars[currentPos]) != -1) && includeDelims)
		{
			return "" + chars[currentPos++];
		}
			//need to get the token wo delimiters.
		else
		{
			return nextToken(delimiters.toCharArray());
		}
	}

	//Returns the nextToken wo delimiters
	private String nextToken(char[] delimiters)
	{
		String token = "";
		long pos = currentPos;

		//skip possible delimiters
		while (Array.indexOf(delimiters, chars[currentPos]) != -1)
		{
			//The last one is a delimiter (i.e there is no more tokens)
			if (++currentPos == chars.length)
			{
				currentPos = pos;
				throw new IllegalArgumentException();
			}
		}

		//getting the token
		while (Array.indexOf(delimiters, chars[currentPos]) == -1)
		{
			token += chars[currentPos];
			//the last one is not a delimiter
			if (++currentPos == chars.length)
			{
				break;
			}
		}
		return token;
	}


	/** 
	 Determines if there are more tokens to return from the source string
	 
	 @return True or false, depending if there are more tokens
	*/
	public final boolean HasMoreTokens()
	{
		//keeping the current pos
		long pos = currentPos;

		try
		{
			NextToken();
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
		finally
		{
			currentPos = pos;
		}
		return true;
	}
}