package NotificationSystem;

public class Main {
    public static void main(String[] args) {

        NotificationChannelFactory factory = new NotificationChannelFactory();
        NotificationService service = new NotificationService(factory);

        Notification notification =
                new Notification("Kapil Yadav", "Your order is shipped!");

        service.send("EMAIL", notification);
        service.send("SMS", notification);
        service.send("PUSH", notification);
    }
}
