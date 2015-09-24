import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import pviz.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

    public class PvizGenerator {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("c", true, "Cluste file");
        options.addOption("p", true, "Points folder");
        options.addOption("d", true, "Output folder");
        options.addOption("o", true, "Original stock file");
        options.addOption("v", true, "Vector file");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String cluster = cmd.getOptionValue("c");
            String point = cmd.getOptionValue("p");
            String outputFolder = cmd.getOptionValue("d");
            String originalStockFile = cmd.getOptionValue("o");
            String vectorFolder = cmd.getOptionValue("v");

            PvizGenerator generator = new PvizGenerator(cluster, point, outputFolder, vectorFolder, originalStockFile);
            generator.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String clusterFile;
    private String pointsFolder;
    private String destFolder;
    private String vectorFile;
    private String originalFile;

    public PvizGenerator(String clusterFile, String pointsFolder, String destFolder, String vectorFile, String originalFile) {
        this.clusterFile = clusterFile;
        this.pointsFolder = pointsFolder;
        this.destFolder = destFolder;
        this.vectorFile = vectorFile;
        this.originalFile = originalFile;
    }

    private Clusters loadClusters() {
        Clusters clusters;
        FileInputStream adrFile = null;
        try {
            adrFile = new FileInputStream(clusterFile);
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


    public void process() {
        File inFolder = new File(pointsFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder: " + pointsFolder);
            return;
        }

        // create the output folder
        File outFolder = new File(destFolder);
        outFolder.mkdirs();

        Clusters clusters = loadClusters();
        Map<Integer, String> permNoToSymbol = Utils.loadMapping(originalFile);

        for (File inFile : inFolder.listFiles()) {
            if (inFile.isDirectory()) {
                continue;
            }

            processFile(inFile, clusters, permNoToSymbol);
        }
    }
    /**
     * Process a stock file and generate vectors for a month or year period
     */
    private void processFile(File inFile, Clusters clusters, Map<Integer, String> permNoToSymbol) {
        int size = -1;
        FileOutputStream fileOutputStream = null;

        String fileName = inFile.getName();
        String fileNameWithOutExt = FilenameUtils.removeExtension(fileName);
        String outFileName = destFolder + "/" + fileNameWithOutExt + ".pviz";
        String vectorFile = this.vectorFile + "/" + fileNameWithOutExt + ".csv";
        FileReader input = null;
        List<String> symbols = loadSymbols(vectorFile, permNoToSymbol);
        // create the XML
        Plotviz plotviz = new Plotviz();
        Glyph glyph = new Glyph(1, 1);
        Plot plot = new Plot();
        plot.setGlyph(glyph);
        plot.setPointsize(1);
        plot.setTitle(fileNameWithOutExt);
        plotviz.setPlot(plot);
        plotviz.setClusters(clusters.getCluster());
        List<PVizPoint> pVizPoints = new ArrayList<PVizPoint>();
        int index = 0;
        try {
            input = new FileReader(inFile);
            BufferedReader bufRead = new BufferedReader(input);
            String inputLine;
            while ((inputLine = bufRead.readLine()) != null) {
                if (index >= symbols.size()) {
                    throw new RuntimeException("Index cannot be greater than symbols: index =" + index + " symbols ="  + symbols);
                }
                Point p = Utils.readPoint(inputLine);
                PVizPoint pVizPoint = new PVizPoint(p.getIndex(), p.getClazz(), symbols.get(index), new Location(p.getX(), p.getY(), p.getZ()));
                pVizPoints.add(pVizPoint);
                index++;
            }
            plotviz.setPoints(pVizPoints);

            // now write the xml
            fileOutputStream = new FileOutputStream(outFileName);
            JAXBContext ctx = JAXBContext.newInstance(Plotviz.class);
            Marshaller ma = ctx.createMarshaller();
            ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ma.marshal(plotviz, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    // load symbols for each point in file
    private List<String> loadSymbols(String vectorFile, Map<Integer, String> permNoToSymbol) {
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
