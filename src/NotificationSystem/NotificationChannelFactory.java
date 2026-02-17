package NotificationSystem;
import java.util.HashMap;
import java.util.Map;

public class NotificationChannelFactory  {

    private final Map<String, INotificationChannel> channels = new HashMap<>();

    public NotificationChannelFactory() {
        channels.put("EMAIL", new EmailNotification());
        channels.put("SMS", new SmsNotification());
        channels.put("PUSH", new PushNotification());
    }

    public INotificationChannel getChannel(String type) {
        if (!channels.containsKey(type)) {
            throw new IllegalArgumentException("Unsupported channel: " + type);
        }
        return channels.get(type);
    }
}
