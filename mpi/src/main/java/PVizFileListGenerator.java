import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

public class PVizFileListGenerator {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("s", true, "Start date");
        options.addOption("e", true, "End date");
        options.addOption("i", true, "Original file");
        options.addOption("d", true, "Mode, 1 - month, 2 year, 3 whole, 4 continous year");
        options.addOption("o", true, "Output file name");
        options.addOption("ext", true, "Extension");

        CommandLineParser commandLineParser = new BasicParser();
        FileOutputStream fos = null;
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String date = cmd.getOptionValue("s");
            String end = cmd.getOptionValue("e");
            String days = cmd.getOptionValue("d");
            String out = cmd.getOptionValue("o");
            String ext = cmd.getOptionValue("ext");
            String inputFile = cmd.getOptionValue("i");

            Date startDate = Utils.parseDateString(date);
            Date endDate = Utils.parseDateString(end);

            int mode = Integer.parseInt(days);
            List<Date> dates;
            if (mode == 6) {
                Set<Date> dateSet = DateUtils.retrieveDates(inputFile);
                dates = DateUtils.sortDates(dateSet);
            } else {
                dates = new ArrayList<Date>();
            }
            List<String> list = DateUtils.genDateList(startDate, endDate, mode, dates);
            fos = new FileOutputStream(out);
            BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
            for (String s : list) {
                bufWriter.write(s + "." + ext);
                bufWriter.newLine();
            }
            bufWriter.flush();
            bufWriter.close();
        } catch (ParseException | IOException e) {
            System.out.println("Failed to write file");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignore) {
                }
            }
        }
    }


}
