import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ConsecutiveDistancePrint {
    private String vectorFolder;
    private int distanceType;

    public ConsecutiveDistancePrint(String vectorFolder, int distanceType) {
        this.vectorFolder = vectorFolder;
        this.distanceType = distanceType;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("sd", true, "Start date");
        options.addOption("ed", true, "End date");
        options.addOption("dm", true, "Date mode");
        options.addOption("t", true, "distance type");
        options.addOption("i", true, "Original file");
        options.addOption("sy", true, "Symbol");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  vectorFolder = cmd.getOptionValue("v");
            int distanceType = Integer.parseInt(cmd.getOptionValue("t"));
            int mode = Integer.parseInt(cmd.getOptionValue("dm"));
            String input = cmd.getOptionValue("i");
            String sd = cmd.getOptionValue("sd");
            String ed = cmd.getOptionValue("ed");
            String symbol = cmd.getOptionValue("sy");

            Date startDate = Utils.parseDateString(sd);
            Date endDate = Utils.parseDateString(ed);

            List<Date> dates;
            if (mode == 6 || mode == 9 || mode == 10) {
                Set<Date> dateSet = DateUtils.retrieveDates(input);
                dates = DateUtils.sortDates(dateSet);
            } else {
                dates = new ArrayList<Date>();
            }

            List<String> dateString = DateUtils.genDateList(startDate, endDate, mode, dates);
            Map<Integer, String> mappings = Utils.loadMapping(input);
            Map<String, Integer> intervtedMappings = invert(mappings);

            int permNo = intervtedMappings.get(symbol);

            ConsecutiveDistancePrint consecutiveDistancePrint = new ConsecutiveDistancePrint(vectorFolder, distanceType);
            consecutiveDistancePrint.process(dateString, permNo, distanceType);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println(options.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static <V, K> Map<V, K> invert(Map<K, V> map) {
        Map<V, K> inv = new HashMap<V, K>();
        for (Map.Entry<K, V> entry : map.entrySet())
            inv.put(entry.getValue(), entry.getKey());

        return inv;
    }

    public void process(List<String> dateString, int permNo, int dist) {
        for (int i = 0; i < dateString.size(); i++) {
            StringBuilder sb = new StringBuilder();
            String name = dateString.get(i);
            String fileName = vectorFolder + "/" + name + ".csv";
            File file = new File(fileName);

            List<VectorPoint> vectorPoints = Utils.readVectors(file, 0, 7000);
            // get the vector point
            VectorPoint vc1 = getVectorPoint(permNo, vectorPoints);
            VectorPoint vc2 = getVectorPoint(1, vectorPoints);

            double cor = vc1.correlation(vc2, dist);

            sb.append(name).append(": ").append(cor).append(", ");
            System.out.println(sb.toString());
        }
    }

    public VectorPoint getVectorPoint(int permNo, List<VectorPoint> vectorPoints) {
        for (VectorPoint vectorPoint : vectorPoints) {
            if (vectorPoint.getKey() == permNo) {
                return vectorPoint;
            }
        }
        return null;
    }


}
