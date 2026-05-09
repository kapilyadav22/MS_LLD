package FoodDelivery;

import java.util.*;

/*
    FOOD DELIVERY APP (Swiggy/Zomato) - LLD

    Covered:
    1. Strategy Pattern
       - Delivery Assignment
       - Payment

    2. State Pattern
       - Order Lifecycle

    3. Observer Pattern
       - Notifications

    4. SOLID Principles
       - Separation of responsibilities
       - Extensible design

    Core Flow:
    Customer -> Cart -> Order -> Payment
    -> Restaurant accepts
    -> Delivery Partner assigned
    -> Order Delivered
*/

public class FoodDeliveryApp {
    public static void main(String[] args) {
        // ---------------- RESTAURANT ----------------
        Restaurant restaurant = new Restaurant(1, "Burger King");
        MenuItem burger = new MenuItem(1, "Burger", 150);
        MenuItem fries = new MenuItem(2, "Fries", 100);
        restaurant.addMenuItem(burger);
        restaurant.addMenuItem(fries);

        // ---------------- CUSTOMER ----------------
        Customer customer = new Customer(1, "Kapil");
        customer.getCart().addItem(burger, 2);
        customer.getCart().addItem(fries, 1);

        // ---------------- DELIVERY PARTNERS ----------------
        List<DeliveryPartner> partners = new ArrayList<>();
        partners.add(new DeliveryPartner(1, "Rahul"));
        partners.add(new DeliveryPartner(2, "Aman"));

        // ---------------- STRATEGIES ----------------
        DeliveryStrategy deliveryStrategy = new NearestDeliveryStrategy();
        Payment payment = new UPIPayment();

        // ---------------- ORDER SERVICE ----------------
        OrderService orderService = new OrderService(deliveryStrategy);
        Order order = orderService.placeOrder(customer, restaurant, payment, partners);

        // ---------------- ORDER FLOW ----------------
        order.nextState();
        order.nextState();
        order.nextState();
        order.nextState();
    }
}

// ======================================================
// USER
// ======================================================

abstract class User {
    protected int id;
    protected String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

// ======================================================
// CUSTOMER
// ======================================================

class Customer extends User {
    private Cart cart;

    public Customer(int id, String name) {
        super(id, name);
        this.cart = new Cart();
    }

    public Cart getCart() {
        return cart;
    }
}

// ======================================================
// DELIVERY PARTNER
// ======================================================

class DeliveryPartner extends User {
    private boolean available;

    public DeliveryPartner(int id, String name) {
        super(id, name);
        this.available = true;
    }

    public boolean isAvailable() {
        return available;
    }

    public void assignOrder() {
        available = false;
    }

    public void completeOrder() {
        available = true;
    }

    @Override
    public String toString() {
        return name;
    }
}

// ======================================================
// MENU ITEM
// ======================================================

class MenuItem {
    private int id;
    private String name;
    private double price;

    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}

// ======================================================
// RESTAURANT
// ======================================================

class Restaurant {
    private int id;
    private String name;
    private List<MenuItem> menu;

    public Restaurant(int id, String name) {
        this.id = id;
        this.name = name;
        this.menu = new ArrayList<>();
    }

    public void addMenuItem(MenuItem item) {
        menu.add(item);
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    public String getName() {
        return name;
    }
}

// ======================================================
// CART ITEM
// ======================================================

class CartItem {
    private MenuItem item;
    private int quantity;

    public CartItem(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return item.getPrice() * quantity;
    }

    public MenuItem getItem() {
        return item;
    }
}

// ======================================================
// CART
// ======================================================

class Cart {
    private List<CartItem> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public void addItem(MenuItem item, int quantity) {
        items.add(new CartItem(item, quantity));
    }

    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public List<CartItem> getItems() {
        return items;
    }
}

// ======================================================
// OBSERVER PATTERN
// ======================================================

interface Observer {
    void update(String message);
}

class CustomerNotifier implements Observer {
    @Override
    public void update(String message) {
        System.out.println("NOTIFICATION: " + message);
    }
}

// ======================================================
// ORDER STATUS
// ======================================================

enum OrderStatus {
    CREATED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

// ======================================================
// STATE PATTERN
// ======================================================

interface OrderState {
    void next(Order order);

