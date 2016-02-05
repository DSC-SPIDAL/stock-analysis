package msc.fall2015.stock.kmeans.hbase.utils;

import org.apache.commons.cli.Option;

public class Utils {
    public static Option createOption(String opt, boolean hasArg, String description, boolean required) {
        Option symbolListOption = new Option(opt, hasArg, description);
        symbolListOption.setRequired(required);
        return symbolListOption;
    }
}
