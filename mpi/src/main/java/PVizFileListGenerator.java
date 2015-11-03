import org.apache.commons.cli.*;

import java.io.*;
import java.util.Date;
import java.util.List;

public class PVizFileListGenerator {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("s", true, "Start date");
        options.addOption("e", true, "End date");
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

            Date startDate = Utils.parseDateString(date);
            Date endDate = Utils.parseDateString(end);

            List<String> list = Utils.genDateList(startDate, endDate, Integer.parseInt(days));
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
