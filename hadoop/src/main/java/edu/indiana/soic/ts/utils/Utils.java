package edu.indiana.soic.ts.utils;

import edu.indiana.soic.ts.pviz.Plotviz;
import org.apache.commons.cli.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static Option createOption(String opt, boolean hasArg, String description, boolean required) {
        Option symbolListOption = new Option(opt, hasArg, description);
        symbolListOption.setRequired(required);
        return symbolListOption;
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
}
