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

import edu.indiana.soic.ts.utils.CleanMetric;
import edu.indiana.soic.ts.utils.Record;
import edu.indiana.soic.ts.utils.Utils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;


public class InsertAllMapper extends Mapper<LongWritable, Text, Text, Text   > {
    private static final Logger log = LoggerFactory.getLogger(BulkDataLoader.class);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        CleanMetric cleanMetric = new CleanMetric();

        try {
            Record record = Utils.parseRecordLine(value.toString(), cleanMetric, false);
            if (record != null && record.getSymbol() != -1 && record.getSymbolString() != null && !record.getSymbolString().trim().equals("")){
                String rowKey = record.getSymbol() + "_" + record.getSymbolString();
                String rowVal = record.getDateString() + "_" + record.getPrice() + "_" + record.getVolume() + "_" + record.getFactorToAdjPrice() + "_" + record.getFactorToAdjVolume();
                context.write(new Text(rowKey), new Text(rowVal));
            }
        } catch (ParseException e) {
            String msg = "Failed to read the record";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
