package bootstrap;

import java.util.Properties;

public class VersionCheck {
    private static Properties APP, VERSIONS;
    
    public static boolean validate(Properties APP_inc, Properties VERSIONS_inc){
        APP = APP_inc;
        VERSIONS = VERSIONS_inc;

        return true;
    }
}
