package log;

import org.junit.Assert;
import org.junit.Test;

public class LoggerTest {

    private static final Logger LOGGER = Logger.getLogger();
    private static final String MESSAGE = "Testing message.";

    @Test
    public void getLoggerTest() {
        Assert.assertEquals(Logger.getLogger(), LOGGER);
    }

    @Test
    public void logTest() {
        Assert.assertTrue(LOGGER.log(MESSAGE));
    }
}
