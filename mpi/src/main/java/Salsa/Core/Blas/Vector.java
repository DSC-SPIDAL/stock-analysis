package Salsa.Core.Blas;

import Salsa.Core.*;

public class Vector<T> implements Iterable<T>
{
	protected T[] _elements;
	protected int _length;

	public Vector(T[] values)
	{
		_length = values.length;
		_elements = values;
	}

	public Vector(int length)
	{
		_length = length;
		_elements = new T[length];
	}

	public final T getItem(int index)
	{
		return _elements[index];
	}
	public final void setItem(int index, T value)
	{
		_elements[index] = value;
	}

	public final int getLength()
	{
		return _length;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region IEnumerable<T> Members

	public final java.util.Iterator<T> iterator()
	{
		for (int i = 0; i < _length; i++)
		{
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
			yield return _elements[i];
		}
	}

	public final java.util.Iterator GetEnumerator()
	{
		return GetEnumerator();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	public void SetAll(T value)
	{
		for (int i = 0; i < _length; i++)
		{
			_elements[i] = value;
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < _elements.length; i++)
		{
			if (i != 0)
			{
				sb.append(',');
			}

			sb.append(_elements[i]);
		}

		return sb.toString();
	}

//C# TO JAVA CONVERTER TODO TASK: The following operator overload is not converted by C# to Java Converter:
	public static implicit operator T[] (Vector<T> v)
	{
		return v._elements;
	}

//C# TO JAVA CONVERTER TODO TASK: The following operator overload is not converted by C# to Java Converter:
	public static implicit <T> operator Vector(T[] v)
	{
		return new this.Vector<T>(v);
	}

	public static boolean CheckDimensions(Vector<T> left, Vector<T> right, boolean throwException)
	{
		if (left._length != right._length)
		{
			if (throwException)
			{
				throw new IndexOutOfBoundsException();
			}

			return false;
		}

		return true;
	}
}