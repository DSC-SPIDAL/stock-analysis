package edu.indiana.soic.ts.mapreduce;

import edu.indiana.soic.ts.utils.CleanMetric;
import edu.indiana.soic.ts.utils.Constants;
import edu.indiana.soic.ts.utils.VectorPoint;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public class StockVectorCalculatorMapper extends TableMapper<IntWritable, Text> {
    private static final Logger LOG = LoggerFactory.getLogger(StockVectorCalculatorMapper.class);
    /**
     * The start date we are interested in
     */
    private String startDate;
    /**
     * The end  date we are interested in
     */
    private String endDate;

    private int noOfDays;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        Configuration conf = context.getConfiguration();
        startDate = conf.get(Constants.Job.START_DATE);
        endDate = conf.get(Constants.Job.END_DATE);
        noOfDays = Integer.valueOf(conf.get(Constants.Job.NO_OF_DAYS));
    }

    /**
     * Read the required columns and write to HDFS
     * @param row
     * @param value
     * @param context
     * @throws InterruptedException
     * @throws IOException
     */
    public void map(ImmutableBytesWritable row, Result value, Context context) throws InterruptedException, IOException {
        // go through the column family
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> columnFamilyMap : value.getMap().entrySet()) {
            List<Double> priceList = new ArrayList<Double>();
            int count = 1;
            // go through the column
            double totalCap = 0;
            String rowKey = Bytes.toString(value.getRow());
            String[] idKey = rowKey.split("_");
            int id = Integer.valueOf(idKey[0]);
            String symbol = idKey[1];
            int index = 0;
            VectorPoint vectorPoint = new VectorPoint(id, symbol, noOfDays, true);
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entryVersion : columnFamilyMap.getValue().entrySet()) {
                for (Map.Entry<Long, byte[]> entry : entryVersion.getValue().entrySet()) {
                    String column = Bytes.toString(entryVersion.getKey());
                    byte[] val = entry.getValue();
                    String valOfColumn = new String(val);
                    LOG.info("RowKey : " + rowKey + " Column Key : " + column + " Column Val : " + valOfColumn);
                    if (!valOfColumn.isEmpty()) {
                        String[] priceAndCap = valOfColumn.split("_");
                        if (priceAndCap.length > 1) {
                            String pr = priceAndCap[0];
                            String cap = priceAndCap[1];
                            if (pr != null && !pr.equals("null")){
                                double price = Double.valueOf(pr);
                                vectorPoint.add(price, index);
                                index++;
                            }
                            if (cap != null && !cap.equals("null")){
                                totalCap += Double.valueOf(cap);
                            }
                        }
                    }
                }
                count++;
            }
            vectorPoint.setTotalCap(totalCap);
            String serialize = null;
            if(vectorPoint.cleanVector(new CleanMetric())){
                serialize = vectorPoint.serialize();
                System.out.println(serialize);
            }
            if (serialize != null){
                context.write(new IntWritable(id), new Text(serialize));
            }
        }
    }
}
