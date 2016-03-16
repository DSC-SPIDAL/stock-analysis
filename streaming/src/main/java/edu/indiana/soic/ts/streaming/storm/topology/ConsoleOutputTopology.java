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
package edu.indiana.soic.ts.streaming.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import edu.indiana.soic.ts.streaming.storm.bolt.ConsoleOutputBolt;
import edu.indiana.soic.ts.streaming.storm.spout.HBaseSpout;
import edu.indiana.soic.ts.streaming.storm.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleOutputTopology {
    private final static Logger logger = LoggerFactory.getLogger(ConsoleOutputTopology.class);

    /**
     * HBase Data Output Topology
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        int topoWorkers = Constants.STORM_TOPOLOGY_WORKERS;

        int spoutTasks = Constants.STORM_SPOUT_TASKS;
        builder.setSpout("hbaseSpout", new HBaseSpout("20040101","20040201"), spoutTasks);

        int boltTasks = Constants.STORM_BOLT_TASKS;
        builder.setBolt("outputBolt", new ConsoleOutputBolt(), boltTasks)
                .shuffleGrouping("hbaseSpout");

        Config conf = new Config();

        if (args != null && args.length > 0) { // run on storm cluster
            conf.setNumAckers(1);
            conf.setNumWorkers(topoWorkers);
            StormSubmitter.submitTopology(args[0], conf,
                    builder.createTopology());
        } else { // run on local cluster
            conf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", conf, builder.createTopology());
            Utils.sleep(100000);
            cluster.killTopology("test");
            cluster.shutdown();
        }
    }
}