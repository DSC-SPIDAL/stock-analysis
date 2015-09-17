import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;

/**
 * The generated point files have different number of points.
 * We need to make sure that all the point files have the same stock ordering with same stocks before applying the rotations.
 */
public class PointTransformer {
    private String globalVectorFile;
    private String globalPointFile;
    private String pointFolder;
    private String vectorFolder;
    private String destPointFolder;
    private String weightFolder;
    private String symbolList;
    private String stocksFile;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("sf", true, "The complese stock file");
        options.addOption("g", true, "Global file");
        options.addOption("gp", true, "Global pooint file");
        options.addOption("v", true, "Input Vector folder");
        options.addOption("p", true, "Points folder");
        options.addOption("d", true, "Destination point folder");
        options.addOption("w", true, "Destination weight folder"); // folder where common weights are stored
        options.addOption("s", true, "Symbol List");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String globalPointFile = cmd.getOptionValue("gp");
            String globalFile = cmd.getOptionValue("g");
            String vectorFile = cmd.getOptionValue("v");
            String pointsFolder = cmd.getOptionValue("p");
            String distFolder = cmd.getOptionValue("d");
            String weightFolder = cmd.getOptionValue("w");
            String symbolList = cmd.getOptionValue("s");
            String stockFile = cmd.getOptionValue("sf");

            PointTransformer pointTransformer = new PointTransformer(globalFile, pointsFolder, vectorFile,
                    distFolder, globalPointFile, weightFolder, symbolList, stockFile);
            pointTransformer.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public PointTransformer(String globalVectorFile, String pointFolder, String vectorFolder,
                            String destPointFolder, String globalPointFile, String weightFolder, String symbolList, String stockFile) {
        this.globalVectorFile = globalVectorFile;
        this.pointFolder = pointFolder;
        this.vectorFolder = vectorFolder;
        this.destPointFolder = destPointFolder;
        this.globalPointFile = globalPointFile;
        this.weightFolder = weightFolder;
        this.symbolList = symbolList;
        this.stocksFile = stockFile;
    }

    public void process() {
        File inFolder = new File(vectorFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder");
            return;
        }

        // make the common weight folder
        File commonWeightFolder = new File(weightFolder);
        if (!commonWeightFolder.exists()) {
            commonWeightFolder.mkdirs();
        }

        // first get the global vector file and its keys
        File globalFile = new File(globalVectorFile);
        Map<Integer, String> keysToSymbols = loadKeys(stocksFile, symbolList);
        List<Integer> globalKeys = Utils.readVectorKeys(globalFile);

        TreeSet<Integer> commonKeys = new TreeSet<Integer>();
        Map<String, Map<Integer, Point>> filesToPoint = new HashMap<String, Map<Integer, Point>>();

        if (keysToSymbols != null && keysToSymbols.size() > 0)  {
            commonKeys.addAll(keysToSymbols.keySet());
        } else {
            commonKeys.addAll(globalKeys);
        }

        // go through every vector file and get the common keys
        for (int i = 0; i < inFolder.listFiles().length; i++) {
            File inFile = inFolder.listFiles()[i];
            List<Integer> partKeys = Utils.readVectorKeys(inFile);
            // load the corresponding points file
            String fileName = inFile.getName();
            String fileNameWithOutExt = FilenameUtils.removeExtension(fileName);
            String pointFile = pointFolder + "/" + fileNameWithOutExt + ".txt";
            Map<Integer, Point> points = Utils.loadPoints(new File(pointFile), partKeys);
            filesToPoint.put(fileName, points);
            commonKeys.retainAll(partKeys);
        }

        // write the global file
        String commonGlobalPointFile = destPointFolder + "/" + globalFile.getName();
        String commonGlobalWectorFile = weightFolder + "/" + FilenameUtils.removeExtension(globalFile.getName()) + ".csv";
        Map<Integer, Point> globalPoints = Utils.loadPoints(new File(globalPointFile), globalKeys);
        Map<Integer, Double> globalCaps = Utils.loadCaps(globalFile);

        writePoints(commonKeys, globalPoints, commonGlobalPointFile, commonGlobalWectorFile, globalCaps);

        for (Map.Entry<String, Map<Integer, Point>> entry : filesToPoint.entrySet()) {
            // now write the common keys back as new files, we need to preserve the order as well
            // get the file
            Map<Integer, Point> pointMap = entry.getValue();
            String file =  entry.getKey();
            String fileNameWithOutExt = FilenameUtils.removeExtension(file);
            String weightFile = weightFolder + "/" + fileNameWithOutExt + ".csv";

            Map<Integer, Double> caps = Utils.loadCaps(new File(vectorFolder + "/" + entry.getKey()));
            writePoints(commonKeys, pointMap, destPointFolder + "/" + file, weightFile, caps);
        }
    }

    private void writePoints(TreeSet<Integer> commonKeys, Map<Integer, Point> pointMap, String file, String weightFile, Map<Integer, Double> caps) {
        BufferedWriter bufWriter = null;
        WriterWrapper weightWriter = null;
        try {
            double max = Double.MIN_VALUE;
            for (Double d : caps.values()) {
                if (max < d) {
                    max = d;
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
            weightWriter = new WriterWrapper(weightFile, true);
            int i = 0;
            for (Integer key : commonKeys) {
                Point p = pointMap.get(key);
                p.setIndex(i++);
                bufWriter.write(p.serialize());
                bufWriter.newLine();
                weightWriter.write(caps.get(key) / max);
                weightWriter.line();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file", e);
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (IOException ignore) {
                }
            }
            if (weightWriter != null) {
                weightWriter.close();
            }
        }
    }

    public static Map<Integer, String> loadKeys(String inFile, String symbols) {
        List<String> symbolsList = new ArrayList<String>();
        if (symbols != null) {
            String []splits = symbols.split(",");
            Collections.addAll(symbolsList, splits);
        }
        if (!symbolsList.isEmpty()) {
            System.out.println("Reading original stock file: " + inFile);
            BufferedReader bufRead = null;
            Map<Integer, String> maps = new HashMap<Integer, String>();
            try {
                FileReader input = new FileReader(inFile);
                bufRead = new BufferedReader(input);
                Record record;
                while ((record = Utils.parseFile(bufRead)) != null) {
                    String s = record.getSymbolString();
                    if (symbolsList.contains(s)) {
                        maps.put(record.getSymbol(), record.getSymbolString());
                    }
                }
                System.out.println("No of stocks: " + maps.size());
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to open file");
            }
            return maps;
        }
        return null;
    }
}
