import mpi.MPI;
import mpi.MPIException;
import mpi.MpiOps;
import Salsa.Core.*;
import Salsa.Core.Blas.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Program {
	private static String _vectorFile;
	private static String _distFile;
	private static boolean _normalize;

	private static int _size;
	private static double _dmax = -Double.MAX_VALUE;
	private static double _dmin = Double.MAX_VALUE;
    private static int vectorLength = 30;

    private static MpiOps mpiOps;

	static void main(String[] args) {
        ReadConfiguration(args);
        try {
            MPI.Init(args);
            List<VectorPoint> vecs = ReadVectors();
            _size = vecs.size();

            int rank = mpiOps.getRank();
            int worldSize = mpiOps.getSize();

            Block[][] processToCloumnBlocks = BlockPartitioner.Partition(_size, _size, worldSize, worldSize);
            Block[] myColumnBlocks = processToCloumnBlocks[rank];

            PartialMatrix<Double> myRowStrip = new PartialMatrix<Double>(myColumnBlocks[0].RowRange, new Range(0, _size - 1));

            ComputeDistanceBlocks(myRowStrip, myColumnBlocks, vecs);
            _dmin = mpiOps.allReduce(_dmin, MPI.MIN);
            _dmax = mpiOps.allReduce(_dmax, MPI.MAX);

            if (_dmax < 1) { // no need to normalize whe max distance is also less than 1
                _normalize = false;
            }

            if (rank == 0) {
                System.out.println("Min distance: " + _dmin);
                System.out.println("Max distance: " + _dmax);
            }

            WriteFullMatrixOnRank0(_distFile, _size, rank, myRowStrip, myColumnBlocks[0].RowRange,
                    processToCloumnBlocks[0][0].RowRange, _normalize, _dmax);
            mpiOps.barrier();
            if (rank == 0) {
                System.out.println("Done.");
            }
        } catch (MPIException e) {
            throw new RuntimeException("MPI Error: ", e);
        }
    }

	private static void WriteFullMatrixOnRank0(String fileName, int size, int rank, PartialMatrix<Double> partialMatrix,
                                               Range myRowRange, Range rootRowRange, boolean normalize, double dmax) {
        int a = size / mpiOps.getSize();
        int b = size % mpiOps.getSize();

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

        DataOutputStream writer = null;
        if (rank == 0) {

            try {
                writer = new DataOutputStream(new FileOutputStream(fileName));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Cannot find filename: " + fileName);
            }

            // I am rank0 and I am the one who will fill the fullMatrix. So let's fill what I have already.
            for (int i = partialMatrix.getGlobalRowStartIndex(); i <= partialMatrix.getGlobalRowEndIndex(); i++) {
                Double[] values = partialMatrix.GetRowValues(i);
                for (double value : values) {
                    try {
                        writer.writeShort((int) ((normalize ? value / dmax : value) * Short.MAX_VALUE));
                    } catch (IOException e) {
                        throw new RuntimeException("Cannot write to file: " + fileName);
                    }
                }
            }
        }


        // For all the remaining rows that rank0 does not have receive in blocks of rows
        for (int i = rootRowRange.EndIndex + 1; i < size; ) {
            if (rank == 0) {
                // I am rank0 and let's declare the next row range that I want to receive.
                int end = i + numOfRowsPerReceive - 1;
                end = end >= size ? size - 1 : end;
                nextRowRange = new Range(i, end);
            }

            // Announce everyone about the next row ranges that rank0 has declared.
            tangible.RefObject<Range> tempRef_nextRowRange = new tangible.RefObject<Range>(nextRowRange);
            mpiOps.broadcast(tempRef_nextRowRange, 0);
            nextRowRange = tempRef_nextRowRange.argValue;

            if (rank == 0) {
				/* I am rank0 and now let's try to receive the declared next row range from others */

                // A variable to hold the rank of the process, which has the row that I am (rank0) going to receive
                int processRank;

                double[] values = new double[];
                for (int j = nextRowRange.StartIndex; j <= nextRowRange.EndIndex; j++) {
                    // Let's find the process that has the row j.
                    processRank = j < (b * (a + 1)) ? j / (a + 1) : b + ((j - (b * (a + 1))) / a);

                    // For each row that I (rank0) require I will receive from the process, which has that row.
                    try {
                        mpiOps.getComm().recv(values, 0, MPI.DOUBLE, processRank, 100);
                    } catch (MPIException e) {
                        e.printStackTrace();
                    }

                    // Set the received values in the fullMatrix
                    for (double value : values) {
                        try {
                            writer.writeShort((int) ((normalize ? value / dmax : value) * Short.MAX_VALUE));
                        } catch (IOException e) {
                            throw new RuntimeException("Cannot write to file: " + fileName);
                        }
                    }
                }
            } else {
				/* I am just an ordinary process and I am ready to give rank0 whatever the row it requests if I have that row */

                // find the intersection of the row ranges of what I (the ordinary process) have and what rank0 wants and then send those rows to rank0
                if (myRowRange.IntersectsWith(nextRowRange)) {
                    Range intersection = myRowRange.GetIntersectionWith(nextRowRange);
                    for (int k = intersection.StartIndex; k <= intersection.EndIndex; k++) {
                        try {
                            mpiOps.getComm().send(partialMatrix.GetRowValues(k), 0, MPI.DOUBLE, 0, 100);
                        } catch (MPIException e) {
                            throw new RuntimeException("Failed to send the data", e);
                        }
                    }
                }
            }

            i += numOfRowsPerReceive;
        }

        // I am rank0 and I came here means that I wrote full matrix to disk. So I will clear the writer and stream.
        if (rank == 0) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

    private static void ComputeDistanceBlocks(PartialMatrix<Double> myRowStrip, Block[] myColumnBlocks, java.util.List<VectorPoint> vecs) {
        for (Block block : myColumnBlocks) {
            for (int r = block.RowRange.StartIndex; r <= block.RowRange.EndIndex; ++r) {
                VectorPoint vr = vecs.get(r);
                for (int c = block.ColumnRange.StartIndex; c <= block.ColumnRange.EndIndex; ++c) {
                    VectorPoint vc = vecs.get(c);
                    double dist = vr.correlation(vc);
                    myRowStrip[r][c] = dist;
                    if (dist > _dmax) {
                        _dmax = dist;
                    }

                    if (dist < _dmin) {
                        _dmin = dist;
                    }
                }
            }
        }
    }

	private static List<VectorPoint> ReadVectors() {
		List<VectorPoint> vecs = new ArrayList<VectorPoint>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(_vectorFile));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                String parts[] = line.split(" ");
                String key = parts[0];
                double []numbers = new double[vectorLength];

                if (vectorLength != parts.length - 1) {
                    throw new RuntimeException("The number of points in file " + (parts.length - 1) +
                            " is not equal to the expected value: " + vectorLength);
                }

                for (int i = 1; i < parts.length; i++) {
                    numbers[i] = Double.parseDouble(parts[i]);
                }
                VectorPoint p = new VectorPoint(key, numbers);
                vecs.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return vecs;
	}

    private static void ReadConfiguration(String []args) {
        Options options = new Options();
        options.addOption("v", true, "Vector file");
        options.addOption("d", true, "Distance file");
        options.addOption("n", false, "normalize");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            _vectorFile = cmd.getOptionValue("v");
            _distFile = cmd.getOptionValue("d");
            _normalize = cmd.hasOption("n");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}