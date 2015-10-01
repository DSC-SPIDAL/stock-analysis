import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class CCommonGenerator {
    private String pointFolder;
    private String vectorFolder;
    private String destFolder;
    private String rotateFolder;
    private String firstFile;
    private String secondFile;
    private int mode = 4;
    private boolean listGen = false;
    private Date startDate;
    private Date endDate;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("p", true, "Points folder");
        options.addOption("d", true, "Destination point folder");
        options.addOption(Utils.createOption("r", true, "Rotations folder", true));
        options.addOption(Utils.createOption("ff", true, "First file", false));
        options.addOption(Utils.createOption("sf", true, "Second file", false));
        options.addOption(Utils.createOption("l", false, "Generate file list", false));
        options.addOption(Utils.createOption("sd", true, "Start date", false));
        options.addOption(Utils.createOption("ed", true, "End date", false));
        options.addOption(Utils.createOption("md", true, "End date", false));

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String vectorFolder = cmd.getOptionValue("v");
            String pointsFolder = cmd.getOptionValue("p");
            String distFolder = cmd.getOptionValue("d");
            String rotateFolder = cmd.getOptionValue("r");
            String firstFile = cmd.getOptionValue("ff");
            String secondFile = cmd.getOptionValue("sf");
            String startDate = cmd.getOptionValue("sd");
            String endDate = cmd.getOptionValue("ed");
            String mode = cmd.getOptionValue("md");
            boolean listGen = cmd.hasOption("l");
            CCommonGenerator pointTransformer = new CCommonGenerator(pointsFolder, vectorFolder,
                    distFolder, rotateFolder, firstFile, secondFile, listGen);
            if (listGen) {
                if (mode != null) {
                    pointTransformer.setMode(Integer.parseInt(mode));
                }
                pointTransformer.setDates(Utils.parseDateString(startDate), Utils.parseDateString(endDate));
            }
            pointTransformer.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public CCommonGenerator(String pointFolder, String vectorFolder,
                                     String destPointFolder, String rotateFolder,
                                     String firstFile, String secondFile, boolean listGen) {
        this.pointFolder = pointFolder;
        this.vectorFolder = vectorFolder;
        this.destFolder = destPointFolder;
        this.firstFile = firstFile;
        this.secondFile = secondFile;
        this.rotateFolder = rotateFolder;
        this.listGen = listGen;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setDates(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;
    }

    public void process() {
        if (!listGen) {
            processPair(firstFile, secondFile);
        } else {
            List<String> dates = Utils.genDateList(this.startDate, this.endDate, mode);
            writeList(dates, destFolder + "/list.txt");
        }
    }

    private void processPair(String firstFileWithoutExt, String secondfFileWithoutExt) {
        String firstFileName = firstFileWithoutExt + ".csv";
        String secondFileName = secondfFileWithoutExt + ".csv";

        String firstVectorFileName = this.vectorFolder + "/" + firstFileName;
        String secondVectorFileName = this.vectorFolder + "/" + secondFileName;
        String firstPointFileName = this.rotateFolder + "/" + firstFileWithoutExt + ".txt";
        String secondPointFileName = this.pointFolder + "/" + secondfFileWithoutExt + ".txt";

        System.out.println("Processing pair: " + firstVectorFileName + " " + secondVectorFileName + " " + firstPointFileName + " " + secondPointFileName);

        // first lets read the vector keys
        List<Integer> firstVectorKeys = Utils.readVectorKeys(new File(firstVectorFileName));
        List<Integer> secondVectorKeys = Utils.readVectorKeys(new File(secondVectorFileName));

        // now lets get the common keys for both of them
        TreeSet<Integer> commonKeys = new TreeSet<Integer>(firstVectorKeys);
        commonKeys.retainAll(new TreeSet<Integer>(secondVectorKeys));
        System.out.println("Fits keys: " + firstVectorKeys.size() + " Second keys:"  + secondVectorKeys.size() + " Common keys: " + commonKeys.size());
        // now read the two point files
        Map<Integer, Point> firstPoints = Utils.loadPoints(new File(firstPointFileName), firstVectorKeys);
        Map<Integer, Point> secondPoints = Utils.loadPoints(new File(secondPointFileName), secondVectorKeys);
        Map<Integer, Double> secondWeights = Utils.loadCaps(new File(secondVectorFileName));
        // go through the common keys and write them out
        String pointOutFolder = destFolder + "/" + secondfFileWithoutExt + "/points";
        String weightOutFolder = destFolder + "/" + secondfFileWithoutExt + "/weights";

        new File(pointOutFolder).mkdirs();
        new File(weightOutFolder).mkdirs();

        String fistPointCommonFileName = pointOutFolder + "/first.txt";
        String secondPointCommonFileName = pointOutFolder + "/second.txt";
        String secondWeightFileName = weightOutFolder + "/" + "second.csv";

        writePoints(commonKeys, firstPoints, fistPointCommonFileName);
        writePoints(commonKeys, secondPoints, secondPointCommonFileName);
        writeWeights(commonKeys, secondWeightFileName, secondWeights);
    }

    private void writeList(List<String> dates, String file) {
        BufferedWriter bufWriter = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
            for (String d : dates) {
                bufWriter.write(d);
                bufWriter.newLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePoints(TreeSet<Integer> commonKeys, Map<Integer, Point> pointMap, String file) {
        BufferedWriter bufWriter = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
            int i = 0;
            for (Integer key : commonKeys) {
                Point p = pointMap.get(key);
                if (p == null) {
                    System.out.println("Key cannot be found: " + key);
                }
                p.setIndex(i++);
                bufWriter.write(p.serialize());
                bufWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file", e);
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void writeWeights(TreeSet<Integer> commonKeys, String weightFile, Map<Integer, Double> caps) {
        WriterWrapper weightWriter = null;
        try {
            double max = Double.MIN_VALUE;
            for (Double d : caps.values()) {
                if (max < d) {
                    max = d;
                }
            }
            weightWriter = new WriterWrapper(weightFile, true);
            int i = 0;
            for (Integer key : commonKeys) {
                weightWriter.write(caps.get(key) / max);
                weightWriter.line();
            }
        } finally {
            if (weightWriter != null) {
                weightWriter.close();
            }
        }
    }
}
