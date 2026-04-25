package utils;

public class SystemUtils {

    public static String getOS(){
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) return "WINDOWS";

        else if (os.contains("mac")) return "MAC";
        
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix"))
            return "LINUX";

        else return "OTHERS";
    }
}
