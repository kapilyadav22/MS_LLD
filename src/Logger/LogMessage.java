package Logger;

public class LogMessage {
    final LogLevel level;
    final String message;
    final long timestamp;
    final String threadName;

    LogMessage(LogLevel level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.threadName = Thread.currentThread().getName();
    }
}
