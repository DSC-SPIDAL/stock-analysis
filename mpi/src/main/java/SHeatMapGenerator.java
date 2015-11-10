import edu.indiana.soic.spidal.common.BinaryReader2D;
import edu.indiana.soic.spidal.common.Range;
import edu.indiana.soic.spidal.common.TransformationFunction;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SHeatMapGenerator {
    public static String _aMat;
    public static String _bMat;
    public static int _cols;
    public static int _rows;
    public static String _outdir;
    private static boolean _useTDistanceMaxForA;
    private static boolean _useTDistanceMaxForB;
    private static double _xmaxbound = 1.0;
    private static double _ymaxbound = 1.0;
    private static int _xres = 50;
    private static int _yres = 50;
    private static double _alpha = 2.0;
    private static double _pcutf = 0.85;
    private static boolean _zto1 = true;
    private static int _aTransfm, _bTransfm;
    private static double _aTransfp, _bTransfp;
    private static double _distcutA;
    private static double _distcutB;
    private static double _mindistA;
    private static double _mindistB;
    private static String _aName;
    private static String _bName;
    private static String _clusterfile;
    public static String _title;
    private static double _lengthCut;
    private static boolean _readPointsA;
    private static boolean _readPointsB;
    public static double _scaleA = 1.0;
    public static double _scaleB = 1.0;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("c", true, "Config file");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String input = cmd.getOptionValue("c");
            SHeatMapGenerator vg = new SHeatMapGenerator(input);
            vg.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public SHeatMapGenerator(String confiFile) {
        // first read the configurations
        readConfiguration(confiFile);
    }

    public void process() {
        // first load the first file
        ReadOutPut outA = readFile(_aMat, _rows, _cols, _readPointsA, _scaleA);
        ReadOutPut outB = readFile(_bMat, _rows, _cols, _readPointsB, _scaleB);

        // get the bin size
        double deltaX = (outA.max - outA.min) / _xres;
        double deltaY = (outB.max - outB.min) / _yres;
        double deltaS = deltaX * deltaY;
        // now create a cell array with number of bins
        long [][] cells = new long[_xres][];
        for (int i = 0; i < _xres; i++) {
            cells[i] = new long[_yres];
            for (int j = 0; j < _yres; j++) {
                cells[i][j] = 0;
            }
        }
        // update the cells according to values
        for (int i = 0; i < _rows; i++) {
            for (int j = 0; j < _cols; j++) {
                double x = outA.values[i][j];
                double y = outB.values[i][j];
                x = Transform(x, _aTransfm, _aTransfp);
                y = Transform(y, _bTransfm, _bTransfp);
                updateCells(x, y, outA.max, outA.min, outB.max, outB.min, deltaX, deltaY, cells, 1, 2);
            }
        }
        // draw the graphs
        try {
            generateDensityDataFile(cells, outA.max, outA.min, outB.max, outB.min, deltaX, deltaY, deltaS, _title, 1.0, 1.0, (long)_rows * _cols);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double Transform(double val, int transfm, double transfp) {
        if (transfm == 10) {
            val = Math.min(1.0, val);
            return Math.pow(val, transfp);
        }
        return val;
    }

    private static void updateCells(double x, double y,
                                    double xmax, double xmin,
                                    double ymax, double ymin,
                                    double deltax, double deltay,
                                    long[][] cells, int i, int j) {
        if (x > xmax) return;
        if (y > ymax) return;

        // cell number based on zero index from bottom left corner
        // if x is equal to xmax then it's placed in the last cell, which is xres-1 in zero based index
        // same is done for y when y == ymax
        int cellx = x == xmax ? _xres - 1 : (int) Math.floor((x - xmin) / deltax);
        int celly = y == ymax ? _yres - 1 : (int) Math.floor((y - ymin) / deltay);

        if (x > xmax || y > ymax || x < xmin || y < ymin) {
            // now this should never be reached
            throw new RuntimeException("bad(1)-> x: " + x + " y: " + y + " xmax: " + xmax + " xmin: " +
                    xmin + " ymax: " + ymax + " ymin: " + ymin + "lengthcut: " + _lengthCut + " row: " + i + " col: " + j);
        }

        if (cellx >= _xres || celly >= _yres) {
            // now this should never be reached
            throw new RuntimeException("bad(2)-> x: " + x + " y:" + y + " xmax: " + xmax + " xmin: " +
                    xmin + " ymax: " + ymax + " ymin: " + ymin + " cellx: " + cellx + " celly: " + celly);
        }

        ++cells[celly][cellx];
    }

    private String getProperty(Properties p, String name, String def) {
        String val = System.getProperty(name);
        if (val == null) {
            if (def != null) {
                val = p.getProperty(name, def);
                if (val == null) {
                    throw new RuntimeException("Property not specified in config file: " + name);
                }
            } else {
                val = p.getProperty(name);
            }
        }
        return val;
    }

    private void readConfiguration(String configFile) {
        System.out.println(configFile);
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(configFile));
            _aMat = getProperty(p, "Amat", null);
            _aName = getProperty(p, "Aname", null);
            _aTransfm = Integer.parseInt(getProperty(p, "Atransfm", null));
            _aTransfp = Double.parseDouble(getProperty(p, "Atransfp", null));
            _bMat = getProperty(p, "Bmat", null);
            _bName = getProperty(p, "Bname", null);
            _bTransfm = Integer.parseInt(getProperty(p, "Btransfm", null));
            _bTransfp = Double.parseDouble(getProperty(p, "Btransfp", null));
            _useTDistanceMaxForA = Boolean.parseBoolean(getProperty(p, "usetdistancemaxforA", null));
            _useTDistanceMaxForB = Boolean.parseBoolean(getProperty(p, "usetdistancemaxforB", null));
            _readPointsA = Boolean.parseBoolean(getProperty(p, "readPointsA", null));
            _readPointsB = Boolean.parseBoolean(getProperty(p, "readPointsB", null));
            _outdir = getProperty(p, "outdir", null);
            _cols = Integer.parseInt(getProperty(p, "cols", null));
            _rows = Integer.parseInt(getProperty(p, "rows", null));
            _xmaxbound = Double.parseDouble(getProperty(p, "xmaxbound", null));
            _ymaxbound = Double.parseDouble(getProperty(p, "ymaxbound", null));
            _xres = Integer.parseInt(getProperty(p, "xres", null));
            _yres = Integer.parseInt(getProperty(p, "yres", null));
            _alpha = Double.parseDouble(getProperty(p, "alpha", null));
            _pcutf = Double.parseDouble(getProperty(p, "pcutf", null));
            _zto1 = Boolean.parseBoolean(getProperty(p, "zto1", null));
            _distcutA = Double.parseDouble(getProperty(p, "distcutA", null));
            _distcutB = Double.parseDouble(getProperty(p, "distcutB", null));
            _mindistA = Double.parseDouble(getProperty(p, "mindistA", null));
            _mindistB = Double.parseDouble(getProperty(p, "mindistB", null));
            _scaleA = Double.parseDouble(getProperty(p, "scaleA", "1.0"));
            _scaleB = Double.parseDouble(getProperty(p, "scaleB", "1.0"));
            // We dont use clusters configuration
            _title = getProperty(p, "title", null);
        } catch (IOException e) {
            System.out.println("Failed to read the configuration");
            e.printStackTrace();
        }
    }

    private class ReadOutPut {
        double [][] values;
        double max;
        double min;
    }

    /**
     * A file can be a point file or a distance file
     * @param file
     * @param rows
     * @param cols
     * @param points
     * @return
     */
    private ReadOutPut readFile(String file, int rows, int cols, boolean points, double scale) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        System.out.println("SCALE: " + scale);
        double[][] finalValues = new double[rows][];
        if (!points) {
            short[][] values = BinaryReader2D.readRowRange(file, new Range(0, rows - 1), cols, ByteOrder.BIG_ENDIAN, true, new TransformationFunction() {
                @Override
                public double transform(double val) {
                    return val;
                }
            });
            for (int i = 0; i < values.length; i++) {
                finalValues[i] = new double[cols];
                for (int j = 0; j < values[i].length; j++) {
                    finalValues[i][j] = ((double) values[i][j]) / Short.MAX_VALUE;
                    if (finalValues[i][j] < 0) {
                        System.out.println("minus distance");
                    }
                    if (finalValues[i][j] > max) {
                        max = finalValues[i][j];
                    }
                    if (finalValues[i][j] < min) {
                        min = finalValues[i][j];
                    }
                    finalValues[i][j] *= scale;
                }
            }
        } else {
            List<Point> pointList = new ArrayList<Point>();
            try {
                FileReader input = new FileReader(file);
                BufferedReader bufRead = new BufferedReader(input);
                String inputLine;
                while ((inputLine = bufRead.readLine()) != null) {
                    Point p = Utils.readPoint(inputLine);
                    pointList.add(p);
                }
                for (int i = 0; i < rows; i++) {
                    finalValues[i] = new double[cols];
                    for (int j = 0; j < cols; j++) {
                        Point pi = pointList.get(i);
                        Point pj = pointList.get(j);
                        finalValues[i][j] = pi.distance(pj);
                        if (finalValues[i][j] > max) {
                            max = finalValues[i][j];
                        }
                        if (finalValues[i][j] < min) {
                            min = finalValues[i][j];
                        }
                        finalValues[i][j] *= scale;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ReadOutPut readOutPut = new ReadOutPut();
        readOutPut.max = max;
        readOutPut.min = min;
        readOutPut.values = finalValues;

        return readOutPut;
    }

    private static void generateDensityDataFile(long[][] cells, double xmax, double xmin,
                                                double ymax, double ymin,
                                                double deltax, double deltay,
                                                double deltas, String prefix, double denomcut, double pairFraction, long totalPairs) throws IOException {
        long[] xHist = new long[_xres];
        long[] yHist = new long[_yres];

        for (int i = 0; i < _xres; i++) {
            xHist[i] = 0;
        }

        for (int i = 0; i < _yres; i++) {
            yHist[i] = 0;
        }

        long cellmax = 0, count = 0, v;
        for (int i = 0; i < _yres; i++) {
            for (int j = 0; j < _xres; j++) {
                v = cells[i][j];
                xHist[j] += v;
                yHist[i] += v;
                count += v;
                if (v > cellmax) {
                    cellmax = v;
                }
            }
        }

        double cellmean = ((double) count) / (_xres * _yres);
        double power = cellmax < (_alpha * cellmean) ? 1.0 : (Math.log(_alpha) / Math.log(cellmax / cellmean));
        // Constant value by which the number of points in a 2D square is multiplied.
        // The resulting value is independent of the total number of points as well as
        // the x,y resolution. The mult value is a factor changing the z value scale.
        double c = _zto1 ? (1.0 / cellmax) : (1.0 / (count * deltas));

        // Output density values
        System.out.println("***************************************************************");
        System.out.println("DataSet\t" + prefix);
        System.out.println("CellMean\t" + cellmean);
        System.out.println("CellMax\t" + cellmax);
        System.out.println("Power\t" + power);
        System.out.println("Const\t" + c);
        System.out.println("TotalPairs\t" + totalPairs);
        System.out.println("PairFraction\t" + pairFraction);
        for (int i = 0; i < 10; i++) {
            double density = i / 10.0;
            double densityToCount = Math.pow(density, (1 / power)) / c;
            System.out.println(density + "\t" + densityToCount);
        }
        System.out.println("***************************************************************");

        int xpointcount = 2 * _xres;
        int ypointcount = 2 * _yres;

        String aNameFinal = _aTransfm > -1 ? "Transformed-" + _aName : _aName;
        String bNameFinal = _bTransfm > -1 ? "Transformed-" + _bName : _bName;

        String dCutStringA = "DCut[" + (_mindistA > -1 ? (new Double(_mindistA)).toString() : "none") + "," + (_distcutA > -1 ? (new Double(_distcutA)).toString() : "none") + "]";
        String dCutStringB = "DCut[" + (_mindistB > -1 ? (new Double(_mindistB)).toString() : "none") + "," + (_distcutB > -1 ? (new Double(_distcutB)).toString() : "none") + "]";
        String vsString = bNameFinal + "-Vs-" + aNameFinal;

        String dir = _outdir;
        if (false) {
            dir = combine(_outdir, "denomcut_" + (new Double(denomcut)).toString());
            (new java.io.File(dir)).mkdir();
        }

        String densityFile = combine(dir, prefix + "-density-" + dCutStringA + "-" + dCutStringB + "-" + vsString + ".txt");
        String xHistFile = combine(dir, prefix + "-xHist-" + dCutStringA + "-" + dCutStringB + "-" + vsString + ".txt");
        String yHistFile = combine(dir, prefix + "-yHist-" + dCutStringA + "-" + dCutStringB + "-" + vsString + ".txt");
        String gnuplotScriptFileLarge = combine(dir, prefix + "-gnuplot-" + dCutStringA + "-" + dCutStringB + "-" + vsString + "-large.txt");
        String gnuplotScriptFileSmall = combine(dir, prefix + "-gnuplot-" + dCutStringA + "-" + dCutStringB + "-" + vsString + "-small.txt");

        String plotBat = combine(dir, "plot.bat");

        PrintWriter densityFileWriter = new PrintWriter(new FileWriter(densityFile));
        PrintWriter xHistWriter = new PrintWriter(new FileWriter(xHistFile));
        PrintWriter yHistWriter = new PrintWriter(new FileWriter(yHistFile));
        PrintWriter gnuplotWriterLarge = new PrintWriter(new FileWriter(gnuplotScriptFileLarge));
        PrintWriter gnuplotWriterSmall = new PrintWriter(new FileWriter(gnuplotScriptFileSmall));
        PrintWriter plotBatWriter = new PrintWriter(new FileWriter(plotBat, true));
        try {
            // Generating plot bat
            plotBatWriter.println("gnuplot " + (new java.io.File(gnuplotScriptFileLarge)).getName());
            densityFileWriter.println("#xcoord\tycoord\thistogramValue");
            xHistWriter.println("#xval\thistogramvalue");
            yHistWriter.println("#yval\thistogramvalue");

            // Generating x histogram
            double xoffset = xmin + 0.5 * deltax;
            for (int i = 0; i < _xres; ++i) {
                double xcoord = xoffset + i * deltax;
                xHistWriter.println(xcoord + "\t" + xHist[i]);
            }

            // Generating y histogram
            double yoffset = ymin + 0.5 * deltay;
            for (int i = 0; i < _yres; ++i) {
                double ycoord = yoffset + i * deltay;
                yHistWriter.println(ycoord + "\t" + yHist[i]);
            }

            for (int i = 0; i < xpointcount; i++) {
                double x = xmin + ((IsOdd(i) ? (i + 1) / 2 : i / 2) * deltax);
                int cellx = IsOdd(i) ? (i - 1) / 2 : i / 2;

                for (int j = 0; j < ypointcount; j++) {
                    double y = ymin + ((IsOdd(j) ? (j + 1) / 2 : j / 2) * deltay);
                    int celly = IsOdd(j) ? (j - 1) / 2 : j / 2;

                    double cellvalue = Math.pow((cells[celly][cellx] * c), power);

                    // todo: commented for now
                    // cellvalue = cellvalue > pcutf ? pcutf : cellvalue < ncutf ? ncutf : cellvalue;
                    cellvalue = _pcutf > -1 && cellvalue > _pcutf ? _pcutf : cellvalue;

                    densityFileWriter.println(x + "\t" + y + "\t" + cellvalue);
                }
                densityFileWriter.println();
            }

            if (_xmaxbound == -1) {
                _xmaxbound = xmax;
            }

            if (_ymaxbound == -1) {
                _ymaxbound = ymax;
            }

            // Fill up the remaining region from beyond x=xmax and y=ymax as zero
            densityFileWriter.println();
            densityFileWriter.println(xmin + "\t" + ymax + "\t" + 0.0);
            densityFileWriter.println(xmin + "\t" + _ymaxbound + "\t" + 0.0);
            densityFileWriter.println();
            densityFileWriter.println(xmax + "\t" + ymax + "\t" + 0.0);
            densityFileWriter.println(xmax + "\t" + _ymaxbound + "\t" + 0.0);
            densityFileWriter.println();
            densityFileWriter.println(xmax + "\t" + ymin + "\t" + 0.0);
            densityFileWriter.println(xmax + "\t" + _ymaxbound + "\t" + 0.0);
            densityFileWriter.println();
            densityFileWriter.println(_xmaxbound + "\t" + ymin + "\t" + 0.0);
            densityFileWriter.println(_xmaxbound + "\t" + _ymaxbound + "\t" + 0.0);


            //Tangible multiline preserve/* Writing Gnuplot script */
            WriteGnuplotScript(bNameFinal, aNameFinal, prefix, vsString, densityFile, xHistFile, yHistFile, gnuplotWriterLarge, gnuplotWriterSmall, denomcut, pairFraction, totalPairs);
        } finally {
            densityFileWriter.close();
            xHistWriter.close();
            yHistWriter.close();
            gnuplotWriterLarge.close();
            gnuplotWriterSmall.close();
            plotBatWriter.close();
        }
    }

    private static String combine(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    private static boolean IsOdd(int value) {
        return (value & 1) == 1;
    }

    private static void WriteGnuplotScript(String bNameFinal, String aNameFinal,
                                           String prefix, String vsString, String densityFile,
                                           String xHistFile, String yHistFile,
                                           PrintWriter gnuplotWriterLarge, PrintWriter gnuplotWriterSmall,
                                           double denomcut, double pairFraction, long totalPairs) {
        gnuplotWriterLarge.println("set terminal png truecolor nocrop font arial 14 size 1200,1200");
        gnuplotWriterSmall.println("set terminal png truecolor nocrop font arial 14 size 1000,500");

        gnuplotWriterLarge.println();

        String pngfile = prefix + "-plot-" + vsString + "DensitySat[" + (_pcutf > -1 ? (new Double(_pcutf)).toString() : "none") + "]" + (false ? "-DenomCut[" + denomcut + "]-" : "-") + "large.png";
        gnuplotWriterLarge.println("set output '" + pngfile + "'");
        pngfile = prefix + "-plot-" + vsString + "DensitySat[" + (_pcutf > -1 ? (new Double(_pcutf)).toString() : "none") + "]" + (false ? "-DenomCut[" + denomcut + "]-" : "-") + "small.png";
        gnuplotWriterSmall.println("set output '" + pngfile + "'");

        gnuplotWriterLarge.println("set size 1.0, 1.0");
        gnuplotWriterLarge.println("set multiplot");
        gnuplotWriterSmall.println("set multiplot");

        gnuplotWriterLarge.println();
        gnuplotWriterSmall.println();

        // Title box
        gnuplotWriterLarge.println("set origin 0.0, 0.85");
//            gnuplotWriterSmall.WriteLine("set origin 0.0, 0.7");
        gnuplotWriterLarge.println("set size 0.95, 0.1");
//            gnuplotWriterSmall.WriteLine("set size 0.95, 0.1");
        gnuplotWriterLarge.println("set border linecolor rgbcolor \"white\"");
//            gnuplotWriterSmall.WriteLine("set border linecolor rgbcolor \"white\"");
        gnuplotWriterLarge.println("unset key");
//            gnuplotWriterSmall.WriteLine("unset key");
        String dcutStringA = (_mindistA > -1 ? (new Double(_mindistA)).toString() : "none") + "," + (_distcutA > -1 ? (new Double(_distcutA)).toString() : "none");
        String dcutStringB = (_mindistB > -1 ? (new Double(_mindistB)).toString() : "none") + "," + (_distcutB > -1 ? (new Double(_distcutB)).toString() : "none");
        //String title = String.format(_title, (_pcutf > -1 ? (new Double(_pcutf)).toString() : "none"), bNameFinal, dcutStringB, aNameFinal, dcutStringA, (prefix.equals("whole") ? "Whole Sample" : (prefix.equals("selected") ? "Selected Clusters Intra Pairs" : "Selected Clusters Inter Pairs")));
        String title = _title;
        if (false) {
            title += "\\nDenomCut[" + denomcut + "] PairFraction[" + Math.round(pairFraction * Math.pow(10, 6)) / Math.pow(10, 6) + "] TotalPairs[" + totalPairs + "]";
        }
        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
//            gnuplotWriterSmall.WriteLine("set title \"" + title + "textcolor rgbcolor \"black\"");
        gnuplotWriterLarge.println("plot [0:1] [0:1] 0.0 lt rgb \"white\"");
//            gnuplotWriterSmall.WriteLine("plot [0:1] [0:1] 0.0 lt rgb \"white\"");

        gnuplotWriterLarge.println("set border linecolor rgbcolor \"black\"");
        gnuplotWriterSmall.println("set border linecolor rgbcolor \"black\"");

        gnuplotWriterLarge.println("set dummy u,v");
        gnuplotWriterSmall.println("set dummy u,v");

        gnuplotWriterLarge.println("unset key");
        gnuplotWriterSmall.println("unset key");

        gnuplotWriterLarge.println("set size ratio 1.0");
//            gnuplotWriterSmall.WriteLine("set size ratio 1.0");

        gnuplotWriterLarge.println("set style fill  solid 0.85 noborder");
        gnuplotWriterSmall.println("set style fill  solid 0.85 noborder");

        gnuplotWriterLarge.println("set style line 1 lt 1 lw 4");
        gnuplotWriterSmall.println("set style line 1 lt 1 lw 4");

        gnuplotWriterLarge.println("set pm3d map");
        gnuplotWriterSmall.println("set pm3d map");

        gnuplotWriterLarge.println("set palette rgbformulae 30,31,32 model RGB negative");
        gnuplotWriterSmall.println("set palette rgbformulae 30,31,32 model RGB negative");

        gnuplotWriterLarge.println();
        gnuplotWriterSmall.println();

        // Y histogram (rotated)
        gnuplotWriterLarge.println("set origin 0.0, 0.45");
        gnuplotWriterLarge.println("set size 0.45, 0.45");
        gnuplotWriterLarge.println("set xtics rotate by -90");
        String xlabel = "Count";
        gnuplotWriterLarge.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
        String ylabel = bNameFinal;
        gnuplotWriterLarge.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
        title = "Histogram (rotated) of " + bNameFinal + " distances";
        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterLarge.println("plot [][:" + _ymaxbound + "] '" + (new java.io.File(yHistFile)).getName() + "' using 2:1 with filledcurves y1 lt rgb \"black\"");

        gnuplotWriterLarge.println("set xtics rotate by 0");
        gnuplotWriterLarge.println();


        // Density plot
        gnuplotWriterLarge.println("set origin 0.45, 0.45");
        gnuplotWriterSmall.println("set origin 0.0, 0.0");
        gnuplotWriterLarge.println("set size 0.5, 0.5");
        gnuplotWriterSmall.println("set size square 0.5, 1.0");
        xlabel = aNameFinal;
        gnuplotWriterLarge.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterSmall.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
        ylabel = bNameFinal;
        gnuplotWriterLarge.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterSmall.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
        title = "Heat Map of " + vsString;
        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterSmall.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterLarge.println("splot [:" + _xmaxbound + "] [:" + _ymaxbound + "] '" + (new java.io.File(densityFile)).getName() + "'");
        gnuplotWriterSmall.println("splot [:" + _xmaxbound + "] [:" + _ymaxbound + "] '" + (new java.io.File(densityFile)).getName() + "'");

        gnuplotWriterLarge.println("unset pm3d");


        gnuplotWriterLarge.println();
        gnuplotWriterSmall.println();

        // Y histogram (unrotated)
        gnuplotWriterLarge.println("set origin 0.0, 0.0");
        gnuplotWriterLarge.println("set size 0.45, 0.45");
        xlabel = bNameFinal;
        gnuplotWriterLarge.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
        ylabel = "Count";
        gnuplotWriterLarge.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
        title = "Histogram of " + bNameFinal + " distances";
        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterLarge.println("plot [:" + _ymaxbound + "] []'" + (new java.io.File(yHistFile)).getName() + "' with filledcurves x1 lt rgb \"black\"");


        gnuplotWriterLarge.println();

        // X histogram
        gnuplotWriterLarge.println("set origin 0.45, 0.0");
        gnuplotWriterSmall.println("set origin 0.5, 0.08");
        gnuplotWriterLarge.println("set size 0.45, 0.45");
        gnuplotWriterSmall.println("set size square 0.5, 0.85");
        xlabel = aNameFinal;
        gnuplotWriterLarge.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterSmall.println("set xlabel \"" + xlabel + "\" textcolor rgbcolor \"black\"");
        ylabel = "Count";
        gnuplotWriterLarge.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterSmall.println("set ylabel \"" + ylabel + "\" textcolor rgbcolor \"black\"");
        title = "Histogram of " + aNameFinal + " distances";
        gnuplotWriterLarge.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterSmall.println("set title \"" + title + "\" textcolor rgbcolor \"black\"");
        gnuplotWriterLarge.println("plot [:" + _xmaxbound + "] []'" + (new java.io.File(xHistFile)).getName() + "' with filledcurves x1 lt rgb \"black\"");
        gnuplotWriterSmall.println("plot [:" + _xmaxbound + "] []'" + (new java.io.File(xHistFile)).getName() + "' with filledcurves x1 lt rgb \"black\"");

        gnuplotWriterLarge.println();
        gnuplotWriterSmall.println();

        gnuplotWriterLarge.println("unset multiplot");
    }
}
