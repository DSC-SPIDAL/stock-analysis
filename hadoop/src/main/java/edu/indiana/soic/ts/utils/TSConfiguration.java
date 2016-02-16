package edu.indiana.soic.ts.utils;

import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class TSConfiguration {
    public static final String BASE_PATH = "base.dir";
    public static final String INPUT_DIR = "input.dir";

    public static final String PREPROC_DIR = "preproc.dir";
    public static final String POSTPROC_DIR = "postproc.dir";

    public static final String VECTOR_DIR = "vector.dir";
    public static final String DISTANCE_DIR = "distance.dir";
    public static final String INTERMEDIATE_DIR = "intermediate.dir";
    public static final String HIST_DIR = "histogram.dir";

    public static final String START_DATE = "time.start";
    public static final String END_DATE = "time.end";
    public static final String TIME_SHIFT_HEAD = "time.shift.head";
    public static final String TIME_SHIFT_TAIL = "time.shift.tail";
    public static final String TIME_WINDOW = "time.window";

    public static final String DISTANCE_FUNCTION = "distance.function";

    public static final String MATRIX_BLOCK_SIZE = "matrix.block.size";

    public class Histogram {
        public static final String MIN = "histogram.min";
        public static final String MAX = "histogram.max";
        public static final String NO_OF_BINS = "histogram.bins";
    }

    public class PViz {
        public static final String DIR = "pviz.dir";
        public static final String CLUSTER_FILE = "pviz.cluster.file";
        public static final String PVIZ_FILE = "pviz.file";
    }

    public class Label {
        public static final String DIR = "label.dir";
    }

    private final Map conf;

    private String basePath;

    public TSConfiguration(String file) {
        try {
            this.conf = (Map) Yaml.load(new File(file));
            this.basePath = getString(BASE_PATH);

            if (this.basePath == null) {
                throw new RuntimeException("Basepath must be specified");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to load configuration from file: " + file, e);
        }
    }

    public Map getConf() {
        return conf;
    }

    public Integer getInt(String key) {
        return (Integer) conf.get(key);
    }

    public String getString(String key) {
        return (String) conf.get(key);
    }

    public String getAggregatedPath(String path) {
        return basePath + "/" + path;
    }

    public String getVectorDir() {
        String preprocDir = getString(PREPROC_DIR);
        String vector = getString(VECTOR_DIR);

        return getAggregatedPath(preprocDir) + "/" + vector;
    }

    public String getDistDir() {
        String preprocDir = getString(PREPROC_DIR);
        String distance = getString(DISTANCE_DIR);

        return getAggregatedPath(preprocDir) + "/" + distance;
    }

    public String getInterMediateDistanceDir() {
        String preprocDir = getString(PREPROC_DIR);
        String distance = getString(DISTANCE_DIR);
        String intermediate = getString(INTERMEDIATE_DIR);

        return getAggregatedPath(preprocDir) + "/" + intermediate + "/" + distance;
    }

    public String getInterMediateVectorDir() {
        String preprocDir = getString(PREPROC_DIR);
        String vector = getString(VECTOR_DIR);
        String intermediate = getString(INTERMEDIATE_DIR);

        return getAggregatedPath(preprocDir) + "/" + intermediate + "/" + vector;
    }

    public String getIntemediateHistDir() {
        String preprocDir = getString(PREPROC_DIR);
        String hist = getString(HIST_DIR);
        String intermediate = getString(INTERMEDIATE_DIR);
        return getAggregatedPath(preprocDir) + "/" + intermediate + "/" + hist;
    }

    public String getHistDir() {
        String preprocDir = getString(PREPROC_DIR);
        String hist = getString(HIST_DIR);
        return getAggregatedPath(preprocDir) + "/" + hist;
    }

    public String getLabelDir() {
        String labelDir = getString(Label.DIR);
        String postProcDir = getString(POSTPROC_DIR);

        return getAggregatedPath(postProcDir + "/" + labelDir);
    }

    public String getPVizDir() {
        String postProcDir = getString(POSTPROC_DIR);
        String pvizDir = getString(PViz.DIR);

        return getAggregatedPath(postProcDir + "/" + pvizDir);
    }

    public String getClusterFile() {
        String postProcDir = getString(POSTPROC_DIR);
        String clusterFile = getString(PViz.CLUSTER_FILE);

        return getAggregatedPath(postProcDir + "/" + clusterFile);
    }
}
