package edu.indiana.soic.ts.mapreduce.postproc;

import edu.indiana.soic.ts.mapreduce.pwd.*;
import edu.indiana.soic.ts.pviz.*;
import edu.indiana.soic.ts.utils.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

public class PVizGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(PVizGenerator.class);
    private String labelDir;
    private String pvizDir;
    private String clusterFile;
    private String intermediatePvizDir;

    private TSConfiguration tsConfiguration;

    public static void main(String[] args) throws Exception {
        String  configFile = Utils.getConfigurationFile(args);
        TSConfiguration tsConfiguration = new TSConfiguration(configFile);
        PVizGenerator vectorCalculator = new PVizGenerator();
        vectorCalculator.configure(tsConfiguration);
        vectorCalculator.submitJob();
    }

    public void configure(TSConfiguration tsConfiguration) {
        this.tsConfiguration = tsConfiguration;
        this.labelDir = tsConfiguration.getLabelDir();
        this.pvizDir = tsConfiguration.getPVizDir();
        this.clusterFile = tsConfiguration.getClusterFile();
        this.intermediatePvizDir = tsConfiguration.getIntermediatePvizDir();
    }

    public void submitJob() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] status = fs.listStatus(new Path(labelDir));
        for (int i = 0; i < status.length; i++) {
            String labelFile = status[i].getPath().getName();
            try {
                execJob(conf, labelFile);
            } catch (Exception e) {
                String message = "Failed to execute pviz gen:" + labelFile;
                LOG.info(message, e);
                throw new RuntimeException(message);
            }
        }
    }

    public int execJob(Configuration conf, String fileName) throws Exception {
        Job job = new Job(conf, "PvizGeneration-" + fileName);

		/* create the base dir for this job. Delete and recreates if it exists */
        Path hdOutDir = new Path(this.intermediatePvizDir);
        FileSystem fs = FileSystem.get(conf);
        fs.delete(hdOutDir, true);
        if (!fs.mkdirs(hdOutDir)) {
            throw new IOException("Mkdirs failed to create " + hdOutDir.toString());
        }

        Path inputFilePath = new Path(this.labelDir + "/" + fileName);
        Path outputFilePath = new Path(this.intermediatePvizDir + "/" + fileName);

        Configuration jobConf = job.getConfiguration();
        jobConf.set(TSConfiguration.PViz.PVIZ_FILE, fileName);
        jobConf.set(TSConfiguration.PViz.DIR, this.pvizDir);
        jobConf.set(TSConfiguration.Label.DIR, this.labelDir);
        jobConf.set(TSConfiguration.PViz.CLUSTER_FILE, this.clusterFile);

        job.setJarByClass(PVizGenerator.class);
        job.setMapperClass(LabelReadMapper.class);
        job.setReducerClass(PVizReducer.class);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(SWGWritable.class);
        FileInputFormat.setInputPaths(job, inputFilePath);
        FileOutputFormat.setOutputPath(job, outputFilePath);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setNumReduceTasks(1);

        long startTime = System.currentTimeMillis();
        int exitStatus = job.waitForCompletion(true) ? 0 : 1;
        double executionTime = (System.currentTimeMillis() - startTime) / 1000.0;
        LOG.info("Job Finished in " + executionTime + " seconds");
        return exitStatus;
    }

    public static class LabelReadMapper extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                Point p = Utils.readPointWithSymbol(value.toString());
                context.write(new Text(p.getSymbol()), new Text(p.serialize()));
            } catch (Exception e) {
                String msg = "Failed to read the point";
                LOG.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
    }

    public static class PVizReducer extends Reducer<LongWritable, Text, Text, Text> {
        private String clusterFile;
        private String pvizDir;
        private Clusters clusters;
        private List<Point> points = new ArrayList<Point>();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);

            Configuration conf = context.getConfiguration();
            pvizDir = conf.get(TSConfiguration.PViz.DIR);
            clusterFile = conf.get(TSConfiguration.PViz.CLUSTER_FILE);

            FileSystem fs = FileSystem.get(conf);
            // lets load the cluster file first
            Path clusterFilePath = new Path(clusterFile);
            clusters = loadClusters(fs, clusterFilePath);

        }

        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text t : values) {
                try {
                    Point p = Utils.readPointWithSymbol(t.toString());
                    points.add(p);
                } catch (Exception e) {
                    String msg = "Failed to read the point: " + t.toString();
                    LOG.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);

            Configuration conf = context.getConfiguration();
            FileSystem fs = FileSystem.get(conf);
            Path pvizPath = new Path(pvizDir);
            createPvizFile(fs, clusters, points, pvizPath);
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
    private static void createPvizFile(FileSystem fs, Clusters clusters, List<Point> points, Path pvizFile) {
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
        try {
            for (Point p : points) {
                PVizPoint pVizPoint = new PVizPoint(p.getIndex(), p.getClazz(), p.getSymbol(), new Location(p.getX(), p.getY(), p.getZ()));
                pVizPoints.add(pVizPoint);
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
}
