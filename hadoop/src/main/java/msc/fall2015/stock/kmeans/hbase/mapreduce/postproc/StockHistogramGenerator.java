package msc.fall2015.stock.kmeans.hbase.mapreduce.postproc;

import msc.fall2015.stock.kmeans.hbase.mapreduce.StockDataReaderMapper;
import msc.fall2015.stock.kmeans.hbase.mapreduce.StockVectorCalculatorMapper;
import msc.fall2015.stock.kmeans.hbase.utils.Bin;
import msc.fall2015.stock.kmeans.hbase.utils.TableUtils;
import msc.fall2015.stock.kmeans.hbase.utils.Utils;
import msc.fall2015.stock.kmeans.utils.Constants;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class StockHistogramGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(StockHistogramGenerator.class);

    private Bin[] getBins(int noOfBins, int max, int min) {
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

    private

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
