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
package edu.indiana.soic.ts.streaming.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.google.protobuf.ServiceException;
import edu.indiana.soic.ts.streaming.storm.utils.HBaseDumper;
import edu.indiana.soic.ts.streaming.storm.utils.StreamData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DumpToHBaseBolt extends BaseRichBolt {

    private final static Logger logger = LoggerFactory.getLogger(DumpToHBaseBolt.class);

    private HBaseDumper hbaseDumper;

    private OutputCollector collector;

    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        try {
            createTable("result","dcf");
            hbaseDumper = new HBaseDumper("result", "dcf", new String[]{"price"});
            this.collector = collector;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Create table if not exists
     * @throws Exception
     */
    private void createTable(String tableName, String columnFamily) throws Exception {
        try {
            Configuration configuration = HBaseConfiguration.create();
            HBaseAdmin.checkHBaseAvailable(configuration);
            Connection connection = ConnectionFactory.createConnection(configuration);

            // Instantiating HBaseAdmin class
            Admin admin = connection.getAdmin();

            // Instantiating table descriptor class
            HTableDescriptor stockTableDesc = new HTableDescriptor(TableName.valueOf(tableName));

            // Adding column families to table descriptor
            HColumnDescriptor stockColDesc = new HColumnDescriptor(columnFamily);
            stockTableDesc.addFamily(stockColDesc);

            // Execute the table through admin
            if (!admin.tableExists(stockTableDesc.getTableName())) {
                admin.createTable(stockTableDesc);
                logger.info("Table " + tableName + " created !!!");
            }

            // Load hbase-site.xml
            HBaseConfiguration.addHbaseResources(configuration);
        } catch (ServiceException e) {
            logger.error("Error occurred while creating HBase tables", e);
            throw new Exception("Error occurred while creating HBase tables", e);
        }
    }


    @Override
    public void execute(Tuple input) {
        List<Object> fields = input.getValues();
        byte[] rowKey = ((StreamData) fields.get(0)).getData();
        byte[][] valuesArray = new byte[1][];
        byte[] priceBytes = ((StreamData) fields.get(4)).getData();
        valuesArray[0] = priceBytes;
        try {
            hbaseDumper.writeToTable(rowKey, valuesArray);
        } catch (IOException e) {
            e.printStackTrace();
            collector.reportError(e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public void cleanup() {
        try {
            hbaseDumper.flushTable();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}