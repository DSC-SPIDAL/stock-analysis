package edu.indiana.soic.ts.mapreduce.postproc;

import edu.indiana.soic.ts.pviz.*;
import edu.indiana.soic.ts.utils.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PVizGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(PVizGenerator.class);
    private String startDate;
    private String endDate;
    private int window;
    private int headShift;
    private int tailShift;
    private TSConfiguration tsConfiguration;

    public void configure(TSConfiguration tsConfiguration) {
        Map conf = tsConfiguration.getConf();
        this.tsConfiguration = tsConfiguration;
        this.startDate = (String) conf.get(TSConfiguration.START_DATE);
        this.endDate = (String) conf.get(TSConfiguration.END_DATE);
        this.window = (int) conf.get(TSConfiguration.TIME_WINDOW);
        this.headShift = (int) conf.get(TSConfiguration.TIME_SHIFT_HEAD);
        this.tailShift = (int) conf.get(TSConfiguration.TIME_SHIFT_TAIL);

        if (startDate == null || startDate.isEmpty()) {
            throw new RuntimeException("Start date should be specified");
        }

        if (endDate == null || endDate.isEmpty()) {
            throw new RuntimeException("End date should be specified");
        }
    }

    public static class LabelReadMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
        private String clusterFile;
        private String pointsFolder;
        private String destFolder;
        private String vectorFile;
        private String originalFile;
        private String labelFilesDir;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            FileSystem fs = FileSystem.get(conf);

            // read the file
            String fileName = value.toString();
            Path path = new Path(labelFilesDir);



            // now load the vectors
            Path vectorFilePath = new Path(vectorFile);
            List<String> symbols = loadSymbols(fs, vectorFilePath);

            // now load the point file with the labels

        }
    }

    public static class VectorReadMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            super.map(key, value, context);
        }
    }

    public static class PVizReducer extends Reducer<LongWritable, Text, Text, Text> {
        private String clusterFile;
        private String pvizDir;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            Configuration conf = context.getConfiguration();



            FileSystem fs = FileSystem.get(conf);
            // lets load the cluster file first
            Path clusterFilePath = new Path(clusterFile);
            Clusters clusters = loadClusters(fs, clusterFilePath);
        }

        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            super.reduce(key, values, context);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);
        }
    }

    private static Clusters loadClusters(FileSystem fs, Path clusterFile) {
        Clusters clusters;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(fs.open(clusterFile));
            JAXBContext ctx = JAXBContext.newInstance(Clusters.class);
            Unmarshaller um = ctx.createUnmarshaller();
            clusters = (Clusters) um.unmarshal(inputStreamReader);
            return clusters;
        } catch (JAXBException | IOException e) {
            LOG.error("Failed to load the clusters", e);
            throw new RuntimeException(e);
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * Process a stock file and generate vectors for a month or year period
     */
    private static void processFile(FileSystem fs, Path labelFile, List<String> symbols, Clusters clusters, Path pvizFile) {
        int size = -1;
        InputStreamReader input = null;
        // create the XML
        Plotviz plotviz = new Plotviz();
        Glyph glyph = new Glyph(1, 1);
        Plot plot = new Plot();
        plot.setGlyph(glyph);
        plot.setPointsize(1);
        plot.setTitle(pvizFile.getName());
        plotviz.setPlot(plot);
        plotviz.setClusters(clusters.getCluster());
        List<PVizPoint> pVizPoints = new ArrayList<PVizPoint>();
        int index = 0;
        try {
            input = new InputStreamReader(fs.open(labelFile));
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
            Utils.savePlotViz(fs.create(pvizFile), plotviz);
        } catch (Exception e) {
            String s = "Failed to write the XML file";
            LOG.error(s, e);
            throw new RuntimeException(s, e);
        }
    }

    // load symbols for each point in file
    private static List<String> loadSymbols( FileSystem fs, Path vectorFile) {
        LOG.info("Loading symbols from vector file: " + vectorFile);
        List<VectorPoint> vectorPoints = null;
        try {
            vectorPoints = Utils.readVectors(fs.open(vectorFile));
            List<String> symbols = new ArrayList<String>();
            for (int i = 0; i < vectorPoints.size(); i++) {
                VectorPoint v = vectorPoints.get(i);
                String symbol =  v.getSymbol();
                symbols.add(symbol);
            }
            return symbols;
        } catch (IOException e) {
            String msg = "Failed to load symbols from vector file: " + vectorFile;
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    private void createFiles(Configuration conf, FileSystem fs, String outDir) {
        try {
            TreeMap<String, List<Date>> genDates = TableUtils.genDates(TableUtils.getDate(startDate),
                    TableUtils.getDate(endDate), this.window, TimeUnit.DAYS, this.headShift, this.tailShift, TimeUnit.DAYS);
            Path dirPath = new Path(outDir);
            for (String s : genDates.keySet()) {
                Path vFile = new Path(dirPath, s);
                SequenceFile.Writer vWriter = SequenceFile.createWriter(fs,
                        conf, vFile, LongWritable.class, Text.class,
                        SequenceFile.CompressionType.NONE);

                vWriter.append(new LongWritable(0),
                        new Text(s));
                vWriter.close();
            }
        } catch (ParseException e) {
            String s = "Failed to create the data list";
            LOG.error(s);
            throw new RuntimeException(s, e);
        } catch (IOException e) {
            String s = "Failed to create the data list file";
            LOG.error(s);
            throw new RuntimeException(s, e);
        }
    }
}
