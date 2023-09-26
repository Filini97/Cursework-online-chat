package config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigTest {
    private static final Config CONFIG = Config.getInstance();
    protected int port;
    protected String host;
    private static final String PATH = "./config/settings.properties";

    @Test
    public void getInstanceTest() {
        Assert.assertEquals(Config.getInstance(), CONFIG);
    }

    @Before
    public void setup() {
        try (FileReader fileReader = new FileReader(PATH)) {
            Properties props = new Properties();
            props.load(fileReader);
            port = Integer.parseInt(props.getProperty("port"));
            host = props.getProperty("host");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPortTest() {
        Assert.assertEquals(CONFIG.getPort(), port);
    }

    @Test
    public void getHostTest() {
        Assert.assertEquals(CONFIG.getHost(), host);
    }
}
