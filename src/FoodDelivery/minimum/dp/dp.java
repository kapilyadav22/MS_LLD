package FoodDelivery.minimum.dp;

import java.util.*;

/*
    DELIVERY PARTNER ACTOR - FOOD DELIVERY APP

    Covered:
    1. Assign Delivery Partner
    2. Pickup Order
    3. Deliver Order
    4. Delivery Partner Availability
    5. Delivery State Management

    Pattern:
    - Strategy Pattern (Delivery Assignment)
    - State Pattern (Delivery Lifecycle)
*/

public class FoodDeliveryApp {
    public static void main(String[] args) {
        List<DeliveryPartner> partners = new ArrayList<>();
        partners.add(new DeliveryPartner(1, "Rahul"));
        partners.add(new DeliveryPartner(2, "Aman"));

        FoodDelivery.minimum.customer.Order order = new FoodDelivery.minimum.customer.Order(101);

        DeliveryStrategy strategy = new NearestPartnerStrategy();
        DeliveryPartner partner = strategy.assignPartner(partners);

        order.assignPartner(partner);
        System.out.println("\nAssigned Partner : " + partner.getName());

        partner.pickupOrder(order);
        partner.deliverOrder(order);
    }
}

// DELIVERY PARTNER

class DeliveryPartner {
    private int id;
    private String name;
    private boolean available;

    public DeliveryPartner(int id, String name) {
        this.id = id;
        this.name = name;
        this.available = true;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getName() {
        return name;
    }

    public void assignOrder() {
        available = false;
    }

    public void completeOrder() {
        available = true;
    }

    public void pickupOrder(FoodDelivery.minimum.customer.Order order) {
        order.nextState();
        System.out.println("Partner picked up order.");
    }

    public void deliverOrder(FoodDelivery.minimum.customer.Order order) {
        order.nextState();
        completeOrder();
        System.out.println("Order delivered successfully.");
    }
}

// DELIVERY STRATEGY

interface DeliveryStrategy {
    DeliveryPartner assignPartner(List<DeliveryPartner> partners);
}

class NearestPartnerStrategy implements DeliveryStrategy {
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

// ORDER STATUS

enum OrderStatus {
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED
}

// STATE PATTERN

public interface OrderState {
    void next(FoodDelivery.minimum.customer.Order order);

    FoodDelivery.minimum.customer.OrderStatus getStatus();
}

public class ReadyForPickupState implements OrderState {
    @Override
    public void next(FoodDelivery.minimum.customer.Order order) {
        order.setState(new OutForDeliveryState());
    }

    @Override
    public FoodDelivery.minimum.customer.OrderStatus getStatus() {
        return FoodDelivery.minimum.customer.OrderStatus.READY_FOR_PICKUP;
    }
}

class OutForDeliveryState implements OrderState {
    @Override
    public void next(FoodDelivery.minimum.customer.Order order) {
        order.setState(new DeliveredState());
    }

    @Override
    public FoodDelivery.minimum.customer.OrderStatus getStatus() {
        return FoodDelivery.minimum.customer.OrderStatus.OUT_FOR_DELIVERY;
    }
}

class DeliveredState implements OrderState {
    @Override
    public void next(FoodDelivery.minimum.customer.Order order) {
        System.out.println("Order already delivered.");
    }

    @Override
    public FoodDelivery.minimum.customer.OrderStatus getStatus() {
        return FoodDelivery.minimum.customer.OrderStatus.DELIVERED;
    }
}

// ORDER

class Order {
    private int id;
    private DeliveryPartner partner;
    private OrderState state;

    public Order(int id) {
        this.id = id;
        this.state = new ReadyForPickupState();
    }

    public void assignPartner(DeliveryPartner partner) {
        this.partner = partner;
    }

    public void nextState() {
        System.out.println("\nCURRENT STATE : " + state.getStatus());
        state.next(this);
        System.out.println("NEW STATE     : " + state.getStatus());
    }

    public void setState(OrderState state) {
        this.state = state;
    }
}
