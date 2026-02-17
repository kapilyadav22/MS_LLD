package Logger;

enum AppenderType {
    CONSOLE,
    FILE,
    DATABASE
}


class AppenderFactory {

    public static LogAppender create(AppenderType type) {
        switch (type) {
            case CONSOLE:
                return new ConsoleAppender();
            case FILE:
                return new FileAppender();
            case DATABASE:
                return new DatabaseAppender();
            default:
                throw new IllegalArgumentException("Unknown appender type");
        }
    }
}


public class Main {
    public static void main(String[] args) {
        LogAppender appender =  AppenderFactory.create(AppenderType.DATABASE);
        LoggerConfig config = new LoggerConfig(LogLevel.INFO,appender);
//        Logger logger = Logger.getInstance(config);

        Logger logger =  Logger.getInstance();
        logger.setConfig(config);

        logger.log(LogLevel.DEBUG, "Ignored");
        logger.log(LogLevel.ERROR, "Something failed");

        logger.log(LogLevel.INFO, "Service started");
        logger.log(LogLevel.DEBUG, "This will be ignored");

    }
}
