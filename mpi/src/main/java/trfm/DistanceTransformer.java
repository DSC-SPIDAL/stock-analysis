package trfm;

import edu.indiana.soic.spidal.common.TransformationFunction;

public class DistanceTransformer implements TransformationFunction {
    @Override
    public double transform(double val) {
//        double convert = 2 - 2 * val;
//        return Math.sqrt(2 * (1 - convert));
        return val;
    }
}
