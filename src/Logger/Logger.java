package Logger;

import java.util.concurrent.*;
import java.util.concurrent.*;

/*start with synchronous logging,
If this is a high-throughput service,
I’d switch to async logging using a background consumer and graceful shutdown.”
 */

public class Logger {

//    private static volatile Logger instance;

//    private final BlockingQueue<LogMessage> queue;
    private LoggerConfig config;
//    private final ExecutorService consumerExecutor;

    public void setConfig(LoggerConfig config){
        this.config  = config;
    }

//    private Logger(LoggerConfig config) {
//        this.queue = new ArrayBlockingQueue<>(10_000); // bounded
//        this.config = config;
//        this.consumerExecutor = Executors.newSingleThreadExecutor();
//        startConsumer();
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> Logger.getInstance(config).shutdown())
//        );
//    }

     private static class SingletonHolder{
        private static final Logger instance= new Logger();
     }

    public static Logger getInstance(){
        return SingletonHolder.instance;
    }

    // MULTI-THREAD SAFE
    public void log(LogLevel level, String message) {
        if (level.getSeverity() < config.getMinLogLevel().getSeverity()) {
            return;
        }
        LogMessage logMessage = new LogMessage(level, message);
        config.getAppender().append(logMessage);

        /*
         boolean offered = queue.offer(new LogMessage(level, message));
            if (!offered) {
                // queue full → drop or metric
            }
         */

    }

    /*
    private void startConsumer() {
        consumerExecutor.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    LogMessage msg = queue.take();
                    config.getAppender().append(msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
*/

    /*
    // SAFE SHUTDOWN
    public void shutdown() {
        consumerExecutor.shutdown();
        try {

            while (!consumerExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                // wait
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
     */

}
