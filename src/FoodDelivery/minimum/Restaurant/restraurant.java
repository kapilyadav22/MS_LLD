package FoodDelivery.minimum.Restaurant;

import java.util.*;

/*
    RESTAURANT ACTOR - FOOD DELIVERY APP

    Covered:
    1. Manage Menu
    2. Receive Orders
    3. Accept / Reject Orders
    4. Prepare Order
    5. Order State Management

    Pattern:
    - State Pattern
*/

public class FoodDeliveryApp {
    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant(1, "Burger King");
        MenuItem burger = new MenuItem(1, "Burger", 150);
        MenuItem fries = new MenuItem(2, "Fries", 100);

        restaurant.addMenuItem(burger);
        restaurant.addMenuItem(fries);
        restaurant.showMenu();

        List<MenuItem> items = new ArrayList<>();
        items.add(burger);
        items.add(fries);

        Order order = new Order(1, items);

        restaurant.receiveOrder(order);
        restaurant.acceptOrder(order);
        restaurant.prepareOrder(order);
        restaurant.readyForPickup(order);
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
        System.out.println("\n----- MENU -----");
        for (MenuItem item : menu) {
            System.out.println(item.getName() + " - " + item.getPrice());
        }
    }

    public void receiveOrder(Order order) {
        System.out.println("\nRestaurant received order : " + order.getId());
    }

    public void acceptOrder(Order order) {
        order.nextState();
        System.out.println("Restaurant accepted order.");
    }

    public void prepareOrder(Order order) {
        order.nextState();
        System.out.println("Restaurant preparing order.");
    }

    public void readyForPickup(Order order) {
        order.nextState();
        System.out.println("Order ready for pickup.");
    }
}

// ORDER STATUS

enum OrderStatus {
    CREATED,
    ACCEPTED,
    PREPARING,
    READY_FOR_PICKUP,
    CANCELLED
}

// STATE PATTERN

interface OrderState {
    void next(Order order);

    OrderStatus getStatus();
}

class CreatedState implements FoodDelivery.minimum.dp.OrderState {
    @Override
    public void next(Order order) {
        order.setState(new AcceptedState());
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CREATED;
    }
}

class AcceptedState implements FoodDelivery.minimum.dp.OrderState {
    @Override
    public void next(Order order) {
        order.setState(new PreparingState());
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.ACCEPTED;
    }
}

class PreparingState implements FoodDelivery.minimum.dp.OrderState {
    @Override
    public void next(Order order) {
        order.setState(new FoodDelivery.minimum.dp.ReadyForPickupState());
    }

    @Override
    public void next(FoodDelivery.minimum.customer.Order order) {
        System.out.println("");
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PREPARING;
    }
}

class ReadyForPickupState implements FoodDelivery.minimum.dp.OrderState {
    @Override
    public void next(Order order) {
        System.out.println("Order already ready.");
    }

    @Override
    public void next(FoodDelivery.minimum.customer.Order order) {

    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.READY_FOR_PICKUP;
    }
}

// ORDER

class Order {
    private int id;
    private List<MenuItem> items;
    private FoodDelivery.minimum.dp.OrderState state;

    public Order(int id, List<MenuItem> items) {
        this.id = id;
        this.items = items;
        this.state = new CreatedState();
    }

    public void nextState() {
        System.out.println("\nCURRENT STATE : " + state.getStatus());
        state.next(this);
        System.out.println("NEW STATE     : " + state.getStatus());
    }

    public void setState(FoodDelivery.minimum.dp.OrderState state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }
}
