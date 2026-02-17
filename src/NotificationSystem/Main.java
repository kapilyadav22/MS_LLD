package NotificationSystem;


/*
Start with creating JobPostingPortal.simpleone.simple notification service, add observers later

 */
public class Main {
    public static void main(String[] args) {
        NotificationObservable observable = new NotificationObservable();
        Logger logger = new Logger(observable);

        observable.addObserver(logger);

        NotificationChannelFactory factory = new NotificationChannelFactory();
        NotificationService service = new NotificationService(factory,observable);
        Notification notification =  new Notification("Kapil Yadav", "Your order is shipped!");

        service.send("EMAIL", notification);
        service.send("SMS", notification);
        service.send("PUSH", notification);
    }
}
