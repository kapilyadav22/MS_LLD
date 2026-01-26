package Logger;

 import java.util.concurrent.*;

public class Logger {

    private static volatile Logger instance;

    private final BlockingQueue<LogMessage> queue;
    private final LogAppender appender;
    private final LogLevel minLevel;

    private Logger(LogAppender appender, LogLevel minLevel) {
        this.queue = new LinkedBlockingQueue<>();
        this.appender = appender;
        this.minLevel = minLevel;
        startConsumer();
    }

    public static Logger getInstance(LogAppender appender, LogLevel minLevel) {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger(appender, minLevel);
                }
            }
        }
        return instance;
    }

    public void log(LogLevel level, String message) {
        if (level.ordinal() < minLevel.ordinal()) return;
        queue.offer(new LogMessage(level, message));
    }

    private void startConsumer() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    LogMessage msg = queue.take();
                    appender.append(msg);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        //worker.setDaemon(true); //setDaemon(true) was used to avoid blocking JVM shutdown,
        worker.start();
    }
}
