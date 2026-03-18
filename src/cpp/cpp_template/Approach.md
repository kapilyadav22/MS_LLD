1. Understand + Clarify + Scope
    What are the core features? (must-have vs good-to-have)
    What scale? (single machine vs distributed)
    Any constraints? (concurrency, latency, memory)

Example:
Ride sharing:
    Book ride? ✅
    Match driver? ✅
    Payment? ❌ (skip)

--------------------------------------------------------------

2. IDENTIFY CORE ENTITIES (MOST IMPORTANT STEP)
Trick: Extract all nouns from problem statement

Example:
🚕 Ride Sharing
    User
    Driver
    Ride
    Location
    MatchingService

🎬 BookMyShow
    User
    Movie
    Show
    Seat
    Booking

💰 Splitwise
    User
    Expense
    Group
    Balance

Golden Rule:  If it has state + behavior → make it a class

--------------------------------------------------------------
🧱 3. CLASSIFY ENTITIES (Pattern Recognition)

Every LLD problem fits into these buckets:

    1. Core Entities (data holders)
    User
    Ride
    Seat
    Item 

    2. Manager / Service Classes (brain)
        RideManager
        BookingService
        ExpenseManager

    These handle:
    business logic
    orchestration

    3. Strategy / Policy Classes (optional)
    PricingStrategy
    MatchingStrategy
    PaymentStrategy

--------------------------------------------------------------

4. DEFINE RELATIONSHIPS
Ask:
    One-to-one?
    One-to-many?
    Many-to-many?

Example:
    One User → Many Rides
    One Show → Many Seats

--------------------------------------------------------------

5. WRITE INTERFACE / FLOW FIRST (VERY IMPORTANT)

Before coding classes, define flow:

Example (Ride booking)
    User → requestRide()
    RideManager → findDriver()
    Driver → acceptRide()
    Ride → start()
    Ride → end()

--------------------------------------------------------------

6.PICK DESIGN PATTERNS (IF NEEDED)

| Pattern   | When to use                             |
| --------- | --------------------------------------- |
| Singleton | One manager (RideManager)               |
| Strategy  | Multiple algorithms (pricing, matching) |
| Observer  | Notifications                           |
| State     | Vending machine, order state            |
| Factory   | Object creation                         |


--------------------------------------------------------------

7. START CODING (STRUCTURED WAY)

Order matters 

    Step 1: Enums
        enum RideStatus { REQUESTED, ONGOING, COMPLETED };
    Step 2: Core Classes
        class User {};
        class Driver {};
        class Ride {};
    Step 3: Manager Classes
        class RideManager {};
    Step 4: Glue everything



input -> manager (should have methods like init,book,.... and will have unorderedmap or vector for multiple games/rides etc/)
-> manager.x to entity.x (entity is reponsible for updating)
