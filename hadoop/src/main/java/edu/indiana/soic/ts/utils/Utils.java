package edu.indiana.soic.ts.utils;

import edu.indiana.soic.ts.pviz.Plotviz;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public static Option createOption(String opt, boolean hasArg, String description, boolean required) {
        Option symbolListOption = new Option(opt, hasArg, description);
        symbolListOption.setRequired(required);
        return symbolListOption;
    }

    public static String getConfigurationFile(String []args) {
        Options options = new Options();
        options.addOption(Utils.createOption("c", true, "Configuration file", true));
        CommandLineParser commandLineParser = new BasicParser();
        CommandLine cmd;
        try {
            cmd = commandLineParser.parse(options, args);
            return cmd.getOptionValue("c");
        } catch (org.apache.commons.cli.ParseException e) {
            throw new RuntimeException("Invalid command line arguments");
        }
    }

    public static VectorPoint parseVector(String line) {
        // process the line.
        String parts[] = line.trim().split(" ");
        if (parts.length > 0 && !(parts.length == 1 && parts[0].equals(""))) {
            int key = Integer.parseInt(parts[0]);
            double cap = Double.parseDouble(parts[1]);

            int vectorLength = parts.length - 2;
            double[] numbers = new double[vectorLength];
            for (int i = 2; i < parts.length; i++) {
                numbers[i - 2] = Double.parseDouble(parts[i]);
            }
            VectorPoint p = new VectorPoint(key, numbers);
            p.addCap(cap);
            return p;
        }
        return null;
    }

    public static List<VectorPoint> readVectors(File file) {
        List<VectorPoint> vecs = new ArrayList<VectorPoint>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                VectorPoint p = parseVector(line);
                vecs.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignore) {
                }
            }
        }
        return vecs;
    }

    public static void savePlotViz(String outFileName, Plotviz plotviz) throws FileNotFoundException, JAXBException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outFileName);
            JAXBContext ctx = JAXBContext.newInstance(Plotviz.class);
            Marshaller ma = ctx.createMarshaller();
            ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ma.marshal(plotviz, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public static Point readPoint(String line) throws Exception {
        try {
            String[] splits = line.split("\t");

            int i = Integer.parseInt(splits[0]);
            double x = Double.parseDouble(splits[1]);
            double y = Double.parseDouble(splits[2]);
            double z = Double.parseDouble(splits[3]);
            int clazz = Integer.parseInt(splits[4]);

            return new Point(i, x, y, z, clazz);
        } catch (NumberFormatException e) {
            throw new Exception(e);
        }
    }

    public static Object loadObject(String className) {
        ClassLoader classLoader = Utils.class.getClassLoader();

        try {
            Class aClass = classLoader.loadClass(className);
            LOG.info("aClass.getName() = " + aClass.getName());
            return aClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            String s = "Failed to load class";
            LOG.error(s, e);
            throw new RuntimeException(s, e);
        }
    }

    public static Record parseRecordLine(String line, CleanMetric metric, boolean convert) throws ParseException {
        String[] array = line.trim().split(",");
        if (array.length >= 3) {
            int permNo = Integer.parseInt(array[0]);
            Date date = Utils.formatter.parse(array[1]);
            String stringSymbol = array[2];
            if (array.length >= 7) {
                double price = -1;
                if (!array[5].equals("")) {
                    price = Double.parseDouble(array[5]);
                    if (convert) {
                        if (price < 0) {
                            price *= -1;
                            if (metric != null) {
                                metric.negativeCount++;
                            }
                        }
                    }
                }

                double factorToAdjPrice = 0;
                if (!"".equals(array[4].trim())) {
                    factorToAdjPrice = Double.parseDouble(array[4]);
                }

                double factorToAdjVolume = 0;
                if (!"".equals(array[3].trim())) {
                    factorToAdjVolume = Double.parseDouble(array[3]);
                }

                int volume = 0;
                if (!array[6].equals("")) {
                    volume = Integer.parseInt(array[6]);
                }

                return new Record(price, permNo, date, array[1], stringSymbol, volume, factorToAdjPrice, factorToAdjVolume);
            } else {
                return new Record(-1, permNo, date, array[1], stringSymbol, 0, 0, 0);
            }
        }
        return null;
    }
}
