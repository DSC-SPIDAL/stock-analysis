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
package edu.indiana.soic.ts.streaming.dataflow;

import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.io.TextIO;
import com.google.cloud.dataflow.sdk.options.Default;
import com.google.cloud.dataflow.sdk.options.Description;
import com.google.cloud.dataflow.sdk.options.PipelineOptions;
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory;
import com.google.cloud.dataflow.sdk.transforms.Combine;
import com.google.cloud.dataflow.sdk.transforms.DoFn;
import com.google.cloud.dataflow.sdk.transforms.GroupByKey;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.values.KV;
import com.google.cloud.dataflow.sdk.values.PCollection;
import edu.indiana.soic.ts.streaming.dataflow.utils.DistanceFunction;
import edu.indiana.soic.ts.streaming.dataflow.utils.VectorPoint;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class StockAnalysisPipeline1 {
    private final static Logger logger = LoggerFactory.getLogger(StockAnalysisPipeline1.class);

    //FIXME this should go as a side input
    private static final int numberOfCompanies = 100;
    private static final int numberOfDays = 250;


    public static interface StockAnalysisPipelineOptions extends PipelineOptions {
        @Description("Path to input file")
        @Default.String("")
        String getInputFilePath();

        void setInputFilePath(String value);

        @Description("Output file path")
        @Default.String("")
        String getOutputFilePath();

        void setOutputFilePath(String value);
    }

    public static void main(String[] args) throws IOException {
//        createTestInputFile();

        StockAnalysisPipelineOptions options = PipelineOptionsFactory.fromArgs(args).as(StockAnalysisPipelineOptions.class);
        Pipeline pipeline = Pipeline.create(options);

        //reading data
        PCollection<KV<String, VectorPoint>> entries = pipeline.apply(TextIO.Read.from("./inputFiles/dataflow_input.csv"))
                .apply(ParDo.of(new DoFn<String, KV<String, VectorPoint>>() {
                    @Override
                    public void processElement(ProcessContext processContext) throws Exception {
                        String[] bits = processContext.element().split(",");
                        String key = bits[0];
                        double[] values = new double[bits.length - 1];
                        for (int i = 1; i < bits.length; i++) {
                            values[i - 1] = Double.parseDouble(bits[i]);
                        }
                        //FIXME we don't have id, symbol, cap etc..
                        VectorPoint vp = new VectorPoint(0, "", values, 0.0);
                        processContext.output(KV.of(key, vp));
                    }
                }));

        //exploding data
        PCollection<KV<String, VectorPoint>> explodedEntries = entries.apply(ParDo.of(new DoFn<KV<String,
                VectorPoint>, KV<String, VectorPoint>>() {
            @Override
            public void processElement(ProcessContext processContext) throws Exception {
                KV<String, VectorPoint> kv = processContext.element();
                String key = kv.getKey();
                VectorPoint value = kv.getValue();
                for (int i = 0; i < numberOfCompanies; i++) {
                    processContext.output(KV.of(key + "_" + i, value));
                    processContext.output(KV.of(i + "_" + key, value));
                }
            }
        }));

        //distance calculation
        PCollection<KV<String, Double>> distanceMatrixEntries = explodedEntries.apply(GroupByKey.create()).apply(
                ParDo.of(new DoFn<KV<String, Iterable<VectorPoint>>, KV<String, Double>>() {
                    @Override
                    public void processElement(ProcessContext processContext) throws Exception {
                        String key = processContext.element().getKey();
                        Iterator<VectorPoint> stockPricesList = processContext.element().getValue().iterator();
                        if (stockPricesList.hasNext()) {
                            VectorPoint vp1 = stockPricesList.next();
                            if (stockPricesList.hasNext()) {
                                VectorPoint vp2 = stockPricesList.next();
                                //FIXME change to proper distance calculation
                                double distance = DistanceFunction.calculateDistance(vp1, vp2);
                                processContext.output(KV.of(key, distance));
                            }
                        }
                    }
                }));

        //imploding row data
        PCollection<KV<String, KV<String, Double>>> implodedRowEntries = distanceMatrixEntries.apply(
                ParDo.of(new DoFn<KV<String, Double>, KV<String, KV<String, Double>>>() {
                    @Override
                    public void processElement(ProcessContext processContext) throws Exception {
                        String key = processContext.element().getKey();
                        processContext.output(KV.of(key.split("_")[0], KV.of(key.split("_")[1], processContext.element().getValue())));
                    }
                }));

        //rows as strings
        PCollection<KV<String, String>> rowStrings = implodedRowEntries.apply(GroupByKey.create()).
                apply(ParDo.of(new DoFn<KV<String, Iterable<KV<String, Double>>>, KV<String, String>>() {
                    @Override
                    public void processElement(ProcessContext processContext) throws Exception {
                        String key = processContext.element().getKey();
                        Iterator<KV<String, Double>> iterator = processContext.element().getValue().iterator();
                        double[] elements = new double[numberOfCompanies];
                        while (iterator.hasNext()) {
                            KV<String, Double> val = iterator.next();
                            elements[Integer.parseInt(val.getKey().trim())] = val.getValue();
                        }
                        String line = StringUtils.join(ArrayUtils.toObject(elements), ',');
                        processContext.output(KV.of(key, line));
                    }
                }));

        //combining rows to a matrix as a single string (this has only one row)
        PCollection<String> distanceMatrixString = rowStrings.apply(
                Combine.globally(new Combine.CombineFn<KV<String, String>, Map<Integer, String>, String>() {

            @Override
            public Map<Integer, String> createAccumulator() {
                return new TreeMap<Integer, String>();
            }

            @Override
            public Map<Integer, String> addInput(Map<Integer, String> stringString, KV<String, String> stringStringKV) {
                stringString.put(Integer.parseInt(stringStringKV.getKey().trim()),stringStringKV.getValue());
                return stringString;
            }

            @Override
            public Map<Integer, String> mergeAccumulators(Iterable<Map<Integer, String>> iterable) {
                Iterator<Map<Integer,String>> iterator = iterable.iterator();
                TreeMap<Integer,String> treeMap = new TreeMap<>();
                while(iterator.hasNext()){
                    treeMap.putAll(iterator.next());
                }
                return treeMap;
            }

            @Override
            public String extractOutput(Map<Integer, String> stringString) {
                String temp = "";
                for(Map.Entry<Integer,String> entry : stringString.entrySet()){
                    temp += entry.getValue() + "\n";
                }
                if(temp.length() > 0)
                    return temp.substring(0, temp.length()-1);
                else
                    return "";
            }
        }));

        distanceMatrixString.apply(TextIO.Write.to("./inputFiles/dataflow_output.csv"));

//        rowStrings.apply(ParDo.of(new DoFn<KV<String, String>, String>() {
//            @Override
//            public void processElement(ProcessContext processContext) throws Exception {
//                processContext.output(processContext.element().getKey() + "," + processContext.element().getValue());
//            }
//        })).apply(TextIO.Write.to("./inputFiles/dataflow_output.csv"));

//        distanceMatrixEntries.apply(ParDo.of(new DoFn<KV<String, Double>, String>() {
//            @Override
//            public void processElement(ProcessContext processContext) throws Exception {
//                KV<String, Double> kv = processContext.element();
//                processContext.output(kv.getKey() + "," + kv.getValue());
//            }
//        })).apply(TextIO.Write.to("./inputFiles/dataflow_output.csv"));


        pipeline.run();
        System.exit(0);
    }

    public static void createTestInputFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./inputFiles/dataflow_input.csv"));
        for (int i = 0; i < numberOfCompanies; i++) {
            String temp = i + ",";
            for (int j = 0; j < numberOfDays; j++) {
                double rand = Math.random() * 1000;
                temp += rand + ",";
            }
            writer.write(temp.substring(0, temp.length() - 1) + "\n");
        }
        writer.close();
    }
}