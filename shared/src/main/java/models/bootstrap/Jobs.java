package models.bootstrap;

import java.util.ArrayList;
import java.util.List;

import models.postals.Report.Job;

public class Jobs {
    
    public static class Schema extends Job{
        // Right now, there is no requirement for special variables for this job.
    }

    public static class Version extends Job{
        /*
        This should include update details if any like,
            - update type (critical / optional / plugin)
            - current version
            - available version
            - changes
        */

        public enum UpdateTypes{
            CRITICAL,
            OPTIONAL,
            PLUGIN
        }

        public static class Update{
            public UpdateTypes type;
            public String name;
            public String curr_ver;
            public String avail_ver;
            public List<String> changes = new ArrayList<>();
        }

        public List<Update> updates = new ArrayList<>();
    }
}
