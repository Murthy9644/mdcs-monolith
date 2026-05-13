package bootstrap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;

import file_io.DataClasses;
import file_io.FileIO;
import logger.Log;
import response_classes.BootstrapResponse;
import response_classes.ServerResponseClasses;
import network.ServerRequest;

public class VersionCheck {
    private static Properties VERSIONS;
    private static Log logger;

    private static boolean versionFormat(BootstrapResponse.GeneralResponse bsres) {
        logger.info("bootstrap", "Checking version format");

        for (String key : VERSIONS.stringPropertyNames()) {
            String version = VERSIONS.getProperty(key);

            if (!version.matches("^[0-9]+.[0-9]+.[0-9]$")) {
                bsres.app_state = BootstrapResponse.AppState.TERMINATE;
                bsres.status = BootstrapResponse.Status.INVALID_VERSION_FORMAT;
                bsres.body.add(version);
                bsres.message = "Application startup aborted";

                logger.error("bootstrap", "Invalid version format (" + key + ")");

                return false;
            }
        }

        logger.info("bootstrap", "Version format check passed");

        return true;
    }

    private static boolean updateCheck(BootstrapResponse.GeneralResponse bsres)
            throws Exception {
        logger.info("bootstrap", "Checking for updates");

        try {
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
                        plugin_metadata.get(plugin_name).installed_version);

            // Convert to json format string
            String json_body = FileIO.toJson(body);

            // Sending request to server
            String response = ServerRequest.post(
                "http://localhost:1097/mdcs/version/check",
                new String[] { "Content-Type", "application/json" },
                json_body
            );

            ServerResponseClasses.UpdateResponse res = FileIO.toObject(response, ServerResponseClasses.UpdateResponse.class);
            
            // Check for critical app update
            if (res.app.critical_update){
                bsres.app_state = BootstrapResponse.AppState.BLOCK;

                bsres.status = BootstrapResponse.Status.CRITICAL_UPDATE;
                bsres.message = "Application startup blocked";

                bsres.update_info.app_update_avail = true;
                bsres.update_info.update_type = BootstrapResponse.UpdateType.CRITICAL;
                bsres.update_info.app_avail_ver = res.app.available_version;
                bsres.update_info.app_curr_ver = res.app.current_version;
                bsres.update_info.changes = res.changes;

                return false;
            }

            boolean update_found = false;

            // Check for minor or patch updates
            String[] avail_update = res.app.available_version.split("\\.");
            String[] curr_update = res.app.current_version.split("\\.");

            if (Integer.parseInt(avail_update[0] > Integer.parseInt(curr_update[0]))){ // Major
                bsres.update_info.app_update_avail = true;
                bsres.update_info.update_type = BootstrapResponse.UpdateType.OPTIONAL;
                bsres.update_info.app_avail_ver = res.app.available_version;
                bsres.update_info.app_curr_ver = res.app.current_version;

                update_found = true;

            } else if (Integer.parseInt(avail_update[1]) > Integer.parseInt(curr_update[1])){ // Minor
                bsres.update_info.app_update_avail = true;
                bsres.update_info.update_type = BootstrapResponse.UpdateType.OPTIONAL;
                bsres.update_info.app_avail_ver = res.app.available_version;
                bsres.update_info.app_curr_ver = res.app.current_version;

                update_found = true;

            } else if (Integer.parseInt(avail_update[2]) > Integer.parseInt(curr_update[2])){ // Patch
                bsres.update_info.app_update_avail = true;
                bsres.update_info.update_type = BootstrapResponse.UpdateType.PATCH;
                bsres.update_info.app_avail_ver = res.app.available_version;
                bsres.update_info.app_curr_ver = res.app.current_version;

                update_found = true;

            } else bsres.update_info.app_update_avail = false;

            for (String plugin_name : res.plugins.keySet()){
                ServerResponseClasses.Plugin plugin = res.plugins.get(plugin_name);

                if (res.plugins.get(plugin_name).update_required){
                    BootstrapResponse.PluginInfo info = new BootstrapResponse.PluginInfo();
                    info.available_ver = plugin.available_version;
                    info.installed_ver = plugin.installed_version;
                    info.is_compatible = plugin.is_compatible;

                    if (bsres.update_info.plugin_ver == null)
                        bsres.update_info.plugin_ver = new HashMap<>();

                    bsres.update_info.plugin_ver.put(plugin_name, info);

                    update_found = true;
                }
            }

            if (update_found){
                bsres.update_info.changes = res.changes;
                logger.info("bootstrap", "Update is available to download");
            } else {
                logger.info("bootstrap", "Current version is up-to-date");
            }

        } catch (JsonProcessingException e) {
            bsres.app_state = BootstrapResponse.AppState.TERMINATE;
            bsres.status = BootstrapResponse.Status.INVALID_FILE_FORMAT;
            bsres.body = null;
            bsres.message = "Application startup aborted";

            return false;
        } catch (IOException e) {
            bsres.app_state = BootstrapResponse.AppState.CONTINUE;
            bsres.status = BootstrapResponse.Status.UPDATE_CHECK_FAILED;
            bsres.body = null;
            bsres.message = "Update check failed: Network issue";

            return false;
        } catch (InterruptedException e) {
            bsres.app_state = BootstrapResponse.AppState.CONTINUE;
            bsres.status = BootstrapResponse.Status.UPDATE_CHECK_FAILED;
            bsres.body = null;
            bsres.message = "Update check interrupted";

            Thread.currentThread().interrupt();

            return false;
        }

        return true;
    }

    public static boolean validate(
            Properties v_inc,
            BootstrapResponse.GeneralResponse bsres,
            Log logger_inc) throws Exception {
        VERSIONS = v_inc;
        logger = logger_inc;

        if (versionFormat(bsres) && updateCheck(bsres))
            return true;

        return false;
    }
}