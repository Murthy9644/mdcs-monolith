package logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import file_io.FileIO;
import utils.SystemUtils;

public class Log{
    public HashMap<String, ArrayList<String>> logs;

    private static String levelToFile(String level){
        switch (level.toLowerCase()) {
            case "error": return "Error";

            case "info": return "App";

            case "network": return "Network";
        
            default: return "App";
        }
    }

    // Set path based on the log type
    private static Path findPath(String doc_name){
        String path = Paths.get(SystemUtils.getAppDataDirectory(), "logs").toString();

        return Paths.get(path, doc_name + ".log");
    }

    public void error(String module, String message){
        LocalDateTime time = LocalDateTime.now();

        String line = String.format(
            "[ %s ] [ ERROR ] [ %s ] %s", 
            time.toString(), module, message
        );

        this.logs.get(levelToFile("error")).add(line);
    }

    public void info(String module, String message){
        LocalDateTime time = LocalDateTime.now();

        String line = String.format(
            "[ %s ] [ INFO ] [ %s ] %s", 
            time.toString(), module, message
        );

        this.logs.get(levelToFile("info")).add(line);
    }

    public void warn(String module, String message){
        LocalDateTime time = LocalDateTime.now();

        String line = String.format(
            "[ %s ] [ WARN ] [ %s ] %s", 
            time.toString(), module, message
        );

        this.logs.get(levelToFile("warn")).add(line);
    }

    public void network(String module, String message){
        LocalDateTime time = LocalDateTime.now();

        String line = String.format(
            "[ %s ] [ NETWORK ] [ %s ] %s", 
            time.toString(), module, message
        );

        this.logs.get(levelToFile("network")).add(line);
    }

    // Write logs into respected file
    public void flush() throws IOException{
        
        for (String divison : this.logs.keySet()){
            String content = "";
            Path path = findPath(divison);

            for (String entry : this.logs.get(divison)) content += (entry + "\n");

            if (!content.isEmpty())
                FileIO.fileWrite(path.toString(), content, "append");

            this.logs.get(divison).clear();
        }
    }

    public Log(){
        this.logs = new HashMap<>();
        this.logs.put("App", new ArrayList<>());
        this.logs.put("Network", new ArrayList<>());
        this.logs.put("Error", new ArrayList<>());
    }
}