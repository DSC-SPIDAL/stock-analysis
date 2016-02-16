package edu.indiana.soic.ts.utils;

import java.util.ArrayList;
import java.util.List;

public class Bin {
    public int index;
    public double start;
    public double end;
    public List<Integer> permNos = new ArrayList<Integer>();
    public List<String> symbols = new ArrayList<String>();

    public String serializeSymbols() {
        StringBuilder sb = new StringBuilder();
        sb.append(start).append(",").append(end);
        for (String s : symbols) {
            sb.append(",").append(s);
        }
        return sb.toString();
    }

    public String serializePermNos() {
        StringBuilder sb = new StringBuilder();
        for (Integer s : permNos) {
            sb.append(",").append(s);
        }
        return sb.toString();
    }
}
