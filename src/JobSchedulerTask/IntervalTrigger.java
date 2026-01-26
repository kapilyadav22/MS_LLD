package JobSchedulerTask;


public class IntervalTrigger implements Trigger {

    private long nextRun;
    private final long interval;

    public IntervalTrigger(long startTime, long interval) {
        this.nextRun = startTime;
        this.interval = interval;
    }

    @Override
    public long nextRunTime() {
        return nextRun;
    }

    @Override
    public boolean isRecurring() {
        return true;
    }

    @Override
    public void update() {
        nextRun += interval;
    }
}
