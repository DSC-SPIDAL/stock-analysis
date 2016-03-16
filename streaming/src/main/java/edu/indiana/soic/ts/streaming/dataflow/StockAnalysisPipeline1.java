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
import com.google.cloud.dataflow.sdk.transforms.windowing.SlidingWindows;
import com.google.cloud.dataflow.sdk.transforms.windowing.Window;
import com.google.cloud.dataflow.sdk.values.KV;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.cloud.dataflow.sdk.values.PCollectionView;
import edu.indiana.soic.ts.streaming.dataflow.utils.DistanceMatrix;
import edu.indiana.soic.ts.streaming.dataflow.utils.StockPricePoint;
import edu.indiana.soic.ts.streaming.dataflow.utils.SymbolEncoder;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StockAnalysisPipeline1 {
    private final static Logger logger = LoggerFactory.getLogger(StockAnalysisPipeline1.class);


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
        final SymbolEncoder symbolEncoder = new SymbolEncoder();

        StockAnalysisPipelineOptions options = PipelineOptionsFactory.fromArgs(args).as(StockAnalysisPipelineOptions.class);
        Pipeline pipeline = Pipeline.create(options);

        //Reading and time stamping the stock prices
        PCollection<KV<Integer, StockPricePoint>> stockPrices = pipeline.apply(TextIO.Read.from("./inputFiles/jan_mar_2004.csv"))
                .apply(ParDo.of(new DoFn<String, KV<Integer, StockPricePoint>>() {
                    @Override
                    public void processElement(ProcessContext c) throws Exception {
                        String[] fields = c.element().split(",");
                        StockPricePoint stockPoint = new StockPricePoint();
                        stockPoint.setId(fields[0]);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        stockPoint.setDate(sdf.parse(fields[1].trim()));
                        stockPoint.setSymbol(fields[2]);
                        stockPoint.setPrice(Double.parseDouble(fields[5].trim()));
                        stockPoint.setCap(Double.parseDouble(fields[6].trim()));
                        Instant instant = new Instant(stockPoint.getDate().getTime());
                        int index = symbolEncoder.getSymbolIndex(stockPoint.getSymbol());
                        //debugging - we cannot handle large amounts of data when using local runner
                        if(index > 1000 && index < 1100) {
                            c.outputWithTimestamp(KV.of(symbolEncoder.getSymbolIndex(stockPoint.getSymbol()), stockPoint), instant);
                        }
                    }
                }));


        //creating the sliding windows
        PCollection<KV<Integer, StockPricePoint>> slidingWindowStockPrices = stockPrices.apply(
                Window.<KV<Integer, StockPricePoint>>into(
                        SlidingWindows.of(Duration.standardDays(20)).every(Duration.standardDays(20))
                )
        );

        //accumulating stock prices per company per window
        PCollection<KV<Integer,List<StockPricePoint>>> stockPricesPerCompanyPerWindow = slidingWindowStockPrices
                .apply(GroupByKey.create()).apply(ParDo.of(new DoFn<KV<Integer,Iterable<StockPricePoint>>,
                        KV<Integer,List<StockPricePoint>>>() {
            @Override
            public void processElement(ProcessContext c) throws Exception {
                Integer key = c.element().getKey();
                Iterator<StockPricePoint> iterator = c.element().getValue().iterator();
                List<StockPricePoint> stockPricePoints = new ArrayList<>();
                while(iterator.hasNext()){
                    stockPricePoints.add(iterator.next());
                }
                c.output(KV.of(key, stockPricePoints));
            }
        }));

        //accumulating companies per window
        PCollectionView<Set<Integer>> companiesPerWindow = slidingWindowStockPrices.apply(Combine.globally(
                new Combine.CombineFn<KV<Integer, StockPricePoint>, Set<Integer>, Set<Integer>>() {
            @Override
            public Set<Integer> createAccumulator() {
                return new HashSet<>();
            }
            @Override
            public Set<Integer> addInput(Set<Integer> indices, KV<Integer, StockPricePoint> integerStockPricePointKV) {
                indices.add(integerStockPricePointKV.getKey());
                return indices;
            }
            @Override
            public Set<Integer> mergeAccumulators(Iterable<Set<Integer>> iterable) {
                HashSet<Integer> indices = new HashSet<>();
                Iterator<Set<Integer>> iterator = iterable.iterator();
                while (iterator.hasNext()) {
                    indices.addAll(iterator.next());
                }
                return indices;
            }
            @Override
            public Set<Integer> extractOutput(Set<Integer> indices) {
                return indices;
            }
        }).asSingletonView());

        //explode the company entries in each window to create distance matrix entries
        PCollection<KV<String,List<StockPricePoint>>> explodedEntries = stockPricesPerCompanyPerWindow.apply(
                ParDo.withSideInputs(companiesPerWindow).of(new DoFn<KV<Integer, List<StockPricePoint>>,
                        KV<String, List<StockPricePoint>>>() {
                    @Override
                    public void processElement(ProcessContext c) throws Exception {
                        Set<Integer> indices = c.sideInput (companiesPerWindow);
                        Integer key = c.element().getKey();
                        List<StockPricePoint> stockPricePoints = c.element().getValue();
                        Iterator<Integer> iterator = indices.iterator();
                        while(iterator.hasNext()){
                            Integer temp = iterator.next();
                            // we generate only the lower half. The distance matrix is symmetric
                            if(key > temp) {
                                c.output(KV.of(key + "_" + temp, stockPricePoints));
                            }else if (temp > key){
                                c.output(KV.of(temp + "_" + key, stockPricePoints));
                            }
                        }
                    }
                }));

        //grouping two entries to create a distance entry in the matrix and calculating the distance
        PCollection<KV<String,Double>> distances = explodedEntries.apply(GroupByKey.create()).apply(
                ParDo.of(new DoFn<KV<String,Iterable<List<StockPricePoint>>>, KV<String,Double>>() {
            @Override
            public void processElement(ProcessContext processContext) throws Exception {
                Integer keyX = Integer.parseInt(processContext.element().getKey().split("_")[0]);
                Integer keyY = Integer.parseInt(processContext.element().getKey().split("_")[1]);
                Iterator<List<StockPricePoint>> iterator = processContext.element().getValue().iterator();
                List<StockPricePoint> stockPricesX = iterator.next();
                List<StockPricePoint> stockPricesY = iterator.next();
                //TODO calculate distance
                processContext.output(KV.of(keyX+"_"+keyY, 0.0));
            }
        }));

        //formulate the distance matrix
        PCollection<DistanceMatrix> distanceMatrix = distances.apply(Combine.globally(
                new Combine.CombineFn<KV<String,Double>, DistanceMatrix, DistanceMatrix>() {
                    @Override
                    public DistanceMatrix createAccumulator() {
                        return new DistanceMatrix();
                    }

                    @Override
                    public DistanceMatrix addInput(DistanceMatrix distanceMatrix, KV<String, Double> stringDoubleKV) {
                        distanceMatrix.addPoint(Integer.parseInt(stringDoubleKV.getKey().split("_")[0]),
                                Integer.parseInt(stringDoubleKV.getKey().split("_")[1]),stringDoubleKV.getValue());
                        return distanceMatrix;
                    }

                    @Override
                    public DistanceMatrix mergeAccumulators(Iterable<DistanceMatrix> iterable) {
                        DistanceMatrix distanceMatrix = new DistanceMatrix();
                        Iterator<DistanceMatrix> iterator = iterable.iterator();
                        while(iterator.hasNext()){
                            distanceMatrix.merge(iterator.next());
                        }
                        return distanceMatrix;
                    }

                    @Override
                    public DistanceMatrix extractOutput(DistanceMatrix distanceMatrix) {
                        return distanceMatrix;
                    }
                }).withoutDefaults());

        //write to file
        distanceMatrix.apply(ParDo.of(new DoFn<DistanceMatrix, String>() {
            @Override
            public void processElement(ProcessContext processContext) throws Exception {
                String temp = processContext.timestamp() + "\n";
                temp += processContext.element().getDistanceValues().toString()+ "\n";
                temp += processContext.element().getRow().toString() + "\n";
                temp += processContext.element().getColumn().toString() + "\n";
                temp += "-------------------------------------------------------------------------------------------";
                processContext.output(temp);
            }
        })).apply(TextIO.Write.to("output.txt"));

        pipeline.run();
        System.exit(0);
    }
}