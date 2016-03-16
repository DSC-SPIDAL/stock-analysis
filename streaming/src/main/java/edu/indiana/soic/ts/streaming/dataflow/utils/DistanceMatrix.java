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

import java.io.Serializable;
import java.util.ArrayList;

public class DistanceMatrix implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(DistanceMatrix.class);
    private static final long serialVersionUID = 8022985239527089893L;

    private ArrayList<Double> distanceValues;

    private ArrayList<Integer> row;

    private ArrayList<Integer> column;

    public DistanceMatrix(){
        this.distanceValues = new ArrayList<>(1400);
        this.row = new ArrayList<>(1400);
        this.column = new ArrayList<>(1400);
    }

    public void addPoint(int row, int col, double distance){
        this.distanceValues.add(distance);
        this.row.add(row);
        this.column.add(col);
    }

    public DistanceMatrix merge(DistanceMatrix another){
        for(int i=0;i<another.getSize();i++){
            this.addPoint(another.getRow().get(i),another.getColumn().get(i),
                    another.getDistanceValues().get(i));
        }
        return this;
    }

    public int getSize(){
        return distanceValues.size();
    }

    public ArrayList<Double> getDistanceValues() {
        return distanceValues;
    }

    public ArrayList<Integer> getRow() {
        return row;
    }

    public ArrayList<Integer> getColumn() {
        return column;
    }
}