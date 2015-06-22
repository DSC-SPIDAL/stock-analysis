package Salsa.Core.Blas;

import Salsa.Core.*;

public class MatrixTextWriter
{
	private char _delimiter;

	public MatrixTextWriter()
	{
		this(',');
	}

	public MatrixTextWriter(char delimiter)
	{
		_delimiter = delimiter;
	}

	public final void Write(Matrix<Short> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var writer = new StreamWriter(fileName))
		StreamWriter writer = new StreamWriter(fileName);
		try
		{
			Write(matrix, writer);
		}
		finally
		{
			writer.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(Matrix<ushort> matrix, string fileName)
	public final void Write(Matrix<Short> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var writer = new StreamWriter(fileName))
		StreamWriter writer = new StreamWriter(fileName);
		try
		{
			Write(matrix, writer);
		}
		finally
		{
			writer.dispose();
		}
	}

	public final void Write(Matrix<Integer> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var writer = new StreamWriter(fileName))
		StreamWriter writer = new StreamWriter(fileName);
		try
		{
			Write(matrix, writer);
		}
		finally
		{
			writer.dispose();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(Matrix<uint> matrix, string fileName)
	public final void Write(Matrix<Integer> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var writer = new StreamWriter(fileName))
		StreamWriter writer = new StreamWriter(fileName);
		try
		{
			Write(matrix, writer);
		}
		finally
		{
			writer.dispose();
		}
	}

	public final void Write(Matrix<Float> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var writer = new StreamWriter(fileName))
		StreamWriter writer = new StreamWriter(fileName);
		try
		{
			Write(matrix, writer);
		}
		finally
		{
			writer.dispose();
		}
	}

	public final void Write(Matrix<Double> matrix, String fileName)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var writer = new StreamWriter(fileName))
		StreamWriter writer = new StreamWriter(fileName);
		try
		{
			Write(matrix, writer);
		}
		finally
		{
			writer.dispose();
		}
	}

	public final void Write(Matrix<Short> matrix, StreamWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				if (j > 0)
				{
					writer.Write(_delimiter);
				}

				writer.Write(matrix.getElements()[i][j].toString());
			}

			writer.WriteLine();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(Matrix<ushort> matrix, StreamWriter writer)
	public final void Write(Matrix<Short> matrix, StreamWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				if (j > 0)
				{
					writer.Write(_delimiter);
				}

				writer.Write(matrix.getElements()[i][j].toString());
			}

			writer.WriteLine();
		}
	}

	public final void Write(Matrix<Integer> matrix, StreamWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				if (j > 0)
				{
					writer.Write(_delimiter);
				}

				writer.Write(matrix.getElements()[i][j].toString());
			}

			writer.WriteLine();
		}
	}

//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public void Write(Matrix<uint> matrix, StreamWriter writer)
	public final void Write(Matrix<Integer> matrix, StreamWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				if (j > 0)
				{
					writer.Write(_delimiter);
				}

				writer.Write(matrix.getElements()[i][j].toString());
			}

			writer.WriteLine();
		}
	}

	public final void Write(Matrix<Float> matrix, StreamWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				if (j > 0)
				{
					writer.Write(_delimiter);
				}

				writer.Write(matrix.getElements()[i][j].toString());
			}

			writer.WriteLine();
		}
	}

	public final void Write(Matrix<Double> matrix, StreamWriter writer)
	{
		for (int i = 0; i < matrix.getRowCount(); i++)
		{
			for (int j = 0; j < matrix.getColumnCount(); j++)
			{
				if (j > 0)
				{
					writer.Write(_delimiter);
				}

				writer.Write(matrix.getElements()[i][j].toString());
			}

			writer.WriteLine();
		}
	}
}