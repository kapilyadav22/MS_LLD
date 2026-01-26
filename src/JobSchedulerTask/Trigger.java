package JobSchedulerTask;

public interface Trigger {
    long nextRunTime();
    boolean isRecurring();
    void update();
}
