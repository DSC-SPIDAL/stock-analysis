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
package edu.indiana.soic.ts.streaming.dataflow.utils;

import com.google.protobuf.ServiceException;
import edu.indiana.soic.ts.streaming.storm.utils.Constants;
import edu.indiana.soic.ts.streaming.storm.utils.HBaseStream;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HelperMethods {
    private final static Logger logger = LoggerFactory.getLogger(HelperMethods.class);

    public static void main(String[] args) throws IOException, ServiceException {
        HBaseStream hbaseStream = new HBaseStream(Constants.TIME_SORTED_STOCK_TABLE_NAME);
        ResultScanner rs = hbaseStream.scanTable(Constants.TIME_SORTED_STOCK_TABLE_CF,"20040101", "20040401");
        Result rr = new Result();
        BufferedWriter writer = new BufferedWriter(new FileWriter("./inputFiles/2004.csv"));
        if (rs != null) {
            byte[] id, date, symbol, price, cap;
            while (rr != null) {
                try {
                    rr = rs.next();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                if (rr != null && !rr.isEmpty()) {
                    id = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.ID_COLUMN_BYTES);
                    date = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.DATE_COLUMN_BYTES);
                    symbol = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.SYMBOL_COLUMN_BYTES);
                    price = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.PRICE_COLUMN_BYTES);
                    cap = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.CAP_COLUMN_BYTES);
                    if(id != null && date != null && symbol != null && price != null && cap != null)
                        writer.write(new String(id) + "," + new String(date) + "," + new String(symbol) + ",,,"
                            + new String(price) + "," + new String(cap) + "\n");
                }
            }
        }
        rs.close();
        writer.close();

//        HBaseStream hbaseStream = new HBaseStream(Constants.TIME_SORTED_STOCK_TABLE_NAME);
//        ResultScanner rs = hbaseStream.scanTable(Constants.TIME_SORTED_STOCK_TABLE_CF,"20040101", "20170101");
//        Result rr = new Result();
//        HashSet<String> companies = new HashSet<>(10000);
//        if (rs != null) {
//            byte[] symbol;
//            while (rr != null) {
//                try {
//                    rr = rs.next();
//                } catch (IOException e) {
//                    logger.error(e.getMessage(), e);
//                }
//                if (rr != null && !rr.isEmpty()) {
//                    symbol = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.SYMBOL_COLUMN_BYTES);
//                    if(symbol != null)
//                        companies.add(new String(symbol).trim());
//                }
//            }
//        }
//        rs.close();
//        System.out.println("unique companies : " + companies.size());
//        BufferedWriter writer = new BufferedWriter(new FileWriter("streaming/src/main/resources/symbol_encoding.csv"));
//        Iterator<String> iterator = companies.iterator();
//        int i = 0;
//        while(iterator.hasNext()){
//            writer.write(iterator.next() + "," + i + "\n");
//            i++;
//        }
//        writer.close();

//        BufferedReader reader = new BufferedReader(new FileReader("./inputFiles/2004.csv"));
//        String line = reader.readLine();
//        int lineCount  = 0;
//        HashSet<String> companies = new HashSet<>(1000);
//        while(line != null){
//            lineCount++;
//            companies.add(line.split(",")[2].trim());
//
//            line =  reader.readLine();
//        }
//
//        System.out.println("line count : " + lineCount);
//        System.out.println("unique companies : " + companies.size());
    }
}