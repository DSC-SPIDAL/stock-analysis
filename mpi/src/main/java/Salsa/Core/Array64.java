package Salsa.Core;

public class Array64<T> implements Serializable
{
	// These need to be const so that the getter/setter get inlined by the JIT into 
	// calling methods just like with a real array to have any chance of meeting our 
	// performance goals.
	//
	// BLOCK_SIZE must be a power of 2, and we want it to be big enough that we allocate
	// blocks in the large object heap so that they don't move.
	public static final int BLOCK_SIZE_LOG2 = 19;
	public static final int BLOCK_SIZE = 524288;
	public static final int BLOCK_SIZE_MINUS_ONE = BLOCK_SIZE - 1;

	// Don't use a multi-dimensional array here because then we can't right size the last
	// block and we have to do range checking on our own and since there will then be 
	// exception throwing in our code there is a good chance that the JIT won't inline.
	private T[][] _elements;
	private long _length;

	// maximum Array64 size = BLOCK_SIZE * Int.MaxValue
	public Array64(long length)
	{
		int numBlocks = (int)(length / BLOCK_SIZE);
		int numElementsInLastBlock = (int)(length - (numBlocks * BLOCK_SIZE));
		_length = length;

		if (numElementsInLastBlock > 0)
		{
			numBlocks += 1;
			_elements = new T[numBlocks][];

			for (int i = 0; i < numBlocks - 1; i++)
			{
				_elements[i] = new T[BLOCK_SIZE];
			}

			_elements[numBlocks - 1] = new T[numElementsInLastBlock];
		}
		else
		{
			_elements = new T[numBlocks][];

			for (int i = 0; i < numBlocks; i++)
			{
				_elements[i] = new T[BLOCK_SIZE];
			}
		}
	}

	public final long getLength()
	{
		return _length;
	}

		// these must be _very_ simple in order to ensure that they get inlined into
		// their caller 
	public final T getItem(long elementNumber)
	{
//C# TO JAVA CONVERTER WARNING: The right shift operator was not replaced by Java's logical right shift operator since the left operand was not confirmed to be of an unsigned type, but you should review whether the logical right shift operator (>>>) is more appropriate:
		int blockNum = (int)(elementNumber >> BLOCK_SIZE_LOG2);
		int elementNumberInBlock = (int)(elementNumber & BLOCK_SIZE_MINUS_ONE);
		return _elements[blockNum][elementNumberInBlock];
	}
	public final void setItem(long elementNumber, T value)
	{
//C# TO JAVA CONVERTER WARNING: The right shift operator was not replaced by Java's logical right shift operator since the left operand was not confirmed to be of an unsigned type, but you should review whether the logical right shift operator (>>>) is more appropriate:
		int blockNum = (int)(elementNumber >> BLOCK_SIZE_LOG2);
		int elementNumberInBlock = (int)(elementNumber & BLOCK_SIZE_MINUS_ONE);
		_elements[blockNum][elementNumberInBlock] = value;
	}
}