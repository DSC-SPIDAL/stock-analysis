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
package edu.indiana.soic.ts.streaming.storm.utils;

import com.google.protobuf.ServiceException;
import org.apache.crunch.*;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.io.hbase.HBaseTarget;
import org.apache.crunch.io.hbase.HBaseTypes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class CrunchHBaseDataInserter extends Configured implements Tool, Serializable {
    private static final long serialVersionUID = -3864240977498777497L;

    private final static Logger logger = LoggerFactory.getLogger(CrunchHBaseDataInserter.class);

    public static void main(String[] args) throws Exception {
        final int res = ToolRunner.run(new Configuration(), new CrunchHBaseDataInserter(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] strings) throws Exception {
        createTable();
        final Configuration config = getConf();
        final Pipeline pipeline = new MRPipeline(CrunchHBaseDataInserter.class, "PipelineWithHBasePut", config);

//        PCollection<String> lines = pipeline.readTextFile(Constants.HDFS_INPUT_PATH + "/2004_2015.csv");
        PCollection<String> lines = pipeline.readTextFile("hdfs://localhost:9000/input" + "/2004_2015.csv");

        PCollection<Put> resultPut = CrunchUtils.returnRows(lines);
        pipeline.write(resultPut, new HBaseTarget(Constants.TIME_SORTED_STOCK_TABLE_NAME));
        PipelineResult result = pipeline.done();
        return result.succeeded() ? 0 : 1;
    }

    private static void createTable() throws Exception {
        try {
            Configuration configuration = HBaseConfiguration.create();
            HBaseAdmin.checkHBaseAvailable(configuration);
            Connection connection = ConnectionFactory.createConnection(configuration);

            // Instantiating HBaseAdmin class
            Admin admin = connection.getAdmin();

            // Instantiating table descriptor class
            HTableDescriptor stockTableDesc = new HTableDescriptor(TableName.valueOf(Constants.TIME_SORTED_STOCK_TABLE_NAME));

            // Adding column families to table descriptor
            HColumnDescriptor stock_0415 = new HColumnDescriptor(Constants.TIME_SORTED_STOCK_TABLE_CF);
            stockTableDesc.addFamily(stock_0415);

            // Execute the table through admin
            if (!admin.tableExists(stockTableDesc.getTableName())) {
                admin.createTable(stockTableDesc);
                System.out.println("Time sorted stock table created !!!");
            }

            // Load hbase-site.xml
            HBaseConfiguration.addHbaseResources(configuration);
        } catch (ServiceException e) {
            logger.error("Error occurred while creating HBase tables", e);
            throw new Exception("Error occurred while creating HBase tables", e);
        }
    }

    static class CrunchUtils {
        private final static Logger logger = LoggerFactory.getLogger(CrunchUtils.class);

        /**
         * This method return collection of <Put> objects which can be inserted to HBase
         * @param lines lines from the CSV file
         * @return collection of <Put>
         */
        public static PCollection<Put> returnRows(PCollection<String> lines) {
            // This will work fine because the DoFn is defined inside of a static method.
            return lines.parallelDo(new DoFn<String, Put>() {
                @Override
                public void process(String line, Emitter<Put> emitter) {
                    String id, date, rowKey;
                    String[] fields = line.split(",");
                    if (fields.length > 0 && fields[0] != null && !fields[0].equals("")) {
                        id = fields[0];
                        if (fields.length > 1 && fields[1] != null && !fields[1].equals("")) {
                            date = fields[1];
                            rowKey = date + "_" + id;

                            Put row = new Put(rowKey.getBytes());
                            row.addColumn(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES,
                                    Constants.ID_COLUMN_BYTES, Bytes.toBytes(id));
                            row.addColumn(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES,
                                    Constants.DATE_COLUMN_BYTES, Bytes.toBytes(date));

                            if (fields.length > 2 && fields[2] != null && !fields[2].equals("")) {
                                row.addColumn(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES,
                                        Constants.SYMBOL_COLUMN_BYTES, Bytes.toBytes(fields[2]));
                            }

                            if (fields.length > 5 && fields[5] != null && !fields[5].equals("")) {
                                row.addColumn(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES,
                                        Constants.PRICE_COLUMN_BYTES, Bytes.toBytes(fields[5]));
                            }

                            if (fields.length > 6 && fields[6] != null && !fields[6].equals("")) {
                                row.addColumn(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES,
                                        Constants.CAP_COLUMN_BYTES, Bytes.toBytes(fields[6]));
                            }

                            emitter.emit(row);
                        }
                    }
                }
            }, HBaseTypes.puts());
        }
    }
}