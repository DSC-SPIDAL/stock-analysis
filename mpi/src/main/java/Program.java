package VectorPairwise;

import Common.*;
import MPI.*;
import Salsa.Core.*;
import Salsa.Core.Blas.*;

public class Program
{
	private static String _vectorFile;
	private static String _distFile;
	private static boolean _normalize;

	private static int _size;
	private static double _dmax = -Double.MAX_VALUE;
	private static double _dmin = Double.MAX_VALUE;
	static void main(String[] args)
	{
		// Load the command line args into our helper class which allows us to name arguments
		Arguments tempVar = new Arguments(args);
		tempVar.Usage = "Usage: VectorPairwise.exe /config=<string>";
		Arguments pargs = tempVar;

		if (pargs.CheckRequired(new String[] {"config"}) == false)
		{
			System.out.println(pargs.Usage);
			return;
		}
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (new MPI.Environment(ref args))

		tangible.RefObject<String> tempRef_args = new tangible.RefObject<String>(args);
		MPI.Environment tempVar2 = new MPI.Environment(tempRef_args);
		args = tempRef_args.argValue;
		try
		{
			ReadConfiguration(pargs);
			java.util.List<VectorPoint> vecs = ReadVectors();
			_size = vecs.size();

			int rank = Communicator.world.getRank();
			int worldSize = Communicator.world.getSize();

			Block[][] processToCloumnBlocks = BlockPartitioner.Partition(_size, _size, worldSize, worldSize);
			Block[] myColumnBlocks = processToCloumnBlocks[rank];

			PartialMatrix<Double> myRowStrip = new PartialMatrix<Double>(myColumnBlocks[0].RowRange, new Range(0, _size - 1));


			ComputeDistanceBlocks(myRowStrip, myColumnBlocks, vecs);
			_dmin = Communicator.world.Allreduce(_dmin, Operation<Double>.Min);
			_dmax = Communicator.world.Allreduce(_dmax, Operation<Double>.Max);

			if (_dmax < 1) // no need to normalize whe max distance is also less than 1
			{
				_normalize = false;
			}

			if (rank == 0)
			{
				System.out.println("Min distance: " + _dmin);
				System.out.println("Max distance: " + _dmax);
			}

			WriteFullMatrixOnRank0(_distFile, _size, rank, myRowStrip, myColumnBlocks[0].RowRange, processToCloumnBlocks[0][0].RowRange, _normalize, _dmax);
			MPI.Communicator.world.Barrier();
			if (rank == 0)
			{
				System.out.println("Done.");
			}
		}
		finally
		{
			tempVar2.dispose();
		}
	}

	private static void WriteFullMatrixOnRank0(String fileName, int size, int rank, PartialMatrix<Double> partialMatrix, Range myRowRange, Range rootRowRange, boolean normalize, double dmax)
	{
		FileStream fileStream = null;
		BinaryWriter writer = null;

		int a = size / MPI.Communicator.world.getSize();
		int b = size % MPI.Communicator.world.getSize();

		/*
		 * A note on row ranges and assigned process numbers.
		 * First b number of process will have (a + 1) number of rows each.
		 * The rest will have only 'a' number of rows. So if a row number, j,
		 * falls inside the first set, i.e. j < (b * (a + 1)), then the rank 
		 * of the process that handles this row is equal to the integer division
		 * of j / (a + 1). Else, i.e. j >= (b * (a + 1)) then that row is 
		 * in the second set of processes. Thus, the rank of the process handling
		 * this row is equal to the integer calculation of b + [(j - (b * (a + 1)) / a]
		 */

		int numOfRowsPerReceive = a;

		Range nextRowRange = null;

		if (rank == 0)
		{
			fileStream = File.Create(fileName, 4194304);
			writer = new BinaryWriter(fileStream);

			// I am rank0 and I am the one who will fill the fullMatrix. So let's fill what I have already.
			for (int i = partialMatrix.GlobalRowStartIndex; i <= partialMatrix.GlobalRowEndIndex; i++)
			{
				double[] values = partialMatrix.GetRowValues(i);
				for (double value : values)
				{
					writer.Write((short)((normalize? value / dmax : value) * Short.MAX_VALUE));
				}
			}
		}



		// For all the remaining rows that rank0 does not have receive in blocks of rows
		for (int i = rootRowRange.EndIndex + 1; i < size;)
		{
			if (rank == 0)
			{
				// I am rank0 and let's declare the next row range that I want to receive.
				int end = i + numOfRowsPerReceive - 1;
				end = end >= size ? size - 1 : end;
				nextRowRange = new Range(i, end);
			}

			// Announce everyone about the next row ranges that rank0 has declared.
			tangible.RefObject<T> tempRef_nextRowRange = new tangible.RefObject<T>(nextRowRange);
			MPI.Communicator.world.<Range>Broadcast(tempRef_nextRowRange, 0);
			nextRowRange = tempRef_nextRowRange.argValue;

			if (rank == 0)
			{
				/* I am rank0 and now let's try to receive the declared next row range from others */

				// A variable to hold the rank of the process, which has the row that I am (rank0) going to receive
				int processRank;

				double[] values;
				for (int j = nextRowRange.StartIndex; j <= nextRowRange.EndIndex; j++)
				{
					// Let's find the process that has the row j.
					processRank = j < (b * (a + 1)) ? j / (a + 1) : b + ((j - (b * (a + 1))) / a);

					// For each row that I (rank0) require I will receive from the process, which has that row.
					values = MPI.Communicator.world.<double[]>Receive(processRank, 100);

					// Set the received values in the fullMatrix
					for (double value : values)
					{
						writer.Write((short)((normalize ? value / dmax : value) * Short.MAX_VALUE));
					}
				}
			}
			else
			{
				/* I am just an ordinary process and I am ready to give rank0 whatever the row it requests if I have that row */

				// find the intersection of the row ranges of what I (the ordinary process) have and what rank0 wants and then send those rows to rank0
				if (myRowRange.IntersectsWith(nextRowRange))
				{
					Range intersection = myRowRange.GetIntersectionWith(nextRowRange);
					for (int k = intersection.StartIndex; k <= intersection.EndIndex; k++)
					{
						MPI.Communicator.world.<double[]>Send(partialMatrix.GetRowValues(k), 0, 100);
					}
				}
			}

			i += numOfRowsPerReceive;
		}

		// I am rank0 and I came here means that I wrote full matrix to disk. So I will clear the writer and stream.
		if (rank == 0)
		{
			writer.Flush();
			fileStream.Close();
			writer.Close();
		}

	}

