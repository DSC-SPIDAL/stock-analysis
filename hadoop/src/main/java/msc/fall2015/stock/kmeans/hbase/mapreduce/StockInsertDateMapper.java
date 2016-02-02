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

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class StockInsertDateMapper extends
        Mapper<LongWritable, Text, Text, Text   > {

    private static final Logger log = LoggerFactory.getLogger(StockBulkDataLoader.class);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    /** {@inheritDoc} */
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String[] fields = null;
        String date= null,rowKey = null, rowVal = null;
        try {
            fields = value.toString().split(",");
        } catch (Exception ex) {
            context.getCounter("HBaseKVMapper", "PARSE_ERRORS").increment(1);
            return;
        }

        if (fields.length > 1 && fields[1] != null && !fields[1].equals("")) {
            date = fields[1];
        }

        if (date != null){
            rowKey = date;
            rowVal = date;
            context.write(new Text(rowKey), new Text(rowVal));
        }

        context.getCounter("HBaseKVMapper", "NUM_MSGS").increment(1);
    }
}
