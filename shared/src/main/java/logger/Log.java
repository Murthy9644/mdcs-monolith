package logger;

import java.nio.file.Paths;
import java.time.LocalDateTime;

import file_io.FileIO;
import utils.SystemUtils;

class Log{
    public static final String file = "log";
    public String path;
    public String line;

    // Set path based on the log type
    private void findPath(String level){
        String path = Paths.get(SystemUtils.getAppDataDirectory(), "logs").toString();

        switch (level.toLowerCase()){
            case "error":
                this.path = Paths.get(path, "Error.log").toString();
                break;

            case "info":
                this.path = Paths.get(path, "App.log").toString();
                break;

            case "network":
                this.path = Paths.get(path, "Network.log").toString();
                break;

            default:
                this.path = Paths.get(path, "App.log").toString();
        }
    }

    public Log(String level, String module, String message) throws Exception{
        LocalDateTime time = LocalDateTime.now();

        this.line = String.format(
            "[ %s ] [ %s ] [ %s ] %s\n",
            time.toString(), level, module, message
        );

        this.findPath(level);
        FileIO.fileWrite(this.path, line, "append");
    }
}