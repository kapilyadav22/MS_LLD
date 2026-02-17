package NotificationSystem;


interface INotification {
    String getUserId();
    String getMessage();
}

public class Notification implements INotification {
    private final String userId;
    private final String message;

    public Notification(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    @Override
    public String getUserId() { return userId; }

    @Override
    public String getMessage() { return message; }
}
