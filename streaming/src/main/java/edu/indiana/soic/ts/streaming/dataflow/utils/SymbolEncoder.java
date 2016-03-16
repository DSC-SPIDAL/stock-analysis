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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;

public class SymbolEncoder implements Serializable{

    private final static Logger logger = LoggerFactory.getLogger(SymbolEncoder.class);
    private static final long serialVersionUID = 2871400601589544196L;

    HashMap<String,Integer> mapOfSymbols;

    public SymbolEncoder() throws IOException {
        mapOfSymbols = new HashMap<>(15000);
        BufferedReader reader =  new BufferedReader(
                new InputStreamReader(SymbolEncoder.class.getResourceAsStream("/symbol_encoding.csv")));
        String temp = reader.readLine();
        while (temp != null){
            String[] bits = temp.split(",");
            mapOfSymbols.put(bits[0],Integer.parseInt(bits[1]));
            temp =  reader.readLine();
        }
    }

    public int getSymbolIndex(String symbol){
        if(mapOfSymbols.get(symbol) != null){
            return mapOfSymbols.get(symbol);
        }
        return -1;
    }
}