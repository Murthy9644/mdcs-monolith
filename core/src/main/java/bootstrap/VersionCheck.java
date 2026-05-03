package bootstrap;

import java.util.HashMap;
import java.util.Properties;

public class VersionCheck {
    private static Properties APP, VERSIONS;
    private static HashMap<String, String> response;

    private static boolean versionFormat(){

        for (String key : VERSIONS.stringPropertyNames()){
            String version = VERSIONS.getProperty(key);

            if (!version.matches("^[0-9]+.[0-9]+.[0-9]$")){
                response.put("app_state", "terminate");
                response.put("status", "INVALID_VERSION_FORMAT");
                response.put("body", version);
                response.put("message", "App startup aborted");

                return false;
            }
        }

        return true;
    }
    
    public static boolean validate(
        Properties a_inc, 
        Properties v_inc, 
        HashMap<String, String> res
    ){
        APP = a_inc;
        VERSIONS = v_inc;
        response = res;

        return versionFormat();
    }
}
