package Salsa.Core.Blas;

import Salsa.Core.*;

public class PartialMatrixBinaryWriter
{
	public final void Write(PartialMatrix<Short> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Create(fileName))
		Stream stream = File.Create(fileName);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var writer = new BinaryWriter(stream))
			BinaryWriter writer = new BinaryWriter(stream);
			try
			{
				Write(matrix, writer);
			}
			finally
			{
				writer.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(PartialMatrix<ushort> matrix, string fileName)
	public final void Write(PartialMatrix<Short> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Create(fileName))
		Stream stream = File.Create(fileName);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var writer = new BinaryWriter(stream))
			BinaryWriter writer = new BinaryWriter(stream);
			try
			{
				Write(matrix, writer);
			}
			finally
			{
				writer.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final void Write(PartialMatrix<Integer> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Create(fileName))
		Stream stream = File.Create(fileName);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var writer = new BinaryWriter(stream))
			BinaryWriter writer = new BinaryWriter(stream);
			try
			{
				Write(matrix, writer);
			}
			finally
			{
				writer.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(PartialMatrix<uint> matrix, string fileName)
	public final void Write(PartialMatrix<Integer> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Create(fileName))
		Stream stream = File.Create(fileName);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var writer = new BinaryWriter(stream))
			BinaryWriter writer = new BinaryWriter(stream);
			try
			{
				Write(matrix, writer);
			}
			finally
			{
				writer.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final void Write(PartialMatrix<Float> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Create(fileName))
		Stream stream = File.Create(fileName);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var writer = new BinaryWriter(stream))
			BinaryWriter writer = new BinaryWriter(stream);
			try
			{
				Write(matrix, writer);
			}
			finally
			{
				writer.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final void Write(PartialMatrix<Double> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (Stream stream = File.Create(fileName))
		Stream stream = File.Create(fileName);
		try
		{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var writer = new BinaryWriter(stream))
			BinaryWriter writer = new BinaryWriter(stream);
			try
			{
				Write(matrix, writer);
			}
			finally
			{
				writer.dispose();
			}
		}
		finally
		{
			stream.dispose();
		}
	}

	public final void Write(PartialMatrix<Short> matrix, BinaryWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				writer.Write(matrix.getElements()[i][j]);
			}
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(PartialMatrix<ushort> matrix, BinaryWriter writer)
	public final void Write(PartialMatrix<Short> matrix, BinaryWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				writer.Write(matrix.getElements()[i][j]);
			}
		}
	}

	public final void Write(PartialMatrix<Integer> matrix, BinaryWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				writer.Write(matrix.getElements()[i][j]);
			}
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(PartialMatrix<uint> matrix, BinaryWriter writer)
	public final void Write(PartialMatrix<Integer> matrix, BinaryWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				writer.Write(matrix.getElements()[i][j]);
			}
		}
	}

	public final void Write(PartialMatrix<Float> matrix, BinaryWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				writer.Write(matrix.getElements()[i][j]);
			}
		}
	}

	public final void Write(PartialMatrix<Double> matrix, BinaryWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				writer.Write(matrix.getElements()[i][j]);
			}
		}
	}
}