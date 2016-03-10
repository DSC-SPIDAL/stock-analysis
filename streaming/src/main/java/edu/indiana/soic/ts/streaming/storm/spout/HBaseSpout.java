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
package edu.indiana.soic.ts.streaming.storm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import edu.indiana.soic.ts.streaming.storm.utils.HBaseStream;
import edu.indiana.soic.ts.streaming.utils.Constants;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class HBaseSpout extends BaseRichSpout{

    private final static Logger logger = LoggerFactory.getLogger(HBaseSpout.class);

    /**
     * queue for tuples
     */
    private LinkedBlockingQueue<Values> queue;

    /**
     * collector for spout
     */
    private SpoutOutputCollector collector;

    /**
     * The start date in yyyymmdd format.
     */
    private String startDate;

    /**
     * The stop date in yyyymmdd format.
     */
    private String endDate;

    /**
     * HBase stream data generator
     */
    private HBaseStream hbaseStream;

    /**
     * Results Scanner
     */
    private ResultScanner rs;

    /**
     * Whether scanning HBase is completed
     */
    private boolean scanCompleted = false;

    public HBaseSpout(String startDate, String endDate){
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void open(@SuppressWarnings("rawtypes") Map conf,
                     TopologyContext context, SpoutOutputCollector collector) {

        this.collector = collector;
        int queueSize = Constants.STORM_SPOUT_DEFAULT_QUEUE_SIZE;
        this.queue = new LinkedBlockingQueue<>(queueSize);

        try {
            Thread scanThread = new Thread(new HBaseStreamScanner());
            scanThread.setDaemon(true);
            scanThread.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nextTuple() {
        Values tuple;
        try {
            while ((tuple = queue.take()) != null) {
                collector.emit(tuple, tuple.get(0));
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("rowKey", "id", "date", "symbol", "price", "cap"));
    }

    /**
     * Put a tuple to queue
     *
     * @param tuple
     * @return
     */
    protected void putTuple(Values tuple) {
        try {
            queue.put(tuple);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Scan HBase table and send data
     *
     */
    private class HBaseStreamScanner implements Runnable {

        // HBase stream data generator
        private HBaseStream hbaseStream;

        public HBaseStreamScanner() throws Exception {
            this.hbaseStream = new HBaseStream(Constants.TIME_SORTED_STOCK_TABLE_NAME);
        }

        @Override
        public void run() {
            long num = 0L;
            long startTime = System.currentTimeMillis();
            ResultScanner rs = hbaseStream.scanTable(Constants.TIME_SORTED_STOCK_TABLE_CF,
                    startDate, endDate);
            Result rr = new Result();
            if (rs != null) {
                byte[] rowKey = null;
                byte[] id, date, symbol, price, cap;
                while (rr != null) {
                    try {
                        rr = rs.next();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (rr != null && !rr.isEmpty()) {
                        num++;
                        rowKey = rr.getRow();
                        Values values = new Values();
                        values.add(new String(rowKey));
                        id = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.ID_COLUMN_BYTES);
                        values.add(new String(id));
                        date = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.DATE_COLUMN_BYTES);
                        values.add(new String(date));
                        symbol = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.SYMBOL_COLUMN_BYTES);
                        if (symbol != null) {
                            values.add(new String(symbol));
                        } else {
                            values.add("");
                        }
                        price = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.PRICE_COLUMN_BYTES);
                        if (price != null) {
                            values.add(Double.parseDouble(new String(price)));
                        } else {
                            values.add(0.0);
                        }
                        cap = rr.getValue(Constants.TIME_SORTED_STOCK_TABLE_CF_BYTES, Constants.CAP_COLUMN_BYTES);
                        if (cap != null) {
                            values.add(Double.parseDouble(new String(cap)));
                        } else {
                            values.add(0);
                        }
                        putTuple(values);
                    }
                }
                logger.info("total records: " + num + ", total scan time: " + (System.currentTimeMillis() - startTime) + " ms");
                rs.close();
            }
        }
    }
}