import org.apache.commons.cli.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RemoveInvalidDays {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", true, "Input");
        options.addOption("o", true, "Output"); // Destination folder
        options.addOption("np", true, "Number of stocks");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            Map<String, Integer> dateCount = new HashMap<>();


            CommandLine cmd = commandLineParser.parse(options, args);
            String  input = cmd.getOptionValue("i");
            String  output = cmd.getOptionValue("o");
            int stocks = Integer.parseInt(cmd.getOptionValue("np"));

            FileOutputStream fos = new FileOutputStream(output);
            BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            FileReader inputFile = new FileReader(input);
            BufferedReader bufRead = new BufferedReader(inputFile);
            Record record;
            int count;
            int lineCount = 0;
            while ((record = Utils.parseFile(bufRead)) != null) {
                lineCount++;
                String dateSting = record.getDateString();
                count = 1;
                if (dateCount.get(dateSting) != null) {
                    count += dateCount.get(dateSting);
                }
                dateCount.put(dateSting, count);
            }
            inputFile.close();
            bufRead.close();
            System.out.println("Original lines: " + lineCount);

            inputFile = new FileReader(input);
            BufferedReader secondReader = new BufferedReader(inputFile);
            String line;
            lineCount = 0;
            while ((line = secondReader.readLine()) != null) {
                record = Utils.parseLine(line, null, false);
                if (record != null) {
                    Integer c = dateCount.get(record.getDateString());
                    if (c != null && c > stocks) {
                        lineCount++;
                        bufWriter.write(line);
                        bufWriter.newLine();
                    } else {
                        System.out.println("Only one day line: " + line);
                    }
                } else {
                    System.out.println("Only one day line: " + line);
                }
            }
            System.out.println("Wrote lines: " + lineCount);
            bufWriter.flush();
            bufWriter.close();
            bufRead.close();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
