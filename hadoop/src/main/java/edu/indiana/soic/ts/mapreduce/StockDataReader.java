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

import edu.indiana.soic.ts.utils.Constants;
import edu.indiana.soic.ts.utils.TableUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * This class reads data of a given data range and find the regression (intercept, slope and error)
 * mapper class : HBaseDataReaderMapper
 */
public class StockDataReader {
    private static String startDate;
    private static String endDate;
    private static final Logger log = LoggerFactory.getLogger(StockDataReader.class);

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        try {
            startDate = args[1];
            endDate = args[2];
            System.out.println("Start Date : " + startDate);
            System.out.println("End Date : " + endDate);
            if (startDate == null || startDate.isEmpty()) {
                // set 1st starting date
                startDate = "20040102";
            }
            if (endDate == null || endDate.isEmpty()) {
                endDate = "20141231";
            }
            Configuration config = HBaseConfiguration.create();
            Job job = new Job(config, "ExampleRead");
            job.setJarByClass(StockDataReaderMapper.class);     // class that contains mapper

            Scan scan = new Scan();
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
            scan.setCacheBlocks(false);  // don't set to true for MR jobs
            List<String> suitableDates = TableUtils.getDates(startDate, endDate);
            if (suitableDates != null && !suitableDates.isEmpty()){
                for (String date : suitableDates){
                    scan.addColumn(Constants.STOCK_TABLE_CF_BYTES, date.getBytes());
                }
            }
            TableMapReduceUtil.initTableMapperJob(
                    Constants.STOCK_TABLE_NAME,        // input HBase table name
                    scan,             // Scan instance to control CF and attribute selection
                    StockDataReaderMapper.class,   // mapper
                    null,             // mapper output key
                    null,             // mapper output value
                    job);
            job.setOutputFormatClass(NullOutputFormat.class);   // because we aren't emitting anything from mapper

            boolean b = job.waitForCompletion(true);
            if (!b) {
                throw new IOException("error with job!");
            }
        } catch (ParseException e) {
            log.error("Error while parsing date", e);
            throw new RuntimeException("Error while parsing date", e);
        }
    }
}
