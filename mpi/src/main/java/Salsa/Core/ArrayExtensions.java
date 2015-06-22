package Salsa.Core;

public final class ArrayExtensions
{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public static byte[] Compress(double[] toCompress)
	public static byte[] Compress(double[] toCompress)
	{
		int count = toCompress.length;
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var ms = new MemoryStream())
		MemoryStream ms = new MemoryStream();
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var gz = new GZipStream(ms, CompressionMode.Compress, true))
			GZipStream gz = new GZipStream(ms, CompressionMode.Compress, true);
			try
			{
				BinaryWriter writer = new BinaryWriter(gz);
				writer.Write(count);

				for (int i = 0; i < count; i++)
				{
					writer.Write(toCompress[i]);
				}

				writer.Flush();
				gz.Flush();
			}
			finally
			{
				gz.dispose();
			}
			ms.Flush();
			return ms.ToArray();
		}
		finally
		{
			ms.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public static double[] DecompressDouble(byte[] toDecompress)
	public static double[] DecompressDouble(byte[] toDecompress)
	{
		double[] result = null;

//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var ms = new MemoryStream(toDecompress))
		MemoryStream ms = new MemoryStream(toDecompress);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var gz = new GZipStream(ms, CompressionMode.Decompress, true))
			GZipStream gz = new GZipStream(ms, CompressionMode.Decompress, true);
			try
			{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//				using (var br = new BinaryReader(gz))
				BinaryReader br = new BinaryReader(gz);
				try
				{
					int count = br.ReadInt32();
					result = new double[count];

					for (int i = 0; i < count; i++)
					{
						result[i] = br.ReadDouble();
					}
				}
				finally
				{
					br.dispose();
				}
			}
			finally
			{
				gz.dispose();
			}
		}
		finally
		{
			ms.dispose();
		}

		return result;
	}
}