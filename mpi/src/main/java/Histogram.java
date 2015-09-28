import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Histogram {
    private String vectorFolder;
    private String distFolder;
    private String stockFile;
    private static int INC = 3000;
    private int bins = 10;
    private boolean global = false;
    // we will use this many files to generate the global bins
    static public int GLOBAL_FILE_COUNT = 10;

    public Histogram(String vectorFolder, String distFolder, int bins, String stockFile, boolean global) {
        this.vectorFolder = vectorFolder;
        this.distFolder = distFolder;
        this.bins = bins;
        this.stockFile = stockFile;
        this.global = global;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("d", true, "Destination folder"); // Destination folder
        options.addOption("b", true, "Number of bins");
        options.addOption("s", true, "Stock file"); // Original global file
        options.addOption(Utils.createOption("g", false, "Use global bins", false));

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            int bins = Integer.parseInt(cmd.getOptionValue("b"));
            String stockFile = cmd.getOptionValue("s");
            boolean globalBins = cmd.hasOption("g");

            Histogram histogram = new Histogram(_vectorFile, _distFile, bins, stockFile, globalBins);
            histogram.process();
        } catch (ParseException e) {
            System.out.println(options.toString());
        }
    }

    private void process() {
        System.out.println("Starting Histogram calculator...");
        File inFolder = new File(vectorFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder");
            return;
        }
        Map<Integer, String> permNoToSymbol = Utils.loadMapping(stockFile);

        // create the out directory
        Utils.createDirectory(distFolder);
        BlockingQueue<File> files = new LinkedBlockingQueue<File>();
        List<File> list = new ArrayList<File>();
        Collections.addAll(list, inFolder.listFiles());
        Collections.sort(list);
        files.addAll(list);

        if (!this.global) {
            for (File f : files) {
                String outFileName = distFolder + "/" + f.getName();
                System.out.println("generate histogram: " + outFileName);
                Bin[] bins = genHistoGram(f, this.bins, permNoToSymbol);
                writeBins(outFileName, bins);
            }
        } else {
            Bin []globalBins = genGlobalBins(list);
            for (File f : files) {
                String outFileName = distFolder + "/" + f.getName();
                System.out.println("generate histogram: " + outFileName);
                Bin[] bins = genHistoUsingGlobalBins(f, this.bins, permNoToSymbol, globalBins);
                writeBins(outFileName, bins);
            }
        }
        System.out.println("Histogram calculator finished...");
    }

    public void writeBins(String outFile, Bin[] bins) {
        BufferedWriter bufWriter = null;
        try {
            FileOutputStream fos = new FileOutputStream(new File(outFile));
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
            for (int i = 0; i < bins.length; i++) {
                String s = i + "," + bins[i].serializeSymbols();
                bufWriter.write(s);
                bufWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Faile to write bins", e);
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bin[] genHistoUsingGlobalBins(File inFile, int noOfBins, Map<Integer, String> permNoToSymbol, Bin []globalBins) {
        Map<Integer, Double> vecs = proceeVectorFile(inFile);
        List<Double> values = new ArrayList<Double>(vecs.values());
        Collections.sort(values);

        Bin []bins = new Bin[noOfBins];
        for (int i = 0; i < noOfBins; i++) {
            Bin bin = new Bin();
            bin.start = globalBins[i].start;
            bin.end = globalBins[i].end;
        }

        for (Map.Entry<Integer, Double> e : vecs.entrySet()) {
            int perm = e.getKey();
            double val = e.getValue();
            Bin b = getBinIndex(val, bins);
            b.permNos.add(perm);
            b.symbols.add(permNoToSymbol.get(perm));
        }
        return bins;
    }

    public Bin[] genGlobalBins(List<File> allFiles) {
        System.out.println("Generating global bins: " + this.bins);
        // we will use a pre defined number of files to generate the global bins
        int size = allFiles.size();
        int ratio = size / GLOBAL_FILE_COUNT;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < size; i++) {
            File f = allFiles.get(i + ratio);
            Map<Integer, Double> vecs = proceeVectorFile(f);
            List<Double> values = new ArrayList<Double>(vecs.values());
            Collections.sort(values);

            if (values.size() <= 1) continue;

            double fileMax = values.get(values.size() - 1);
            double fileMin = values.get(0);

            if (fileMax > max) {
                max = fileMax;
            }
            if (fileMin < min) {
                min = fileMin;
            }
        }

        double delta = (max - min) / this.bins;
        Bin []bins = new Bin[this.bins];
        for (int i = 0; i < bins.length; i++) {
            Bin b = new Bin();
            b.start = i * delta;
            b.end = i * (delta + 1);
        }
        System.out.println("Global bins MAX: " + max + " MIN: " + min);
        return bins;
    }

    public Bin[] genHistoGram(File inFile, int noOfBins, Map<Integer, String> permNoToSymbol) {
        Map<Integer, Double> vecs = proceeVectorFile(inFile);
        List<Double> values = new ArrayList<Double>(vecs.values());
        Collections.sort(values);

        int binSize = values.size() / bins;

        Bin []bins = new Bin[noOfBins];
        for (int i = 0; i < noOfBins; i++) {
            Bin bin = new Bin();
            bin.start = values.get(i * binSize);
            int index = (i + 1) * binSize;
            if (index >= values.size()) index = values.size() - 1;
            bin.end = values.get(index);
            bins[i] = bin;
        }

        for (Map.Entry<Integer, Double> e : vecs.entrySet()) {
            int perm = e.getKey();
            double val = e.getValue();
            Bin b = getBinIndex(val, bins);
            b.permNos.add(perm);
            b.symbols.add(permNoToSymbol.get(perm));
        }
        return bins;
    }

    private Bin getBinIndex(double val, Bin []bins) {
        for (int i = 0; i < bins.length - 1; i++) {
            Bin b = bins[i];
            // add all that is below the 0'th bin to 0
            if (val < b.start) {
                return b;
            }

            if (b.start >= val) {
                return b;
            } else if (b.end > val) {
                return b;
            }
        }
        // add all that is over the last bin value to last
        return bins[bins.length - 1];
    }

    public Map<Integer, Double> proceeVectorFile(File inFile) {
        System.out.println("Generating histogram for stocks file: " + inFile);
        Map<Integer, Double> deltas = new HashMap<Integer, Double>();
        if (!inFile.exists()) {
            System.out.println("ERROR: In file doens't exist");
            return null;
        }
        int startIndex = 0;
        int endIndex = -1;

        List<VectorPoint> vectors;
        do {
            startIndex = endIndex + 1;
            endIndex = startIndex + INC - 1;

            vectors = Utils.readVectors(inFile, startIndex, endIndex);
            if (vectors.size() == 0) {
                break;
            }
            // write the vectors to file
            for (VectorPoint v : vectors) {
                double delta = vectorDelta(v.getNumbers());
                deltas.put(v.getKey(), delta);
            }
        } while (true);
        return deltas;
    }

    private double vectorDelta(double []n) {
        double sum = 0.0;
        double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;
        for (double aN : n) {
            sum += aN;
            min = aN < min ? aN : min;
            max = aN > max ? aN : max;
        }
//        double delta = max - min;
        double delta = n[0] - n[n.length - 1];
        return delta * n.length / sum;
    }
}
