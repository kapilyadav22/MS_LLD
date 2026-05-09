import java.util.*;

// -------------------- ENUM --------------------
enum ChannelType {
    EMAIL,
    SMS,
    PUSH
}

// -------------------- NOTIFICATION ENTITY --------------------
class Notification {
    private String id;
    private String message;
    private ChannelType type;

    public Notification(String id, String message, ChannelType type) {
        this.id = id;
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public ChannelType getType() {
        return type;
    }
}

// -------------------- STRATEGY --------------------
interface NotificationChannel {
    void send(String userName, String message);
}

// -------------------- CHANNEL IMPLEMENTATIONS --------------------
class EmailChannel implements NotificationChannel {
    public void send(String userName, String message) {
        System.out.println("[EMAIL] to " + userName + ": " + message);
    }
}

class SMSChannel implements NotificationChannel {
    public void send(String userName, String message) {
        System.out.println("[SMS] to " + userName + ": " + message);
    }
}

class PushChannel implements NotificationChannel {
    public void send(String userName, String message) {
        System.out.println("[PUSH] to " + userName + ": " + message);
    }
}

// -------------------- FACTORY --------------------
class NotificationFactory {
    public static NotificationChannel createChannel(ChannelType type) {
        switch (type) {
            case EMAIL: return new EmailChannel();
            case SMS: return new SMSChannel();
            case PUSH: return new PushChannel();
        }
        return null;
    }
}

// -------------------- OBSERVER --------------------
interface Observer {
    void update(Notification notification);
}

// -------------------- USER --------------------
class User implements Observer {
    private String name;
    private Map<ChannelType, NotificationChannel> subscriptions = new HashMap<>();

    public User(String name) {
        this.name = name;
    }

    public void subscribe(ChannelType type, NotificationChannel channel) {
        subscriptions.put(type, channel);
    }

    public void unsubscribe(ChannelType type) {
        subscriptions.remove(type);
    }

    @Override
    public void update(Notification notification) {
        NotificationChannel channel = subscriptions.get(notification.getType());
        if (channel != null) {
            channel.send(name, notification.getMessage());
        }
    }
}

// -------------------- SUBJECT --------------------
class NotificationService {
    private List<Observer> observers = new ArrayList<>();
    private Map<ChannelType, NotificationChannel> channelCache = new HashMap<>();

    public void register(Observer o) {
        observers.add(o);
    }

    public void unregister(Observer o) {
        observers.remove(o);
    }

    private NotificationChannel getChannel(ChannelType type) {
        if (!channelCache.containsKey(type)) {
            channelCache.put(type, NotificationFactory.createChannel(type));
        }
        return channelCache.get(type);
    }

    public void subscribeUser(User user, ChannelType type) {
        user.subscribe(type, getChannel(type));
    }
    public void unSubscribeUser(User user, ChannelType type) {
        user.unsubscribe(type);
    }

    public void sendNotification(Notification notification) {
        for (Observer o : observers) {
            o.update(notification);
        }
    }
}

// -------------------- MAIN --------------------
public class Main {
    public static void main(String[] args) {

        NotificationService service = new NotificationService();

        // -------- USERS (ONLY 2) --------
        User user1 = new User("Alice");
        User user2 = new User("Bob");

        // -------- REGISTER USERS --------
        service.register(user1);
        service.register(user2);

        // -------- SUBSCRIPTIONS --------
        service.subscribeUser(user1, ChannelType.EMAIL);
        service.subscribeUser(user1, ChannelType.SMS);

        service.subscribeUser(user2, ChannelType.PUSH);

        // -------- SEND NOTIFICATIONS --------
        System.out.println("---- EMAIL ----");
        service.sendNotification(new Notification("1", "Welcome!", ChannelType.EMAIL) );

        System.out.println("---- SMS ----");
        service.sendNotification( new Notification("2", "OTP 1234", ChannelType.SMS));

        System.out.println("---- PUSH ----");
        service.sendNotification(new Notification("3", "New Alert!", ChannelType.PUSH) );
    }
}