package file_io;

class UserDataFile {
    public String user_id;
    public String user_name;
    public String email;

    public UserDataFile() {
        user_id = "";
        user_name = "";
        email = "";
    }

    public UserDataFile(String details[]) {
        user_id = details[0].isEmpty() ? FileIO.getUserAttr("user_id") : details[0];
        user_name = details[1].isEmpty() ? FileIO.getUserAttr("username") : details[1];
        email = details[2].isEmpty() ? FileIO.getUserAttr("email") : details[2];
    }
}

class DeviceConfigs{
    public String device_id;
    public String device_name;
    public String workspace_name;
    public String device_password;

    public DeviceConfigs(){
        device_id = "";
        device_name = "";
        workspace_name = "";
        device_password = "";
    }

    public DeviceConfigs(String details[]){
        device_id = details[0].isEmpty() ? FileIO.getDeviceConfig("deviceid") : details[0];
        device_name = details[1].isEmpty() ? FileIO.getDeviceConfig("devicename") : details[1];
        workspace_name = details[2].isEmpty() ? FileIO.getDeviceConfig("workspacename") : details[2];
        device_password = details[3].isEmpty() ? FileIO.getDeviceConfig("devicepassword") : details[3];
    }
}

class AppConfigs{
    public String theme;
    public String control_mode;
    public String auto_launch;
    public String permissions_access;
    public String bg_processes;
    public String external_server;

    public AppConfigs(){
        theme = "Default";
        control_mode = "Console";
        auto_launch = "false";
        permissions_access = "false";
        bg_processes = "false";
        external_server = "false";
    }

    public AppConfigs(String details[]){
        theme = details[0].isEmpty() ? FileIO.getAppConfig("theme") : details[0];
        control_mode = details[1].isEmpty() ? FileIO.getAppConfig("controlmode") : details[1];
        auto_launch = details[2].isEmpty() ? FileIO.getAppConfig("autolaunch") : details[2];
        permissions_access = details[3].isEmpty() ? FileIO.getAppConfig("permissions") : details[3];
        bg_processes = details[4].isEmpty() ? FileIO.getAppConfig("bgprocesses") : details[4];
        external_server = details[5].isEmpty() ? FileIO.getAppConfig("server") : details[5];
    }
}