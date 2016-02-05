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

package msc.fall2015.stock.kmeans.hbase.mapreduce;

import msc.fall2015.stock.kmeans.hbase.utils.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StockInsertDateReducer extends TableReducer<Text, Text, ImmutableBytesWritable> {
    private static final Logger log = LoggerFactory.getLogger(StockInsertDateReducer.class);
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration c = context.getConfiguration();
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        log.info("Reducer : key : " + key.toString()) ;
        byte[] rowKey = Bytes.toBytes(key.toString());
        Put row = new Put(rowKey);
        for (Text val : values) {
            if (val != null){
                String value = val.toString();
                row.add(Constants.STOCK_DATES_CF_BYTES, Bytes.toBytes(Constants.DATE_COLUMN), Bytes.toBytes(value));
            }
        }
        context.write(new ImmutableBytesWritable(rowKey), row);
    }
}
