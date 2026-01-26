package Logger;

public class ConsoleAppender implements LogAppender {
    public void append(LogMessage message) {
        System.out.println(
                message.timestamp + " [" + message.threadName + "] "
                        + message.level + " - " + message.message
        );
    }
}
