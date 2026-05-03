package bootstrap;

import java.util.Properties;

public class BootstrapHandler {
    private static Properties APP, VERSIONS;
    
    public static boolean run(Properties APP_inc, Properties VERSIONS_inc){
        APP = APP_inc;
        VERSIONS = VERSIONS_inc;

        VersionCheck.validate(APP, VERSIONS);

        return true;
    }
}
