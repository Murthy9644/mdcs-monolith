package bootstrap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;

import file_io.DataClasses;
import file_io.FileIO;
import logger.Log;
import models.bootstrap.ServerResponseClasses;
import models.bootstrap.BootstrapResponse.*;
import network.ServerRequest;

public class VersionCheck {
    private static ServerRequest server;
    private static Properties VERSIONS;
    private static Log logger;

    private static boolean versionFormat(GeneralResponse bsres) {
        logger.info("bootstrap", "Checking version format");
        BootstrapIssue issue = new BootstrapIssue();
        issue.phase = "Version Validation";

        for (String key : VERSIONS.stringPropertyNames()) {
            String version = VERSIONS.getProperty(key);

            if (!version.matches("^[0-9]+\\.[0-9]+\\.[0-9]$")) {
                bsres.setAppState(AppState.TERMINATE);
                issue.status = Status.INVALID_VERSION_FORMAT;
                issue.issues.add(version);
                issue.message = "Application startup aborted";

                logger.error("bootstrap", "Invalid version format: " + key);
                bsres.reports.add(issue);

                return false;
            }
        }

        logger.info("bootstrap", "Version format check passed");

        return true;
    }

    private static boolean updateCheck(GeneralResponse bsres)
            throws Exception {
        logger.network("bootstrap", "Checking for updates");
        BootstrapIssue issue = new BootstrapIssue();
        issue.phase = "Version Validation";
        bsres.update_info.update_type = null;

        try {
            // Read plugins metadata
            DataClasses.Plugins plugins = FileIO.fileRead(DataClasses.Plugins.class);
            Map<String, DataClasses.Plugin> plugin_metadata = plugins.plugins;

            if (plugin_metadata == null) plugin_metadata = new HashMap<>();

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
            String response = server.post(
                "/version/check",
                new String[] { "Content-Type", "application/json" },
                json_body
            );

            ServerResponseClasses.UpdateResponse res = FileIO.toObject(response, ServerResponseClasses.UpdateResponse.class);
            
            // Check for critical app update
            if (res.app.critical_update){
                bsres.setAppState(AppState.BLOCK);

                bsres.update_info.message = "Application startup blocked";
                bsres.update_info.app_update_avail = true;
                bsres.update_info.update_type = UpdateType.CRITICAL;
                bsres.update_info.app_avail_ver = res.app.available_version;
                bsres.update_info.app_curr_ver = res.app.current_version;
                bsres.update_info.changes = res.changes;

                logger.network("bootstrap", "Critical update detected");

                return false;
            }

            boolean update_found = false;

            // Check for minor or patch updates
            String[] avail_update = res.app.available_version.split("\\.");
            String[] curr_update = res.app.current_version.split("\\.");

            if (Integer.parseInt(avail_update[0]) > Integer.parseInt(curr_update[0])){ // Major
                bsres.update_info.app_update_avail = true;
                bsres.update_info.update_type = UpdateType.OPTIONAL;
                bsres.update_info.app_avail_ver = res.app.available_version;
                bsres.update_info.app_curr_ver = res.app.current_version;

                logger.network("bootstrap", "New application update found");

                update_found = true;

            } else if (Integer.parseInt(avail_update[1]) > Integer.parseInt(curr_update[1])){ // Minor
                bsres.update_info.app_update_avail = true;
                bsres.update_info.update_type = UpdateType.OPTIONAL;
                bsres.update_info.app_avail_ver = res.app.available_version;
                bsres.update_info.app_curr_ver = res.app.current_version;

                logger.network("bootstrap", "New application update found");

                update_found = true;

            } else if (Integer.parseInt(avail_update[2]) > Integer.parseInt(curr_update[2])){ // Patch
                bsres.update_info.app_update_avail = true;
                bsres.update_info.update_type = UpdateType.PATCH;
                bsres.update_info.app_avail_ver = res.app.available_version;
                bsres.update_info.app_curr_ver = res.app.current_version;

                logger.network("bootstrap", "New application update found");

                update_found = true;

            } else bsres.update_info.app_update_avail = false;

            for (String plugin_name : res.plugins.keySet()){
                ServerResponseClasses.Plugin plugin = res.plugins.get(plugin_name);

                // Mark incompatibility
                plugin_metadata.get(plugin_name).is_compatible = res.plugins.get(plugin_name).is_compatible;

                if (res.plugins.get(plugin_name).update_required){
                    PluginInfo info = new PluginInfo();
                    info.available_ver = plugin.available_version;
                    info.installed_ver = plugin.installed_version;
                    info.is_compatible = plugin.is_compatible;

                    if (bsres.update_info.plugin_ver == null)
                        bsres.update_info.plugin_ver = new HashMap<>();

                    bsres.update_info.plugin_ver.put(plugin_name, info);
                    bsres.update_info.update_type = UpdateType.PLUGIN;

                    logger.network("bootstrap", "New update found: " + plugin_name + " (" + plugin.available_version + ")");

                    update_found = true;
                }
            }

            plugins.plugins = plugin_metadata;
            FileIO.fileWrite(plugins);

            if (update_found){
                bsres.update_info.changes = res.changes;
            } else {
                logger.network("bootstrap", "Current version is up-to-date");
            }

            issue.issues = null;
            issue.status = null;
            issue.message = "Version validated successfully";

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            bsres.setAppState(AppState.CONTINUE);
            issue.status = Status.INVALID_UPDATE_RESPONSE;
            issue.issues = null;
            issue.message = "Proceeding without update check";

            logger.error("bootstrap", "Failed to parse response: " + e.getMessage());

            return false;
        } catch (IOException e) {
            bsres.setAppState(AppState.CONTINUE);
            issue.status = Status.UPDATE_CHECK_FAILED;
            issue.issues = null;
            issue.message = "Proceeding without update check";

            logger.error("bootstrap", "Failed to check for updates: " + e.getMessage());

            return false;
        } catch (InterruptedException e) {
            bsres.setAppState(AppState.CONTINUE);
            issue.status = Status.UPDATE_CHECK_FAILED;
            issue.issues = null;
            issue.message = "Proceeding without update check";

            logger.error("bootstrap", "Failed to check for updates: " + e.getMessage());

            Thread.currentThread().interrupt();

            return false;
        } catch (Exception e){
            logger.error("bootstrap", "Failed to validate version: " + e.getMessage());
        } finally{
            if (issue.status != null) bsres.reports.add(issue);
        }

        return true;
    }

    public static boolean validate(
            ServerRequest server_inc,
            Properties v_inc,
            GeneralResponse bsres,
            Log logger_inc) throws Exception {
        server = server_inc;
        VERSIONS = v_inc;
        logger = logger_inc;

        if (versionFormat(bsres) && updateCheck(bsres))
            return true;

        return false;
    }
}