package Salsa.Core;

// C#: Note works for value types only
// It may seem strange that this code actually allocates enough space for two cache lines' 
// worth of data instead of just one. That's because, on .NET, you can't specify the alignment 
// of data beyond some inherent 4-byte and 8-byte alignment guarantees, which aren't big enough 
// for our purposes. Even if you could specify a starting alignment, the compacting garbage 
// collector is likely to move your object and thus change its alignment dynamically. Without 
// alignment to guarantee the starting address of the data, the only way to deal with this is 
// to allocate enough space both before and after data to ensure that no other objects can 
// share the cache line.
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class will differ from the original:
//ORIGINAL LINE: [StructLayout(LayoutKind.Explicit, Size = 128)] public struct CacheAwareStorage<T> where T : struct
//C# TO JAVA CONVERTER TODO TASK: The C# 'struct' constraint has no equivalent in Java:
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
public final class CacheAwareStorage<T extends struct>
{
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [FieldOffset(64)] public T Value;
	public T Value;

	public CacheAwareStorage clone()
	{
		CacheAwareStorage varCopy = new CacheAwareStorage();

		varCopy.Value = this.Value;

		return varCopy;
	}
}