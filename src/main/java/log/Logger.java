package log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static Logger logger;
    private static final String PATH = "./src/main/java/log/log.txt";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Logger getLogger() {
        if(logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public boolean log (String message) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(PATH, true));
            writer.write(LocalDateTime.now().format(formatter) + " " + message + "\n");
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}