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

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;

public class StockDataReaderMapper extends TableMapper<Text, Text> {

    public void map(ImmutableBytesWritable row, Result value, Context context) throws InterruptedException, IOException {
        SimpleRegression regression;
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> columnFamilyMap : value.getMap().entrySet()) {
            regression = new SimpleRegression();
            int count = 1;
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entryVersion : columnFamilyMap.getValue().entrySet()) {
                for (Map.Entry<Long, byte[]> entry : entryVersion.getValue().entrySet()) {
                    String rowKey = Bytes.toString(value.getRow());
                    String column = Bytes.toString(entryVersion.getKey());
                    byte[] val = entry.getValue();
                    String valOfColumn = new String(val);
                    System.out.println("RowKey : " + rowKey + " Column Key : " + column + " Column Val : " + valOfColumn);
                    if (!valOfColumn.isEmpty()) {
                        String[] priceAndCap = valOfColumn.split("_");
                        if (priceAndCap.length > 1) {
                            String pr = priceAndCap[0];
                            if (pr != null && !pr.equals("null")){
                                double price = Double.valueOf(pr);
                                if (price < 0) {
                                    price = price - 2 * price;
                                }
                                System.out.println("Price : " + price + " count : " + count);
                                regression.addData(count, price);
                            }
                        }
                    }
                }
                count++;
            }
            // displays intercept of regression line
            System.out.println("Intercept : " + regression.getIntercept());

            // displays slope of regression line
            System.out.println("Slope : " + regression.getSlope());

            // displays slope standard error
            System.out.println("Slope STD Error : " + regression.getSlopeStdErr());
        }
    }

}
