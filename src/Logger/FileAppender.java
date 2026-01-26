package Logger;

public class FileAppender implements LogAppender {
    public synchronized void append(LogMessage message) {
        // write to file (simplified)
    }
}


//👉 synchronized here is fine — background thread only.