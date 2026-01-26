package JobSchedulerTask;


public class OneTimeTrigger implements Trigger {

    private final long runAt;

    public OneTimeTrigger(long runAt) {
        this.runAt = runAt;
    }

    @Override
    public long nextRunTime() {
        return runAt;
    }

    @Override
    public boolean isRecurring() {
        return false;
    }

    @Override
    public void update() {
        // no-op
    }
}

