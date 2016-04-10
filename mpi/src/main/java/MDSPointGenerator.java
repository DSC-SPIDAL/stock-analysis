import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class MDSPointGenerator {
    private String pointFolder;
    private String vectorFolder;
    private String mdsFolder;
    private String firstFile;
    private String secondFile;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("p", true, "Points folder");
        options.addOption(Utils.createOption("r", true, "MDS folder", true));
        options.addOption(Utils.createOption("ff", true, "First file", false));
        options.addOption(Utils.createOption("sf", true, "Second file", false));

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String vectorFolder = cmd.getOptionValue("v");
            String pointsFolder = cmd.getOptionValue("p");
            String rotateFolder = cmd.getOptionValue("r");
            String firstFile = cmd.getOptionValue("ff");
            String secondFile = cmd.getOptionValue("sf");
            MDSPointGenerator pointTransformer = new MDSPointGenerator(pointsFolder, vectorFolder,
                    rotateFolder, firstFile, secondFile);
            pointTransformer.process();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MDSPointGenerator(String pointFolder, String vectorFolder,
                            String mdsFolder,
                            String firstFile, String secondFile) {
        this.pointFolder = pointFolder;
        this.vectorFolder = vectorFolder;
        this.firstFile = firstFile;
        this.secondFile = secondFile;
        this.mdsFolder = mdsFolder;
    }

    public void process() throws FileNotFoundException {
        processPair(firstFile, secondFile);
    }

    private void processPair(String firstFileWithoutExt, String secondfFileWithoutExt) {
        String firstVectorFileName = this.vectorFolder + "/" + firstFileWithoutExt  + ".csv";
        String secondVectorFileName = this.vectorFolder + "/" + secondfFileWithoutExt  + ".csv";
        String firstPointFileName = this.mdsFolder + "/" + firstFileWithoutExt + ".txt";
        String secondPointFileName = this.pointFolder + "/" + secondfFileWithoutExt + ".txt";

        System.out.println("Processing pair: " + firstVectorFileName + " " + secondVectorFileName + " " + firstPointFileName + " " + secondPointFileName);

        // first lets read the vector keys
        List<Integer> firstVectorKeys = Utils.readVectorKeys(new File(firstVectorFileName));
        List<Integer> secondVectorKeys = Utils.readVectorKeys(new File(secondVectorFileName));

        // now read the two point files
        Map<Integer, Point> firstPoints = Utils.loadPoints(new File(firstPointFileName), firstVectorKeys);

        // go through the second vector keys and output the corresponding point
        Random random = new Random();
        List<Point> secondPoints = new ArrayList<>();
        for (int i = 0; i < secondVectorKeys.size(); i++) {
            int key = secondVectorKeys.get(i);
            Point p;
            if (firstVectorKeys.contains(key)) {
                p = firstPoints.get(key);
            } else {
                p = new Point(i, random.nextDouble(), random.nextDouble(), random.nextDouble(), 1);
            }

            secondPoints.add(p);
        }
        writePoints(secondPoints, secondPointFileName);
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void writePoints(List<Point> pointMap, String file) {
        BufferedWriter bufWriter = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
            int i = 0;
            for (Point p : pointMap) {
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
}
