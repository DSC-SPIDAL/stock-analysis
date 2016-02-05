package edu.indiana.soic.ts.utils;

import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class TSConfiguration {
    public static final String BASE_PATH = "base.path";

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

    public Integer getInt(String key) {
        return (Integer) conf.get(key);
    }

    public String getString(String key) {
        return (String) conf.get(key);
    }

    public String getAggregatedPath(String path) {
        return basePath + "/" + getString(path);
    }
}
