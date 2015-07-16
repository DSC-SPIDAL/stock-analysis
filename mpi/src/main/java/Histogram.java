
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class Histogram {
    private String vectorFolder;
    private String distFolder;
    private static int INC = 3000;
    private int bins = 6;

    public Histogram(String vectorFolder, String distFolder, int bins) {
        this.vectorFolder = vectorFolder;
        this.distFolder = distFolder;
        this.bins = bins;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("d", true, "Distance matrix folder");
        options.addOption("b", true, "Number of bins");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            int bins = Integer.parseInt(cmd.getOptionValue("b"));
            Histogram histogram = new Histogram(_vectorFile, _distFile, bins);
        } catch (ParseException e) {
            System.out.println(options.toString());
        }
    }

    private class Bin {
        double start;
        double end;
        int freq;
    }

    public void genHistoGram(File inFile, String outFile) {
        Map<Integer, Double> vecs = proceeVectorFile(inFile);
        List<Double> values = new ArrayList<Double>(vecs.values());
        Collections.sort(values);

        int binSize = values.size() / bins;
        int currentCount = 0;
        for (int i = 0; i < values.size(); i++) {
            if (currentCount < 0) {

            }
        }
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
        double delta = max - min;
        return delta * n.length / sum;
    }
}
