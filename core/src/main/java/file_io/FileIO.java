package file_io;

import com.fasterxml.jackson.databind.ObjectMapper;

import utils.SystemUtils;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileIO {
    private static ObjectMapper mapper;
    private static File user_file;
    private static File device_file;
    private static File config_file;
    private static UserDataFile user = null;
    private static DeviceConfigs device = null;
    private static AppConfigs app = null;

    public static String getUserAttr(String attr) {
        readFile("userdata");

        switch (attr) {
            case "userid":
                return user.user_id;

            case "username":
                return user.user_name;

            case "email":
                return user.email;

            default:
                return null;
        }
    }

    public static String getDeviceConfig(String config) {
        readFile("deviceconfig");

        switch (config) {
            case "deviceid":
                return device.device_id;

            case "devicename":
                return device.device_name;

            case "workspacename":
                return device.workspace_name;

            case "devicepassword":
                return device.device_password;

            default:
                return null;
        }
    }

    public static String getAppConfig(String config) {
        readFile("appconfig");

        switch (config) {
            case "theme":
                return app.theme;

            case "controlmode":
                return app.control_mode;

            case "autolaunch":
                return app.auto_launch;

            case "permissions":
                return app.permissions_access;

            case "bgprocesses":
                return app.bg_processes;

            case "server":
                return app.external_server;

            default:
                return null;
        }
    }

    public static void readFile(String file_name) {

        switch (file_name) {
            case "user":

                try {
                    user = mapper.readValue(user_file, UserDataFile.class);
                } catch (Exception e) {
                    if (user == null)
                        user = new UserDataFile();
                }

                break;

            case "device":

                try {
                    device = mapper.readValue(device_file, DeviceConfigs.class);
                } catch (Exception e) {
                    if (device == null)
                        device = new DeviceConfigs();
                }

                break;

            case "config":

                try {
                    app = mapper.readValue(config_file, AppConfigs.class);
                } catch (Exception e) {
                    if (app == null)
                        app = new AppConfigs();
                }

            default:
                break;
        }
    }

    public static void writeFile(String file_name, String details[]) {

        switch (file_name) {
            case "userdata":
                user = new UserDataFile(details);

                try {
                    mapper.writeValue(user_file, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "deviceconfig":
                device = new DeviceConfigs(details);

                try {
                    mapper.writeValue(device_file, device);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "appconfig":
                if (details == null)
                    app = new AppConfigs();
                else
                    app = new AppConfigs(details);

                try {
                    mapper.writeValue(config_file, app);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            default:
                break;
        }
    }

    public FileIO(){
        mapper = new ObjectMapper();
        mapper.enable(INDENT_OUTPUT);

        String appdata_path;
        File app_folder;

        switch (SystemUtils.getOS()){
            case "WINDOWS":
                appdata_path = Paths.get(System.getenv("APPDATA")).toString();
                break;

            case "LINUX":
                if ((appdata_path = Paths.get(System.getenv("XDG_CONFIG_HOME")).toString()) == null)
                    appdata_path = Paths.get(System.getProperty("user.home"), ".config").toString();

                break;

            case "MAC":
                appdata_path = Paths.get(
                    System.getProperty("user.home"), 
                    "Library", 
                    "Application Support"
                ).toString();

                break;

            default: appdata_path = Paths.get(System.getProperty("user.home")).toString();
        }

        app_folder = new File(appdata_path.toString(), "MDCS");

        if (!app_folder.exists()) app_folder.mkdirs();

        Path user_data_path = Paths.get(
            appdata_path, "MDCS", "user.json"
        );
        user_file = user_data_path.toFile();

        Path device_conf_path = Paths.get(
            appdata_path, "MDCS", "device.json"
        );
        device_file = device_conf_path.toFile();

        Path app_config_path = Paths.get(
            appdata_path, "MDCS", "configs.json"
        );
        config_file = app_config_path.toFile();
    }
}
