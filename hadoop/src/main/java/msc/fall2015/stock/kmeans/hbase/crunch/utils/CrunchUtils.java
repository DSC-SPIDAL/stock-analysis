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

package msc.fall2015.stock.kmeans.hbase.crunch.utils;

import msc.fall2015.stock.kmeans.utils.Constants;
import org.apache.crunch.DoFn;
import org.apache.crunch.Emitter;
import org.apache.crunch.PCollection;
import org.apache.crunch.io.hbase.HBaseTypes;
import org.apache.crunch.types.writable.Writables;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class CrunchUtils {

    public static PCollection<String> splitLines(PCollection<String> lines) {
        return lines.parallelDo(new DoFn<String, String>() {
            @Override
            public void process(String line, Emitter<String> emitter) {
                for (String word : line.split(",")) {
                    emitter.emit(word);
                }
            }
        }, Writables.strings());
    }

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
                String id = null, symbol = null, date = null, cap = null, price = null, rowKey, rowVal;
                String[] fields = line.split(",");
                if (fields.length > 0 && fields[0] != null && !fields[0].equals("")) {
                    id = fields[0];
                }

                if (fields.length > 1 && fields[1] != null && !fields[1].equals("")) {
                    date = fields[1];
                }

                if (fields.length > 2 && fields[2] != null && !fields[2].equals("")) {
                    symbol = fields[2];
                }

                if (fields.length > 3 && fields[3] != null && !fields[3].equals("")) {
                    price = fields[3];
                }

                if (fields.length > 4 && fields[4] != null && !fields[4].equals("")) {
                    cap = fields[4];
                }
                if (id != null && symbol != null) {
                    rowKey = id + "_" + symbol;
                    rowVal = date + "_" + price + "_" + cap;
                    Put row = new Put(rowKey.getBytes());
                    String[] split = rowVal.split("_");
                    if (split.length > 2){
                        row.add(Constants.STOCK_TABLE_CF_BYTES, Bytes.toBytes(split[0]), Bytes.toBytes(split[1] + "_" + split[2]));
                    }else if (split.length > 1 && split.length < 2){
                        row.add(Constants.STOCK_TABLE_CF_BYTES, Bytes.toBytes(split[0]), Bytes.toBytes(split[1] + "_NAN" ));
                    }else if (split.length > 0 && split.length <1){
                        row.add(Constants.STOCK_TABLE_CF_BYTES, Bytes.toBytes(split[0]), Bytes.toBytes("NAN_NAN" ));
                    }
                    emitter.emit(row);
                }
            }
        }, HBaseTypes.puts());
    }

    /**
     * This method return collection of <Put> objects which can be inserted to HBase
     * @param lines lines from the CSV file
     * @return collection of <Put>
     */
    public static PCollection<Put> returnDates(PCollection<String> lines) {
        // This will work fine because the DoFn is defined inside of a static method.
        return lines.parallelDo(new DoFn<String, Put>() {
            @Override
            public void process(String line, Emitter<Put> emitter) {
                String date = null, rowKey = null, rowVal = null;
                String[] fields = line.split(",");

                if (fields.length > 1 && fields[1] != null && !fields[1].equals("")) {
                    date = fields[1];
                }

                if (date != null){
                    rowKey = date;
                    rowVal = date;
                    Put row = new Put(rowKey.getBytes());
                    row.add(Constants.STOCK_DATES_CF_BYTES, Bytes.toBytes(rowVal), Bytes.toBytes(rowVal));
                    emitter.emit(row);
                }
            }
        }, HBaseTypes.puts());
    }
}
