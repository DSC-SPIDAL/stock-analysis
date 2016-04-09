import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class MDSPointGenerator {
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
    private String inputFile;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", true, "Input Vector folder");
        options.addOption("i", true, "Input file");
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
            String inputFile = cmd.getOptionValue("i");
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
                    distFolder, rotateFolder, firstFile, secondFile, listGen, inputFile);
            if (listGen) {
                if (mode != null) {
                    pointTransformer.setMode(Integer.parseInt(mode));
                }
                pointTransformer.setDates(Utils.parseDateString(startDate), Utils.parseDateString(endDate));
            }
            pointTransformer.process();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MDSPointGenerator(String pointFolder, String vectorFolder,
                            String destPointFolder, String rotateFolder,
                            String firstFile, String secondFile, boolean listGen, String inputFile) {
        this.pointFolder = pointFolder;
        this.vectorFolder = vectorFolder;
        this.destFolder = destPointFolder;
        this.firstFile = firstFile;
        this.secondFile = secondFile;
        this.rotateFolder = rotateFolder;
        this.listGen = listGen;
        this.inputFile = inputFile;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setDates(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;
    }

    public void process() throws FileNotFoundException {
        if (!listGen) {
            processPair(firstFile, secondFile);
        } else {
            List<Date> dates;
            if (mode == 6 || mode == 9 || mode == 10) {
                Set<Date> dateSet = DateUtils.retrieveDates(inputFile);
                dates = DateUtils.sortDates(dateSet);
            } else {
                dates = new ArrayList<Date>();
            }
            List<String> list = DateUtils.genDateList(startDate, endDate, mode, dates);
            writeList(list, destFolder + "/list.txt");
        }
    }

    private void processPair(String firstFileWithoutExt, String secondfFileWithoutExt) {
        String firstVectorFileName = this.vectorFolder + "/" + firstFileWithoutExt  + ".csv";
        String secondVectorFileName = this.vectorFolder + "/" + secondfFileWithoutExt  + ".csv";
        String firstPointFileName = this.rotateFolder + "/" + firstFileWithoutExt + ".txt";
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
