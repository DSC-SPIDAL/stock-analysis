import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The generated point files have different number of points.
 * We need to make sure that all the point files have the same stock ordering with same stocks before applying the rotations.
 */
public class PointTransformer {
    private String globalVectorFile;
    private String pointFolder;
    private String vectorFolder;

    public void process() {
        File inFolder = new File(vectorFolder);
        if (!inFolder.isDirectory()) {
            System.out.println("In should be a folder");
            return;
        }

        // first get the global vector file and its keys
        File globalFile = new File(globalVectorFile);
        TreeSet<Integer> globalKeys = Utils.readVectorKeys(globalFile);

        // go through every vector file and get the common keys
        TreeSet<Integer> commonKeys = new TreeSet<>(globalKeys);
        for (int i = 0; i < inFolder.listFiles().length; i++) {
            Set<Integer> partKeys = Utils.readVectorKeys(inFolder.listFiles()[i]);
            commonKeys.retainAll(partKeys);
        }

        // now write the common keys back as new files, we need to preserve the order as well
        Iterator<Integer> keyIterator = commonKeys.iterator();
        while (keyIterator.hasNext()) {
            Integer key = keyIterator.next();

        }
    }
}
