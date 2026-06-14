package cli.interact;

import java.util.Properties;

import bootstrap.Supervise;
import cli.utils.ConsoleIO;
import models.bootstrap.Jobs.Version;
import models.postals.Report;
import models.postals.Report.Job;
import models.postals.Report.JobType;
import network.ProtoMet;

/**
 * Handles report from the bootstrap sequence, which includes schema validation, version
 * validation and update checking.
 */

public class Bootstrap {
    private ProtoMet server;
    private Properties VERSIONS;
    private ConsoleIO io;
    private Report report;

    public boolean updates(){
        Version job = (Version) this.report.jobs.poll();

        // Because there are no logs exist => no updates.
        if (job == null) return true;

        if (job.updates.size() > 0)
            this.io.info("New update(s) available to install\n");

        int count = 0;
        for (Version.Update update : job.updates){
            count++;

            this.io.print(
                count +
                ". " + update.type.toString() + " update available for " + update.name + "\n"
            );

            this.io.print("\nCurrent Version: ");
            this.io.muted(update.curr_ver + "\n");

            this.io.print("Available Version: " + update.avail_ver + "\n");

            this.io.print("\nChanges:\n");

            for (String change : update.changes)
                this.io.print("\t- " + change + "\n");

            if (update.type == Version.UpdateTypes.CRITICAL)
                this.io.critical("Update is mandatory to continue to the application.\n");
            
            this.io.info("Do you want to install the update(s)? yes (or) no\n");
            this.io.print("please enter: yes (or) no\n");

            String choice = this.io.ask();

            if (choice.equalsIgnoreCase("yes")){
                // yup
                // placeholder
            }

            if (update.type == Version.UpdateTypes.CRITICAL) return false;
        }
        
        return true;
    }

    public void log(){
        /*
        All the bootstrap logs are printed first and then the updates are checked. Because, the
        update related logs are not included in these logs right, so we are safe.
        */

        while (!this.report.jobs.isEmpty()){
            Job job = this.report.jobs.peek();

            for (String line : job.logs){
                String params[] = line.split("<>");

                this.io.map(params[0], params[1]);
            }

            // Version job report should still be in the queue. we will pop this later.
            if (job.type != JobType.VERSION) this.report.jobs.poll();
        }
    }

    /**
     * Receives report from bootstrap and prints the logs, decides app state (continue / block /
     * terminate)
     */
    public boolean attend(){
        Supervise supervisor = new Supervise(this.server, this.VERSIONS, this.report);
        Thread bootstrap = new Thread(supervisor);

        try{
            bootstrap.setDaemon(true);

            bootstrap.start();
            bootstrap.join();
        } catch (InterruptedException e){
            // Not an error, but intentional. Stop the application because, we don't klnow the
            // cause

            bootstrap.interrupt();
            return false;
        }

        // Still need to handle app states (Terminate / Block / Continue)

        this.log();
        
        return this.updates();
    }
    
    public Bootstrap(ProtoMet server, Properties VERSIONS){
        this.server = server;
        this.VERSIONS = VERSIONS;

        this.io = new ConsoleIO();
        this.report = new Report();
    }
}
