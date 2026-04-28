package logger;

import java.nio.file.Paths;
import java.time.LocalDateTime;

import file_io.FileIO;
import utils.SystemUtils;

public class Log{

    // Set path based on the log type
    private static String findPath(String level){
        String path = Paths.get(SystemUtils.getAppDataDirectory(), "logs").toString();
        String file_path;

        switch (level.toLowerCase()){
            case "error":
                file_path = Paths.get(path, "Error.log").toString();
                break;

            case "info":
                file_path = Paths.get(path, "App.log").toString();
                break;

            case "network":
                file_path = Paths.get(path, "Network.log").toString();
                break;

            default:
                file_path = Paths.get(path, "App.log").toString();
        }

        return file_path;
    }

    // Write log into respected file
    public static void write(String level, String module, String message) throws Exception{
        LocalDateTime time = LocalDateTime.now();

        String line = String.format(
            "[ %s ] [ %s ] [ %s ] %s\n",
            time.toString(), level, module, message
        );

        String path = findPath(level);
        FileIO.fileWrite(path, line, "append");
    }
}