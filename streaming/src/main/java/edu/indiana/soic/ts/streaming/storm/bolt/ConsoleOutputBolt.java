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
package edu.indiana.soic.ts.streaming.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import edu.indiana.soic.ts.streaming.storm.utils.StreamData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ConsoleOutputBolt extends BaseRichBolt {

    private static final long serialVersionUID = -8622707506877979035L;
    private final static Logger logger = LoggerFactory.getLogger(ConsoleOutputBolt.class);

    private OutputCollector collector;
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map stormConf,
                        TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        List<Object> fields = input.getValues();
        logger.info("recv tuple: " + input + ", field num: " + fields.size());
        String rowKey = new String(((StreamData)fields.get(0)).getData());
        logger.info("tuple field 0: " + rowKey);
        for (int i = 1; i < fields.size(); i++) {
            logger.info("tuple field " + i + ": " + new String(((StreamData)fields.get(i)).getData()));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

}