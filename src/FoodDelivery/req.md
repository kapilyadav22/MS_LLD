Food Delivery App (Zomato/Swiggy) — LLD

A good interview-level design should cover:

User places order
Restaurant manages menu
Delivery partner assigned
Order tracking
Payment
Order states
Basic scalability/extensibility

This problem is mainly based on:

Strategy Pattern → delivery assignment, pricing
State Pattern → order lifecycle
Observer Pattern → live tracking/notifications
Singleton → services/managers
Factory (optional) → payment creation


Core Requirements
Functional Requirements

Customer
    Browse restaurants
    View menu
    Add items to cart
    Place order
    Pay
    Track order

Restaurant
    Manage menu
    Accept/reject order
    Prepare food

Delivery Partner
    Accept delivery
    Pick order
    Deliver order

