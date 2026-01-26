package Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance(
                new ConsoleAppender(),
                LogLevel.INFO
        );

        logger.log(LogLevel.INFO, "Service started");
        logger.log(LogLevel.DEBUG, "This will be ignored");

    }
}
