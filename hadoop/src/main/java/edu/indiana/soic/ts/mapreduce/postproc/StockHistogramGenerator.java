package edu.indiana.soic.ts.mapreduce.postproc;

import edu.indiana.soic.ts.utils.Bin;
import edu.indiana.soic.ts.utils.Constants;
import edu.indiana.soic.ts.utils.Utils;
import edu.indiana.soic.ts.utils.VectorPoint;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StockHistogramGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(StockHistogramGenerator.class);

    public class HistogramMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
        private Bin[] bins;
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);

            Configuration conf = context.getConfiguration();
            double min = conf.getDouble(Constants.Histogram.MIN, -1);
            double max = conf.getDouble(Constants.Histogram.MAX, 1);
            int noOfBins = conf.getInt(Constants.Histogram.NO_OF_BINS, 50);

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
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("d", true, "Destination folder"); // Destination folder
        options.addOption("b", true, "Number of bins");
        options.addOption("s", true, "Stock file");         // Original global file

        options.addOption(Utils.createOption("g", false, "Use global bins", false));

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  _vectorFile = cmd.getOptionValue("v");
            String _distFile = cmd.getOptionValue("d");
            int bins = Integer.parseInt(cmd.getOptionValue("b"));
            String stockFile = cmd.getOptionValue("s");
            boolean globalBins = cmd.hasOption("g");


        } catch (org.apache.commons.cli.ParseException e) {
            String s = "Failed to read the command line options";
            LOG.error(s, e);
            throw new RuntimeException(s, e);
        }
    }
}
