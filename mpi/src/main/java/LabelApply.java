import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;

public class LabelApply {
    private String vectorFolder;
    private String pointsFolder;
    private String distFolder;
    private String originalStockFile;
    private String sectorFile;

    private Map<Integer, String> permNoToSymbol = new HashMap<Integer, String>();
    private Map<String, Integer> sectorToClazz = new HashMap<String, Integer>();
    private Map<String, String> invertedSectors = new HashMap<String, String>();

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("p", true, "Points folder");
        options.addOption("d", true, "Destination folder");
        options.addOption("o", true, "Original stock file");
        options.addOption("s", true, "Sector file");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String vectorFile = cmd.getOptionValue("v");
            String pointsFolder = cmd.getOptionValue("p");
            String distFolder = cmd.getOptionValue("d");
            String originalStocks = cmd.getOptionValue("o");
            String sectorFile = cmd.getOptionValue("s");

            LabelApply program = new LabelApply(vectorFile, pointsFolder, distFolder, originalStocks, sectorFile);
            program.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public LabelApply(String vectorFolder, String pointsFolder, String distFolder, String originalStockFile, String sectorFile) {
        this.vectorFolder = vectorFolder;
        this.pointsFolder = pointsFolder;
        this.distFolder = distFolder;
        this.originalStockFile = originalStockFile;
        this.sectorFile = sectorFile;
        init();
    }

    private void init() {
        permNoToSymbol = loadMapping(originalStockFile);
        Map<String, List<String>> sectors = loadSectors(sectorFile);
        sectorToClazz = convertSectorsToClazz(sectors);
    }

    public void process() {
        File inFolder = new File(vectorFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder: " + vectorFolder);
            return;
        }

        for (File inFile : inFolder.listFiles()) {
            String fileName = inFile.getName();
            String fileNameWithOutExt = FilenameUtils.removeExtension(fileName);
            processFile(fileNameWithOutExt);
        }
    }

    private Map<String, List<String>> loadSectors(String sectorFile) {
        FileReader input;
        Map<String, List<String>> sectors = new HashMap<String, List<String>>();
        try {
            input = new FileReader(sectorFile);
            BufferedReader bufRead = new BufferedReader(input);
            String line;

            while ((line = bufRead.readLine()) != null) {
                SectorRecord sectorRecord = Utils.readSectorRecord(line);
                List<String> stockList = sectors.get(sectorRecord.getSector());
                if (stockList == null) {
                    stockList = new ArrayList<String>();
                    sectors.put(sectorRecord.getSector(), stockList);
                }
                stockList.add(sectorRecord.getSymbol());

                invertedSectors.put(sectorRecord.getSymbol(), sectorRecord.getSector());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sector file", e);
        }
        return sectors;
    }

    private Map<String, Integer> convertSectorsToClazz(Map<String, List<String>> sectors) {
        List<String> sectorNames = new ArrayList<>(sectors.keySet());
        Collections.sort(sectorNames);
        Map<String, Integer> sectorsToClazz = new HashMap<String, Integer>();
        for (int i = 0; i < sectorNames.size(); i++) {
            sectorsToClazz.put(sectorNames.get(i), i + 1);
        }
        return sectorsToClazz;
    }

    private void processFile(String file) {
        String vectorFile = vectorFolder + "/" + file + ".csv";
        String pointsFile = pointsFolder + "/" + file + "points.txt";
        String pointsOutFile = distFolder + "/" + file + ".txt";
        List<String> symbols = loadSymbols(vectorFile);

        applyLabel(pointsFile, pointsOutFile, symbols);
    }

    private void applyLabel(String inPointsFile, String outPointsFile, List<String> symbols) {
        System.out.println("Applying labels for points file: " + inPointsFile);
        FileReader input;
        BufferedWriter bufWriter = null;
        try {
            FileOutputStream fos = new FileOutputStream(new File(outPointsFile));
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            File inFile = new File(inPointsFile);
            if (!inFile.exists()) {
                return;
            }
            input = new FileReader(inPointsFile);
            BufferedReader bufRead = new BufferedReader(input);
            String inputLine;
            int index = 0;
            while ((inputLine = bufRead.readLine()) != null && index < symbols.size())  {
                Point p = Utils.readPoint(inputLine);
                String symbol = symbols.get(index);
                // get the corresponding symbol
                // get the class for this one
                String sector = invertedSectors.get(symbol);
                int clazz = 0;
                if (sector != null) {
                    clazz = sectorToClazz.get(sector);
                }
                p.setClazz(clazz);
                String s = p.serialize();
                bufWriter.write(s);
                bufWriter.newLine();
                index++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read/write file", e);
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    // first read the original stock file and load the mapping from permno to stock symbol
    private Map<Integer, String> loadMapping(String inFile) {
        System.out.println("Reading original stock file: " + inFile);
        BufferedReader bufRead = null;
        Map<Integer, String> maps = new HashMap<Integer, String>();
        try {
            FileReader input = new FileReader(inFile);
            bufRead = new BufferedReader(input);

            Record record;
            while ((record = Utils.parseFile(bufRead)) != null) {
                maps.put(record.getSymbol(), record.getSymbolString());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open file");
        }
        return maps;
    }

    // load symbols for each point in file
    private List<String> loadSymbols(String vectorFile) {
        System.out.println("Loading symbols from vector file: " + vectorFile);
        File vf = new File(vectorFile);
        List<VectorPoint> vectorPoints = Utils.readVectors(vf, 0, 7000);
        List<String> symbols = new ArrayList<String>();
        for (int i = 0; i < vectorPoints.size(); i++) {
            VectorPoint v = vectorPoints.get(i);
            String symbol =  permNoToSymbol.get(v.getKey());
            symbols.add(symbol);
        }
        return symbols;
    }
}
