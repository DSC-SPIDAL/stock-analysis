package msc.fall2015.stock.kmeans.hbase.utils;

import org.apache.commons.cli.Option;

public class Utils {
    public static Option createOption(String opt, boolean hasArg, String description, boolean required) {
        Option symbolListOption = new Option(opt, hasArg, description);
        symbolListOption.setRequired(required);
        return symbolListOption;
    }

    public static VectorPoint parseVector(String line) {
        // process the line.
        String parts[] = line.trim().split(" ");
        if (parts.length > 0 && !(parts.length == 1 && parts[0].equals(""))) {
            int key = Integer.parseInt(parts[0]);
            double cap = Double.parseDouble(parts[1]);

            int vectorLength = parts.length - 2;
            double[] numbers = new double[vectorLength];
            for (int i = 2; i < parts.length; i++) {
                numbers[i - 2] = Double.parseDouble(parts[i]);
            }
            VectorPoint p = new VectorPoint(key, numbers);
            if (key < 10) {
                p = new VectorPoint(key, globalVectorLength, true);
                p.setConstantVector(true);
            } else if (globalVectorLength < 0){
                globalVectorLength = vectorLength;
            }
            p.addCap(cap);
            vecs.add(p);
            return p;
        }
        return null;
    }
}
