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

    // Format log entry
    public void format(String level, String module, String message){
        LocalDateTime time = LocalDateTime.now();

        String line = String.format(
            "[ %s ] [ %s ] [ %s ] %s\n",
            time.toString(), level.toUpperCase(), module, message
        );
        
        this.logs.get(levelToFile(level)).add(line);
    }

    // Write logs into respected file
    public void write() throws IOException{
        
        for (String divison : this.logs.keySet()){
            String content = "";
            Path path = findPath(divison);

            for (String entry : this.logs.get(divison)) content += entry;

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