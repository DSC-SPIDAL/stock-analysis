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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HelperMethods {
    private final static Logger logger = LoggerFactory.getLogger(HelperMethods.class);

    public static void main(String[] args) throws IOException, ServiceException {
        generateInputFiles();
    }

    public static void generateInputFiles() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("./inputFiles/2004_2015.csv"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("./inputFiles/2004_2015_filtered.csv"));
        HashMap<String,Integer> companies = new HashMap<>(15000);
        String temp = reader.readLine();
        int count = 0;
        int wrongFormatCount = 0;
        while(temp != null){
            count++;
            String temp2 = temp.replaceAll(",",", ");
            String[] bits = temp2.split(",");
            if(bits.length == 7 && !bits[2].trim().isEmpty()){
                writer.write(temp + "\n");
                String symbol = bits[2].trim();
                if(companies.get(symbol) != null){
                    companies.put(symbol, companies.get(symbol) + 1);
                }else {
                    companies.put(symbol, 1);
                }
            }
            else{
                wrongFormatCount++;
            }

            temp = reader.readLine();
        }
        writer.close();
        System.out.println("total entries : " + count);
        System.out.println("wrong format entries : " + wrongFormatCount);
        System.out.println("unique companies : " + companies.size());
        writer = new BufferedWriter(new FileWriter("streaming/src/main/resources/symbol_encoding.csv"));
        Iterator<Map.Entry<String,Integer>> iterator = companies.entrySet().iterator();
        int i = 0;
        while(iterator.hasNext()){
            Map.Entry<String,Integer> entry = iterator.next();
            writer.write(entry.getKey() + "," + i + "," + entry.getValue() + "\n");
            i++;
        }
        writer.close();
    }
}