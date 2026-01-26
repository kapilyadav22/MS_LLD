package JobSchedulerTask;


import java.time.LocalTime;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        JobScheduler scheduler = new JobScheduler();
        scheduler.start();

        // One-time job (runs after 3 seconds)
        Job oneTimeJob = new Job(() -> {
            System.out.println(
                    "One-time job executed at " + System.currentTimeMillis()
            );
        });

        scheduler.schedule(
                oneTimeJob,
                new OneTimeTrigger(System.currentTimeMillis() + 3000)
        );

        // Recurring job (runs every 2 seconds)
        Job recurringJob = new Job(() -> {
            System.out.println(
                    "Recurring job executed at " + LocalTime.now()
            );
        });

        scheduler.schedule(
                recurringJob,
                new IntervalTrigger(
                        System.currentTimeMillis() + 1000,
                        2000
                )
        );

        // Keep main thread alive for demo
        Thread.sleep(10000);
        System.out.println("Main thread exiting...");
    }
}
