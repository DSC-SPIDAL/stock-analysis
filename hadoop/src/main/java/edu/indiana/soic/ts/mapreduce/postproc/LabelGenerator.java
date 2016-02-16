package edu.indiana.soic.ts.mapreduce.postproc;

import edu.indiana.soic.ts.utils.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LabelGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(LabelGenerator.class);
    private String pointFileDir;
    private String vectorFileDir;
    private String interOutDir;

    private TSConfiguration tsConfiguration;

    public static void main(String[] args) throws Exception {
        String  configFile = Utils.getConfigurationFile(args);
        TSConfiguration tsConfiguration = new TSConfiguration(configFile);
        LabelGenerator vectorCalculator = new LabelGenerator();
        vectorCalculator.configure(tsConfiguration);
        vectorCalculator.submitJob();
    }

    public void configure(TSConfiguration tsConfiguration) {
        this.tsConfiguration = tsConfiguration;
        this.interOutDir = tsConfiguration.getIntermediateLabelDir();
        this.vectorFileDir = tsConfiguration.getVectorDir();
        this.pointFileDir = tsConfiguration.getPointDir();
    }

    public int execJob(Configuration conf, String fileName) throws Exception {
        Job job = new Job(conf, "Labelgen-" + fileName);

        Path vectorFilePath = new Path(this.vectorFileDir + "/" + fileName);
        Path pointFilePath = new Path(this.pointFileDir + "/" + fileName);

		/* create the out dir for this job. Delete and recreates if it exists */
        Path labelOutDir = new Path(this.interOutDir + "/" + fileName);
        FileSystem fs = FileSystem.get(conf);
        fs.delete(labelOutDir, true);

        MultipleInputs.addInputPath(job, vectorFilePath, TextInputFormat.class, VectorReadMapper.class);
        MultipleInputs.addInputPath(job, pointFilePath, TextInputFormat.class, PointReadMapper.class);

        job.setJarByClass(LabelGenerator.class);
        job.setReducerClass(LabelGeneratorReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileOutputFormat.setOutputPath(job, labelOutDir);
        job.setOutputFormatClass(TextOutputFormat.class);

        long startTime = System.currentTimeMillis();
        int exitStatus = job.waitForCompletion(true) ? 0 : 1;
        double executionTime = (System.currentTimeMillis() - startTime) / 1000.0;
        LOG.info("Job Finished in " + executionTime + " seconds");
        return exitStatus;
    }

    public void submitJob() {
        Configuration conf = new Configuration();
        FileSystem fs;
        try {
            fs = FileSystem.get(conf);
            FileStatus[] status = fs.listStatus(new Path(this.pointFileDir));
            for (FileStatus statu : status) {
                String fileName = statu.getPath().getName();
                try {
                    execJob(conf, fileName);
                    Utils.concatOutput(conf, fileName, this.interOutDir + "/" + fileName, tsConfiguration.getLabelDir());
                } catch (Exception e) {
                    String message = "Failed to executed label generation:" + fileName;
                    LOG.info(message, e);
                    throw new RuntimeException(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class VectorReadMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            VectorPoint p = Utils.parseVector(value.toString());
            if (p != null && p.getSymbol() != null) {
                context.write(key, new Text("#" + p.getSymbol()));
            } else {
                String msg = "Invalid vector point";
                LOG.error(msg);
                throw new RuntimeException(msg);
            }
        }
    }

    public class PointReadMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                context.write(key, value);
            } catch (Exception e) {
                String msg = "Failed to read the point";
                LOG.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
    }

    public class LabelGeneratorReducer extends Reducer<LongWritable, Text, Text, Text> {
        private Map<String, Integer> symbolsToClass;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            Configuration conf = context.getConfiguration();
            FileSystem fs = FileSystem.get(conf);

            String histoFile = conf.get(TSConfiguration.Histogram.HISTO_FILE);
            Path histoFilePath = new Path(histoFile);
            symbolsToClass = loadHistoSectors(fs, histoFilePath);
        }

        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String symbol = null;
            String pointValue = null;
            for (Text t : values) {
                String ts = t.toString();
                if (ts.startsWith("#")) {
                    symbol = ts.substring(1);
                } else {
                    pointValue = ts;
                }
            }

            if (symbol != null && pointValue != null) {
                try {
                    Point p = Utils.readPointWithoutSymbol(pointValue);
                    p.setClazz(symbolsToClass.get(symbol));
                    context.write(new Text(symbol), new Text(p.serialize()));
                } catch (Exception e) {
                    String msg = "Failed to read point: " + pointValue;
                    LOG.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            }
        }
    }

    private static Map<String, Integer> loadHistoSectors(FileSystem fs, Path histoFile) {
        BufferedReader br = null;
        Map<String, Integer> classToSymbo =  new HashMap<>();
        try {
            br = new BufferedReader(new InputStreamReader(fs.open(histoFile)));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                Bin bin = Utils.readBin(line);
                if (bin != null) {
                    for (String s : bin.symbols) {
                        classToSymbo.put(s, bin.index + 1);
                    }
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
        return classToSymbo;
    }
}
