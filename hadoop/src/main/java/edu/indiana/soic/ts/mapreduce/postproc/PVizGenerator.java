package edu.indiana.soic.ts.mapreduce.postproc;

import edu.indiana.soic.ts.pviz.*;
import edu.indiana.soic.ts.utils.Point;
import edu.indiana.soic.ts.utils.Utils;
import edu.indiana.soic.ts.utils.VectorPoint;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PVizGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(PVizGenerator.class);

    public class PViZGeneratorMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
        private String clusterFile;
        private String pointsFolder;
        private String destFolder;
        private String vectorFile;
        private String originalFile;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            super.map(key, value, context);
        }
    }

    private static Clusters loadClusters(String clusterFile) {
        Clusters clusters;
        FileInputStream adrFile = null;
        try {
            adrFile = new FileInputStream(clusterFile);
            JAXBContext ctx = JAXBContext.newInstance(Clusters.class);
            Unmarshaller um = ctx.createUnmarshaller();
            clusters = (Clusters) um.unmarshal(adrFile);
            return clusters;
        } catch (FileNotFoundException | JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (adrFile != null) {
                try {
                    adrFile.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * Process a stock file and generate vectors for a month or year period
     */
    private static void processFile(File inFile, String destFolder, String vFile, Clusters clusters) {
        int size = -1;
        String fileName = inFile.getName();
        String fileNameWithOutExt = FilenameUtils.removeExtension(fileName);
        String outFileName = destFolder + "/" + fileNameWithOutExt + ".pviz";
        String vectorFile = vFile + "/" + fileNameWithOutExt + ".csv";
        FileReader input = null;
        List<String> symbols = loadSymbols(vectorFile);
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
            Utils.savePlotViz(outFileName, plotviz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // load symbols for each point in file
    private static List<String> loadSymbols(String vectorFile) {
        LOG.info("Loading symbols from vector file: " + vectorFile);
        File vf = new File(vectorFile);
        List<VectorPoint> vectorPoints = Utils.readVectors(vf);
        List<String> symbols = new ArrayList<String>();
        for (int i = 0; i < vectorPoints.size(); i++) {
            VectorPoint v = vectorPoints.get(i);
            String symbol =  v.getSymbol();
            symbols.add(symbol);
        }
        LOG.info("No of symbols for point: " + symbols.size());
        return symbols;
    }
}
