package edu.indiana.soic.ts.dist.impl;

import edu.indiana.soic.ts.dist.DistanceFunction;
import edu.indiana.soic.ts.utils.VectorPoint;

import java.util.Map;

public class WeightFunction implements DistanceFunction {
    @Override
    public void prepare(Map conf) {

    }

    @Override
    public double calc(VectorPoint v1, VectorPoint v2) {
        return 0;
    }
}
