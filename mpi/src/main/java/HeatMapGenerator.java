//
//import mpi.*;
//import Salsa.Core.*;
//import Salsa.Core.Blas.*;
//
//import java.io.*;
//import java.io.File;
//import java.util.Arrays;
//import java.util.Properties;
//
//public class HeatMapGenerator {
//    private static String _aMat;
//    private static String _bMat;
//    private static int _cols;
//    private static int _rows;
//    private static String _outdir;
//    private static boolean _useTDistanceMaxForA;
//    private static boolean _useTDistanceMaxForB;
//    private static double _xmaxbound = 1.0;
//    private static double _ymaxbound = 1.0;
//    private static int _xres = 50;
//    private static int _yres = 50;
//    private static double _alpha = 2.0;
//    private static double _pcutf = 0.85;
//    private static boolean _zto1 = true;
//    private static int _aTransfm, _bTransfm;
//    private static double _aTransfp, _bTransfp;
//    private static double _distcutA;
//    private static double _distcutB;
//    private static double _mindistA;
//    private static double _mindistB;
//    private static String _aName;
//    private static String _bName;
//    private static String _clusterfile;
//    private static String _title;
//    private static double _lengthCut;
//    private static boolean _useClusters; // indicates if clustering data is available
//    private static double[] _denomcuts;
//    private static String _oldscoremat;
//    private static String _newscoremat;
//    private static boolean _denomcutsenabled;
//    private static boolean _readPointsA; // indicates if the distance file is actually a 3D coordinate file for A
//    private static boolean _readPointsB; // indicates if the distance file is actually a 3D coordinate file for B
//
//    private static double[] _xmaxWhole, _xminWhole, _ymaxWhole, _yminWhole;
//    private static double[] _xmaxSelected, _xminSelected, _ymaxSelected, _yminSelected;
//    private static double[] _xmaxSelectedInter, _xminSelectedInter, _ymaxSelectedInter, _yminSelectedInter;
//
//    private static double[] _deltaxWhole, _deltayWhole, _deltaxSelected, _deltaySelected, _deltaxSelectedInter, _deltaySelectedInter;
//
//    // Surface area of each small 2D square
//    private static double[] _deltasWhole, _deltasSelected, _deltasSelectedInter;
//
//    private static long _totalPairs = 0, _totalIntraPairs = 0, _totalInterPairs = 0;
//    private static long[] _consideredPairs, _consideredPairsIntra, _consideredPairsInter;
//
//    private static final java.util.HashSet<Integer> SelectedCnums = new java.util.HashSet<Integer>();
//    private static final java.util.Hashtable PnumToCnum = new java.util.Hashtable();
//    //private static java.util.ArrayList<ISequence> _seqs = new java.util.ArrayList<ISequence>();
//
//    private static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Usage: ScatterLargeScale.exe <configfile>");
//        }
//
//        tangible.RefObject<String> tempRef_args = new tangible.RefObject<String>(args);
//        try {
//            ReadConfiguration(args[0]);
//            InitializeArrays();
//
//            if (_denomcutsenabled) {
//                Arrays.sort(_denomcuts);
//            }
//
//            if (_useClusters) {
//                PopulatePnumToCnum();
//            }
//            MPI.Init(args);
//            int rank = MPI.COMM_WORLD.getRank();
//            int worldSize = MPI.COMM_WORLD.getSize();
//
//            Block[][] processToCloumnBlocks = BlockPartitioner.Partition(_rows, _cols, worldSize, worldSize);
//            Block[] myColumnBlocks = processToCloumnBlocks[rank];
//
//            PartialMatrix myRowStripMatrixForA = new PartialMatrix(myColumnBlocks[0].RowRange, new Range(0, _cols - 1));
//            PartialMatrix myRowStripMatrixForB = new PartialMatrix(myColumnBlocks[0].RowRange, new Range(0, _cols - 1));
////C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
////ORIGINAL LINE: PartialMatrix<byte> myRowStripMatrixForDenomCut = new PartialMatrix<byte>(myColumnBlocks[0].RowRange, new Range(0, _cols - 1));
//            PartialMatrix<Byte> myRowStripMatrixForDenomCut = new PartialMatrix<Byte>(myColumnBlocks[0].RowRange, new Range(0, _cols - 1));
//
//            InitalizeDenomMask(myColumnBlocks, myRowStripMatrixForDenomCut);
//            ReadDistanceBlocks(myRowStripMatrixForA, myRowStripMatrixForB, myColumnBlocks, myRowStripMatrixForDenomCut);
//
//            _xminWhole = Communicator.world.Allreduce(_xminWhole, Operation < Double >.Min);
//            _xmaxWhole = Communicator.world.Allreduce(_xmaxWhole, Operation < Double >.Max);
//            _yminWhole = Communicator.world.Allreduce(_yminWhole, Operation < Double >.Min);
//            _ymaxWhole = Communicator.world.Allreduce(_ymaxWhole, Operation < Double >.Max);
//
//            _totalPairs = Communicator.world.Reduce(_totalPairs, Operation < Long >.Add, 0);
//            _consideredPairs = Communicator.world.Reduce(_consideredPairs, Operation < Long >.Add, 0);
//
//
//            if (_useClusters) {
//                _xminSelected = Communicator.world.Allreduce(_xminSelected, Operation < Double >.Min);
//                _xmaxSelected = Communicator.world.Allreduce(_xmaxSelected, Operation < Double >.Max);
//                _yminSelected = Communicator.world.Allreduce(_yminSelected, Operation < Double >.Min);
//                _ymaxSelected = Communicator.world.Allreduce(_ymaxSelected, Operation < Double >.Max);
//
//                _xminSelectedInter = Communicator.world.Allreduce(_xminSelectedInter, Operation < Double >.Min);
//                _xmaxSelectedInter = Communicator.world.Allreduce(_xmaxSelectedInter, Operation < Double >.Max);
//                _yminSelectedInter = Communicator.world.Allreduce(_yminSelectedInter, Operation < Double >.Min);
//                _ymaxSelectedInter = Communicator.world.Allreduce(_ymaxSelectedInter, Operation < Double >.Max);
//
//                _totalIntraPairs = Communicator.world.Reduce(_totalIntraPairs, Operation < Long >.Add, 0);
//                _totalInterPairs = Communicator.world.Reduce(_totalInterPairs, Operation < Long >.Add, 0);
//
//                _consideredPairsIntra = Communicator.world.Reduce(_consideredPairsIntra, Operation < Long >.Add, 0);
//                _consideredPairsInter = Communicator.world.Reduce(_consideredPairsInter, Operation < Long >.Add, 0);
//            }
//
//            // Output min/max
//            if (rank == 0) {
//                System.out.println("**************************************************");
//                System.out.println("Denomcut Enabled: " + _denomcutsenabled);
//                for (int i = 0; i < _denomcuts.length; i++) {
//                    System.out.printf("\n\tDenomcut: %1$s", _denomcuts[i], "\r\n");
//                    System.out.printf("\txmaxwhole:%1$s xminwhole:%2$s ymaxwhole:%3$s yminwhole:%4$s", _xmaxWhole[i], _xminWhole[i], _ymaxWhole[i], _yminWhole[i], "\r\n");
//                    if (_useClusters) {
//                        System.out.printf("\txmaxselected:%1$s xminselected:%2$s ymaxselected:%3$s yminselected:%4$s", _xmaxSelected[i], _xminSelected[i], _ymaxSelected[i], _yminSelected[i], "\r\n");
//                        System.out.printf("\txmaxselectedinter:%1$s xminselectedinter:%2$s ymaxselectedinter:%3$s yminselectedinter:%4$s", _xmaxSelectedInter[i], _xminSelectedInter[i], _ymaxSelectedInter[i], _yminSelectedInter[i], "\r\n");
//                    }
//                }
//            }
//
//            for (int i = 0; i < _denomcuts.length; i++) {
//                // global xmax, xmin, ymax, and ymin should be set by now
//                _deltaxWhole[i] = (_xmaxWhole[i] - _xminWhole[i]) / _xres;
//                _deltayWhole[i] = (_ymaxWhole[i] - _yminWhole[i]) / _yres;
//                _deltasWhole[i] = _deltaxWhole[i] * _deltayWhole[i];
//
//                if (_useClusters) {
//                    _deltaxSelected[i] = (_xmaxSelected[i] - _xminSelected[i]) / _xres;
//                    _deltaySelected[i] = (_ymaxSelected[i] - _yminSelected[i]) / _yres;
//                    _deltasSelected[i] = _deltaxSelected[i] * _deltaySelected[i];
//
//                    _deltaxSelectedInter[i] = (_xmaxSelectedInter[i] - _xminSelectedInter[i]) / _xres;
//                    _deltaySelectedInter[i] = (_ymaxSelectedInter[i] - _yminSelectedInter[i]) / _yres;
//                    _deltasSelectedInter[i] = _deltaxSelectedInter[i] * _deltaySelectedInter[i];
//                }
//            }
//
//            long[][][] histCellsForWholeSample = new long[_denomcuts.length][][];
//            long[][][] histCellsForSelectedClusters = new long[_denomcuts.length][][];
//            long[][][] histCellsForSelectedClustersInter = new long[_denomcuts.length][][];
//            for (int i = 0; i < _denomcuts.length; i++) {
//                histCellsForWholeSample[i] = new long[_yres][];
//                histCellsForSelectedClusters[i] = new long[_yres][];
//                histCellsForSelectedClustersInter[i] = new long[_yres][];
//                for (int j = 0; j < _yres; j++) {
//                    histCellsForWholeSample[i][j] = new long[_xres];
//                    histCellsForSelectedClusters[i][j] = new long[_xres];
//                    histCellsForSelectedClustersInter[i][j] = new long[_xres];
//                    for (int k = 0; k < _xres; k++) {
//                        histCellsForWholeSample[i][j][k] = 0;
//                        histCellsForSelectedClusters[i][j][k] = 0;
//                        histCellsForSelectedClustersInter[i][j][k] = 0;
//                    }
//                }
//            }
//            GeneratePartialHistograms(histCellsForWholeSample, histCellsForSelectedClusters, histCellsForSelectedClustersInter, myRowStripMatrixForA, myRowStripMatrixForB, myColumnBlocks, myRowStripMatrixForDenomCut);
//
//            histCellsForWholeSample = Communicator.world.Reduce(histCellsForWholeSample, Sum2DArray, 0);
//
//            if (_useClusters) {
//                histCellsForSelectedClusters = Communicator.world.Reduce(histCellsForSelectedClusters, Sum2DArray, 0);
//                histCellsForSelectedClustersInter = Communicator.world.Reduce(histCellsForSelectedClustersInter, Sum2DArray, 0);
//            }
//
//
//            if (rank == 0) {
//                // Rank 0 should have all the cells from each process by now.
//                for (int i = 0; i < _denomcuts.length; i++) {
//                    double denomcut = _denomcuts[i];
//
//                    double pairFraction = ((double) _consideredPairs[i]) / _totalPairs;
//
//                    System.out.println("Rank 0 starting to write density data file for whole sample with denomcut " + denomcut);
//                    GenerateDensityDataFile(histCellsForWholeSample[i], _xmaxWhole[i], _xminWhole[i], _ymaxWhole[i], _yminWhole[i], _deltaxWhole[i], _deltayWhole[i], _deltasWhole[i], "whole", denomcut, pairFraction, _totalPairs);
//                    System.out.println("Rank 0 done writing density data file for whole sample with denomcut " + denomcut);
//
//                    if (_useClusters) {
//                        pairFraction = ((double) _consideredPairsIntra[i]) / _totalIntraPairs;
//                        System.out.println("Rank 0 starting to write density data file for selected clusters with denomcut " + denomcut);
//                        GenerateDensityDataFile(histCellsForSelectedClusters[i], _xmaxSelected[i], _xminSelected[i], _ymaxSelected[i], _yminSelected[i], _deltaxSelected[i], _deltaySelected[i], _deltasSelected[i], "selected", denomcut, pairFraction, _totalIntraPairs);
//                        System.out.println("Rank 0 done writing density data file for selected clusters with denomcut " + denomcut);
//
//                        pairFraction = ((double) _consideredPairsInter[i]) / _totalInterPairs;
//                        System.out.println("Rank 0 starting to write density data file for selected clusters inter with denomcut " + denomcut);
//                        GenerateDensityDataFile(histCellsForSelectedClustersInter[i], _xmaxSelectedInter[i], _xminSelectedInter[i], _ymaxSelectedInter[i], _yminSelectedInter[i], _deltaxSelectedInter[i], _deltaySelectedInter[i], _deltasSelectedInter[i], "selected-inter", denomcut, pairFraction, _totalInterPairs);
//                        System.out.println("Rank 0 done writing density data file for selected clusters inter with denomcut " + denomcut);
//                    }
//                }
//            }
//
//            MPI.Finalize();
//        } catch (IOException | MPIException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void InitalizeDenomMask(Block[] myColumnBlocks, PartialMatrix myRowStripMatrixForDenomCut) {
//        for (Block block : myColumnBlocks) {
//            for (int r = block.RowRange.StartIndex; r <= block.RowRange.EndIndex; ++r) {
//                for (int c = block.ColumnRange.StartIndex; c <= block.ColumnRange.EndIndex; ++c) {
//                    //ORIGINAL LINE: myRowStripMatrixForDenomCut[r, c] = byte.MaxValue;
//                    // TODO FIX BYTE -> DOUBLE
//                    myRowStripMatrixForDenomCut.setValue(r, c, Byte.MAX_VALUE);
//                }
//            }
//        }
//    }
//
//    private static void InitializeArrays() {
//        int length = _denomcuts.length; // if denomcuts not enabled then this will be just 1
//        _xmaxWhole = new double[length];
//        _xminWhole = new double[length];
//        _ymaxWhole = new double[length];
//        _yminWhole = new double[length];
//
//        _xmaxSelected = new double[length];
//        _xminSelected = new double[length];
//        _ymaxSelected = new double[length];
//        _yminSelected = new double[length];
//
//        _xmaxSelectedInter = new double[length];
//        _xminSelectedInter = new double[length];
//        _ymaxSelectedInter = new double[length];
//        _yminSelectedInter = new double[length];
//
//        _deltaxWhole = new double[length];
//        _deltayWhole = new double[length];
//        _deltaxSelected = new double[length];
//        _deltaySelected = new double[length];
//        _deltaxSelectedInter = new double[length];
//        _deltaySelectedInter = new double[length];
//
//        _deltasWhole = new double[length];
//        _deltasSelected = new double[length];
//        _deltasSelectedInter = new double[length];
//
//        _consideredPairs = new long[length];
//        _consideredPairsIntra = new long[length];
//        _consideredPairsInter = new long[length];
//
//        for (int i = 0; i < length; i++) {
//            _xmaxWhole[i] = _xmaxSelected[i] = _xmaxSelectedInter[i] = Double.NEGATIVE_INFINITY;
//            _ymaxWhole[i] = _ymaxSelected[i] = _ymaxSelectedInter[i] = Double.NEGATIVE_INFINITY;
//
//            _xminWhole[i] = _xminSelected[i] = _xminSelectedInter[i] = Double.POSITIVE_INFINITY;
//            _yminWhole[i] = _yminSelected[i] = _yminSelectedInter[i] = Double.POSITIVE_INFINITY;
//
//            _consideredPairs[i] = 0;
//            _consideredPairsIntra[i] = 0;
//            _consideredPairsInter[i] = 0;
//        }
//    }
//
//    private static void PopulatePnumToCnum() {
//        try (BufferedReader reader = new BufferedReader(new FileReader(_clusterfile))) {
//            String line;
//            char[] sep = new char[]{' ', '\t'};
//            while ((line = reader.readLine()) != null) {
//                String[] splits = line.split(java.util.regex.Pattern.quote(Arrays.toString(sep)), -1);
//                int idx = Integer.parseInt(splits[0]);
//                int cnum = Integer.parseInt(splits[1]);
//                PnumToCnum.put(idx, cnum);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private static void GenerateDensityDataFile(long[][] cells, double xmax, double xmin, double ymax, double ymin, double deltax, double deltay, double deltas, String prefix, double denomcut, double pairFraction, long totalPairs) throws IOException {
//        long[] xHist = new long[_xres];
//        long[] yHist = new long[_yres];
//
//        for (int i = 0; i < _xres; i++) {
//            xHist[i] = 0;
//        }
//
//        for (int i = 0; i < _yres; i++) {
//            yHist[i] = 0;
//        }
//
//        long cellmax = 0, count = 0, v;
//        for (int i = 0; i < _yres; i++) {
//            for (int j = 0; j < _xres; j++) {
//                v = cells[i][j];
//                xHist[j] += v;
//                yHist[i] += v;
//                count += v;
//                if (v > cellmax) {
//                    cellmax = v;
//                }
//            }
//        }
//
//        double cellmean = ((double) count) / (_xres * _yres);
//        double power = cellmax < (_alpha * cellmean) ? 1.0 : (Math.log(_alpha) / Math.log(cellmax / cellmean));
//        // Constant value by which the number of points in a 2D square is multiplied.
//        // The resulting value is independent of the total number of points as well as
//        // the x,y resolution. The mult value is a factor changing the z value scale.
//        double c = _zto1 ? (1.0 / cellmax) : (1.0 / (count * deltas));
//
//        // Output density values
//        System.out.println("***************************************************************");
//        System.out.println("DataSet\t" + prefix);
//        System.out.println("CellMean\t" + cellmean);
//        System.out.println("CellMax\t" + cellmax);
//        System.out.println("Power\t" + power);
//        System.out.println("Const\t" + c);
//        System.out.println("TotalPairs\t" + totalPairs);
//        System.out.println("PairFraction\t" + pairFraction);
//        for (int i = 0; i < 10; i++) {
//            double density = i / 10.0;
//            double densityToCount = Math.pow(density, (1 / power)) / c;
//            System.out.println(density + "\t" + densityToCount);
//        }
//        System.out.println("***************************************************************");
//
//        int xpointcount = 2 * _xres;
//        int ypointcount = 2 * _yres;
//
//        String aNameFinal = _aTransfm > -1 ? "Transformed-" + _aName : _aName;
//        String bNameFinal = _bTransfm > -1 ? "Transformed-" + _bName : _bName;
//
//        String dCutStringA = "DCut[" + (_mindistA > -1 ? (new Double(_mindistA)).toString() : "none") + "," + (_distcutA > -1 ? (new Double(_distcutA)).toString() : "none") + "]";
//        String dCutStringB = "DCut[" + (_mindistB > -1 ? (new Double(_mindistB)).toString() : "none") + "," + (_distcutB > -1 ? (new Double(_distcutB)).toString() : "none") + "]";
//        String vsString = bNameFinal + "-Vs-" + aNameFinal;
//
//        String dir = _outdir;
//        if (_denomcutsenabled) {
//            dir = combine(_outdir, "denomcut_" + (new Double(denomcut)).toString());
//            (new java.io.File(dir)).mkdir();
//        }
//
//        String densityFile = combine(dir, prefix + "-density-" + dCutStringA + "-" + dCutStringB + "-" + vsString + ".txt");
//        String xHistFile = combine(dir, prefix + "-xHist-" + dCutStringA + "-" + dCutStringB + "-" + vsString + ".txt");
//        String yHistFile = combine(dir, prefix + "-yHist-" + dCutStringA + "-" + dCutStringB + "-" + vsString + ".txt");
//        String gnuplotScriptFileLarge = combine(dir, prefix + "-gnuplot-" + dCutStringA + "-" + dCutStringB + "-" + vsString + "-large.txt");
//        String gnuplotScriptFileSmall = combine(dir, prefix + "-gnuplot-" + dCutStringA + "-" + dCutStringB + "-" + vsString + "-small.txt");
//
//        String plotBat = combine(dir, "plot.bat");
//
//        PrintWriter densityFileWriter = new PrintWriter(new FileWriter(densityFile));
//        PrintWriter xHistWriter = new PrintWriter(new FileWriter(xHistFile));
//        PrintWriter yHistWriter = new PrintWriter(new FileWriter(yHistFile));
//        PrintWriter gnuplotWriterLarge = new PrintWriter(new FileWriter(gnuplotScriptFileLarge));
//        PrintWriter gnuplotWriterSmall = new PrintWriter(new FileWriter(gnuplotScriptFileSmall));
//        PrintWriter plotBatWriter = new PrintWriter(new FileWriter(plotBat, true));
//        try {
//            // Generating plot bat
//            plotBatWriter.println("gnuplot " + (new java.io.File(gnuplotScriptFileLarge)).getName());
//            densityFileWriter.println("#xcoord\tycoord\thistogramValue");
//            xHistWriter.println("#xval\thistogramvalue");
//            yHistWriter.println("#yval\thistogramvalue");
//
//            // Generating x histogram
//            double xoffset = xmin + 0.5 * deltax;
//            for (int i = 0; i < _xres; ++i) {
//                double xcoord = xoffset + i * deltax;
//                xHistWriter.println(xcoord + "\t" + xHist[i]);
//            }
//
//            // Generating y histogram
//            double yoffset = ymin + 0.5 * deltay;
//            for (int i = 0; i < _yres; ++i) {
//                double ycoord = yoffset + i * deltay;
//                yHistWriter.println(ycoord + "\t" + yHist[i]);
//            }
//
//            for (int i = 0; i < xpointcount; i++) {
//                double x = xmin + ((IsOdd(i) ? (i + 1) / 2 : i / 2) * deltax);
//                int cellx = IsOdd(i) ? (i - 1) / 2 : i / 2;
//
//                for (int j = 0; j < ypointcount; j++) {
//                    double y = ymin + ((IsOdd(j) ? (j + 1) / 2 : j / 2) * deltay);
//                    int celly = IsOdd(j) ? (j - 1) / 2 : j / 2;
//
//                    double cellvalue = Math.pow((cells[celly][cellx] * c), power);
//
//                    // todo: commented for now
//                    // cellvalue = cellvalue > pcutf ? pcutf : cellvalue < ncutf ? ncutf : cellvalue;
//                    cellvalue = _pcutf > -1 && cellvalue > _pcutf ? _pcutf : cellvalue;
//
//                    densityFileWriter.println(x + "\t" + y + "\t" + cellvalue);
//                }
//                densityFileWriter.println();
//            }
//
//            if (_xmaxbound == -1) {
//                _xmaxbound = xmax;
//            }
//
//            if (_ymaxbound == -1) {
//                _ymaxbound = ymax;
//            }
//
//            // Fill up the remaining region from beyond x=xmax and y=ymax as zero
//            densityFileWriter.println();
//            densityFileWriter.println(xmin + "\t" + ymax + "\t" + 0.0);
//            densityFileWriter.println(xmin + "\t" + _ymaxbound + "\t" + 0.0);
//            densityFileWriter.println();
//            densityFileWriter.println(xmax + "\t" + ymax + "\t" + 0.0);
//            densityFileWriter.println(xmax + "\t" + _ymaxbound + "\t" + 0.0);
//            densityFileWriter.println();
//            densityFileWriter.println(xmax + "\t" + ymin + "\t" + 0.0);
//            densityFileWriter.println(xmax + "\t" + _ymaxbound + "\t" + 0.0);
//            densityFileWriter.println();
//            densityFileWriter.println(_xmaxbound + "\t" + ymin + "\t" + 0.0);
//            densityFileWriter.println(_xmaxbound + "\t" + _ymaxbound + "\t" + 0.0);
//
//
//            //Tangible multiline preserve/* Writing Gnuplot script */
//            WriteGnuplotScript(bNameFinal, aNameFinal, prefix, vsString, densityFile, xHistFile, yHistFile, gnuplotWriterLarge, gnuplotWriterSmall, denomcut, pairFraction, totalPairs);
//        } finally {
//            densityFileWriter.close();
//            xHistWriter.close();
//            yHistWriter.close();
//            gnuplotWriterLarge.close();
//            gnuplotWriterSmall.close();
//            plotBatWriter.close();
//        }
//    }
//
//    private static boolean IsOdd(int value) {
//        return (value & 1) == 1;
//    }
//
//    private static String combine(String path1, String path2) {
//        File file1 = new File(path1);
//        File file2 = new File(file1, path2);
//        return file2.getPath();
//    }
//
//    private static void WriteGnuplotScript(String bNameFinal, String aNameFinal,
//                                           String prefix, String vsString, String densityFile,
//                                           String xHistFile, String yHistFile,
//                                           PrintWriter gnuplotWriterLarge, PrintWriter gnuplotWriterSmall,
//                                           double denomcut, double pairFraction, long totalPairs) {
//        gnuplotWriterLarge.println("set terminal png truecolor nocrop font arial 14 size 1200,1200");
//        gnuplotWriterSmall.println("set terminal png truecolor nocrop font arial 14 size 1000,500");
//
//        gnuplotWriterLarge.println();
//
//        String pngfile = prefix + "-plot-" + vsString + "DensitySat[" + (_pcutf > -1 ? (new Double(_pcutf)).toString() : "none") + "]" + (_denomcutsenabled ? "-DenomCut[" + denomcut + "]-" : "-") + "large.png";
//        gnuplotWriterLarge.println("set output '" + pngfile + "'");
//        pngfile = prefix + "-plot-" + vsString + "DensitySat[" + (_pcutf > -1 ? (new Double(_pcutf)).toString() : "none") + "]" + (_denomcutsenabled ? "-DenomCut[" + denomcut + "]-" : "-") + "small.png";
//        gnuplotWriterSmall.println("set output '" + pngfile + "'");
//
//        gnuplotWriterLarge.println("set size 1.0, 1.0");
//        gnuplotWriterLarge.println("set multiplot");
//        gnuplotWriterSmall.println("set multiplot");
//
//        gnuplotWriterLarge.println();
//        gnuplotWriterSmall.println();
//
//        // Title box
//        gnuplotWriterLarge.println("set origin 0.0, 0.85");
////            gnuplotWriterSmall.WriteLine("set origin 0.0, 0.7");
//        gnuplotWriterLarge.println("set size 0.95, 0.1");
////            gnuplotWriterSmall.WriteLine("set size 0.95, 0.1");
//        gnuplotWriterLarge.println("set border linecolor rgbcolor \"white\"");
////            gnuplotWriterSmall.WriteLine("set border linecolor rgbcolor \"white\"");
//        gnuplotWriterLarge.println("unset key");
////            gnuplotWriterSmall.WriteLine("unset key");
//        String dcutStringA = (_mindistA > -1 ? (new Double(_mindistA)).toString() : "none") + "," + (_distcutA > -1 ? (new Double(_distcutA)).toString() : "none");
//        String dcutStringB = (_mindistB > -1 ? (new Double(_mindistB)).toString() : "none") + "," + (_distcutB > -1 ? (new Double(_distcutB)).toString() : "none");
//        String title = String.format(_title, (_pcutf > -1 ? (new Double(_pcutf)).toString() : "none"), bNameFinal, dcutStringB, aNameFinal, dcutStringA, (prefix.equals("whole") ? "Whole Sample" : (prefix.equals("selected") ? "Selected Clusters Intra Pairs" : "Selected Clusters Inter Pairs")));
//        if (_denomcutsenabled) {
//            title += "\\nDenomCut[" + denomcut + "] PairFraction[" + Math.round(pairFraction * Math.pow(10, 6)) / Math.pow(10, 6) + "] TotalPairs[" + totalPairs + "]";
//        }
//        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
////            gnuplotWriterSmall.WriteLine("set title \"" + title + "textcolor rgbcolor \"black\"");
//        gnuplotWriterLarge.println("plot [0:1] [0:1] 0.0 lt rgb \"white\"");
////            gnuplotWriterSmall.WriteLine("plot [0:1] [0:1] 0.0 lt rgb \"white\"");
//
//        gnuplotWriterLarge.println("set border linecolor rgbcolor \"black\"");
//        gnuplotWriterSmall.println("set border linecolor rgbcolor \"black\"");
//
//        gnuplotWriterLarge.println("set dummy u,v");
//        gnuplotWriterSmall.println("set dummy u,v");
//
//        gnuplotWriterLarge.println("unset key");
//        gnuplotWriterSmall.println("unset key");
//
//        gnuplotWriterLarge.println("set size ratio 1.0");
////            gnuplotWriterSmall.WriteLine("set size ratio 1.0");
//
//        gnuplotWriterLarge.println("set style fill  solid 0.85 noborder");
//        gnuplotWriterSmall.println("set style fill  solid 0.85 noborder");
//
//        gnuplotWriterLarge.println("set style line 1 lt 1 lw 4");
//        gnuplotWriterSmall.println("set style line 1 lt 1 lw 4");
//
//        gnuplotWriterLarge.println("set pm3d map");
//        gnuplotWriterSmall.println("set pm3d map");
//
//        gnuplotWriterLarge.println("set palette rgbformulae 30,31,32 model RGB negative");
//        gnuplotWriterSmall.println("set palette rgbformulae 30,31,32 model RGB negative");
//
//        gnuplotWriterLarge.println();
//        gnuplotWriterSmall.println();
//
//        // Y histogram (rotated)
//        gnuplotWriterLarge.println("set origin 0.0, 0.45");
//        gnuplotWriterLarge.println("set size 0.45, 0.45");
//        gnuplotWriterLarge.println("set xtics rotate by -90");
//        String xlabel = "Count";
//        gnuplotWriterLarge.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
//        String ylabel = bNameFinal;
//        gnuplotWriterLarge.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
//        title = "Histogram (rotated) of " + bNameFinal + " distances";
//        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterLarge.println("plot [][:" + _ymaxbound + "] '" + (new java.io.File(yHistFile)).getName() + "' using 2:1 with filledcurves y1 lt rgb \"black\"");
//
//        gnuplotWriterLarge.println("set xtics rotate by 0");
//        gnuplotWriterLarge.println();
//
//
//        // Density plot
//        gnuplotWriterLarge.println("set origin 0.45, 0.45");
//        gnuplotWriterSmall.println("set origin 0.0, 0.0");
//        gnuplotWriterLarge.println("set size 0.5, 0.5");
//        gnuplotWriterSmall.println("set size square 0.5, 1.0");
//        xlabel = aNameFinal;
//        gnuplotWriterLarge.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterSmall.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
//        ylabel = bNameFinal;
//        gnuplotWriterLarge.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterSmall.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
//        title = "Heat Map of " + vsString;
//        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterSmall.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterLarge.println("splot [:" + _xmaxbound + "] [:" + _ymaxbound + "] '" + (new java.io.File(densityFile)).getName() + "'");
//        gnuplotWriterSmall.println("splot [:" + _xmaxbound + "] [:" + _ymaxbound + "] '" + (new java.io.File(densityFile)).getName() + "'");
//
//        gnuplotWriterLarge.println("unset pm3d");
//
//
//        gnuplotWriterLarge.println();
//        gnuplotWriterSmall.println();
//
//        // Y histogram (unrotated)
//        gnuplotWriterLarge.println("set origin 0.0, 0.0");
//        gnuplotWriterLarge.println("set size 0.45, 0.45");
//        xlabel = bNameFinal;
//        gnuplotWriterLarge.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
//        ylabel = "Count";
//        gnuplotWriterLarge.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
//        title = "Histogram of " + bNameFinal + " distances";
//        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterLarge.println("plot [:" + _ymaxbound + "] []'" + (new java.io.File(yHistFile)).getName() + "' with filledcurves x1 lt rgb \"black\"");
//
//
//        gnuplotWriterLarge.println();
//
//        // X histogram
//        gnuplotWriterLarge.println("set origin 0.45, 0.0");
//        gnuplotWriterSmall.println("set origin 0.5, 0.08");
//        gnuplotWriterLarge.println("set size 0.45, 0.45");
//        gnuplotWriterSmall.println("set size square 0.5, 0.85");
//        xlabel = aNameFinal;
//        gnuplotWriterLarge.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterSmall.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
//        ylabel = "Count";
//        gnuplotWriterLarge.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterSmall.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
//        title = "Histogram of " + aNameFinal + " distances";
//        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterSmall.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
//        gnuplotWriterLarge.println("plot [:" + _xmaxbound + "] []'" + (new java.io.File(xHistFile)).getName() + "' with filledcurves x1 lt rgb \"black\"");
//        gnuplotWriterSmall.println("plot [:" + _xmaxbound + "] []'" + (new java.io.File(xHistFile)).getName() + "' with filledcurves x1 lt rgb \"black\"");
//
//        gnuplotWriterLarge.println();
//        gnuplotWriterSmall.println();
//
//        gnuplotWriterLarge.println("unset multiplot");
//    }
//
//    private static void UpdateCells(double x, double y,
//                                    double xmax, double xmin,
//                                    double ymax, double ymin,
//                                    double deltax, double deltay,
//                                    long[][] cells, int r, int c) {
//        // cell number based on zero index from bottom left corner
//        // if x is equal to xmax then it's placed in the last cell, which is xres-1 in zero based index
//        // same is done for y when y == ymax
//        int cellx = x == xmax ? _xres - 1 : (int) Math.floor((x - xmin) / deltax);
//        int celly = y == ymax ? _yres - 1 : (int) Math.floor((y - ymin) / deltay);
//
//        if (x > xmax || y > ymax || x < xmin || y < ymin) {
//            // now this should never be reached
//            throw new RuntimeException("bad(1)-> x: " + x + " y: " + y + " xmax: " + xmax + " xmin: " +
//                    xmin + " ymax: " + ymax + " ymin: " + ymin + "lengthcut: " + _lengthCut + " row: " + r + " col: " + c);
//        }
//
//        if (cellx >= _xres || celly >= _yres) {
//            // now this should never be reached
//            throw new RuntimeException("bad(2)-> x: " + x + " y:" + y + " xmax: " + xmax + " xmin: " +
//                    xmin + " ymax: " + ymax + " ymin: " + ymin + " cellx: " + cellx + " celly: " + celly);
//        }
//
//        ++cells[celly][cellx];
//        // todo. remove after testing
////            string cell = cellx + "," + celly;
////            cells[cell] = cells.ContainsKey(cell) ? ((long) cells[cell]) + 1 : 1L;
//    }
//
//    private static void UpdateMinMax(double x, double y,
//                                     tangible.RefObject<Double> xmax, tangible.RefObject<Double> xmin,
//                                     tangible.RefObject<Double> ymax, tangible.RefObject<Double> ymin) {
//        if (x > xmax.argValue) {
//            xmax.argValue = x;
//        }
//        if (x < xmin.argValue) {
//            xmin.argValue = x;
//        }
//        if (y > ymax.argValue) {
//            ymax.argValue = y;
//        }
//        if (y < ymin.argValue) {
//            ymin.argValue = y;
//        }
//    }
//                               hawasa kala nam hari..
//    private static long[][] Sum2DArray(long[][] a, long[][] b) {
//        int r = a.length;
//        int c = a[0].length;
//        long[][] sum = new long[r][];
//        for (int i = 0; i < r; i++) {
//            sum[i] = new long[c];
//            for (int j = 0; j < c; j++) {
//                sum[i][j] = a[i][j] + b[i][j];
//            }
//        }
//        return sum;
//    }
//
//    private static String getProperty(Properties p, String name, String def) {
//        String val = System.getProperty(name);
//        if (val == null) {
//            if (def != null) {
//                val = p.getProperty(name, def);
//                if (val == null) {
//                    throw new RuntimeException("Property not specified in config file: " + name);
//                }
//            } else {
//                val = p.getProperty(name);
//            }
//        }
//        return val;
//    }
//
//    private static void ReadConfiguration(String configFile) {
//        System.out.println(configFile);
//        Properties p = new Properties();
//        try {
//            p.load(new FileInputStream(configFile));
//            _aMat = getProperty(p, "Amat", null);
//            _aName = getProperty(p, "Aname", null);
//            _aTransfm = Integer.parseInt(getProperty(p, "Atransfp", null));
//            _aTransfp = Double.parseDouble(getProperty(p, "Atransfp", null));
//            _bMat = getProperty(p, "Bmat", null);
//            _bName = getProperty(p, "Bname", null);
//            _bTransfm = Integer.parseInt(getProperty(p, "Btransfm", null));
//            _bTransfp = Double.parseDouble(getProperty(p, "Btransfp", null));
//            _useTDistanceMaxForA = Boolean.parseBoolean(getProperty(p, "usetdistancemaxforA", null));
//            _useTDistanceMaxForB = Boolean.parseBoolean(getProperty(p, "usetdistancemaxforB", null));
//            _readPointsA = Boolean.parseBoolean(getProperty(p, "readPointsA", null));
//            _readPointsB = Boolean.parseBoolean(getProperty(p, "readPointsB", null));
//            _cols = Integer.parseInt(getProperty(p, "cols", null));
//            _rows = Integer.parseInt(getProperty(p, "rows", null));
//            _outdir = getProperty(p, "outdir", null);
//            _xmaxbound = Double.parseDouble(getProperty(p, "xmaxbound", null));
//            _ymaxbound = Double.parseDouble(getProperty(p, "ymaxbound", null));
//            _xres = Integer.parseInt(getProperty(p, "xres", null));
//            _yres = Integer.parseInt(getProperty(p, "yres", null));
//            _alpha = Double.parseDouble(getProperty(p, "alpha", null));
//            _pcutf = Double.parseDouble(getProperty(p, "pcutf", null));
//            _zto1 = Boolean.parseBoolean(getProperty(p, "zto1", null));
//            _distcutA = Double.parseDouble(getProperty(p, "distcutA", null));
//            _distcutB = Double.parseDouble(getProperty(p, "distcutB", null));
//            _mindistA = Double.parseDouble(getProperty(p, "mindistA", null));
//            _mindistB = Double.parseDouble(getProperty(p, "mindistB", null));
//            _clusterfile = getProperty(p, "clusterfile", null);
//            if (!"none".equals(_clusterfile) && new File(_clusterfile).isFile()) {
//                _useClusters = true;
//            } else {
//                _useClusters = false;
//            }
//            // We dont use clusters configuration
//            _title = getProperty(p, "title", null);
//            _denomcuts = new double[1];
//            _denomcutsenabled = false;
//            _oldscoremat = getProperty(p, "oldscoremat", null);
//            _newscoremat = getProperty(p, "newscoremat", null);
//        } catch (IOException e) {
//            System.out.println("Failed to read the configuration");
//            e.printStackTrace();
//        }
//    }
//
//    private static void GeneratePartialHistograms(long[][][] histCellsForWholeSample,
//                                                  long[][][] histCellsForSelectedClusters,
//                                                  long[][][] histCellsForSelectedClustersInter,
//                                                  PartialMatrix myRowStripMatrixForA,
//                                                  PartialMatrix myRowStripMatrixForB, Block[] myBlocks,
//                                                  PartialMatrix myRowStripMatrixForDenomCut) {
//        DistanceReader distanceReaderA = null, distanceReaderB = null;
//        if (_readPointsA) {
//            distanceReaderA = new DistanceReader(_aMat, _cols, _rows, _readPointsA);
//        }
//
//        if (_readPointsB) {
//            distanceReaderB = new DistanceReader(_bMat, _cols, _rows, _readPointsB);
//        }
//
//        for (int i = 0; i < myBlocks.length; ++i) {
//            Block block = myBlocks[i];
//            // Non diagonal block
//            for (int r = block.RowRange.StartIndex; r <= block.RowRange.EndIndex; ++r) {
//                long l1 = -1;
//                for (int c = block.ColumnRange.StartIndex; c <= block.ColumnRange.EndIndex; ++c) {
//                    long l2 = -1;
//
//                    // Each pair in block
//                    double x = !_readPointsA ? (_useTDistanceMaxForA ? (myRowStripMatrixForA.getElements()[r][c]) / Double.MAX_VALUE : myRowStripMatrixForA.getElements()[r][c]) : distanceReaderA.ReadDistanceFromPointsFile(r, c);
//                    double y = !_readPointsB ? (_useTDistanceMaxForB ? (myRowStripMatrixForB.getElements()[r][c]) / Double.MAX_VALUE : myRowStripMatrixForB.getElements()[r][c]) : distanceReaderB.ReadDistanceFromPointsFile(r, c);
//
//                    // Ignore x or y values greater than distcutA or discutB respectively when distcut values are specified
//                    if ((_distcutA > -1 && x > _distcutA) || (_distcutB > -1 && y > _distcutB)) {
//                        continue;
//                    }
//
//                    // Ignore x or y values smaller than mindistA or mindistB respectively when mindist values are specified
//                    if ((_mindistA > -1 && x < _mindistA) || (_mindistB > -1 && y < _mindistB)) {
//                        continue;
//                    }
//
//                    // Ignore if the corresponding two sequence lengths are not within the given lengthcut
//                    if (_lengthCut > -1 && (Math.abs(l1 - l2) > _lengthCut * ((l1 + l2) / 2.0))) {
//                        continue;
//                    }
//
//                    // Perform transforms (no transform if transform method is -1 for the respective matrix)
//                    x = Transform(x, _aTransfm, _aTransfp);
//                    y = Transform(y, _bTransfm, _bTransfp);
//
//                    for (int j = 0; j < _denomcuts.length; j++) {
//                        if (myRowStripMatrixForDenomCut.getElements()[r][c] > j) {
//                            UpdateCells(x, y, _xmaxWhole[j], _xminWhole[j], _ymaxWhole[j], _yminWhole[j], _deltaxWhole[j], _deltayWhole[j], histCellsForWholeSample[j], r, c);
//
//                            if (_useClusters) {
//                                int rCnum = ((int) PnumToCnum.get(r));
//                                int cCnum = ((int) PnumToCnum.get(c));
//                                if (SelectedCnums.contains(rCnum) && SelectedCnums.contains(cCnum)) {
//                                    if (rCnum == cCnum) {
//                                        // Intra cluster distances
//                                        UpdateCells(x, y, _xmaxSelected[j], _xminSelected[j], _ymaxSelected[j], _yminSelected[j], _deltaxSelected[j], _deltaySelected[j], histCellsForSelectedClusters[j], r, c);
//                                    } else {
//                                        // Inter cluster distances
//                                        UpdateCells(x, y, _xmaxSelectedInter[j], _xminSelectedInter[j], _ymaxSelectedInter[j], _yminSelectedInter[j], _deltaxSelectedInter[j], _deltaySelectedInter[j], histCellsForSelectedClustersInter[j], r, c);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private static double Transform(double val, int transfm, double transfp) {
//        if (transfm == 10) {
//            val = Math.min(1.0, val);
//            return Math.pow(val, transfp);
//        }
//        return val;
//    }
//
//    private static void ReadDistanceBlocks(PartialMatrix myRowStripMatrixForA, PartialMatrix myRowStripMatrixForB, Block[] myColumnBlocks, PartialMatrix myRowStripMatrixForDenomCut) {
//        DistanceReader matReaderA = new DistanceReader(_aMat, _cols, _rows, _readPointsA);
//        DistanceReader matReaderB = new DistanceReader(_bMat, _cols, _rows, _readPointsB);
//        MatrixReader oldScoreReader = null, newScoreReader = null;
//        if (_denomcutsenabled) {
//            oldScoreReader = new MatrixReader(_oldscoremat, _cols, _rows);
//            newScoreReader = new MatrixReader(_newscoremat, _cols, _rows);
//        }
//        for (int i = 0; i < myColumnBlocks.length; ++i) {
//            Block block = myColumnBlocks[i];
//            for (int r = block.RowRange.StartIndex; r <= block.RowRange.EndIndex; ++r) {
//                long l1 = -1;
//                for (int c = block.ColumnRange.StartIndex; c <= block.ColumnRange.EndIndex; ++c) {
//                    long l2 = -1;
//
//                    // Each pair in block
//                    if (!_readPointsA) {
//                        double tA = matReaderA.ReadDistanceFromMatrix(r, c);
//                        myRowStripMatrixForA.getElements()[r][c] = tA;
//                    }
//                    if (!_readPointsB) {
//                        double tB = matReaderB.ReadDistanceFromMatrix(r, c);
//                        myRowStripMatrixForB.getElements()[r][c] = tB;
//                    }
//                    ++_totalPairs;
//
//                    if (_useClusters) {
//                        int rCnum = ((int) PnumToCnum.get(r));
//                        int cCnum = ((int) PnumToCnum.get(c));
//                        if (SelectedCnums.contains(rCnum) && SelectedCnums.contains(cCnum)) {
//                            if (rCnum == cCnum) {
//                                ++_totalIntraPairs;
//                            } else {
//                                ++_totalInterPairs;
//                            }
//                        }
//                    }
//
//                    double x = !_readPointsA ? (_useTDistanceMaxForA ? ((double) myRowStripMatrixForA.getElements()[r][c]) / Double.MAX_VALUE : myRowStripMatrixForA.getElements()[r][c]) : matReaderA.ReadDistanceFromPointsFile(r, c);
//                    double y = !_readPointsB ? (_useTDistanceMaxForB ? ((double) myRowStripMatrixForB.getElements()[r][c]) / Double.MAX_VALUE : myRowStripMatrixForB.getElements()[r][c]) : matReaderB.ReadDistanceFromPointsFile(r, c);
//
//                    // Ignore x or y values greater than distcutA or discutB respectively when distcut values are specified
//                    if ((_distcutA > -1 && x > _distcutA) || (_distcutB > -1 && y > _distcutB)) {
//                        continue;
//                    }
//
//                    // Ignore x or y values smaller than mindistA or mindistB respectively when mindist values are specified
//                    if ((_mindistA > -1 && x < _mindistA) || (_mindistB > -1 && y < _mindistB)) {
//                        continue;
//                    }
//
//                    // Ignore if the corresponding two sequence lengths are not within the given lengthcut
//                    if (_lengthCut > -1 && (Math.abs(l1 - l2) > _lengthCut * ((l1 + l2) / 2.0))) {
//                        continue;
//                    }
//
//                    // Perform transforms (no transform if transform method is -1 for the respective matrix)
//                    x = Transform(x, _aTransfm, _aTransfp);
//                    y = Transform(y, _bTransfm, _bTransfp);
//
//                    double newnomoveroldnom = -1;
//                    if (_denomcutsenabled) {
//                        double oldscoredist = ((double) oldScoreReader.read(r, c)) / Double.MAX_VALUE;
//                        double newscoredist = ((double) newScoreReader.read(r, c)) / Double.MAX_VALUE;
//                        newnomoveroldnom = (1.0 - oldscoredist) / (1.0 - newscoredist);
//
//                        if (newnomoveroldnom < 0) {
//                            throw new RuntimeException("Bad should not happen: negative ratio");
//                        }
//                    }
//
//                    for (int j = _denomcuts.length - 1; j >= 0; --j) {
//                        if (_denomcutsenabled && newnomoveroldnom < _denomcuts[j]) {
////C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
////ORIGINAL LINE: myRowStripMatrixForDenomCut[r, c] = (byte) j;
//                            // TODO
//                            myRowStripMatrixForDenomCut.getElements()[r][c] = (byte) j;
//                        }
//
//                        if (myRowStripMatrixForDenomCut.getElements()[r][c] > j) {
//                            ++_consideredPairs[j];
//                            tangible.RefObject<Double> tempRef_Object = new tangible.RefObject<Double>(_xmaxWhole[j]);
//                            tangible.RefObject<Double> tempRef_Object2 = new tangible.RefObject<Double>(_xminWhole[j]);
//                            tangible.RefObject<Double> tempRef_Object3 = new tangible.RefObject<Double>(_ymaxWhole[j]);
//                            tangible.RefObject<Double> tempRef_Object4 = new tangible.RefObject<Double>(_yminWhole[j]);
//                            UpdateMinMax(x, y, tempRef_Object, tempRef_Object2, tempRef_Object3, tempRef_Object4);
//                            _xmaxWhole[j] = tempRef_Object.argValue;
//                            _xminWhole[j] = tempRef_Object2.argValue;
//                            _ymaxWhole[j] = tempRef_Object3.argValue;
//                            _yminWhole[j] = tempRef_Object4.argValue;
//                            if (_useClusters) {
//                                int rCnum = ((int) PnumToCnum.get(r));
//                                int cCnum = ((int) PnumToCnum.get(c));
//                                if (SelectedCnums.contains(rCnum) && SelectedCnums.contains(cCnum)) {
//                                    if (rCnum == cCnum) {
//                                        // Intra cluster distances
//                                        ++_consideredPairsIntra[j];
//                                        tangible.RefObject<Double> tempRef_Object5 = new tangible.RefObject<Double>(_xmaxSelected[j]);
//                                        tangible.RefObject<Double> tempRef_Object6 = new tangible.RefObject<Double>(_xminSelected[j]);
//                                        tangible.RefObject<Double> tempRef_Object7 = new tangible.RefObject<Double>(_ymaxSelected[j]);
//                                        tangible.RefObject<Double> tempRef_Object8 = new tangible.RefObject<Double>(_yminSelected[j]);
//                                        UpdateMinMax(x, y, tempRef_Object5, tempRef_Object6, tempRef_Object7, tempRef_Object8);
//                                        _xmaxSelected[j] = tempRef_Object5.argValue;
//                                        _xminSelected[j] = tempRef_Object6.argValue;
//                                        _ymaxSelected[j] = tempRef_Object7.argValue;
//                                        _yminSelected[j] = tempRef_Object8.argValue;
//                                    } else {
//                                        // Inter cluster distances
//                                        ++_consideredPairsInter[j];
//                                        tangible.RefObject<Double> tempRef_Object9 = new tangible.RefObject<Double>(_xmaxSelectedInter[j]);
//                                        tangible.RefObject<Double> tempRef_Object10 = new tangible.RefObject<Double>(_xminSelectedInter[j]);
//                                        tangible.RefObject<Double> tempRef_Object11 = new tangible.RefObject<Double>(_ymaxSelectedInter[j]);
//                                        tangible.RefObject<Double> tempRef_Object12 = new tangible.RefObject<Double>(_yminSelectedInter[j]);
//                                        UpdateMinMax(x, y, tempRef_Object9, tempRef_Object10, tempRef_Object11, tempRef_Object12);
//                                        _xmaxSelectedInter[j] = tempRef_Object9.argValue;
//                                        _xminSelectedInter[j] = tempRef_Object10.argValue;
//                                        _ymaxSelectedInter[j] = tempRef_Object11.argValue;
//                                        _yminSelectedInter[j] = tempRef_Object12.argValue;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
