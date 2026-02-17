package Logger;

public class FileAppender implements LogAppender {

    @Override
    public synchronized void append(LogMessage message) {
        // file write
    }
}


//👉 synchronized here is fine — background thread only.