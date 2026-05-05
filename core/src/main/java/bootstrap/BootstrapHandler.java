package bootstrap;

import java.util.HashMap;
import java.util.Properties;

public class BootstrapHandler {
    private static Properties APP, VERSIONS;
    private static HashMap<String, String> response = new HashMap<>();
    
    public static HashMap<String, String> run(Properties APP_inc, Properties VERSIONS_inc){
        APP = APP_inc;
        VERSIONS = VERSIONS_inc;
        
        if (
            VersionCheck.validate(VERSIONS, response)
        ){
            response.put("app_state", "continue");
            response.put("status", "check");
            response.put("body", null);
            response.put("message", "System bootstrap completed successfully");
        }

        return response;
    }
}
