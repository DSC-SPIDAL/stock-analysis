import mpi.MPI;
import mpi.MPIException;
import mpi.MPIPacket;
import mpi.MpiOps;
import Salsa.Core.*;
import Salsa.Core.Blas.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Program {
    private String vectorFolder;
    private String distFolder;
    private boolean normalize;

    private static double _dmax = -Double.MAX_VALUE;
    private static double _dmin = Double.MAX_VALUE;

    private MpiOps mpiOps;

    public Program(String vectorFolder, String distFolder, boolean normalize) {
        this.vectorFolder = vectorFolder;
        this.distFolder = distFolder;
        this.normalize = normalize;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Vector file");
        options.addOption("d", true, "Distance file");
        options.addOption("n", false, "normalize");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            boolean _normalize = cmd.hasOption("n");
            MPI.Init(args);
            Program program = new Program(_vectorFile, _distFile, _normalize);
            program.process();
            MPI.Finalize();
        } catch (ParseException | MPIException e) {
            e.printStackTrace();
        }
    }

    private void process() {
        try {
            mpiOps = new MpiOps();
            File inFolder = new File(vectorFolder);

            if (!inFolder.isDirectory()) {
                System.out.println("In should be a folder");
                return;
            }

            for (File fileEntry : inFolder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    continue;
                }

                List<VectorPoint> vecs = ReadVectors(fileEntry);
                int _size = vecs.size();

                int rank = mpiOps.getRank();
                int worldSize = mpiOps.getSize();

                Block[][] processToCloumnBlocks = BlockPartitioner.Partition(_size, _size, worldSize, worldSize);
                Block[] myColumnBlocks = processToCloumnBlocks[rank];

                PartialMatrix myRowStrip = new PartialMatrix(myColumnBlocks[0].RowRange, new Range(0, _size - 1));

                computeDistanceBlocks(myRowStrip, myColumnBlocks, vecs);
                System.out.println("Rank: " + rank + " Done computations");
                _dmin = mpiOps.allReduce(_dmin, MPI.MIN);
                _dmax = mpiOps.allReduce(_dmax, MPI.MAX);
                if (_dmax < 1) { // no need to normalize whe max distance is also less than 1
                    normalize = false;
                }

                if (rank == 0) {
                    System.out.println("Min distance: " + _dmin);
                    System.out.println("Max distance: " + _dmax);
                }

                WriteFullMatrixOnRank0(distFolder + "/" + fileEntry.getName(), _size, rank, myRowStrip, myColumnBlocks[0].RowRange,
                        processToCloumnBlocks[0][0].RowRange, normalize, _dmax);
                mpiOps.barrier();
                if (rank == 0) {
                    System.out.println("Done.");
                }
            }
        } catch (MPIException e) {
            throw new RuntimeException("MPI Error: ", e);
        }
    }

    private void WriteFullMatrixOnRank0(String fileName, int size, int rank, PartialMatrix partialMatrix,
                                               Range myRowRange, Range rootRowRange, boolean normalize, double dmax) {
        int a = size / mpiOps.getSize();
        int b = size % mpiOps.getSize();
        DecimalFormat df = new DecimalFormat("#.00");
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

        Range nextRowRange = null;

//        DataOutputStream writer = null;
        PrintWriter writer = null;
        if (rank == 0) {

            try {
//                writer = new DataOutputStream(new FileOutputStream(fileName));
                writer = new PrintWriter(new FileWriter(fileName));
            } catch (IOException e) {
                throw new RuntimeException("Cannot find filename: " + fileName);
            }

            // I am rank0 and I am the one who will fill the fullMatrix. So let's fill what I have already.
            for (int i = partialMatrix.getGlobalRowStartIndex(); i <= partialMatrix.getGlobalRowEndIndex(); i++) {
                double[] values = partialMatrix.GetRowValues(i);
//                try {
                    for (double value : values) {
                        int val = (int) ((normalize ? value / dmax : value) * Short.MAX_VALUE);
//                        writer.writeShort(val);
                        writer.print(value + " ");
                    }
//                    writer.print("\n");
//                } catch (IOException e) {
//                    throw new RuntimeException("Cannot write to file: " + fileName);
//                }
            }
        }


        // For all the remaining rows that rank0 does not have receive in blocks of rows
        for (int i = rootRowRange.EndIndex + 1; i < size; ) {
            int rowRange[] = new int[2];
            if (rank == 0) {
                // I am rank0 and let's declare the next row range that I want to receive.
                int end = i + a - 1;
                end = end >= size ? size - 1 : end;
                nextRowRange = new Range(i, end);
                rowRange[0] = nextRowRange.StartIndex;
                rowRange[1] = nextRowRange.EndIndex;
            }
            // Announce everyone about the next row ranges that rank0 has declared.
            try {
                mpiOps.broadcast(rowRange, 0);
            } catch (MPIException e) {
                throw new RuntimeException("Failed to do broadcast", e);

            }
            nextRowRange = new Range(rowRange[0], rowRange[1]);

            if (rank == 0) {
                System.out.println("Process: " + rank + " Row range: " + rowRange[0] + ", " + rowRange[1]);
                /* I am rank0 and now let's try to receive the declared next row range from others */

                // A variable to hold the rank of the process, which has the row that I am (rank0) going to receive
                int processRank;

                double[] values = new double[size];
                for (int j = nextRowRange.StartIndex; j <= nextRowRange.EndIndex; j++) {
                    // Let's find the process that has the row j.
                    processRank = j < (b * (a + 1)) ? j / (a + 1) : b + ((j - (b * (a + 1))) / a);

                    // For each row that I (rank0) require I will receive from the process, which has that row.
                    try {
                        //System.out.println("Process: " + rank + "Waiting to recv: " + processRank);
                        MPIPacket p = mpiOps.receive(processRank, 100, MPIPacket.Type.Double);
                        for (int z = 0; z < p.getMArrayLength(); z++) {
                            values[z] = p.getMArrayDoubleAt(z);
                        }
                        // System.out.println("Process: " + rank + "recved: " + processRank + " doubles: " + s);
                        //System.out.println("Process: " + rank + "Reved: " + processRank);
                    } catch (MPIException e) {
                        e.printStackTrace();
                    }

                    // Set the received values in the fullMatrix
                    for (double value : values) {
//                        try {
                            int val = (int) ((normalize ? value / dmax : value) * Short.MAX_VALUE);
//                            double val = value;
//                            writer.writeShort(val);
                            writer.print(df.format(value) + " ");
//                        } catch (IOException e) {
//                            throw new RuntimeException("Cannot write to file: " + fileName);
//                        }
                    }

                    writer.print("\n");
                }
            } else {
				/* I am just an ordinary process and I am ready to give rank0 whatever the row it requests if I have that row */

                // find the intersection of the row ranges of what I (the ordinary process) have and what rank0 wants and then send those rows to rank0
                if (myRowRange.IntersectsWith(nextRowRange)) {
                    Range intersection = myRowRange.GetIntersectionWith(nextRowRange);
                    for (int k = intersection.StartIndex; k <= intersection.EndIndex; k++) {
                        try {
                            //System.out.println("Sending to: " + 0 + " from: " + rank);
                            double[] doubles = partialMatrix.GetRowValues(k);
                            MPIPacket p = MPIPacket.newDoublePacket(doubles.length);
                            String s = "";
                            for (int j = 0; j < doubles.length; j++) {
                                p.setMArrayDoubleAt(j, doubles[j]);
                                s += doubles[j] + " ";
                            }
                            // System.out.println("Sending to: " + 0 + " from: " + rank + " doubles: " + s);
                            mpiOps.send(p, 0, 100);
                            //System.out.println("Done send:" + 0 + " from: " + rank);
                        } catch (MPIException e) {
                            throw new RuntimeException("Failed to send the data", e);
                        }
                    }
                }
            }

            i += a;
        }

        // I am rank0 and I came here means that I wrote full matrix to disk. So I will clear the writer and stream.
        if (rank == 0) {
//            try {
                writer.flush();
                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private static void computeDistanceBlocks(PartialMatrix myRowStrip, Block[] myColumnBlocks, java.util.List<VectorPoint> vecs) {
        for (Block block : myColumnBlocks) {
            for (int r = block.RowRange.StartIndex; r <= block.RowRange.EndIndex; ++r) {
                VectorPoint vr = vecs.get(r);
                for (int c = block.ColumnRange.StartIndex; c <= block.ColumnRange.EndIndex; ++c) {
                    VectorPoint vc = vecs.get(c);
                    double dist = vr.correlation(vc);
                    myRowStrip.setValue(r , c, dist);
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

    private List<VectorPoint> ReadVectors(File file) {
        List<VectorPoint> vecs = new ArrayList<VectorPoint>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                String parts[] = line.split(" ");
                int key = Integer.parseInt(parts[0]);
                int vectorLength = parts.length - 1;
                double[] numbers = new double[vectorLength];
                if (vectorLength != parts.length - 1) {
                    throw new RuntimeException("The number of points in file " + (parts.length - 1) +
                            " is not equal to the expected value: " + vectorLength);
                }

                for (int i = 1; i < parts.length; i++) {
                    numbers[i - 1] = Double.parseDouble(parts[i]);
                }
                VectorPoint p = new VectorPoint(key, numbers);
                vecs.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vecs;
    }
}