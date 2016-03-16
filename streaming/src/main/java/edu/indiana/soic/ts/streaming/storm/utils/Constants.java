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

public class Constants {

    public static final String ID_COLUMN = "id";
    public static final String DATE_COLUMN = "date";
    public static final String SYMBOL_COLUMN = "symbol";
    public static final String PRICE_COLUMN = "price";
    public static final String CAP_COLUMN = "cap";
    public static final String TIME_SORTED_STOCK_TABLE_NAME = "Time_Sorted_Stock2004_2015Table";
    public static final String TIME_SORTED_STOCK_TABLE_CF = "Time_Sorted_Stock2004_2015CF";

    public static final byte[] ID_COLUMN_BYTES = ID_COLUMN.getBytes();
    public static final byte[] DATE_COLUMN_BYTES = DATE_COLUMN.getBytes();
    public static final byte[] SYMBOL_COLUMN_BYTES = SYMBOL_COLUMN.getBytes();
    public static final byte[] PRICE_COLUMN_BYTES = PRICE_COLUMN.getBytes();
    public static final byte[] CAP_COLUMN_BYTES = CAP_COLUMN.getBytes();
    public static final byte[] TIME_SORTED_STOCK_TABLE_CF_BYTES = TIME_SORTED_STOCK_TABLE_CF.getBytes();

    public static final String HDFS_INPUT_PATH = "hdfs://156.56.179.122:9000/input";
    public static final String HDFS_OUTPUT_PATH = "hdfs://156.56.179.122:9000/output/";
    public static final String HDFS_HOME_PATH = "hdfs://156.56.179.122:9000/";

    public static final int STORM_TOPOLOGY_WORKERS = 1;
    public static final int STORM_SPOUT_TASKS = 1;
    public static final int STORM_BOLT_TASKS = 1;
}
