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

import com.google.protobuf.ServiceException;
import msc.fall2015.stock.kmeans.utils.Constants;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.apache.crunch.*;
import org.apache.crunch.fn.Aggregators;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.io.hbase.HBaseSourceTarget;
import org.apache.crunch.io.hbase.HBaseTarget;
import org.apache.crunch.io.hbase.HBaseTypes;
import org.apache.crunch.types.writable.Writables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CrunchStockDataReader extends Configured implements Tool, Serializable {
    private static final Logger log = LoggerFactory.getLogger(CrunchStockDataReader.class);
    private static String startDate;
    private static String endDate;

    public static void main(final String[] args) throws Exception {
        final int res = ToolRunner.run(new Configuration(), new CrunchStockDataReader(), args);
        System.exit(res);
    }

    @Override
    public int run(final String[] args) throws Exception {
        try {
            startDate = args[1];
            endDate = args[2];
            System.out.println("Start Date : " + startDate);
            System.out.println("End Date : " + endDate);
            if (startDate == null || startDate.isEmpty()) {
                // set 1st starting date
                startDate = "20040102";
            }
            if (endDate == null || endDate.isEmpty()) {
                endDate = "20141231";
            }
            Configuration config = HBaseConfiguration.create();
            Pipeline pipeline = new MRPipeline(CrunchStockDataReader.class, config);

            Scan scan = new Scan();
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
            scan.setCacheBlocks(false);  // don't set to true for MR jobs
            List<String> suitableDates = getDates();
            if (suitableDates != null && !suitableDates.isEmpty()){
                for (String date : suitableDates){
                    scan.addColumn(Constants.STOCK_TABLE_CF_BYTES, date.getBytes());
                }
            }
            createTable();
            // Our hbase source
            HBaseSourceTarget source = new HBaseSourceTarget(Constants.STOCK_TABLE_NAME, scan);

            // Our source, in a format which can be use by crunch
            PTable<ImmutableBytesWritable, Result> rawText = pipeline.read(source);
            PTable<String, String> stringStringPTable = extractText(rawText);
            PTable<String, String> result1 = stringStringPTable.groupByKey()
                    .combineValues(Aggregators.STRING_CONCAT(" ", true));
            // We create the collection of puts from the concatenated datas
            PCollection<Put> resultPut = createPut(result1);

            // We write the puts in hbase, in the target table
            pipeline.write(resultPut, new HBaseTarget(Constants.REGRESSION_TABLE_NAME));

            PipelineResult result = pipeline.done();
            return result.succeeded() ? 0 : 1;
        } catch (ParseException e) {
            log.error("Error while parsing date", e);
            throw new RuntimeException("Error while parsing date", e);
        }
    }

    private static void createTable() throws Exception {
        try {
            Configuration configuration = HBaseConfiguration.create();
            HBaseAdmin.checkHBaseAvailable(configuration);
            Connection connection = ConnectionFactory.createConnection(configuration);

            // Instantiating HbaseAdmin class
            Admin admin = connection.getAdmin();

            // Instantiating table descriptor class
            HTableDescriptor stockTableDesc = new HTableDescriptor(TableName.valueOf(Constants.REGRESSION_TABLE_NAME));

            // Adding column families to table descriptor
            HColumnDescriptor stock_0414 = new HColumnDescriptor(Constants.REGRESSION_TABLE_CF);
            stockTableDesc.addFamily(stock_0414);

            // Execute the table through admin
            if (!admin.tableExists(stockTableDesc.getTableName())) {
                admin.createTable(stockTableDesc);
                System.out.println("Stock table created !!!");
            }

            // Load hbase-site.xml
            HBaseConfiguration.addHbaseResources(configuration);
        } catch (ServiceException e) {
            log.error("Error occurred while creating HBase tables", e);
            throw new Exception("Error occurred while creating HBase tables", e);
        }
    }

    public PCollection<Put> createPut(PTable<String, String> extractedText) {
        return extractedText.parallelDo("Convert to puts", new DoFn<Pair<String, String>, Put>() {
            @Override
            public void process(Pair<String, String> input, Emitter<Put> emitter) {
                Put put = new Put(Bytes.toBytes(input.first()));
                put.add(Constants.REGRESSION_TABLE_CF.getBytes(), Constants.REGRESSION_TABLE_QUALIFIER.getBytes(), Bytes.toBytes(input.second()));
                emitter.emit(put);
            }
        }, HBaseTypes.puts());
    }

    private void getRows(Scan scan, List<String> suitableDates) throws ServiceException, IOException {
        Configuration configuration =  HBaseConfiguration.create();
        HBaseConfiguration.addHbaseResources(configuration);
        HBaseAdmin.checkHBaseAvailable(configuration);
        Connection connection = ConnectionFactory.createConnection(configuration);
        // Instantiating HbaseAdmin class
        Admin admin = connection.getAdmin();

        HTableDescriptor[] tableDescriptor = admin.listTables();

        for (HTableDescriptor aTableDescriptor : tableDescriptor) {
            if (aTableDescriptor.getTableName().getNameAsString().equals(Constants.STOCK_TABLE_NAME)) {
                Table table = connection.getTable(aTableDescriptor.getTableName());
                ResultScanner scanner = table.getScanner(scan);
                printRows(scanner, suitableDates);
            }
        }
    }

    public static void printRows(ResultScanner resultScanner, List<String> allDates) {
        for (Result aResultScanner : resultScanner) {
            printRow(aResultScanner, allDates);
        }
    }
    public static void printRow(Result result, List<String> allDates) {
        try {
            String rowName = Bytes.toString(result.getRow());
            //if you want to get the entire row
            for (String date : allDates){
                byte[] value = result.getValue(Constants.STOCK_TABLE_CF_BYTES, date.getBytes());
                if (value != null){
                    System.out.println("Row Name : " + rowName + " : values : " + new String(value) );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getDates() throws ParseException {
        List<String> allDates = new ArrayList<String>();
        Date startDate = getDate(CrunchStockDataReader.startDate);
        Date endDate = getDate(CrunchStockDataReader.endDate);
        ResultScanner scannerForDateTable = getScannerForDateTable();
        for (Result aResultScanner : scannerForDateTable) {
            String date = new String(aResultScanner.getRow());
            Date rowDate = getDate(date);
            if (startDate.compareTo(rowDate) * rowDate.compareTo(endDate) > 0){
                allDates.add(date);
            }
        }
        return allDates;

    }

    public static Date getDate (String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.parse(date);
    }

    private static ResultScanner getScannerForDateTable() {
        try {
            Configuration configuration = HBaseConfiguration.create();
            HBaseConfiguration.addHbaseResources(configuration);
            HBaseAdmin.checkHBaseAvailable(configuration);
            Connection connection = ConnectionFactory.createConnection(configuration);
            // Instantiating HbaseAdmin class
            Admin admin = connection.getAdmin();

            HTableDescriptor[] tableDescriptor = admin.listTables();
            // printing all the table names.
            for (HTableDescriptor aTableDescriptor : tableDescriptor) {
                if (aTableDescriptor.getTableName().getNameAsString().equals(Constants.STOCK_DATES_TABLE)) {
                    Table table = connection.getTable(aTableDescriptor.getTableName());
                    Scan scan = new Scan();
                    scan.setCaching(20);
                    scan.addFamily(Constants.STOCK_DATES_CF_BYTES);
                    return table.getScanner(scan);
                }
            }
        } catch (ServiceException e) {
            log.error("Error while reading Stock Dates table", e);
        } catch (MasterNotRunningException e) {
            log.error("Error while reading Stock Dates table", e);
        } catch (ZooKeeperConnectionException e) {
            log.error("Error while reading Stock Dates table", e);
        } catch (IOException e) {
            log.error("Error while reading Stock Dates table", e);
        }
        return null;
    }

    public PTable<String, String> extractText(PTable<ImmutableBytesWritable, Result> tableContent) {
        return tableContent.parallelDo("Read data", new DoFn<Pair<ImmutableBytesWritable, Result>, Pair<String, String>>() {
            @Override
            public void process(Pair<ImmutableBytesWritable, Result> row, Emitter<Pair<String, String>> emitter) {
                SimpleRegression regression;
                NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = row.second().getMap();
                System.out.println(map.size());
                for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> columnFamilyMap : map.entrySet()) {
                    regression = new SimpleRegression();
                    int count = 1;
                    for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entryVersion : columnFamilyMap.getValue().entrySet()) {
                        for (Map.Entry<Long, byte[]> entry : entryVersion.getValue().entrySet()) {
                            String rowKey = Bytes.toString(row.second().getRow());
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
                    emitter.emit(new Pair<String, String>(String.valueOf(regression.getIntercept()), String.valueOf(regression.getSlope())));
                }
            }
        }, Writables.tableOf(Writables.strings(), Writables.strings()));
    }

}
