🔥 MOST COMMON FOLLOW-UPS (and How to Answer)
1“What happens if many expired entries exist but cache is full?”

Expected answer:
We can lazily clean expired entries during put() or periodically via a background cleanup thread.

If pushed:
On put(), walk from tail and remove expired nodes
Or run a scheduled cleaner

2 “How would you improve scalability?”

Say:
Current solution uses a single lock
For higher throughput:
Segment the cache
One lock per segment
Similar to ConcurrentHashMap design

This answer is enough — no need to code it.
3. “Why not use LinkedHashMap?”

Perfect answer:
LinkedHashMap doesn’t support TTL and fine-grained eviction control.
Writing it manually gives extensibility and control.

4.“What is the complexity if cleanup runs?”

Answer:
Normal ops → O(1)
Cleanup → O(n) worst case, but amortized cost is acceptable


5.“What if capacity is very large?”

Say:
Memory bound
Can store values off-heap or spill to disk (out of scope for LLD)

6.“What if TTL is very small?”

Answer:
Lazy eviction avoids extra threads
High churn but correctness maintained

7️⃣ “How would you make it distributed?”

Correct response:

That moves into system design, not LLD.
We’d need sharding, consistent hashing, and distributed eviction.

Important:
Don’t over-design here.

⚠️ TRICK QUESTIONS THEY SOMETIMES ASK
❓ “Is this deadlock-free?”
✔ Yes — single lock, no nested locks

❓ “Can this starve threads?”

✔ Minimal risk; lock scope is small

❓ “What happens if ttlMillis is zero or negative?”

✔ Entry expires immediately — acceptable or can guard input

🚫 Things That WOULD FAIL You

❌ Pseudocode only
❌ Using LinkedHashMap directly
❌ No locking discussion
❌ Over-engineering with executors, timers, etc.