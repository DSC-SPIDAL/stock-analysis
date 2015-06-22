package Salsa.Core.Blas;

import Salsa.Core.*;

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to the 'unsafe' modifier in Java:
//ORIGINAL LINE: public sealed unsafe class MatrixBinaryWriter
public final class MatrixBinaryWriter
{
	public void Write(Matrix<Short> matrix, String fileName)
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
//ORIGINAL LINE: public void Write(Matrix<ushort> matrix, string fileName)
	public void Write(Matrix<Short> matrix, String fileName)
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

	public void Write(Matrix<Integer> matrix, String fileName)
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
//ORIGINAL LINE: public void Write(Matrix<uint> matrix, string fileName)
	public void Write(Matrix<Integer> matrix, String fileName)
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

	public void Write(Matrix<Float> matrix, String fileName)
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

	public void Write(Matrix<Double> matrix, String fileName)
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

	public void Write(Matrix<Short> matrix, BinaryWriter writer)
	{
		short[][] element = matrix.getElements();
		int rows = matrix.getRowCount();
		int cols = matrix.getColumnCount();

		for (int i = 0; i < rows; i++)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no direct Java equivalent to 'fixed' blocks:
			fixed (short * dataPtr = element[i])
			{
				short * ptr = dataPtr;

				for (int j = 0; j < cols; j++)
				{
					writer.Write(*ptr);
					ptr++;
				}
			}
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(Matrix<ushort> matrix, BinaryWriter writer)
	public void Write(Matrix<Short> matrix, BinaryWriter writer)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort[][] element = matrix.Elements;
		short[][] element = matrix.getElements();
		int rows = matrix.getRowCount();
		int cols = matrix.getColumnCount();

		for (int i = 0; i < rows; i++)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no direct Java equivalent to 'fixed' blocks:
			fixed (short * dataPtr = element[i])
			{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: ushort* ptr = dataPtr;
				short * ptr = dataPtr;

				for (int j = 0; j < cols; j++)
				{
					writer.Write(*ptr);
					ptr++;
				}
			}
		}
	}

	public void Write(Matrix<Integer> matrix, BinaryWriter writer)
	{
		int[][] element = matrix.getElements();
		int rows = matrix.getRowCount();
		int cols = matrix.getColumnCount();

		for (int i = 0; i < rows; i++)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no direct Java equivalent to 'fixed' blocks:
			fixed (int * dataPtr = element[i])
			{
				int * ptr = dataPtr;

				for (int j = 0; j < cols; j++)
				{
					writer.Write(*ptr);
					ptr++;
				}
			}
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(Matrix<uint> matrix, BinaryWriter writer)
	public void Write(Matrix<Integer> matrix, BinaryWriter writer)
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint[][] element = matrix.Elements;
		int[][] element = matrix.getElements();
		int rows = matrix.getRowCount();
		int cols = matrix.getColumnCount();

		for (int i = 0; i < rows; i++)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no direct Java equivalent to 'fixed' blocks:
			fixed (int * dataPtr = element[i])
			{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: uint* ptr = dataPtr;
				int * ptr = dataPtr;

				for (int j = 0; j < cols; j++)
				{
					writer.Write(*ptr);
					ptr++;
				}
			}
		}
	}

	public void Write(Matrix<Float> matrix, BinaryWriter writer)
	{
		float[][] element = matrix.getElements();
		int rows = matrix.getRowCount();
		int cols = matrix.getColumnCount();

		for (int i = 0; i < rows; i++)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no direct Java equivalent to 'fixed' blocks:
			fixed (float * dataPtr = element[i])
			{
				float * ptr = dataPtr;

				for (int j = 0; j < cols; j++)
				{
					writer.Write(*ptr);
					ptr++;
				}
			}
		}
	}

	public void Write(Matrix<Double> matrix, BinaryWriter writer)
	{
		double[][] element = matrix.getElements();
		int rows = matrix.getRowCount();
		int cols = matrix.getColumnCount();

		for (int i = 0; i < rows; i++)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no direct Java equivalent to 'fixed' blocks:
			fixed (double * dataPtr = element[i])
			{
				double * ptr = dataPtr;

				for (int j = 0; j < cols; j++)
				{
					writer.Write(*ptr);
					ptr++;
				}
			}
		}
	}
}