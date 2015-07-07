import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;

/**
 * The generated point files have different number of points.
 * We need to make sure that all the point files have the same stock ordering with same stocks before applying the rotations.
 */
public class PointTransformer {
    private String globalVectorFile;
    private String globalPointFile;
    private String pointFolder;
    private String vectorFolder;
    private String destPointFolder;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("g", true, "Global file");
        options.addOption("gp", true, "Global pooint file");
        options.addOption("v", true, "Input Vector folder");
        options.addOption("p", true, "Points folder");
        options.addOption("d", true, "Destination point folder");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String globalPointFile = cmd.getOptionValue("gp");
            String globalFile = cmd.getOptionValue("g");
            String vectorFile = cmd.getOptionValue("v");
            String pointsFolder = cmd.getOptionValue("p");
            String distFolder = cmd.getOptionValue("d");

            PointTransformer pointTransformer = new PointTransformer(globalFile, pointsFolder, vectorFile, distFolder, globalPointFile);
            pointTransformer.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public PointTransformer(String globalVectorFile, String pointFolder, String vectorFolder, String destPointFolder, String globalPointFile) {
        this.globalVectorFile = globalVectorFile;
        this.pointFolder = pointFolder;
        this.vectorFolder = vectorFolder;
        this.destPointFolder = destPointFolder;
        this.globalPointFile = globalPointFile;
    }

    public void process() {
        File inFolder = new File(vectorFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder");
            return;
        }

        // first get the global vector file and its keys
        File globalFile = new File(globalVectorFile);
        List<Integer> globalKeys = Utils.readVectorKeys(globalFile);

        Map<String, Map<Integer, Point>> filesToPoint = new HashMap<String, Map<Integer, Point>>();

        // go through every vector file and get the common keys
        TreeSet<Integer> commonKeys = new TreeSet<>(globalKeys);
        for (int i = 0; i < inFolder.listFiles().length; i++) {
            File inFile = inFolder.listFiles()[i];
            List<Integer> partKeys = Utils.readVectorKeys(inFile);
            // load the corresponding points file
            String fileName = inFile.getName();
            String fileNameWithOutExt = FilenameUtils.removeExtension(fileName);
            String pointFile = pointFolder + "/" + fileNameWithOutExt;
            Map<Integer, Point> points = loadPoints(new File(pointFile), partKeys);
            filesToPoint.put(fileName, points);
            commonKeys.retainAll(partKeys);
        }

        // write the global file
        String commonGlobalPointFile = destPointFolder + "/" + globalFile.getName();
        Map<Integer, Point> globalPoints = loadPoints(new File(globalPointFile), globalKeys);
        writePoints(commonKeys, globalPoints, commonGlobalPointFile);

        for (Map.Entry<String, Map<Integer, Point>> entry : filesToPoint.entrySet()) {
            // now write the common keys back as new files, we need to preserve the order as well
            // get the file
            Map<Integer, Point> pointMap = entry.getValue();
            String file =  entry.getKey();
            writePoints(commonKeys, pointMap, file);
        }
    }

    private void writePoints(TreeSet<Integer> commonKeys, Map<Integer, Point> pointMap, String file) {
        File commonPointFile = new File(destPointFolder + "/" + file);
        BufferedWriter bufWriter = null;
        try {
            FileOutputStream fos = new FileOutputStream(commonPointFile);
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));
            Iterator<Integer> keyIterator = commonKeys.iterator();
            while (keyIterator.hasNext()) {
                Integer key = keyIterator.next();
                Point p = pointMap.get(key);
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

    /**
     * Load the mapping from permno to point
     * @param pointFile
     * @param keys
     * @return
     */
    private Map<Integer, Point> loadPoints(File pointFile, List<Integer> keys) {
        BufferedReader bufRead = null;
        Map<Integer, Point> points = new HashMap<Integer, Point>();
        try {
            bufRead = new BufferedReader(new FileReader(pointFile));
            String inputLine;
            int index = 0;
            while ((inputLine = bufRead.readLine()) != null) {
                Point p = Utils.readPoint(inputLine);
                points.put(keys.get(index), p);
            }
            return points;
        } catch (IOException e) {
            throw new RuntimeException("Faile to read file");
        }
    }
}
