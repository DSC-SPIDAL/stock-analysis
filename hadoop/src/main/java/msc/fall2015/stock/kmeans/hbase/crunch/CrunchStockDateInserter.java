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

package msc.fall2015.stock.kmeans.hbase.crunch;

import com.google.protobuf.ServiceException;
import msc.fall2015.stock.kmeans.hbase.crunch.utils.CrunchUtils;
import msc.fall2015.stock.kmeans.utils.Constants;
import org.apache.crunch.PCollection;
import org.apache.crunch.Pipeline;
import org.apache.crunch.PipelineResult;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.io.hbase.HBaseTarget;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class CrunchStockDateInserter extends Configured implements Tool, Serializable {
    private static final Logger log = LoggerFactory.getLogger(CrunchStockDateInserter.class);
    public static void main(final String[] args) throws Exception {
        final int res = ToolRunner.run(new Configuration(), new CrunchStockDateInserter(), args);
        System.exit(res);
    }

    @Override
    public int run(final String[] args) throws Exception {
        createTable();
        final Configuration config = getConf();
        final Pipeline pipeline = new MRPipeline(CrunchStockDateInserter.class,
                "PipelineWithFilterFn", config);
        PCollection<String> lines = pipeline.readTextFile(Constants.HDFS_INPUT_PATH + "/2004_2014.csv");
        PCollection<Put> resultPut = CrunchUtils.returnDates(lines);
        System.out.println("********** size ************ : " + resultPut.getSize() );

        pipeline.write(resultPut, new HBaseTarget(Constants.STOCK_DATES_TABLE));
        PipelineResult result = pipeline.done();
        return result.succeeded() ? 0 : 1;
    }

    private static void createTable() throws Exception {
        try {
            Configuration configuration = HBaseConfiguration.create();
            HBaseAdmin.checkHBaseAvailable(configuration);
            Connection connection = ConnectionFactory.createConnection(configuration);

            // Instantiating HbaseAdmin class
            Admin admin = connection.getAdmin();

            // Instantiating table descriptor class
            HTableDescriptor stockTableDesc = new HTableDescriptor(TableName.valueOf(Constants.STOCK_DATES_TABLE));

            // Adding column families to table descriptor
            HColumnDescriptor stock_0414 = new HColumnDescriptor(Constants.STOCK_DATES_CF);
            stockTableDesc.addFamily(stock_0414);

            // Execute the table through admin
            if (!admin.tableExists(stockTableDesc.getTableName())) {
                admin.createTable(stockTableDesc);
                System.out.println("Stock table created !!!");
            }

            // Load hbase-site.xml
            HBaseConfiguration.addHbaseResources(configuration);
        } catch (ServiceException e) {
            log.error("Error occurred while creating HBase tables", e);
            throw new Exception("Error occurred while creating HBase tables", e);
        }
    }
}
