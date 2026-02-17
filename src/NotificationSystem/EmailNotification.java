package NotificationSystem;

public class EmailNotification implements INotificationChannel {
    public void send(Notification notification) {
        System.out.println("Sending EMAIL to " + notification.getUserId()
                + ": " + notification.getMessage());
    }
}
