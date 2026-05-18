package bootstrap;

import java.util.Properties;

import logger.Log;
import models.bootstrap.BootstrapResponse.*;
import network.ServerRequest;

public class BootstrapHandler{
    
    public static GeneralResponse run(ServerRequest server, Properties VERSIONS)
    throws Exception{
        GeneralResponse bsres = new GeneralResponse();

        Log logger = new Log();
        logger.info("bootstrap", "Starting application bootstrap");

        boolean test = SchemaValidation.validate(bsres, logger);

        if (bsres.app_state != AppState.TERMINATE)
            test = VersionCheck.validate(server, VERSIONS, bsres, logger) && test;
        
        if (bsres.app_state != AppState.TERMINATE)
            bsres.user_state = UserStateResolution.resolve(logger);
        
        if (test){
            bsres.setAppState(AppState.CONTINUE);
            bsres.summary = "Application bootstrap completed";
            logger.info("bootstrap", "Bootstrap completed successfully");
        } else{
            bsres.summary = "Bootstrap reported with anomalies";
            logger.info("bootstrap", "Bootstrap completed with anomalies");
        }

        logger.flush();

        return bsres;
    }
}
