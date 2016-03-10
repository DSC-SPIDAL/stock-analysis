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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HBaseDumper {
    private final static Logger logger = LoggerFactory.getLogger(HBaseDumper.class);

    private String tableName;

    private byte[] columnFamily;

    //List of columns in the column family. When adding records the values should be send in the same order
    private byte[][] columns;

    private HTableInterface table;

    private HConnection connection;

    private static boolean AUTO_FLUSH = false;

    private static boolean CLEAR_BUFFER_ON_FAIL = false;

    /**
     * Constructor Function
     */
    public HBaseDumper(String tableName, String columnFamily, String[] columns) throws Exception {
        this.tableName = tableName;
        this.columnFamily = Bytes.toBytes(columnFamily);
        this.columns = new byte[columns.length][];
        for (int i = 0; i < columns.length; i++) {
            this.columns[i] = Bytes.toBytes(columns[i]);
        }
        initHBaseTable();
        startDaemon();
    }

    /**
     * Initialize HTable object for writing data to HBase
     *
     * @return
     */
    private boolean initHBaseTable() throws Exception {
        boolean b = true;
        try {
            Configuration config = HBaseConfiguration.create();
            connection = HConnectionManager.createConnection(config);
            table = connection.getTable(tableName);
            table.setAutoFlush(AUTO_FLUSH, CLEAR_BUFFER_ON_FAIL);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            b = false;
        }
        return b;
    }

    /**
     * Writes the values to the table
     * @param rowKey
     * @param valuesArray
     * @throws IOException
     */
    public void writeToTable(byte[] rowKey, byte[][] valuesArray) throws IOException {
        // put the data into hbase
        Put p = new Put(rowKey);
        for(int i=0;i<columns.length;i++){
            p.addColumn(columnFamily,columns[i],valuesArray[i]);
        }
        table.put(p);
        logger.info(new String(rowKey));
    }

    /**
     * To flush all the records
     * @throws IOException
     */
    public void flushTable() throws IOException {
        table.flushCommits();
        try {
            table.close();
            connection.close();
            logger.info("hbase closed");
        } catch (Exception e) {
            logger.error("cleanup error", e);
        }
    }

    /**
     * Startup daemon thread to flush data to HBase every second
     */
    private void startDaemon() {
        Thread th = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        table.flushCommits();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        };
        th.setDaemon(true);
        th.start();
    }
}