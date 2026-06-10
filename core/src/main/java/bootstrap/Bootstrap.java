package bootstrap;

import java.io.IOException;
import java.util.Properties;

import logger.Log;
import models.jobs.Report;
import models.jobs.Report.AppState;
import network.ProtoMet;

// Initializes and prepares the application before starting

/*
Phases in bootstrap:
        - Schema validation
        - Version validation / Update check
        - User state resolution

It may be noted that user state resolution phase is executed after the remaining phases of
bootstrap. It is called independently by the master and not as a parallel worker.
*/

public class Bootstrap {

    /*
    Processes like schema validation, version validation are independent of each other and thus 
    can be executed in parallel.

    Since, they are mix of CPU bound, I/O bound and Network bound processes, they won't cause much
    context switching for CPU.
    */

    public static Report run(ProtoMet server, Properties VERSIONS){
        Report report = new Report();
        Log logger = new Log();

        logger.info("bootstrap", "Starting application bootstrap");

        Schema schema = new Schema(report, logger);
        Thread sch_worker = new Thread(schema);
        sch_worker.setDaemon(true);

        Version version = new Version(report, logger, server, VERSIONS);
        Thread ver_worker = new Thread(version);
        ver_worker.setDaemon(true);

        ver_worker.start();
        sch_worker.start();

        try { sch_worker.join(); } 
        catch (InterruptedException e) { sch_worker.interrupt(); }
        
        try { ver_worker.join(); } 
        catch (InterruptedException e) { ver_worker.interrupt(); }

        if (report.app_state == AppState.CONTINUE){
            logger.info(
                "bootstrap", 
                "Application bootstrap reported with no severity"
            );

            report.summary = "Application bootstrap reported with no severity";
        }

        else if (report.app_state == AppState.BLOCK){
            logger.info(
                "bootstrap", 
                "Application startup blocked"
            );

            report.summary = "Application startup blocked";
        }

        else if (report.app_state == AppState.TERMINATE){
            logger.error(
                "bootstrap", 
                "Application startup aborted"
            );

            report.summary = "Application startup aborted";
        }

        logger.info("bootstrap", "Application bootstrap completed");

        try { logger.flush(); } 
        catch (IOException e) { }

        return report;
    }
}
