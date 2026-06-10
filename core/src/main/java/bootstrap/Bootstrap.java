package bootstrap;

import java.util.Properties;

import logger.Log;
import models.jobs.Report;
import network.ProtoMet;

// Initializes and prepares the application before starting

/*
Phases in bootstrap:
        - Schema validation
        - Version validation / Update check
        - User state resolution
*/

public class Bootstrap{

    /*
    Processes like schema validation, version validation are independent of each other and thus 
    can be executed in parallel.

    Since, they are mix of CPU bound, I/O bound and Network bound processes, they won't cause much
    context switching for CPU.
    */
    
    public static Report run(ProtoMet server, Properties VERSIONS)
    throws Exception{
        Report report = new Report();
        Log logger = new Log();

        logger.info("bootstrap", "Starting application bootstrap");

        boolean test = Schema.validate(report, logger);

        if (report.app_state != Report.AppState.TERMINATE)
            test = Version.validate(server, VERSIONS, report, logger) && test;
        
        if (test){
            report.setAppState(Report.AppState.CONTINUE);
            report.summary = "Application bootstrap completed";
            logger.info("bootstrap", "Bootstrap completed successfully");
        } else{
            report.summary = "Bootstrap reported with anomalies";
            logger.info("bootstrap", "Bootstrap completed with anomalies");
        }

        logger.flush();

        return report;
    }
}
