package Salsa.Core.Blas;

import Salsa.Core.*;

public class PartialMatrixTextWriter
{
	private char _delimiter;

	public PartialMatrixTextWriter()
	{
		this(',');
	}

	public PartialMatrixTextWriter(char delimiter)
	{
		_delimiter = delimiter;
	}

	public final void Write(PartialMatrix<Short> matrix, String fileName)
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
//ORIGINAL LINE: public void Write(PartialMatrix<ushort> matrix, string fileName)
	public final void Write(PartialMatrix<Short> matrix, String fileName)
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

	public final void Write(PartialMatrix<Integer> matrix, String fileName)
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
//ORIGINAL LINE: public void Write(PartialMatrix<uint> matrix, string fileName)
	public final void Write(PartialMatrix<Integer> matrix, String fileName)
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

	public final void Write(PartialMatrix<Float> matrix, String fileName)
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

	public final void Write(PartialMatrix<Double> matrix, String fileName)
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

	public final void Write(PartialMatrix<Short> matrix, StreamWriter writer)
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
//ORIGINAL LINE: public void Write(PartialMatrix<ushort> matrix, StreamWriter writer)
	public final void Write(PartialMatrix<Short> matrix, StreamWriter writer)
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

	public final void Write(PartialMatrix<Integer> matrix, StreamWriter writer)
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
//ORIGINAL LINE: public void Write(PartialMatrix<uint> matrix, StreamWriter writer)
	public final void Write(PartialMatrix<Integer> matrix, StreamWriter writer)
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

	public final void Write(PartialMatrix<Float> matrix, StreamWriter writer)
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

	public final void Write(PartialMatrix<Double> matrix, StreamWriter writer)
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