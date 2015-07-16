import java.util.ArrayList;
import java.util.List;

public class Bin {
    double start;
    double end;
    List<Integer> permNos = new ArrayList<Integer>();
    List<String> symbols = new ArrayList<>();

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
