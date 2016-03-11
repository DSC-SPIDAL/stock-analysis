import edu.indiana.soic.spidal.common.BinaryReader2D;
import edu.indiana.soic.spidal.common.Range;
import org.apache.commons.cli.*;

import java.nio.ByteOrder;

public class PrintDistanceMatrixFile {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", true, "Input");
        options.addOption("r", true, "rows"); // Destination folder
        options.addOption("np", true, "Number of rows to print");
        options.addOption("ni", true, "Number of items to to display in each row");

        CommandLineParser commandLineParser = new BasicParser();
        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            String  input = cmd.getOptionValue("i");
            int rows = Integer.parseInt(cmd.getOptionValue("r"));
            int itemRows = Integer.parseInt(cmd.getOptionValue("np"));
            int itemPerRow = Integer.parseInt(cmd.getOptionValue("ni"));

            short [][]a = BinaryReader2D.readRowRange(input, new Range( 0, rows), rows, ByteOrder.LITTLE_ENDIAN, false, null);

            for (int i = 0; i < itemRows; i++) {
                for (int j = 0; j < itemPerRow; j++) {
                    System.out.print(a[i][j] + " ,");
                }
                System.out.println();
            }
        } catch (ParseException e) {
            System.out.println(options.toString());
        }


    }
}
