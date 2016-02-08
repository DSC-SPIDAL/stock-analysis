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
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class VectorCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(VectorCalculator.class);

    private String startDate;
    private String endDate;
    private int window;
    private int headShift;
    private int tailShift;
    private TSConfiguration tsConfiguration;

    public void configure(TSConfiguration tsConfiguration) {
        Map conf = tsConfiguration.getConf();
        this.tsConfiguration = tsConfiguration;
        startDate = (String) conf.get(TSConfiguration.START_DATE);
        endDate = (String) conf.get(TSConfiguration.END_DATE);
        this.window = (int) conf.get(TSConfiguration.TIME_WINDOW);
        this.headShift = (int) conf.get(TSConfiguration.TIME_SHIFT_HEAD);
        this.tailShift = (int) conf.get(TSConfiguration.TIME_SHIFT_TAIL);

        LOG.info("Start Date : " + startDate);
        LOG.info("End Date : " + endDate);
        if (startDate == null || startDate.isEmpty()) {
            throw new RuntimeException("Start date should be specified");
        }
        if (endDate == null || endDate.isEmpty()) {
            throw new RuntimeException("End date should be specified");
        }
    }

    public void submitJob() {
        try {
            Configuration config = HBaseConfiguration.create();
            config.set("mapreduce.output.textoutputformat.separator", ",");
            TreeMap<String, List<Date>> genDates = TableUtils.genDates(TableUtils.getDate(startDate),
                    TableUtils.getDate(endDate), this.window, TimeUnit.DAYS, this.headShift, this.tailShift, TimeUnit.DAYS);
            for (String id : genDates.keySet()){
                LOG.info("Vector calculator for start: {}, end: {} time window: {}, shift: {}", startDate, endDate, window, headShift);
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
                Job job = new Job(config,"Vector calculation: " + id);
                job.setJarByClass(VectorCalculator.class);
                TableMapReduceUtil.initTableMapperJob(
                        Constants.STOCK_TABLE_NAME,        // input HBase table name
                        scan,             // Scan instance to control CF and attribute selection
                        VectorCalculatorMapper.class,   // mapper
                        IntWritable.class,             // mapper output key
                        Text.class,             // mapper output value
                        job);
                // adjust directories as required
                FileOutputFormat.setOutputPath(job, new Path(tsConfiguration.getVectorDir() + "/" + id));
                boolean b = job.waitForCompletion(true);
                if (!b) {
                    LOG.error("Error with job for vector calculation");
                    throw new RuntimeException("Error with job for vector calculation");
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
        String  configFile = Utils.getConfigurationFile(args);
        TSConfiguration tsConfiguration = new TSConfiguration(configFile);
        VectorCalculator vectorCalculator = new VectorCalculator();
        vectorCalculator.configure(tsConfiguration);
        vectorCalculator.submitJob();
    }
}
