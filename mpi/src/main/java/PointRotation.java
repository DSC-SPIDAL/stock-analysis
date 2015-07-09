import org.apache.commons.cli.*;

import java.io.*;

public class PointRotation {
    private String inputFile;
    private String outputFile;
    private int degrees;

    public PointRotation(String inputFile, String outputFile, int degrees) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.degrees = degrees;
    }

    public void process() {
        applyRotation(inputFile, outputFile);
    }

    private void applyRotation(String inPointsFile, String outPointsFile) {
        System.out.println("Applying labels for points file: " + inPointsFile);
        FileReader input;
        BufferedWriter bufWriter = null;
        try {
            FileOutputStream fos = new FileOutputStream(outPointsFile);
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            File inFile = new File(inPointsFile);
            if (!inFile.exists()) {
                return;
            }
            input = new FileReader(inPointsFile);
            BufferedReader bufRead = new BufferedReader(input);
            String inputLine;
            while ((inputLine = bufRead.readLine()) != null)  {
                Point p = Utils.readPoint(inputLine);
                rotate(degrees, p);
                String s = p.serialize();
                bufWriter.write(s);
                bufWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read/write file", e);
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void rotate(double theta, Point p) {
        double sin_t = Math.sin(Math.toRadians(theta));
        double cos_t = Math.cos(Math.toRadians(theta));

        double x = p.getX();
        double y = p.getY();
        p.x = x * cos_t - y * sin_t;
        p.y = y * cos_t + x * sin_t;
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", true, "Input point file");
        options.addOption("o", true, "output point file");
        options.addOption("d", true, "Degrees");
        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String vectorFile = cmd.getOptionValue("i");
            String pointsFolder = cmd.getOptionValue("o");
            String degrees = cmd.getOptionValue("d");

            PointRotation program = new PointRotation(vectorFile, pointsFolder, Integer.parseInt(degrees));
            program.process();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
