package NotificationSystem;

public class PushNotification implements INotificationChannel {
    public void send(Notification notification) {
        System.out.println("Sending PUSH to " + notification.getUserId()
                + ": " + notification.getMessage());
    }
}
