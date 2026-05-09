package file_io;

import java.nio.file.Paths;

import utils.SystemUtils;

// Maintains blue print of file structures mapped to java classes

public class DataClasses{

    // Generic field rules class
    public static class FieldRules{
        public String name;
        public String type;

        public FieldRules(String name, String type){
            this.name = name;
            this.type = type;
        }
    }

    public interface HasPath{
        String getPath();
    }

    // Template for Accounts.json file
    public static class Accounts implements HasPath{   

        // File location
        public static final String path = Paths.get(
            SystemUtils.getAppDataDirectory(), 
            "entities", "Accounts.json"
        ).toString();

        public int user_id;

        public String
            user_name,
            email,
            auth_token;

        public boolean login_status;

        public String getPath(){ return path; }

        // When data is sent
        public Accounts(String details[]){
            this.user_id        = Integer.parseInt(details[0]);
            this.user_name      = details[1];
            this.email          = details[2];
            this.auth_token     = details[3];
            this.login_status   = details[4] == "true";
        }

        // When data is null
        public Accounts(){
            this.user_id        = 0;
            this.user_name      = "";
            this.email          = "";
            this.auth_token     = "";
            this.login_status   = false;
        }
    }

    // Template for Device.json
    public static class Device implements HasPath{

        // File location
        public static final String path = Paths.get(
            SystemUtils.getAppDataDirectory(), 
            "entities", "Device.json"
        ).toString();

        public String
            device_id,
            device_name,
            workspace_id,
            workspace_name,
            device_status;

        public String getPath(){ return path; }

        // When data is sent
        public Device(String details[]){
            this.device_id      = details[0];
            this.device_name    = details[1];
            this.workspace_id   = details[2];
            this.workspace_name = details[3];
            this.device_status  = details[4];
        }

        // When data is null
        public Device(){
            this.device_id      = "";
            this.device_name    = "";
            this.workspace_id   = "";
            this.workspace_name = "";
            this.device_status  = "";
        }
    }

    // Template for Configs.json
    public static class Configs implements HasPath{

        // File location
        public static final String path = Paths.get(
            SystemUtils.getAppDataDirectory(), 
            "application", "Configs.json"
        ).toString();

        public String getPath(){ return path; }
    }

    // Template for ModulePaths.json
    public static class ModulePaths implements HasPath{

        // File location
        public static final String path = Paths.get(
            SystemUtils.getAppDataDirectory(), 
            "application", "ModulePaths.json"
        ).toString();

        public String
            clipboard,
            file_share,
            folder_sync,
            protocols,
            application_acess,
            scheduler,
            host,
            client,
            mesh;

        public String getPath(){ return path; }

        // When data is sent
        public ModulePaths(String details[]){
            this.clipboard          = details[0];
            this.file_share         = details[1];
            this.folder_sync        = details[2];
            this.protocols          = details[3];
            this.application_acess  = details[4];
            this.scheduler          = details[5];
            this.host               = details[6];
            this.client             = details[7];
            this.mesh               = details[8];
        }

        // When data is null
        public ModulePaths(){
            this.clipboard          = "";
            this.file_share         = "";
            this.folder_sync        = "";
            this.protocols          = "";
            this.application_acess  = "";
            this.scheduler          = "";
            this.host               = "";
            this.client             = "";
            this.mesh               = "";
        }
    }

    // Template for Data.json
    public static class Data implements HasPath{

        // File location
        public static final String path = Paths.get(
            SystemUtils.getAppDataDirectory(), 
            "application", "Data.json"
        ).toString();

        public String getPath(){ return path; }
    }
}