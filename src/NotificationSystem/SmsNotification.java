package NotificationSystem;

public class SmsNotification implements INotificationChannel {
    public void send(Notification notification) {
        System.out.println("Sending SMS to " + notification.getUserId()
                + ": " + notification.getMessage());
    }
}

