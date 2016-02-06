package edu.indiana.soic.ts.mapreduce.postproc;

import edu.indiana.soic.ts.pviz.Cluster;
import edu.indiana.soic.ts.pviz.Clusters;
import edu.indiana.soic.ts.utils.Point;
import edu.indiana.soic.ts.utils.Utils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(LabelGenerator.class);

    public class LabelGeneratorMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
        private String clusterFile;
        private String pointsFolder;
        private String destFolder;
        private String vectorFile;
        private String originalFile;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            super.map(key, value, context);
        }
    }

    private Clusters loadClusters(String cluserInputFile) {
        Clusters clusters;
        FileInputStream adrFile = null;
        try {
            adrFile = new FileInputStream(cluserInputFile);
            JAXBContext ctx = JAXBContext.newInstance(Clusters.class);
            Unmarshaller um = ctx.createUnmarshaller();
            clusters = (Clusters) um.unmarshal(adrFile);
            return clusters;
        }
        catch (FileNotFoundException | JAXBException e) {
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

    public static void saveClusters(String outFileName, Clusters plotviz) throws FileNotFoundException, JAXBException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outFileName);
            JAXBContext ctx = JAXBContext.newInstance(Clusters.class);
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


    public void changeClassLabels(String cluserInputFile, String clusterOutputFile) {
        LOG.info("Reading cluster file: " + cluserInputFile);
        Clusters clusters = loadClusters(cluserInputFile);
        if (clusters == null) {
            LOG.info("No clusters found to change");
            return;
        }
        Map<Integer, String> classToSector = new HashMap<Integer, String>();
//        for (Map.Entry<String, Integer> e: sectorToClazz.entrySet()) {
//            classToSector.put(e.getValue(), e.getKey());
//        }
        for (Cluster c : clusters.getCluster()) {
            // find the cluster label
            // this is the label
            String key = classToSector.get(c.getKey());
            if (key != null ) {
                System.out.println("Setting label: " + key + " to cluster: " + c.getKey()   );
                c.setLabel(key);
            }
        }
        try {
            LOG.info("Writing cluster file: " + clusterOutputFile);
            saveClusters(clusterOutputFile, clusters);
        } catch (FileNotFoundException | JAXBException e) {
            throw new RuntimeException("Failed to write clusters", e);
        }
    }

    private void applyLabel(String inPointsFile, String outPointsFile, List<String> symbols) {
        LOG.info("Applying labels for points file: " + inPointsFile);
        FileReader input;
        BufferedWriter bufWriter = null;
        try {
            FileOutputStream fos = new FileOutputStream(outPointsFile);
            bufWriter = new BufferedWriter(new OutputStreamWriter(fos));

            File inFile = new File(inPointsFile);
            if (!inFile.exists()) {
                LOG.info("ERROR: In file doens't exist");
                return;
            }
            input = new FileReader(inPointsFile);
            BufferedReader bufRead = new BufferedReader(input);
            String inputLine;
            int index = 0;
            while ((inputLine = bufRead.readLine()) != null && index < symbols.size())  {
                Point p = Utils.readPoint(inputLine);
                String symbol = symbols.get(index);
                int clazz = 0;
//                if (this.invertedFixedClases.containsKey(symbol)) {
//                    clazz = this.invertedFixedClases.get(symbol);
//                } else {
//                    // get the corresponding symbol
//                    // get the class for this one
//                    String sector = invertedSectors.get(symbol);
//                    if (sector != null) {
//                        clazz = sectorToClazz.get(sector);
//                    } else {
////                    System.out.println("No sector: " + symbol);
//                    }
//                }
                p.setClazz(clazz);
                String s = p.serialize();
                bufWriter.write(s);
                bufWriter.newLine();
                index++;
            }
            LOG.info("Read lines: " + index);
        } catch (Exception e) {
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
}
