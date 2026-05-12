package bootstrap;

import java.util.HashMap;
import java.util.Properties;

import logger.Log;

public class BootstrapHandler{
    private static Properties VERSIONS;
    private static HashMap<String, String> response = new HashMap<>();
    
    public static HashMap<String, String> run(Properties APP_inc, Properties VERSIONS_inc)
    throws Exception{
        VERSIONS = VERSIONS_inc;
        Log logger = new Log();
        logger.info("bootstrap", "Starting application bootstrap");
        
        if (
            SchemaValidation.validate(response, logger)
            && VersionCheck.validate(VERSIONS, response, logger)
        ){
            response.put("app_state", "continue");
            response.put("status", "CHECK");
            response.put("body", null);
            response.put("message", "Application bootstrap completed");
        }
        
        response.put("user_state", UserStateResolution.resolve(logger));

        logger.info("bootstrap", "Bootstrap completed successfully");
        logger.flush();

        return response;
    }
}
