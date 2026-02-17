package Tier2.observerp;

import java.util.ArrayList;
import java.util.List;

interface Observer {
    void update(String symbol, double price);
}

interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

class Stock implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String symbol;
    private double price;

    public Stock(String symbol) {
        this.symbol = symbol;
    }

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    public void setPrice(double price) {
        this.price = price;
        notifyObservers();  // Price changed, tell everyone
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(symbol, price);
        }
    }
}

class PriceDisplay implements Observer {
    public void update(String symbol, double price) {
        System.out.println("Display updated: " + symbol + " = $" + price);
    }
}

class PriceAlert implements Observer {
    private double threshold;

    public PriceAlert(double threshold) {
        this.threshold = threshold;
    }

    public void update(String symbol, double price) {
        if (price > threshold) {
            System.out.println("Alert! " + symbol + " exceeded $" + threshold);
        }
    }
}

// Usage



