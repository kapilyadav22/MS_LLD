package JobSchedulerTask;

import java.util.*;
import java.util.concurrent.*;

// ===================== JobScheduler =====================
public class JobScheduler {

    private final PriorityQueue<ScheduledJob> queue =
            new PriorityQueue<>(Comparator.comparingLong(j -> j.nextRunTime));

    private final ExecutorService executor =
            Executors.newFixedThreadPool(2);

    public synchronized void schedule(Job job, Trigger trigger) {
        queue.offer(new ScheduledJob(job, trigger));
        notify();
    }

    public void start() {
        Thread schedulerThread = new Thread(() -> {
            while (true) {
                try {
                    ScheduledJob scheduledJob;

                    synchronized (this) {
                        while (queue.isEmpty()) {
                            wait();
                        }

                        scheduledJob = queue.peek();
                        long waitTime =
                                scheduledJob.nextRunTime - System.currentTimeMillis();

                        if (waitTime > 0) {
                            wait(waitTime);
                            continue;
                        }

                        queue.poll();
                    }

                    executor.submit(scheduledJob.job::execute);

                    if (scheduledJob.trigger.isRecurring()) {
                        scheduledJob.updateNextRun();
                        synchronized (this) {
                            queue.offer(scheduledJob);
                        }
                    }

                } catch (InterruptedException ignored) {
                }
            }
        });

        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }
}


/*
+-------------------+
|   JobScheduler    |
+-------------------+
| - queue: PriorityQueue<ScheduledJob>
| - executor: ExecutorService
+-------------------+
| + schedule(Job, Trigger)
| + start()
+-------------------+

              |
              | manages
              v

+-------------------+
|   ScheduledJob    |
+-------------------+
| - job: Job
| - trigger: Trigger
| - nextRunTime: long
+-------------------+
| + updateNextRun()
+-------------------+

        |                         |
        | has-a                   | has-a
        v                         v

+-------------------+     +-------------------+
|        Job        |     |     Trigger       |<<interface>>
+-------------------+     +-------------------+
| - task: Runnable |     | + nextRunTime()   |
+-------------------+     | + isRecurring()  |
| + execute()       |     | + update()       |
+-------------------+     +-------------------+
                                  ^
                                  |
                    --------------------------------
                    |                              |
        +---------------------+        +----------------------+
        |   OneTimeTrigger    |        |   IntervalTrigger    |
        +---------------------+        +----------------------+
        | - runAt: long       |        | - nextRun: long      |
        +---------------------+        | - interval: long     |
        | + nextRunTime()     |        +----------------------+
        | + isRecurring()     |        | + nextRunTime()      |
        | + update()          |        | + isRecurring()      |
        +---------------------+        | + update()           |
                                       +----------------------+

 */