	private static void ComputeDistanceBlocks(PartialMatrix<Double> myRowStrip, Block[] myColumnBlocks, java.util.List<VectorPoint> vecs)
	{
		for (Block block : myColumnBlocks)
		{
			for (int r = block.RowRange.StartIndex; r <= block.RowRange.EndIndex; ++r)
			{
				VectorPoint vr = vecs.get(r);
				for (int c = block.ColumnRange.StartIndex; c <= block.ColumnRange.EndIndex; ++c)
				{
					VectorPoint vc = vecs.get(c);
					double dist = vr.EuclidenDistanceTo(vc);
					myRowStrip[r][c] = dist;
					if (dist > _dmax)
					{
						_dmax = dist;
					}

					if (dist < _dmin)
					{
						_dmin = dist;
					}
				}
			}
		}
	}


	private static java.util.List<VectorPoint> ReadVectors()
	{
		java.util.List<VectorPoint> vecs = new java.util.ArrayList<VectorPoint>();
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (VectorPointsReader reader = new VectorPointsReader(_vectorFile))
		VectorPointsReader reader = new VectorPointsReader(_vectorFile);
		try
		{
			while (!reader.getEndOfStream())
			{
				vecs.add(reader.ReadVectorPoint());
			}
		}
		finally
		{
			reader.dispose();
		}
		return vecs;
	}

	private static void ReadConfiguration(Arguments pargs)
	{
		String config = pargs.<String>GetValue("config");

		/* Reading parameters file */
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (StreamReader reader = new StreamReader(config))
		StreamReader reader = new StreamReader(config);
		try
		{
			char[] sep = new char[] {' ', '\t'};
			while (!reader.EndOfStream)
			{
				String line = reader.ReadLine();
				// Skip null/empty and comment lines
				if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(line) && !line.startsWith("#"))
				{
					String[] splits = line.trim().split(java.util.regex.Pattern.quote(sep.toString()), -1);
					if (splits.length >= 2)
					{
						String value = splits[1];
//C# TO JAVA CONVERTER NOTE: The following 'switch' operated on a string member and was converted to Java 'if-else' logic:
//						switch (splits[0])
					String tempVar = splits[0];
//ORIGINAL LINE: case "VectorFile":
						if (tempVar.equals("VectorFile"))
						{
						_vectorFile = value;
						}
//ORIGINAL LINE: case "DistFile":
						else if (tempVar.equals("DistFile"))
						{
						_distFile = value;
						}
//ORIGINAL LINE: case "Normalize":
						else if (tempVar.equals("Normalize"))
						{
						_normalize = Boolean.parseBoolean(value);
						}
						else
						{
						throw new RuntimeException("Invalide line in configuration file: " + line);
						}
					}
					else
					{
						throw new RuntimeException("Invalid line in configuration file: " + line);
					}
				}
			}
		}
		finally
		{
			reader.dispose();
		}
	}
}