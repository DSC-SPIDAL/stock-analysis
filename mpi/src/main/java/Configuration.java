
public class Configuration {
    private static Configuration ourInstance = new Configuration();

    public static Configuration getInstance() {
        return ourInstance;
    }

    public double weightAdjustForConstant = 1.0;

    private Configuration() {
    }
}
