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

import com.google.protobuf.ServiceException;
import edu.indiana.soic.ts.utils.Constants;
import edu.indiana.soic.ts.utils.TSConfiguration;
import edu.indiana.soic.ts.utils.Utils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This is the main class to create table which insert all the available dates for the data set. This class creates
 * StockDatesTable and StockDatesCF in Hbase.
 * mapper class : InsertDateMapper
 * reducer class : InsertDateReducer
 * data structure : row key : date, row val : date
 */
public class DateLoader {
    private static final Logger log = LoggerFactory.getLogger(DateLoader.class);

    public static void main(String[] args) {
        try {
            TSConfiguration tsConfiguration = new TSConfiguration(Utils.getConfigurationFile(args));
            Configuration configuration =  HBaseConfiguration.create();
            HBaseAdmin.checkHBaseAvailable(configuration);
            Connection connection = ConnectionFactory.createConnection(configuration);

            // Instantiating HbaseAdmin class
            Admin admin = connection.getAdmin();

            // Instantiating table descriptor class
            HTableDescriptor stockDatesDesc = new HTableDescriptor(TableName.valueOf(Constants.STOCK_DATES_TABLE));

            // Adding column families to table descriptor
            HColumnDescriptor stock_Dates = new HColumnDescriptor(Constants.STOCK_DATES_CF);
            stockDatesDesc.addFamily(stock_Dates);

            if (!admin.tableExists(stockDatesDesc.getTableName())){
                admin.createTable(stockDatesDesc);
                System.out.println("Stock dates table created !!!");
            }
            // Load hbase-site.xml
            HBaseConfiguration.addHbaseResources(configuration);
            Job job = configureInsertAllJob(configuration, tsConfiguration);
            job.waitForCompletion(true);
        } catch (InterruptedException | ClassNotFoundException | IOException | ServiceException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to create job", e);
        }
    }

    public static Job configureInsertAllJob(Configuration configuration, TSConfiguration tsConfiguration) throws IOException {
        Job job = new Job(configuration, "HBase Date Table");
        job.setJarByClass(InsertDateMapper.class);

        job.setMapperClass(InsertDateMapper.class);
        TableMapReduceUtil.initTableReducerJob(Constants.STOCK_DATES_TABLE, InsertDateReducer.class, job);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(tsConfiguration.getInputDir()));
        FileOutputFormat.setOutputPath(job, new Path(Constants.HDFS_OUTPUT_PATH));
        return job;
    }
}
