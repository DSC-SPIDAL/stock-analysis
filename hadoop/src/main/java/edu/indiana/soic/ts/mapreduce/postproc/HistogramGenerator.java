package edu.indiana.soic.ts.mapreduce.postproc;

import edu.indiana.soic.ts.mapreduce.pwd.SWGWritable;
import edu.indiana.soic.ts.utils.*;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class HistogramGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(HistogramGenerator.class);

    private double min;
    private double max;
    private int bins;
    private String vectDir;
    private String interHistDir;

    public void configure(String []args) {
        String  configFile = Utils.getConfigurationFile(args);
        TSConfiguration tsConfiguration = new TSConfiguration(configFile);
        Map tsConf = tsConfiguration.getConf();
    }

    public void execJob(Configuration conf, String vectorFileFullPath, String vectorFile, String interHistDir) throws Exception {
        LOG.info(vectorFileFullPath);
        Job job = new Job(conf, "Pairwise-calc-" + vectorFile);

		/* create the base dir for this job. Delete and recreates if it exists */
        Path hdMainDir = new Path(interHistDir + "/" + vectorFile);
        FileSystem fs = FileSystem.get(conf);
        fs.delete(hdMainDir, true);
        Path hdInputDir = new Path(hdMainDir, "data");
        if (!fs.mkdirs(hdInputDir)) {
            throw new IOException("Mkdirs failed to create " + hdInputDir.toString());
        }
    }

    public void submitJob() {
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            FileStatus[] status = fs.listStatus(new Path(vectDir));
            for (int i = 0; i < status.length; i++) {
                String sequenceFile = status[i].getPath().getName();
                String sequenceFileFullPath = vectDir + "/" + sequenceFile;
                try {
                    execJob(conf, sequenceFileFullPath, sequenceFile, interHistDir);
                } catch (Exception e) {
                    String message = "Failed to executed PWD calculation:" + sequenceFileFullPath + " " + interHistDir;
                    LOG.info(message, e);
                    throw new RuntimeException(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class HistogramMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
        private Bin[] bins;
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);

            Configuration conf = context.getConfiguration();
            double min = conf.getDouble(TSConfiguration.Histogram.MIN, -1);
            double max = conf.getDouble(TSConfiguration.Histogram.MAX, 1);
            int noOfBins = conf.getInt(TSConfiguration.Histogram.NO_OF_BINS, 50);
            this.bins = getBins(noOfBins, max, min);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            VectorPoint p = Utils.parseVector(value.toString());
            if (p != null) {
                double d = vectorDelta(p.getNumbers());
                int binIndex = getBinIndex(d, this.bins);
                context.write(new IntWritable(binIndex), new Text(p.getSymbol()));
            }
        }
    }

    public class HistogramReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
        private Bin[] bins;
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);

            Configuration conf = context.getConfiguration();
            double min = conf.getDouble(TSConfiguration.Histogram.MIN, -1);
            double max = conf.getDouble(TSConfiguration.Histogram.MAX, 1);
            int noOfBins = conf.getInt(TSConfiguration.Histogram.NO_OF_BINS, 50);

            this.bins = getBins(noOfBins, max, min);
        }

        public void reduce(IntWritable key, Iterable<Text> values,
                           Context context) throws IOException, InterruptedException {
            StringBuilder sb = new StringBuilder();
            Bin bin = this.bins[key.get()];
            sb.append(bin.start).append(",").append(bin.end).append(",");
            for (Text t : values) {
                sb.append(t.toString()).append(",");
            }
            context.write(key, new Text(sb.toString()));
        }
    }

    private static int getBinIndex(double val, Bin []bins) {
        for (int i = 0; i < bins.length; i++) {
            Bin b = bins[i];
            // add all that is below the 0'th bin to 0
            if (val < b.start) {
                return i;
            }

            if (b.start <= val && b.end >= val) {
                return i;
            }
        }
        // add all that is over the last bin value to last
        return bins.length - 1;
    }

    private static Bin[] getBins(int noOfBins, double max, double min) {
        double delta = (max - min) / noOfBins;
        Bin []bins = new Bin[noOfBins];
        for (int i = 0; i < bins.length; i++) {
            Bin b = new Bin();
            b.start = min + i * delta;
            b.end = min + (i + 1)* (delta);
            bins[i] = b;
        }
        return bins;
    }

    private static double vectorDelta(double []n) {
        double sum = 0.0;
        for (double aN : n) {
            sum += aN;
        }
        if (sum == 0) return .1;
        double delta = n[n.length - 1] - n[0];
        return delta * n.length / sum;
    }


    public static void main(String[] args) {
        HistogramGenerator hist = new HistogramGenerator();
        hist.configure(args);
        hist.submitJob();
    }
}
