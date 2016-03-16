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

public class StockAnalysisPipeline2 {
    private final static Logger logger = LoggerFactory.getLogger(StockAnalysisPipeline2.class);

    public static int WINDOW_LENGTH = 30;
    public static int SLIDING_INTERVAL = 1;


    public static interface StockAnalysisPipelineOptions extends PipelineOptions {
        @Description("Path to input file")
        @Default.String("./inputFiles/jan_mar_2004.csv")
        String getInputFilePath();

        void setInputFilePath(String value);

        @Description("Output file path")
        @Default.String("output.txt")
        String getOutputFilePath();

        void setOutputFilePath(String value);
    }

    public static void main(String[] args) throws IOException {
        final SymbolEncoder symbolEncoder = new SymbolEncoder();

        StockAnalysisPipelineOptions options = PipelineOptionsFactory.fromArgs(args).as(StockAnalysisPipelineOptions.class);
        Pipeline pipeline = Pipeline.create(options);

        //Reading and time stamping the stock prices
        PCollection<KV<Integer, StockPricePoint>> stockPrices = pipeline.apply(TextIO.Read.from(options.getInputFilePath()))
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
                        SlidingWindows.of(Duration.standardDays(WINDOW_LENGTH)).every(Duration.standardDays(SLIDING_INTERVAL))
                )
        );

        //stock prices of each company within a single window
        PCollection<KV<Integer, List<StockPricePoint>>> stockPricesOfCompany = slidingWindowStockPrices.apply(GroupByKey.create())
                .apply(ParDo.of(new DoFn<KV<Integer, Iterable<StockPricePoint>>, KV<Integer, List<StockPricePoint>>>() {
                    @Override
                    public void processElement(ProcessContext processContext) throws Exception {
                        Integer key = processContext.element().getKey();
                        Iterator<StockPricePoint> iterator = processContext.element().getValue().iterator();
                        ArrayList<StockPricePoint> stockPricePoints = new ArrayList<>(250);
                        while (iterator.hasNext()) {
                            stockPricePoints.add(iterator.next());
                        }
                        processContext.output(KV.of(key, stockPricePoints));
                    }
                }));

        //complete stock prices vector as a view
        PCollectionView<Map<Integer, List<StockPricePoint>>> stockPriceVectorView = stockPricesOfCompany.apply(
                Combine.globally(new Combine.CombineFn<KV<Integer
                        , List<StockPricePoint>>, Map<Integer, List<StockPricePoint>>, Map<Integer, List<StockPricePoint>>>() {
                    @Override
                    public Map<Integer, List<StockPricePoint>> createAccumulator() {
                        return new HashMap<>();
                    }

                    @Override
                    public Map<Integer, List<StockPricePoint>> addInput(Map<Integer, List<StockPricePoint>> stockPricesMap,
                                                                        KV<Integer, List<StockPricePoint>> stockPricePointKV) {
                        if (stockPricesMap.get(stockPricePointKV.getKey()) != null) {
                            stockPricesMap.get(stockPricePointKV.getKey()).addAll(stockPricePointKV.getValue());
                        } else {
                            ArrayList<StockPricePoint> stockPricePoints = new ArrayList<>();
                            stockPricePoints.addAll(stockPricePointKV.getValue());
                            stockPricesMap.put(stockPricePointKV.getKey(), stockPricePoints);
                        }
                        return stockPricesMap;
                    }

                    @Override
                    public Map<Integer, List<StockPricePoint>> mergeAccumulators(Iterable<Map<Integer,
                            List<StockPricePoint>>> iterable) {
                        Map<Integer, List<StockPricePoint>> stockPricesMap = new HashMap<>();
                        Iterator<Map<Integer, List<StockPricePoint>>> iterator = iterable.iterator();
                        while (iterator.hasNext()) {
                            for (Map.Entry<Integer, List<StockPricePoint>> entry : iterator.next().entrySet()) {
                                if (stockPricesMap.get(entry.getKey()) != null) {
                                    stockPricesMap.get(entry.getKey()).addAll(entry.getValue());
                                } else {
                                    ArrayList<StockPricePoint> stockPricePoints = new ArrayList<>();
                                    stockPricePoints.addAll(entry.getValue());
                                    stockPricesMap.put(entry.getKey(), stockPricePoints);
                                }
                            }
                        }
                        return stockPricesMap;
                    }

                    @Override
                    public Map<Integer, List<StockPricePoint>> extractOutput(Map<Integer, List<StockPricePoint>> stockPricesMap) {
                        return stockPricesMap;
                    }
                }).asSingletonView());


        //calculating distance between companies within window
        PCollection<KV<String,Double>> distances = stockPricesOfCompany.apply(ParDo.withSideInputs(stockPriceVectorView)
                .of(new DoFn<KV<Integer, List<StockPricePoint>>, KV<String,Double>>() {
                    @Override
                    public void processElement(ProcessContext processContext) throws Exception {
                        Map<Integer,List<StockPricePoint>> stockPricesVector = processContext.sideInput(stockPriceVectorView);
                        Integer keyX = processContext.element().getKey();
                        List<StockPricePoint> stockPricesX = processContext.element().getValue();
                        for(Map.Entry<Integer,List<StockPricePoint>> entry : stockPricesVector.entrySet()){
                            //calculate only the bottom triangle (matrix is symmetric)
                            Integer keyY = entry.getKey();
                            if(keyY < keyX){
                                List<StockPricePoint> stockPricesY = entry.getValue();
                                //TODO calculate distance
                                processContext.output(KV.of(keyX+"_"+keyY, 0.0));
                            }
                        }
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
        })).apply(TextIO.Write.to(options.getOutputFilePath()));


        pipeline.run();
        System.exit(0);
    }
}