package Logger;

public class DatabaseAppender implements LogAppender {

    @Override
    public void append(LogMessage message) {
        // DB connection pool is thread-safe
    }
}
