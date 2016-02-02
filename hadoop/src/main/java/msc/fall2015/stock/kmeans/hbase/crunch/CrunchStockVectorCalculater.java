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

package msc.fall2015.stock.kmeans.hbase.crunch;

import msc.fall2015.stock.kmeans.hbase.utils.CleanMetric;
import msc.fall2015.stock.kmeans.hbase.utils.TableUtils;
import msc.fall2015.stock.kmeans.hbase.utils.VectorPoint;
import msc.fall2015.stock.kmeans.utils.Constants;
import org.apache.crunch.*;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.io.hbase.HBaseSourceTarget;
import org.apache.crunch.types.writable.Writables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

public class CrunchStockVectorCalculater extends Configured implements Tool, Serializable {
    private static final Logger log = LoggerFactory.getLogger(CrunchStockVectorCalculater.class);
    private static String startDate;
    private static String endDate;
    private static int mode;

    public static void main(final String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("mapreduce.output.textoutputformat.separator", ",");
        final int res = ToolRunner.run(conf, new CrunchStockVectorCalculater(), args);
        System.exit(res);
    }

    @Override
    public int run(final String[] args) throws Exception {
        try {
            Configuration conf = getConf();
            startDate = args[1];
            endDate = args[2];
            mode = Integer.valueOf(args[3]);
            System.out.println("Start Date : " + startDate);
            System.out.println("End Date : " + endDate);
            if (startDate == null || startDate.isEmpty()) {
                // set 1st starting date
                startDate = "20040102";
            }
            if (endDate == null || endDate.isEmpty()) {
                endDate = "20141231";
            }
            if (mode == 0){
                mode = 5;
            }
            Configuration hbaseConfig = HBaseConfiguration.create();
            TreeMap<String, List<Date>> genDates = TableUtils.genDates(TableUtils.getDate(startDate), TableUtils.getDate(endDate), mode);
            PipelineResult result = null;
            hbaseConfig.set("mapreduce.output.textoutputformat.separator", ",");
            Pipeline pipeline = new MRPipeline(CrunchStockVectorCalculater.class, hbaseConfig);
            for (String id : genDates.keySet()){
                Scan scan = new Scan();
                scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
                scan.setCacheBlocks(false);  // don't set to true for MR jobs
                List<Date> dates = genDates.get(id);
                String start  = TableUtils.convertDateToString(dates.get(0));
                String end  = TableUtils.convertDateToString(dates.get(1));
                List<String> suitableDateList = TableUtils.getDates(start, end);
                hbaseConfig.set(Constants.Job.NO_OF_DAYS, String.valueOf(suitableDateList.size()));
                getConf().addResource(hbaseConfig);

                for (String date : suitableDateList){
                    scan.addColumn(Constants.STOCK_TABLE_CF_BYTES, date.getBytes());
                }
                HBaseSourceTarget source = new HBaseSourceTarget(Constants.STOCK_TABLE_NAME, scan);
                // Our source, in a format which can be use by crunch
                PTable<ImmutableBytesWritable, Result> rawText = pipeline.read(source);
                PTable<String, String> stringStringPTable = extractText(rawText);
                pipeline.writeTextFile(stringStringPTable, Constants.HDFS_OUTPUT_PATH + id);
            }
            result = pipeline.done();
            return result.succeeded() ? 0 : 1;
        } catch (ParseException e) {
            log.error("Error while parsing date", e);
            throw new RuntimeException("Error while parsing date", e);
        }
    }

    public PTable<String, String> extractText(final PTable<ImmutableBytesWritable, Result> tableContent) {
        return tableContent.parallelDo("Read data", new DoFn<Pair<ImmutableBytesWritable, Result>, Pair<String, String>>() {
            transient VectorPoint vectorPoint;
            int noOfDays;

            @Override
            public void configure(Configuration conf) {
                super.configure(conf);
                noOfDays = Integer.valueOf(conf.get(Constants.Job.NO_OF_DAYS));
            }

            @Override
            public void process(Pair<ImmutableBytesWritable, Result> row, Emitter<Pair<String, String>> emitter) {
                NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = row.second().getMap();
                // go through the column family
                for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> columnFamilyMap : map.entrySet()) {
                    // go through the column
                    double totalCap = 0;
                    String rowKey = Bytes.toString(row.second().getRow());
                    String[] idKey = rowKey.split("_");
                    int id = Integer.valueOf(idKey[0]);
                    String symbol = idKey[1];
                    int index = 0;
                    vectorPoint = new VectorPoint(id, symbol, noOfDays, true);
                    for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entryVersion : columnFamilyMap.getValue().entrySet()) {
                        for (Map.Entry<Long, byte[]> entry : entryVersion.getValue().entrySet()) {
                            String column = Bytes.toString(entryVersion.getKey());
                            byte[] val = entry.getValue();
                            String valOfColumn = new String(val);
                            System.out.println("RowKey : " + rowKey + " Column Key : " + column + " Column Val : " + valOfColumn);
                            if (!valOfColumn.isEmpty()) {
                                String[] priceAndCap = valOfColumn.split("_");
                                if (priceAndCap.length > 1) {
                                    String pr = priceAndCap[0];
                                    String cap = priceAndCap[1];
                                    if (pr != null && !pr.equals("null")){
                                        double price = Double.valueOf(pr);
                                        vectorPoint.add(price, index);
                                        index++;
                                    }
                                    if (cap != null && !cap.equals("null")){
                                        totalCap += Double.valueOf(cap);
                                    }
                                }
                            }
                        }
                    }
                    vectorPoint.setTotalCap(totalCap);
                    String serialize = null;
                    if(vectorPoint.cleanVector(new CleanMetric())){
                        serialize = vectorPoint.serialize();
                        System.out.println(serialize);
                    }
                    if (serialize != null){
                        emitter.emit(new Pair<String, String>(String.valueOf(id),serialize));
                    }
                }
            }
        }, Writables.tableOf(Writables.strings(), Writables.strings()));
    }

}
