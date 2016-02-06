package edu.indiana.soic.ts.pviz;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Clusters {
    private List<Cluster>  cluster = new ArrayList<Cluster>();

    public List<Cluster> getCluster() {
        return cluster;
    }

    @XmlElement
    public void setCluster(List<Cluster> cluster) {
        this.cluster = cluster;
    }
}
