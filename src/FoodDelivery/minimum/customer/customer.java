package FoodDelivery.minimum.customer;

import java.util.*;

/*
    CUSTOMER ACTOR - FOOD DELIVERY APP

    Covered:
    1. Browse Restaurant Menu
    2. Add Items To Cart
    3. Place Order
    4. Payment Strategy
    5. Order Lifecycle

    Patterns:
    - Strategy Pattern (Payment)
*/

public class FoodDeliveryApp {
    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant(1, "Burger King");
        MenuItem burger = new MenuItem(1, "Burger", 150);
        MenuItem fries = new MenuItem(2, "Fries", 100);

        restaurant.addMenuItem(burger);
        restaurant.addMenuItem(fries);
        restaurant.showMenu();

        Customer customer = new Customer(1, "Kapil");
        customer.addToCart(burger, 2);
        customer.addToCart(fries, 1);

        Payment payment = new UPIPayment();

        Order order = customer.placeOrder(payment);

        System.out.println("\nFinal Order Status : " + order.getStatus());
    }
}

// CUSTOMER

class Customer {
    private int id;
    private String name;
    private Cart cart;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
        this.cart = new Cart();
    }

    public void addToCart(MenuItem item, int quantity) {
        cart.addItem(item, quantity);
    }

    public Order placeOrder(Payment payment) {
        double total = cart.getTotalAmount();
        payment.pay(total);

        Order order = new Order(cart.getItems());
        System.out.println("\nOrder placed successfully.");
        return order;
    }
}

// RESTAURANT

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

    public void showMenu() {
        System.out.println("\n----- MENU : " + name + " -----");
        for (MenuItem item : menu) {
            System.out.println(item.getName() + " - " + item.getPrice());
        }
    }
}

// MENU ITEM

class MenuItem {
    private int id;
    private String name;
    private double price;

    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

// CART ITEM

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

// CART

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

// ORDER

enum OrderStatus {
    CREATED,
    CONFIRMED
}

public class Order {
    private static int idCounter = 1;
    private int id;
    private List<CartItem> items;
    private OrderStatus status;

    public Order(List<CartItem> items) {
        this.id = idCounter++;
        this.items = items;
        this.status = OrderStatus.CONFIRMED;
    }

    public OrderStatus getStatus() {
        return status;
    }
}

// PAYMENT STRATEGY

interface Payment {
    void pay(double amount);
}

class UPIPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("\nPayment of " + amount + " done via UPI.");
    }
}

class CardPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("\nPayment of " + amount + " done via CARD.");
    }
}
