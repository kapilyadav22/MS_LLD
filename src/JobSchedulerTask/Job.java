package JobSchedulerTask;



public class Job {
    private final Runnable task;

    public Job(Runnable task) {
        this.task = task;
    }

    public void execute() {
        task.run();
    }
}
