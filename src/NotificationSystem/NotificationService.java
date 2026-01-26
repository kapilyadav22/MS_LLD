package NotificationSystem;

public class NotificationService {

    private final NotificationChannelFactory factory;

    public NotificationService(NotificationChannelFactory factory) {
        this.factory = factory;
    }

    public void send(String channelType, Notification notification) {
        NotificationChannel channel = factory.getChannel(channelType);
        channel.send(notification);
    }
}
