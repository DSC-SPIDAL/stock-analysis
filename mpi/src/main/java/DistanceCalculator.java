import mpi.MPI;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DistanceCalculator {
    private String vectorFolder;
    private String distFolder;
    private boolean normalize;

    public DistanceCalculator(String vectorFolder, String distFolder, boolean normalize) {
        this.vectorFolder = vectorFolder;
        this.distFolder = distFolder;
        this.normalize = normalize;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("d", true, "Distance matrix folder");
        options.addOption("n", false, "normalize");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            boolean _normalize = cmd.hasOption("n");
            DistanceCalculator program = new DistanceCalculator(_vectorFile, _distFile, _normalize);
            program.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static int INC = 1000;

    private void process() {
        File inFolder = new File(vectorFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder");
            return;
        }

//      DataOutputStream writer = null;
        PrintWriter writer = null;

        for (File fileEntry : inFolder.listFiles()) {
            if (fileEntry.isDirectory()) {
                continue;
            }

            String outFileName = distFolder + "/" + fileEntry.getName();
            try {
                writer = new PrintWriter(new FileWriter(outFileName));
            } catch (IOException e) {
                throw new RuntimeException("Cannot find filename: " + outFileName);
            }

            int lineCount = countLines(fileEntry);

            // initialize the double arrays for this block
            double values[][] = new double[INC][];
            for (int i = 0; i < values.length; i++) {
                values[i] = new double[lineCount];
            }

            int startIndex = 0;
            int endIndex = -1;

            List<VectorPoint> vectors;
            do {
                startIndex = endIndex + 1;
                endIndex = startIndex + INC - 1;

                int readStartIndex = 0;
                int readEndIndex = INC - 1;

                vectors = readVectors(fileEntry, startIndex, endIndex);
                if (vectors.size() == 0) {
                    break;
                }

                System.out.println("Processing block: " + startIndex + " : " + endIndex);
                // now start from the begining and go through the whole file
                List<VectorPoint> secondVectors;
                do {
                    System.out.println("Reading second block: " + readStartIndex + " : " + readEndIndex);
                    if (readStartIndex != startIndex) {
                        secondVectors = readVectors(fileEntry, readStartIndex, readEndIndex);
                    } else {
                        secondVectors = vectors;
                    }

                    if (secondVectors.size() == 0) {
                        break;
                    }

                    for (int i = 0; i < secondVectors.size(); i++) {
                        VectorPoint sv = secondVectors.get(i);
                        for (int j = 0; j < vectors.size(); j++) {
                            VectorPoint fv = vectors.get(j);
                            double cor = sv.correlation(fv);
                            values[j][readStartIndex + i] = cor;
                        }
                    }
                    readStartIndex = readEndIndex + 1;
                    readEndIndex = readStartIndex + INC - 1;
                } while (true);

                // write the vectors to file
                for (int i = 0; i < values.length; i++) {
                    double[] row = values[i];
                    for (double value : row) {
                        writer.print(value + " ");
                    }
                    writer.write("\n");
                }
            } while (true);
        }

        if (writer != null) {
            writer.close();
        }
    }

    private int countLines(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                count++;
            }
            return count;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file");
        }
    }

    private List<VectorPoint> readVectors(File file, int startIndex, int endIndex) {
        List<VectorPoint> vecs = new ArrayList<VectorPoint>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            int readCount = 0;
            while ((line = br.readLine()) != null) {
                if (count >= startIndex) {
                    readCount++;
                    // process the line.
                    String parts[] = line.trim().split(" ");
                    if (parts.length > 0 && !(parts.length == 1 && parts[0].equals(""))) {
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

                }
                count++;
                // we stop
                if (readCount > endIndex - startIndex) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignore) {
                }
            }
        }
        return vecs;
    }
}
