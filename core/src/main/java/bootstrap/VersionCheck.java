package bootstrap;

import java.util.HashMap;
import java.util.Properties;

public class VersionCheck {
    private static Properties VERSIONS;
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

    private static boolean moduleCompatibity(){

        return true;
    }

    private static boolean updateCheck(){

        return true;
    }
    
    public static boolean validate(
        Properties v_inc, 
        HashMap<String, String> res
    ){
        VERSIONS = v_inc;
        response = res;

        if (
            versionFormat() 
            && moduleCompatibity()
            && updateCheck()
        )
            return true;

        return false;
    }
}
