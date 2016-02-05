package edu.indiana.soic.ts.dist.impl;

import edu.indiana.soic.ts.dist.DistanceFunction;
import edu.indiana.soic.ts.utils.VectorPoint;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LogDistance implements DistanceFunction {
    private static Logger LOG = LoggerFactory.getLogger(LogDistance.class);

    public void prepare(Map conf) {

    }

    public double calc(VectorPoint v1, VectorPoint v2) {
        if (type == 5) {
            double c = corr(v1, v2);
            double l1 = vectorLength(1, v1);
            double l2 = vectorLength(1, v2);
            double sqrt = Math.sqrt(Math.pow(l1, 2) + Math.pow(l2, 2) - 2 * l1 * l2 * c);
            if (sqrt == 0) {
            } else if (sqrt > 100000) {
                System.out.println("Sqrt = " + sqrt);
            }
            return sqrt;
        } else {
            double c = corr(v1, v2);
            double l1 = 1;
            double l2 = 1;
            return Math.sqrt(Math.pow(l1, 2) + Math.pow(l2, 2) - 2 * l1 * l2 * c);
        }
    }

    public static double vectorLength(int type, VectorPoint vp) {
        if (vp.getKey() == 0) return 0;

        double change = vp.change();
        if (type == 1) {
            return 10 * Math.abs(Math.log(change));
        } else if (type == 2) {
            return 10 * (change > 1 ? (change - 1) : (1 / change) - 1);
        } else if (type == 3) {
            SimpleRegression regression = new SimpleRegression();
            for (int i = 0; i < vp.getElements(); i++) {
                regression.addData(i, vp.getNumbers()[i]);
            }
            return regression.getSlope();
        }
        return 0;
    }

    public double corr(VectorPoint v1, VectorPoint v2) {
        final int start = 1;
        double []xs;
        double []ys;
        ys = v2.getNumbers();
        xs = v1.getNumbers();

        if (v1.getKey() == v2.getKey()) {
            // both are same so return 1
            return 1;
        }

        if (v1.getKey() == 0 || v2.getKey() == 0) {
            return 0;
        }

        double point1Delta = 0.01/250;
        double alpha10 = 0.1;
        double alpha10Day = alpha10 / 250;
        double alpha20 = 0.2;
        double alpha20Day = alpha20 / 250;

        int noOfElements = v2.noOfElements() >  1 ? v2.noOfElements() : 100;
        if (v1.getKey() < 10) {
            if (v2.getKey() < 10) {
                xs = new double[noOfElements];
            } else {
                xs = new double[ys.length];
            }

            double bd = 1.0;
            double ed = 0.0;

            if (v1.getKey() == 0) {
                for (int i = 0; i < xs.length; i++) {
                    if (i % 2 == 0) {
                        xs[i] = start + point1Delta;
                    } else {
                        xs[i] = start - point1Delta;
                    }
                }
            }
            else if (v1.getKey() == 1) {
                for (int i = 0; i < xs.length; i++) {
                    ed = (1 + alpha10Day) * bd;
                    bd = ed;
                    xs[i] = ed;
                }
            }
            else if (v1.getKey() == 2) {
                for (int i = 0; i < xs.length; i++) {
                    ed = (1 + alpha20Day) * bd;
                    bd = ed;
                    xs[i] = ed;
                }
            }
            else if (v1.getKey() == 3) {
                for (int i = 0; i < xs.length; i++) {
                    ed = bd / (1 + alpha10Day);
                    bd = ed;
                    xs[i] = ed;
                }
            }
            else if (v1.getKey() == 4) {
                for (int i = 0; i < xs.length; i++) {
                    ed = bd / (1 + alpha20Day);
                    bd = ed;
                    xs[i] = ed;
                }
            }
        }

        if (v2.getKey() < 10) {
            if (v1.getKey() < 10) {
                ys = new double[noOfElements];
            } else {
                ys = new double[xs.length];
            }

            double bd = 1.0;
            double ed = 0.0;

            if (v2.getKey() == 0) {
                for (int i = 0; i < ys.length; i++) {
                    if (i % 2 == 0) {
                        ys[i] = start + point1Delta;
                    } else {
                        ys[i] = start - point1Delta;
                    }
                }
            }

            if (v2.getKey() == 1) {
                for (int i = 0; i < ys.length; i++) {
                    ed = (1 + alpha10Day) * bd;
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (v2.getKey() == 2) {
                for (int i = 0; i < ys.length; i++) {
                    ed = (1 + alpha20Day) * bd;
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (v2.getKey() == 3) {
                for (int i = 0; i < ys.length; i++) {
                    ed = bd / (1 + alpha10Day);
                    bd = ed;
                    ys[i] = ed;
                }
            }
            if (v2.getKey() == 4) {
                for (int i = 0; i < ys.length; i++) {
                    ed = bd / (1 + alpha20Day);
                    bd = ed;
                    ys[i] = ed;
                }
            }
        }

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        try {
            return pearsonsCorrelation.correlation(xs, ys);
        } catch (Exception e) {
            System.out.println(xs.length + " " + ys.length);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
