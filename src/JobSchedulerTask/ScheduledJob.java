package JobSchedulerTask;


// ===================== ScheduledJob =====================
public class ScheduledJob {

    final Job job;
    final Trigger trigger;
    long nextRunTime;

    public ScheduledJob(Job job, Trigger trigger) {
        this.job = job;
        this.trigger = trigger;
        this.nextRunTime = trigger.nextRunTime();
    }

    void updateNextRun() {
        trigger.update();
        nextRunTime = trigger.nextRunTime();
    }
}
