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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * This is the HBase data generator class.
 * An HBaseStream object will continuously scan a specified partition by a sharding key.
 *
 */
public class HBaseStream {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(HBaseStream.class);

    private transient Table table;

    /**
     * Constructor Function
     */
    public HBaseStream(String tableName) throws IOException, ServiceException {
        Configuration conf = HBaseConfiguration.create();
        HBaseAdmin.checkHBaseAvailable(conf);
        Connection connection = ConnectionFactory.createConnection(conf);
        table = connection.getTable(TableName.valueOf(tableName));
    }

    /**
     * scan HBase table by a specified startDate.
     * @return ResultScanner
     */
    public ResultScanner scanTable(String cf, String startKey, String endKey) {
        ResultScanner rs;
        Scan scan = new Scan();
        scan.addFamily(cf.getBytes());

        // Set the range of scanning table
        scan.setStartRow(startKey.getBytes());
        scan.setStopRow(endKey.getBytes());

        // get result of this scanning process
        try {
            rs = table.getScanner(scan);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            rs = null;
        }
        return rs;
    }
}