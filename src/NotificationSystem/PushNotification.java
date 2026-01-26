package NotificationSystem;

public class PushNotification implements NotificationChannel {
    public void send(Notification notification) {
        System.out.println("Sending PUSH to " + notification.getUserId()
                + ": " + notification.getMessage());
    }
}
