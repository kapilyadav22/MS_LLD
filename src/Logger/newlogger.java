import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

// ---------------- ENUM ----------------
enum LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

// ---------------- LOG MESSAGE ----------------
class LogMessage {
    private LogLevel level;
    private String message;
    private LocalDateTime timestamp;

    public LogMessage(LogLevel level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

// ---------------- FORMATTER ----------------
interface Formatter {
    String format(LogMessage log);
}

class SimpleFormatter implements Formatter {

    @Override
    public String format(LogMessage log) {
        return "[" + log.getTimestamp() + "] "
                + "[" + log.getLevel() + "] "
                + log.getMessage();
    }
}

// ---------------- APPENDER ----------------
abstract class Appender {

    protected Formatter formatter;

    public Appender(Formatter formatter) {
        this.formatter = formatter;
    }

    abstract void append(LogMessage log);
}

// ---------------- CONSOLE APPENDER ----------------
class ConsoleAppender extends Appender {

    public ConsoleAppender(Formatter formatter) {
        super(formatter);
    }

    @Override
    void append(LogMessage log) {
        System.out.println(formatter.format(log));
    }
}

// ---------------- FILE APPENDER ----------------
class FileAppender extends Appender {

    private String filename;

    public FileAppender(String filename, Formatter formatter) {
        super(formatter);
        this.filename = filename;
    }

    @Override
    void append(LogMessage log) {

        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write(formatter.format(log) + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// ---------------- LOGGER ----------------
class Logger {

    private List<Appender> appenders = new ArrayList<>();
    private LogLevel minLevel;

    public Logger(LogLevel minLevel) {
        this.minLevel = minLevel;
    }

    public void addAppender(Appender appender) {
        appenders.add(appender);
    }

    public void log(LogLevel level, String message) {

        if (level.compareTo(minLevel) < 0) {
            return;
        }

        LogMessage logMessage = new LogMessage(level, message);

        for (Appender appender : appenders) {
            appender.append(logMessage);
        }
    }
}

// ---------------- FACTORY ----------------
class LoggerFactory {

    public static Logger createLogger(LogLevel level, String filename) {

        Formatter formatter = new SimpleFormatter();

        Logger logger = new Logger(level);

        logger.addAppender(new ConsoleAppender(formatter));
        logger.addAppender(new FileAppender(filename, formatter));

        return logger;
    }
}

// ---------------- MAIN ----------------
public class Main {

    public static void main(String[] args) {

        Logger logger =
                LoggerFactory.createLogger(LogLevel.INFO, "logs.txt");

        logger.log(LogLevel.DEBUG, "Debugging system");
        logger.log(LogLevel.INFO, "System started");
        logger.log(LogLevel.WARN, "Low memory");
        logger.log(LogLevel.ERROR, "Crash detected");
        logger.log(LogLevel.INFO, "User logged in");
    }
}

//public class Main {
//
//    static LogLevel parseLevel(String level) {
//        return LogLevel.valueOf(level);
//    }
//
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//
//        int n = sc.nextInt();
//        String minLevelStr = sc.next();
//        String filename = sc.next();
//
//        Logger logger = LoggerFactory.createLogger(parseLevel(minLevelStr), filename);
//
//        sc.nextLine(); // consume newline
//
//        while (n-- > 0) {
//            String levelStr = sc.next();
//            String message = sc.nextLine().trim();
//
//            logger.log(parseLevel(levelStr), message);
//        }
//
//        sc.close();
//    }
//}