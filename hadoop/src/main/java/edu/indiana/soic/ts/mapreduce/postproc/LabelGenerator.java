package edu.indiana.soic.ts.mapreduce.postproc;

import edu.indiana.soic.ts.pviz.Cluster;
import edu.indiana.soic.ts.pviz.Clusters;
import edu.indiana.soic.ts.utils.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(LabelGenerator.class);
    private NumberFormat intgerFormaterr = new DecimalFormat("#00");

    private String vectDir;
    private String interHistDir;
    private String fixedClassesFile;
    private String pointFileDir;

    private TSConfiguration tsConfiguration;

    public void configure(String []args) {
        String  configFile = Utils.getConfigurationFile(args);
        this.tsConfiguration = new TSConfiguration(configFile);
        Map tsConf = tsConfiguration.getConf();

        this.interHistDir = tsConfiguration.getIntemediateHistDir();
        this.vectDir = tsConfiguration.getVectorDir();
    }

    public int execJob(Configuration conf, String vectorFileFullPath, String vectorFile, String interHistDir) throws Exception {
        LOG.info(vectorFileFullPath);
        Job job = new Job(conf, "Pairwise-calc-" + vectorFile);

		/* create the out dir for this job. Delete and recreates if it exists */
        Path hdOutDir = new Path(interHistDir + "/" + vectorFile);
        FileSystem fs = FileSystem.get(conf);
        fs.delete(hdOutDir, true);
        conf.set("mapreduce.output.textoutputformat.separator", ",");
        Path hdInputDir = new Path(this.vectDir + "/" + vectorFile);

        job.setJarByClass(LabelGenerator.class);
        job.setMapperClass(LabelGeneratorMapper.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(job, hdInputDir);
        FileOutputFormat.setOutputPath(job, hdOutDir);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        long startTime = System.currentTimeMillis();
        int exitStatus = job.waitForCompletion(true) ? 0 : 1;
        double executionTime = (System.currentTimeMillis() - startTime) / 1000.0;
        LOG.info("Job Finished in " + executionTime + " seconds");
        return exitStatus;
    }

    public void submitJob() {
        Configuration conf = new Configuration();
        FileSystem fs;
        try {
            fs = FileSystem.get(conf);
            FileStatus[] status = fs.listStatus(new Path(vectDir));
            for (FileStatus statu : status) {
                String sequenceFile = statu.getPath().getName();
                String sequenceFileFullPath = vectDir + "/" + sequenceFile;
                try {
                    execJob(conf, sequenceFileFullPath, sequenceFile, interHistDir);
                    Utils.concatOutput(conf, sequenceFile, interHistDir + "/" + sequenceFile, tsConfiguration.getHistDir());
                } catch (Exception e) {
                    String message = "Failed to executed PWD calculation:" + sequenceFileFullPath + " " + interHistDir;
                    LOG.info(message, e);
                    throw new RuntimeException(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class VectorReadMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
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

    public class PointReadMaper extends Mapper<LongWritable, Text, Text, Text> {
        
    }


    public class LabelGeneratorReducer extends Reducer<LongWritable, Text, Text, Text> {
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            super.reduce(key, values, context);
        }
    }

    public static Clusters loadClusters(String clusterInputFile) {
        Clusters clusters;
        FileInputStream adrFile = null;
        try {
            adrFile = new FileInputStream(clusterInputFile);
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

    private Map<String, List<String>> loadHistoSectors(String sectorFile) {
        FileReader input;
        Map<String, List<String>> sectors = new HashMap<String, List<String>>();
        try {
            input = new FileReader(sectorFile);
            BufferedReader bufRead = new BufferedReader(input);
            String line;

            int i = 1;
            while ((line = bufRead.readLine()) != null) {
                Bin sectorRecord = Utils.readBin(line);
                List<String> stockList = sectorRecord.symbols;
                String startEnd = formatter.format(sectorRecord.end);
                String key = intgerFormaterr.format(i) + ":" + startEnd;
                sectors.put(key, stockList);
                for (String s : stockList) {
                    invertedSectors.put(s, key);
                }
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sector file", e);
        }
        return sectors;
    }

    private static List<String> loadSymbols(Configuration conf, String vectorFile) {
        FileSystem fs = null;
        try {
            List<String> symbols = new ArrayList<String>();
            fs = FileSystem.get(conf);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(new Path(vectorFile))));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                VectorPoint p = Utils.parseVector(line);
                if (p != null) {
                    symbols.add(p.getSymbol());
                }
            }
            bufferedReader.close();
            return symbols;
        } catch (IOException e) {
            String msg = "Failed to read file: " + vectorFile;
            throw new RuntimeException(msg, e);
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
                int clazz = 0;
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
