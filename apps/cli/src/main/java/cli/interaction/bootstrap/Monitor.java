package cli.interaction.bootstrap;

import java.util.Properties;

import bootstrap.Bootstrap;
import cli.utils.tools.ConsoleIO;
import models.jobs.Report;
import models.jobs.Report.Job;
import models.jobs.Report.JobType;
import network.ProtoMet;

/**
 * Handles report from the bootstrap sequence, which includes schema validation, version
 * validation and update checking.
 */

public class Monitor {
    private ProtoMet server;
    private Properties VERSIONS;
    private ConsoleIO io;
    private Report report;

    public void log(){
        /**
         * There is a ceveat here. Which is, what if update job wrote to report before schema?
         * The logs of update acknowledgement would be followed by so many other logs and user may
         * not understand why application was blocked.
         * 
         * For current version, we will print all other logs except update job and then print them
         * 
         * This would still leave other issues. If blocking update is available,
         *      - How would user understand that they need to update the application to continue 
         *      using it?
         *      - How would user understand the severity of the update? (critical / optional)
         *      - How long do we block the application for?
         *      - If the application is blocked, how would user run update command?
         * 
         * For this version, will instruct user to manually download the new version and in future
         * updates, will implement separate update status class to determine type of update.
         */

        for (Job job : this.report.jobs){
            
            if (job.type == JobType.VERSION) continue;

            for (String line : job.logs){
                String params[] = line.split("<>");

                this.io.map(params[0], params[1]);
            }
            
            report.jobs.poll();
        }

        Job job = this.report.jobs.poll();

        if (job != null && job.type == JobType.VERSION){
            
            for (String line : job.logs){
                String params[] = line.split("<>");

                this.io.map(params[0], params[1]);
            }

            if (this.report.app_state == Report.AppState.BLOCK){
                /**
                 * Not using busy while loop to block the application, instead will use a simple
                 * input to block the application, which would be more user friendly and also
                 * allow user to read the update logs and understand the severity of the update.
                 */

                while (true){
                    String input = this.io.ask();

                    if (input.equalsIgnoreCase("exit")) break;
                }
            }
        }
    }

    /**
     * Receives report from bootstrap and prints the logs, decides app state (continue / block /
     * terminate)
     */
    public boolean attend(){
        this.report = Bootstrap.run(this.server, this.VERSIONS);
        
        return true;
    }
    
    public Monitor(ProtoMet server, Properties VERSIONS){
        this.server = server;
        this.VERSIONS = VERSIONS;

        this.io = new ConsoleIO();
    }
}
