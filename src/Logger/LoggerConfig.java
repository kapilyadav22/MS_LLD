package Logger;

public class LoggerConfig {

    private final LogLevel minLogLevel;
    private final LogAppender appender;

    public LoggerConfig(LogLevel minLogLevel, LogAppender appender) {
        this.minLogLevel = minLogLevel;
        this.appender = appender;
    }

    public LogLevel getMinLogLevel() {
        return minLogLevel;
    }

    public LogAppender getAppender() {
        return appender;
    }
}
