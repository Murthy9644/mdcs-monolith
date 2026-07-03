package models.postals;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// DTO for job report to the master.

// Submitted to the master by workers after completing their execution
public class Report {
    
    public enum AppState {
        TERMINATE,
        BLOCK,
        CONTINUE
    }

    public enum JobType{
        SCHEMA,
        VERSION,
        USERSTATE,
        AUTH
    }

    public static abstract class Job{
        public JobType type;
        public List<String> logs = new ArrayList<>();
    }

    public AppState app_state = AppState.CONTINUE;
    public Queue<Job> jobs = new ConcurrentLinkedQueue<>();
    public String summary;

    public void setAppState(AppState state){
        // Can only be stepped up
        // CONTINUE -> BLOCK -> TERMINATE

        if (this.app_state == AppState.TERMINATE)
            return;

        if (
            this.app_state == AppState.BLOCK
            && state == AppState.CONTINUE
        ) return;

        this.app_state = state;
    }
}
