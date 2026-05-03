package bootstrap;

import java.util.HashMap;
import java.util.Properties;

public class BootstrapHandler {
    private static Properties APP, VERSIONS;
    private static HashMap<String, String> response = new HashMap<>();
    
    public static String run(Properties APP_inc, Properties VERSIONS_inc){
        APP = APP_inc;
        VERSIONS = VERSIONS_inc;
        
        if (!VersionCheck.validate(APP, VERSIONS, response))
            return false;

        return "";
    }
}
