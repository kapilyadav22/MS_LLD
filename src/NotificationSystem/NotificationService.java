package NotificationSystem;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private final NotificationChannelFactory factory;
    private final NotificationObservable notificationObservable;

    //If you want to store all notifications
    private final List<INotification> notifications = new ArrayList<>();

    public NotificationService(NotificationChannelFactory factory, NotificationObservable notificationObservable) {
        this.factory = factory;
        this.notificationObservable = notificationObservable;
    }

    public void send(String channelType, Notification notification) {
        INotificationChannel channel = factory.getChannel(channelType);
        channel.send(notification);
        notificationObservable.setNotification(notification);
        notifications.add(notification);
    }
}
