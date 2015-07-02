import java.io.*;
import java.util.*;

public class LabelApply {
    private String vectorFolder;
    private String pointsFolder;
    private String distFolder;
    private String originalStockFile;

    private void applyLabel(String inPointsFile, String outPointsFile, List<String> symbols, Map<String, List<String>> sectors) {

    }

    // first read the original stock file and load the mapping from permno to stock symbol
    private Map<Integer, String> loadMapping(String inFile) {
        BufferedReader bufRead = null;
        Map<Integer, String> maps = new HashMap<Integer, String>();
        try {
            FileReader input = new FileReader(inFile);
            bufRead = new BufferedReader(input);

            Record record;
            while ((record = Utils.parseFile(bufRead)) != null) {
                maps.put(record.getSymbol(), record.getSymbolString());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Faile to open file");
        }
        return maps;
    }

    // load symbols for each point in file
    private List<String> loadSymbols(String vectorFile, Map<Integer, String> permNoToSymbol) {
        File vf = new File(vectorFile);
        List<VectorPoint> vectorPoints = Utils.readVectors(vf, 0, 7000);
        List<String> symbols = new ArrayList<String>();
        for (int i = 0; i < vectorPoints.size(); i++) {
            VectorPoint v = vectorPoints.get(i);
            String symbol =  permNoToSymbol.get(v.getKey());
            symbols.add(symbol);
        }
        return symbols;
    }
}
