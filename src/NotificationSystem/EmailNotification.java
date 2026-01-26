package NotificationSystem;

public class EmailNotification implements NotificationChannel {
    public void send(Notification notification) {
        System.out.println("Sending EMAIL to " + notification.getUserId()
                + ": " + notification.getMessage());
    }
}
