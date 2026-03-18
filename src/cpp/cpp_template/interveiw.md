Use #include<bits/stdc++.h>
Use Initializer list for members initialization, refer Initializer_List.cpp
use unique_ptr, and shared_ptr in runtime creation

Mistakes:
access modifier , change to public for methods:
call methods of pointer instances using ->
call methods of stack instances using .
close the class in cpp with ;

--------------------------------------------------------------

Almost all LLD problems are variations of these 8 patterns.

| Pattern             | Example Problems    |
| ------------------- | ------------------- |
| Board Game          | Chess, Snake Ladder |
| Matching Engine     | Uber                |
| Order Processing    | Swiggy              |
| Resource Allocation | Parking Lot, Elevator
| Rate Limiter        | API limiter         |
| Observer            | Notifications       |
| Inventory           | Amazon              |
| Scheduler           | Calendar            |

--------------------------------------------------------------

MOST IMPORTANT PATTERNS FOR LLD INTERVIEWS

1 Strategy
2 Factory
3 Singleton
4 Observer
5 Builder


--------------------------------------------------------------

UNIVERSAL TEMPLATE In LLD Interviews
    1. Clarify requirements
    2. List entities (nouns)
    3. Classify:
       - Core entities
       - Managers
       - Strategies
    4. Define relationships
    5. Define flow (very important)
    6. Identify patterns
    7. Start coding (top-down)
    