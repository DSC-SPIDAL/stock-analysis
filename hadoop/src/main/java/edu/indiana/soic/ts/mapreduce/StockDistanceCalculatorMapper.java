package edu.indiana.soic.ts.mapreduce;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;

public class StockDistanceCalculatorMapper extends TableMapper<Text, Text> {
    public void map(ImmutableBytesWritable row, Result value, Context context) throws InterruptedException, IOException {
        // go through the column family
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> columnFamilyMap : value.getMap().entrySet()) {
            // go through the column
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entryVersion : columnFamilyMap.getValue().entrySet()) {

            }
        }
    }
}
