import org.apache.commons.cli.*;
import pviz.Clusters;
import pviz.Location;
import pviz.PVizPoint;
import pviz.Plotviz;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TranslatePoints {
    private String pointsFolder;
    private String destFolder;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("p", true, "Points folder");
        options.addOption("d", true, "Output folder");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String cluster = cmd.getOptionValue("c");
            String point = cmd.getOptionValue("p");
            String outputFolder = cmd.getOptionValue("d");
            String originalStockFile = cmd.getOptionValue("o");
            String vectorFolder = cmd.getOptionValue("v");

            TranslatePoints generator = new TranslatePoints(point, outputFolder);
            generator.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public TranslatePoints(String pointsFolder, String destFolder) {
        this.pointsFolder = pointsFolder;
        this.destFolder = destFolder;
    }

    public void process() {
        File inFolder = new File(pointsFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder: " + pointsFolder);
            return;
        }

        // create the output folder
        File outFolder = new File(destFolder);
        outFolder.mkdirs();

        List<Point> specialPoints = new ArrayList<Point>();
        for (File inFile : inFolder.listFiles()) {
            if (inFile.isDirectory()) {
                continue;
            }
            Plotviz plotviz = loadPlotViz(inFile);
            if (plotviz == null) {
                throw new RuntimeException("Failed to load file: " + inFile.getAbsolutePath());
            }
            Point point = findConstPoint(plotviz);
            if (point != null) {
                specialPoints.add(point);
            } else {
                System.out.println("Failed to get const point from file: " + inFile.getAbsolutePath());
            }
        }

        double avgX = 0, avgY = 0, avgZ = 0;
        for (Point p : specialPoints) {
            avgX += p.x;
            avgY += p.y;
            avgZ += p.z;
        }

        avgX /= specialPoints.size();
        avgY /=  specialPoints.size();
        avgZ /= specialPoints.size();

        Point averagePoint = new Point(0, avgX, avgY, avgZ, 0);
        for (File inFile : inFolder.listFiles()) {
            if (inFile.isDirectory()) {
                continue;
            }
            String outFile = outFolder + "/" + inFile.getName();
            translateFile(inFile, outFile, averagePoint);
        }
    }

    public void translateFile(File file, String outFile, Point averagePoint) {
        double avgX = 0, avgY = 0, avgZ = 0;
        Plotviz plotviz = loadPlotViz(file);

        if (plotviz == null) {
            throw new RuntimeException("Failed to load file: " + file.getAbsolutePath());
        }

        Point fileConst = findConstPoint(plotviz);
        if (fileConst != null) {
            avgX = fileConst.x - averagePoint.x;
            avgY = fileConst.y - averagePoint.y;
            avgZ = fileConst.z - averagePoint.z;

            for (PVizPoint p : plotviz.getPoints()) {
                Location location = p.getLocation();
                location.setX(location.getX() - avgX);
                location.setY(location.getY() - avgY);
                location.setZ(location.getZ() - avgZ);
            }

            try {
                Utils.savePlotViz(outFile, plotviz);
            } catch (FileNotFoundException | JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    public Point findConstPoint(Plotviz plotviz) {
        for (PVizPoint p : plotviz.getPoints()) {
            if (p.getClusterkey() == PvizGenerator.CONST_CLUSTER_KEY) {
                Location location = p.getLocation();
                return new Point(0, location.getX(), location.getY(), location.getZ(), p.getClusterkey());
            }
        }
        return null;
    }

    private Plotviz loadPlotViz(File file) {
        Plotviz clusters;
        FileInputStream adrFile = null;
        try {
            adrFile = new FileInputStream(file);
            JAXBContext ctx = JAXBContext.newInstance(Plotviz.class);
            Unmarshaller um = ctx.createUnmarshaller();
            clusters = (Plotviz) um.unmarshal(adrFile);
            return clusters;
        } catch (FileNotFoundException | JAXBException e) {
            e.printStackTrace();
        } finally {
            if (adrFile != null) {
                try {
                    adrFile.close();
                } catch (IOException ignore) {
                }
            }
        }
        return null;
    }

}
