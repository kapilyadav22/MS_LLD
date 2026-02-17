package Logger;

public class ConsoleAppender implements LogAppender {
    @Override
    public void append(LogMessage message) {
        System.out.println(
                message.getTimestamp() + " [" +
                        message.getThreadName() + "] " +
                        message.getLevel() + " - " +
                        message.getMessage()
        );
    }
}
