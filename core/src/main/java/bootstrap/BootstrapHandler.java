package bootstrap;

import java.util.HashMap;
import java.util.Properties;

import logger.Log;
import response_classes.BootstrapResponse;

public class BootstrapHandler{
    private static Properties VERSIONS;
    
    public static HashMap<String, String> run(Properties APP_inc, Properties VERSIONS_inc)
    throws Exception{
        VERSIONS = VERSIONS_inc;
        BootstrapResponse.GeneralResponse bsres = new BootstrapResponse.GeneralResponse();

        Log logger = new Log();
        logger.info("bootstrap", "Starting application bootstrap");
        
        if (
            SchemaValidation.validate(bsres, logger)
            && VersionCheck.validate(VERSIONS, bsres, logger)
        ){
            bsres.app_state = BootstrapResponse.AppState.CONTINUE;
            bsres.status = BootstrapResponse.Status.CHECK;
            bsres.body = null;
            bsres.message = "Application bootstrap completed";
        }
        
        bsres.user_state = UserStateResolution.resolve(logger);

        logger.info("bootstrap", "Bootstrap completed successfully");
        logger.flush();

        return response;
    }
}
