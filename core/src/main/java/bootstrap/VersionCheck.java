package bootstrap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;

import file_io.FileIO;
import logger.Log;
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

    private static boolean moduleCompatibity(){
        logger.info("bootstrap", "Checking module compatibility");
        String app_version_scheme[] = VERSIONS.getProperty("app.version").split("\\.");

        for (String key : VERSIONS.stringPropertyNames()) {
            String major = VERSIONS.getProperty(key).split("\\.")[0];

            if (!major.equals(app_version_scheme[0])) {
                response.put("app_state", "terminate");
                response.put("status", "INCOMPATIBLE_MODULE");
                response.put("body", key + "<>" + VERSIONS.getProperty(key));
                response.put("message", "Application startup aborted");

                logger.error("bootstrap", "Incompatible module version (" + key + ")");

                return false;
            }
        }

        logger.info("bootstrap", "Module compatibility verified");

        return true;
    }

    private static boolean updateCheck(){
        
        logger.info("bootstrap", "Checking for updates");

        try{
            String json_body = FileIO.toJson(VERSIONS);
            ServerRequest.post(
                "http://localhost:1097/mdcs/version/check",
                new String[]{"Content-Type", "application/json"},
                json_body
            );
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
    ){
        VERSIONS = v_inc;
        response = res;
        logger = logger_inc;

        if (
            versionFormat() 
            && moduleCompatibity()
            && updateCheck()
        )
            return true;

        return false;
    }
}
