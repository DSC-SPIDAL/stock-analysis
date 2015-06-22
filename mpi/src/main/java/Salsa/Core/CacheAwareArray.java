package Salsa.Core;

//C# TO JAVA CONVERTER TODO TASK: The C# 'struct' constraint has no equivalent in Java:
public class CacheAwareArray<T extends struct>
{
	private CacheAwareStorage<T>[] _items;

	public CacheAwareArray(int length)
	{
		_items = new CacheAwareStorage<T>[length + 1];
	}

	public final T getItem(int index)
	{
		return _items[index + 1].Value;
	}
	public final void setItem(int index, T value)
	{
		_items[index + 1].Value = value;
	}

	public final int getLength()
	{
		return _items.length - 1;
	}
}