    OrderStatus getStatus();
}

// ---------------- CREATED ----------------

class CreatedState implements OrderState {
    @Override
    public void next(Order order) {
        order.setState(new PreparingState());
        order.notifyObservers("Restaurant started preparing food.");
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CREATED;
    }
}

// ---------------- PREPARING ----------------

class PreparingState implements OrderState {
    @Override
    public void next(Order order) {
        order.setState(new OutForDeliveryState());
        order.notifyObservers("Order is out for delivery.");
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PREPARING;
    }
}

// ---------------- OUT FOR DELIVERY ----------------

class OutForDeliveryState implements OrderState {
    @Override
    public void next(Order order) {
        order.setState(new DeliveredState());
        order.notifyObservers("Order delivered successfully.");
        order.getPartner().completeOrder();
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.OUT_FOR_DELIVERY;
    }
}

// ---------------- DELIVERED ----------------

class DeliveredState implements OrderState {
    @Override
    public void next(Order order) {
        System.out.println("Order already delivered.");
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.DELIVERED;
    }
}

// ======================================================
// ORDER
// ======================================================

class Order {
    private static int idCounter = 1;
    private int id;
    private Customer customer;
    private Restaurant restaurant;
    private List<CartItem> items;
    private double totalAmount;
    private DeliveryPartner partner;
    private OrderState state;
    private List<Observer> observers;

    public Order(Customer customer, Restaurant restaurant, List<CartItem> items) {
        this.id = idCounter++;
        this.customer = customer;
        this.restaurant = restaurant;
        this.items = items;
        this.state = new CreatedState();
        this.observers = new ArrayList<>();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    public void nextState() {
        System.out.println("\nCURRENT STATE : " + state.getStatus());
        state.next(this);
        System.out.println("NEW STATE     : " + state.getStatus());
    }

    public void assignPartner(DeliveryPartner partner) {
        this.partner = partner;
    }

    public DeliveryPartner getPartner() {
        return partner;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public OrderStatus getStatus() {
        return state.getStatus();
    }
}

// ======================================================
// PAYMENT STRATEGY
// ======================================================

interface Payment {
    void pay(double amount);
}

// ---------------- UPI ----------------

class UPIPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Payment of " + amount + " done via UPI.");
    }
}

// ---------------- CARD ----------------

class CardPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Payment of " + amount + " done via CARD.");
    }
}

// ======================================================
// DELIVERY STRATEGY
// ======================================================

interface DeliveryStrategy {
    DeliveryPartner assignPartner(List<DeliveryPartner> partners);
}

// ---------------- NEAREST PARTNER ----------------

class NearestDeliveryStrategy implements DeliveryStrategy {
    @Override
    public DeliveryPartner assignPartner(List<DeliveryPartner> partners) {
        for (DeliveryPartner partner : partners) {
            if (partner.isAvailable()) {
                partner.assignOrder();
                return partner;
            }
        }
        return null;
    }
}

// ======================================================
// ORDER SERVICE
// ======================================================

class OrderService {
    private DeliveryStrategy deliveryStrategy;

    public OrderService(DeliveryStrategy deliveryStrategy) {
        this.deliveryStrategy = deliveryStrategy;
    }

    public Order placeOrder(
            Customer customer,
            Restaurant restaurant,
            Payment payment,
            List<DeliveryPartner> partners
    ) {
        Cart cart = customer.getCart();

        // PAYMENT
        payment.pay(cart.getTotalAmount());

        // CREATE ORDER
        Order order = new Order(customer, restaurant, cart.getItems());

        // OBSERVER
        order.addObserver(new CustomerNotifier());

        // ASSIGN DELIVERY PARTNER
        DeliveryPartner partner = deliveryStrategy.assignPartner(partners);
        order.assignPartner(partner);

        System.out.println("\nDelivery Partner Assigned : " + partner);
        System.out.println("Order placed successfully.");
        return order;
    }
}
