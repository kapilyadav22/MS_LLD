We can use different Pointer Patterns in different use case but generally we avoid using raw pointers in interviews

There can be 5 patterns, we can use while working with instances:
1.) Unique pointer 
2.) Shared pointer (most prefered)
3.) Weak pointer (very less frequently use)
4.) Raw Pointers (creating instances on runtime) (never use in interviews)
5.) Stack based (For managers, when other classes uses it, but doesn't own it) 


1. unique_ptr → Ownership
    Use unique_ptr when only one class owns the object.

Meaning:
    No other class should share it

When the owner dies → object dies

Use Case :  generally creating something on main function like Board, Manager, etc... we can either use unique_ptr 
            or even references

Example :
Car → Engine
Board → Dice
House → Rooms
Order → OrderItems


2. shared_ptr → Shared Ownership (Most Preferred, as it provides flexibility to use the same instance at multiple places)
Use shared_ptr when multiple objects share the same instance. 
Example: Players using same game board

Use shared_ptr when:
    multiple objects need same resource

Examples:
    Players → Board
    Subscribers → Channel
    Services → Strategy
    Users → Logger


3. Raw Pointer → Non Ownership
Use raw pointer when class does NOT own the object.

Use raw pointer when:
    reference only
    no ownership

Examples:
    Command → Receiver
    Iterator → Container
    Strategy → Context


4. weak_ptr → Avoid Circular Dependency (Rarely needed)
Used with shared_ptr. 

A → shared_ptr → B
B → shared_ptr → A



Follow this approach:
stack → reference → unique_ptr → shared_ptr → raw pointer