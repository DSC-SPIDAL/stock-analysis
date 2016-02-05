package edu.indiana.soic.ts.dist;

import edu.indiana.soic.ts.utils.VectorPoint;

import java.util.Map;

public interface DistanceFunction {
    void prepare(Map conf);
    double calc(VectorPoint v1, VectorPoint v2);
}
