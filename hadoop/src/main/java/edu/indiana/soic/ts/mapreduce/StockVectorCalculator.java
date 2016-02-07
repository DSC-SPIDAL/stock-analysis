/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

package edu.indiana.soic.ts.mapreduce;

import edu.indiana.soic.ts.utils.TSConfiguration;
import edu.indiana.soic.ts.utils.TableUtils;
import edu.indiana.soic.ts.utils.Constants;
import edu.indiana.soic.ts.utils.Utils;
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
import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StockVectorCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(StockVectorCalculator.class);

    private String startDate;
    private String endDate;
    private int mode;

    public void configure(Map conf) {
        startDate = (String) conf.get(TSConfiguration.START_DATE);
        endDate = (String) conf.get(TSConfiguration.END_DATE);
        LOG.info("Start Date : " + startDate);
        LOG.info("End Date : " + endDate);
        if (startDate == null || startDate.isEmpty()) {
            throw new RuntimeException("Start date should be specified");
        }
        if (endDate == null || endDate.isEmpty()) {
            throw new RuntimeException("End date should be specified");
        }
        if (mode == 0){
            mode = 1;
        }
    }

    public void submitJob() {
        try {
            Configuration config = HBaseConfiguration.create();
            config.set("mapreduce.output.textoutputformat.separator", ",");
            TreeMap<String, List<Date>> genDates = TableUtils.genDates(TableUtils.getDate(startDate), TableUtils.getDate(endDate), mode);
            for (String id : genDates.keySet()){
                Scan scan = new Scan();
                scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
                scan.setCacheBlocks(false);  // don't set to true for MR jobs
                List<Date> dates = genDates.get(id);
                String start  = TableUtils.convertDateToString(dates.get(0));
                String end  = TableUtils.convertDateToString(dates.get(1));
                List<String> suitableDateList = TableUtils.getDates(start, end);
                config.set(Constants.Job.NO_OF_DAYS, String.valueOf(suitableDateList.size()));
                for (String date : suitableDateList){
                    scan.addColumn(Constants.STOCK_TABLE_CF_BYTES, date.getBytes());
                }
                Job job = new Job(config,"ExampleSummaryToFile");
                job.setJarByClass(StockVectorCalculator.class);
                TableMapReduceUtil.initTableMapperJob(
                        Constants.STOCK_TABLE_NAME,        // input HBase table name
                        scan,             // Scan instance to control CF and attribute selection
                        StockVectorCalculatorMapper.class,   // mapper
                        IntWritable.class,             // mapper output key
                        Text.class,             // mapper output value
                        job);
                FileOutputFormat.setOutputPath(job, new Path(Constants.HDFS_OUTPUT_PATH + id));  // adjust directories as required
                boolean b = job.waitForCompletion(true);
                if (!b) {
                    LOG.error("Error with job...!!!");
                    throw new IOException("Error with job...!!!");
                }
            }

        } catch (ParseException e) {
            LOG.error("Error while parsing date", e);
            throw new RuntimeException("Error while parsing date", e);
        } catch (InterruptedException | ClassNotFoundException | IOException e) {
            LOG.error("Error while creating the job", e);
            throw new RuntimeException("Error while creating the job", e);
        }
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Utils.createOption("c", true, "Configuration file", true));
        CommandLineParser commandLineParser = new BasicParser();
        CommandLine cmd;
        try {
            cmd = commandLineParser.parse(options, args);
            String  configFile = cmd.getOptionValue("c");
            Map conf = (Map) Yaml.load(new File(configFile));

            StockVectorCalculator vectorCalculator = new StockVectorCalculator();
            vectorCalculator.configure(conf);
            vectorCalculator.submitJob();
        } catch (org.apache.commons.cli.ParseException e) {
            String s = "Invalid command line options";
            LOG.error(s, e);
            throw new RuntimeException(s, e);
        } catch (FileNotFoundException e) {
            String s = "Failed read the configuration";
            LOG.error(s, e);
            throw new RuntimeException(s, e);
        }
    }
}
