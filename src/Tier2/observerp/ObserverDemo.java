package Tier2.observerp;

public class ObserverDemo {
    public static void main(String[] args) {
        Stock stock = new Stock("AAPL");

        PriceDisplay display = new PriceDisplay();
        PriceAlert alert = new PriceAlert(150.00);

        stock.attach(display);
        stock.attach(alert);

        stock.setPrice(145.00);  // Both observers get notified
        stock.setPrice(155.00);  // Both observers get notified
    }
}
