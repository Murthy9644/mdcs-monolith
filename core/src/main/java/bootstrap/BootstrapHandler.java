package bootstrap;

import java.util.HashMap;
import java.util.Properties;

public class BootstrapHandler{
    private static Properties VERSIONS;
    private static HashMap<String, String> response = new HashMap<>();
    
    public static HashMap<String, String> run(Properties APP_inc, Properties VERSIONS_inc)
    throws Exception{
        VERSIONS = VERSIONS_inc;
        
        if (
            VersionCheck.validate(VERSIONS, response)
            && SchemaValidation.validate(response)
        ){
            response.put("app_state", "continue");
            response.put("status", "CHECK");
            response.put("body", null);
            response.put("message", "Bootstrap completed successfully");
        }

        return response;
    }
}
