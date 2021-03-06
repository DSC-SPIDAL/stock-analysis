import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import pviz.Cluster;
import pviz.Clusters;
import pviz.Plotviz;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class LabelApply {
    private final String fixedClassesFile;
    private String vectorFolder;
    private String pointsFolder;
    private String distFolder;
    private String originalStockFile;
    private String sectorFile;
    private boolean histogram;
    private String cluserInputFile;
    private String clusterOutputFile;
    private NumberFormat intgerFormaterr = new DecimalFormat("#00");

    private Map<Integer, String> permNoToSymbol = new HashMap<Integer, String>();
    private Map<String, Integer> sectorToClazz = new HashMap<String, Integer>();
    private Map<String, String> invertedSectors = new HashMap<String, String>();
    private Map<String, Integer> invertedFixedClases = new HashMap<String, Integer>();
    private NumberFormat formatter = new DecimalFormat("#0.00");

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder"); // yearly vector data
        options.addOption("p", true, "Points folder"); // yearly mds (rotate) output
        options.addOption("d", true, "Destination folder");
        options.addOption("o", true, "Original stock file"); // global 10 year stock file
        options.addOption("s", true, "Sector file"); // If Histogram true then set this as the folder to histogram output
        options.addOption("h", false, "Gen from histogram");
        options.addOption("e", true, "Extra classes file"); // a file containing fixed classes
        options.addOption("ci", true, "Cluster input file");
        options.addOption("co", true, "Cluster output file");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String vectorFile = cmd.getOptionValue("v");
            String pointsFolder = cmd.getOptionValue("p");
            String distFolder = cmd.getOptionValue("d");
            String originalStocks = cmd.getOptionValue("o");
            String sectorFile = cmd.getOptionValue("s");
            boolean histogram = cmd.hasOption("h");
            String fixedClasses = cmd.getOptionValue("e");
            String clusterInputFile = cmd.getOptionValue("ci");
            String clusterOutputFile = cmd.getOptionValue("co");

            LabelApply program = new LabelApply(vectorFile, pointsFolder, distFolder, originalStocks, sectorFile, histogram, fixedClasses, clusterInputFile, clusterOutputFile);
            program.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public LabelApply(String vectorFolder, String pointsFolder, String distFolder, String originalStockFile, String sectorFile, boolean histogram, String fixedClasses, String clusterInputFile, String clusterOutputFile) {
        this.vectorFolder = vectorFolder;
        this.pointsFolder = pointsFolder;
        this.distFolder = distFolder;
        this.originalStockFile = originalStockFile;
        this.histogram = histogram;
        this.sectorFile = sectorFile;
        this.fixedClassesFile = fixedClasses;
        this.clusterOutputFile = clusterOutputFile;
        this.cluserInputFile = clusterInputFile;
        init();
    }

    private void init() {
        permNoToSymbol = Utils.loadMapping(originalStockFile);
        Map<String, Integer> symbolToPerm = new HashMap<String, Integer>();
        for (Map.Entry<Integer, String> entry : permNoToSymbol.entrySet()) {
            symbolToPerm.put(entry.getValue(), entry.getKey());
        }
    }

    public void process() {
        File inFolder = new File(vectorFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder: " + vectorFolder);
            return;
        }
        boolean clusterSaved = false;
        this.invertedFixedClases = loadFixedClasses(fixedClassesFile);
        if (!histogram) {
            Map<String, List<String>> sectors = loadStockSectors(sectorFile);
            sectorToClazz = convertSectorsToClazz(sectors);
            if (!clusterSaved) {
                changeClassLabels();
                clusterSaved = true;
            }
            for (Map.Entry<String, Integer> entry : sectorToClazz.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }

        for (File inFile : inFolder.listFiles()) {
            String fileName = inFile.getName();
            String fileNameWithOutExt = FilenameUtils.removeExtension(fileName);
            if (histogram) {
                sectorToClazz.clear();
                invertedSectors.clear();

                Map<String, List<String>> sectors = loadHistoSectors(sectorFile + "/" + fileNameWithOutExt + ".csv");
                sectorToClazz = convertSectorsToClazz(sectors);
                if (!clusterSaved) {
                    changeClassLabels();
                    clusterSaved = true;
                }
                for (Map.Entry<String, Integer> entry : sectorToClazz.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
            }
            processFile(fileNameWithOutExt);
        }
    }

    private Clusters loadClusters(String cluserInputFile) {
        Clusters clusters;
        FileInputStream adrFile = null;
        try {
            adrFile = new FileInputStream(cluserInputFile);
            JAXBContext ctx = JAXBContext.newInstance(Clusters.class);
            Unmarshaller um = ctx.createUnmarshaller();
            clusters = (Clusters) um.unmarshal(adrFile);
            return clusters;
        }
        catch (FileNotFoundException | JAXBException e) {
            e.printStackTrace();
        } finally {
            if (adrFile != null) {
                try {
                    adrFile.close();
                } catch (IOException ignore) {
                }
            }
        }
        return null;
    }

    public static void saveClusters(String outFileName, Clusters plotviz) throws FileNotFoundException, JAXBException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outFileName);
            JAXBContext ctx = JAXBContext.newInstance(Clusters.class);
            Marshaller ma = ctx.createMarshaller();
            ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ma.marshal(plotviz, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }


    public void changeClassLabels() {
        System.out.println("Reading cluster file: " + cluserInputFile);
        Clusters clusters = loadClusters(cluserInputFile);
        if (clusters == null) {
            System.out.println("Not clusters found to change");
            return;
        }
        Map<Integer, String> classToSector = new HashMap<Integer, String>();
        for (Map.Entry<String, Integer> e: sectorToClazz.entrySet()) {
            classToSector.put(e.getValue(), e.getKey());
        }
        for (Cluster c : clusters.getCluster()) {
            // find the cluster label
            // this is the label
            String key = classToSector.get(c.getKey());
            if (key != null ) {
                System.out.println("Setting label: " + key + " to cluster: " + c.getKey()   );
                c.setLabel(key);
            }
        }
        try {
            System.out.println("Writing cluster file: " + clusterOutputFile);
            saveClusters(clusterOutputFile, clusters);
        } catch (FileNotFoundException | JAXBException e) {
            throw new RuntimeException("Failed to write clusters", e);
        }
    }

    private Map<String, Integer> loadFixedClasses(String file) {
        FileReader input;
        try {
            Map<Integer, List<String>> fixedClaszzes = new HashMap<Integer, List<String>>();
            Map<String, Integer> invertedFixedClasses = new HashMap<String, Integer>();
            File f = new File(file);
            if (!f.exists()) {
                System.out.println("Extra classes file doesn't exist: " + fixedClassesFile);
                return invertedFixedClasses;
            }
            input = new FileReader(f);
            BufferedReader bufRead = new BufferedReader(input);
            String line;
            while ((line = bufRead.readLine()) != null) {
                String parts[] = line.split(",");
                int clazz = Integer.parseInt(parts[0]);
                List<String> symbols = new ArrayList<String>();
                symbols.addAll(Arrays.asList(parts).subList(1, parts.length));
                fixedClaszzes.put(clazz, symbols);
            }

            for (Map.Entry<Integer, List<String>> e : fixedClaszzes.entrySet()) {
                for (String s : e.getValue()) {
                    invertedFixedClasses.put(s, e.getKey());
                }
            }
            return invertedFixedClasses;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, List<String>> loadHistoSectors(String sectorFile) {
        FileReader input;
        Map<String, List<String>> sectors = new HashMap<String, List<String>>();
        try {
            input = new FileReader(sectorFile);
            BufferedReader bufRead = new BufferedReader(input);
            String line;

            int i = 1;
            while ((line = bufRead.readLine()) != null) {
                Bin sectorRecord = Utils.readBin(line);
                List<String> stockList = sectorRecord.symbols;
                String startEnd = formatter.format(sectorRecord.end);
                String key = intgerFormaterr.format(i) + ":" + startEnd;
                sectors.put(key, stockList);
                for (String s : stockList) {
                    invertedSectors.put(s, key);
                }
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sector file", e);
        }
        return sectors;
    }

    private Map<String, List<String>> loadStockSectors(String sectorFile) {
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
        List<String> sectorNames = new ArrayList<String>(sectors.keySet());
        Collections.sort(sectorNames);
        Map<String, Integer> sectorsToClazz = new HashMap<String, Integer>();
        for (int i = 0; i < sectorNames.size(); i++) {
            sectorsToClazz.put(sectorNames.get(i), i + 1);
            System.out.println(sectorNames.get(i) + ": " + (i + 1));
        }
        return sectorsToClazz;
    }

    private void processFile(String file) {
        String vectorFile = vectorFolder + "/" + file + ".csv";
        String pointsFile = pointsFolder + "/" + file + ".txt";
        String pointsOutFile = distFolder + "/" + file + ".txt";
        List<String> symbols = loadSymbols(vectorFile);

        applyLabel(pointsFile, pointsOutFile, symbols);
    }

    private void applyLabel(String inPointsFile, String outPointsFile, List<String> symbols) {
        System.out.println("Applying labels for points file: " + inPointsFile);
        FileReader input;
        BufferedWriter bufWriter = null;
        try {
            FileOutputStream fos = new FileOutputStream(outPointsFile);
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            File inFile = new File(inPointsFile);
            if (!inFile.exists()) {
                System.out.println("ERROR: In file doens't exist");
                return;
            }
            input = new FileReader(inPointsFile);
            BufferedReader bufRead = new BufferedReader(input);
            String inputLine;
            int index = 0;
            while ((inputLine = bufRead.readLine()) != null && index < symbols.size())  {
                Point p = Utils.readPoint(inputLine);
                String symbol = symbols.get(index);
                int clazz = 0;
                if (this.invertedFixedClases.containsKey(symbol)) {
                    clazz = this.invertedFixedClases.get(symbol);
                } else {
                    // get the corresponding symbol
                    // get the class for this one
                    String sector = invertedSectors.get(symbol);
                    if (sector != null) {
                        clazz = sectorToClazz.get(sector);
                    } else {
//                    System.out.println("No sector: " + symbol);
                    }
                }
                p.setClazz(clazz);
                String s = p.serialize();
                bufWriter.write(s);
                bufWriter.newLine();
                index++;
            }
            System.out.println("Read lines: " + index);
        } catch (Exception e) {
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
        System.out.println("No of symbols for point: " + symbols.size());
        return symbols;
    }
}
