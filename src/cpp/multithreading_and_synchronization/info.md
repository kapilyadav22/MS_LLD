
Multithreading Tools in C++

-----------------------------------------------------------
| Feature                   | Use                         |
| ------------------------- | --------------------------- |
| `std::thread`             | Create threads              |
| `std::mutex`              | Protect shared data         |
| `std::lock_guard`         | Safe mutex handling         |
| `std::condition_variable` | Thread communication        |
| `std::future`             | Async result                |
| `std::async`              | Automatic thread management |
| `std::atomic`             | Lock-free variables         |
------------------------------------------------------------


When to Use Which
-------------------------------------------------------
| Tool                 | Use Case                     |
| -------------------- | ---------------------------- |
| `mutex + lock_guard` | protect complex shared data  |
| `condition_variable` | threads waiting for an event |
| `atomic`             | simple counters / flags      |
-------------------------------------------------------


If you're doing C++ LLD interviews, the 3 most common concurrency patterns you’ll see are:
   1. Thread-safe cache (mutex)
   2. Producer–Consumer queue (condition_variable)
   3. Rate limiter counters (atomic)
Learning these three covers almost all interview scenarios.
