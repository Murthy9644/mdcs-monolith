package bootstrap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;

import file_io.DataClasses;
import file_io.FileIO;
import logger.Log;
import network.ResponseClasses;
import network.ServerRequest;

public class VersionCheck {
    private static Properties VERSIONS;
    private static HashMap<String, String> response;
    private static Log logger;

    private static boolean versionFormat(){
        logger.info("bootstrap", "Checking version format");

        for (String key : VERSIONS.stringPropertyNames()){
            String version = VERSIONS.getProperty(key);

            if (!version.matches("^[0-9]+.[0-9]+.[0-9]$")){
                response.put("app_state", "terminate");
                response.put("status", "INVALID_VERSION_FORMAT");
                response.put("body", version);
                response.put("message", "Application startup aborted");

                logger.error("bootstrap", "Invalid version format (" + key + ")");

                return false;
            }
        }

        logger.info("bootstrap", "Version format check passed");

        return true;
    }

    private static boolean updateCheck()
    throws Exception{        
        logger.info("bootstrap", "Checking for updates");

        try{
            // Read plugins metadata
            Map<String, DataClasses.Plugin> plugin_metadata = FileIO.fileRead(DataClasses.Plugins.class).plugins;

            // Final object to send to server
            Map<String, Map<String, String>> body = new HashMap<>();

            // Put app version
            Map<String, String> app_version = new HashMap<>();
            app_version.put("current_version", VERSIONS.getProperty("app.version"));

            body.put("app", app_version);

            // Put plugins version
            body.put("plugins", new HashMap<>());

            for (String plugin_name : plugin_metadata.keySet())
                body.get("plugins").put(
                    plugin_name, 
                    plugin_metadata.get(plugin_name).installed_version
                );

            // Convert to json format string
            String json_body = FileIO.toJson(body);

            // Sending request to server
            String response = ServerRequest.post(
                "http://localhost:1097/mdcs/version/check",
                new String[]{"Content-Type", "application/json"},
                json_body
            );

            ResponseClasses.UpdateResponse res = FileIO.toObject(response, ResponseClasses.UpdateResponse.class);

            // Work under Progress

        } catch (JsonProcessingException e){
            response.put("app_state", "terminate");
            response.put("status", "INVALID_VERSION_FORMAT");
            response.put("body", "_");
            response.put("message", "Application startup aborted");

            return false;
        } catch (IOException e){
            response.put("app_state", "continue");
            response.put("status", "UPDATE_CHECK_FAILED");
            response.put("body", "_");
            response.put("message", "Update check failed: Network issue");

            return false;
        } catch (InterruptedException e){
            response.put("app_state", "continue");
            response.put("status", "UPDATE_CHECK_FAILED");
            response.put("body", "_");
            response.put("message", "Update check interrupted");

            Thread.currentThread().interrupt();

            return false;
        }

        logger.info("bootstrap", "Current version is up-to-date");

        return true;
    }
    
    public static boolean validate(
        Properties v_inc, 
        HashMap<String, String> res,
        Log logger_inc
    ) throws Exception{
        VERSIONS = v_inc;
        response = res;
        logger = logger_inc;

        if (
            versionFormat()
            && updateCheck()
        )
            return true;

        return false;
    }
}
