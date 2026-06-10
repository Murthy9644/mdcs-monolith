package bootstrap;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import logger.Log;
import models.bootstrap.Network;
import models.bootstrap.Network.UpdRes;
import models.jobs.Report;
import models.jobs.Report.AppState;
import models.jobs.Report.Job;
import models.jobs.Report.JobType;
import network.ProtoMet;
import fileio.DataClasses;
import fileio.FileIO;

/*
Validates version format, checks for update and plugin compatibilty between plugins and
application version.
*/

/*
For now only plugin to application compatibility is checked. Assuming if a plugin is compatible
with the application, they are compatible with another plugin which is compatible with the 
application aswell.
*/

public class Version implements Runnable{
    private Report report;
    private Job job;
    private Log logger;
    private ProtoMet server;
    private Properties ver;
    private Network.UpdRes ver_meta;
    private DataClasses.Plugins plugins;
    
    private void pluginUpate(){
        /*
        Plugin update availabiity can be of 2 types.
        One is when new version is available but the current version is still supported by the
        application.
        Another is when current version has become incompatible with the application. In such
        cases, respective plugin is marked incompatible and its existence is neglected.
        */

        this.logger.info(
            "bootstrap", 
            "Checking for plugins updates and compatibility"
        );

        this.job.logs.add("info<>Checking for plugins updates and compatibility");

        Map<String, UpdRes.Plugin> plugins = this.ver_meta.body.plugins;

        for (String name : plugins.keySet()){
            UpdRes.Plugin plugin = plugins.get(name);
            String curr_ver = plugin.curr_ver;
            String avail_ver = plugin.avail_ver;

            // Set compatibility
            this.plugins.plugins.get(name).compatible = plugin.compatible;

            if (plugin.upd_req){
                // Pass plugin name, currnt version, available version and continue to application

                this.logger.info(
                    "bootstrap", 
                    "Plugin update available [" + name + "|" + curr_ver + "|" + avail_ver + "]"
                );

                this.job.logs.add("_<>- " + name);
                this.job.logs.add("_<>Current version: " + curr_ver);
                this.job.logs.add("_<>Available version: " + avail_ver);
            }
        }
        
        try{
            FileIO.fileWrite(this.plugins);
        } catch (Exception e){
            /*
            When can't write the plugin compatibility back to file, can't say if that plugin is
            valid or not further in application. So, treating all the plugins incompatible. But
            wait, what is even the purpose of the application alone when don't have any plugins
            => Terminate application startup.
            */

            this.logger.error(
                "bootstrap", 
                "Failed to persist plugin compatibility"
            );

            this.job.logs.add("critical<>Failed to persist plugin compatibiliy");
        }
    }
    
    private void appUpdate(){
        /*
        From the metadata we get from the server, will decide if app update is available or not.
        An app update is classified into:
                - Critical update
                - Minor update
                - Patch update
    
        In case of critical update, will block the main app execution (May include modular block
        in future updates).
        In any other cases, will continue to app after noticing the user about the update.
        */
       
        this.logger.info("bootstrap", "Checking for app updates");
        this.job.logs.add("info<>Checking for app updates");

        String curr_ver = this.ver_meta.body.app.cur_ver;
        String avail_ver = this.ver_meta.body.app.avail_ver;
    
        // Checking for critical update
        if (this.ver_meta.body.app.critical_update){
            // Block the app startup and inform user

            this.logger.info(
                "bootstrap", 
                "New (critical) update available for installation"
            );

            this.job.logs.add("info<>New (critical) update available for installation");
            this.job.logs.add("_<>Current version: " + curr_ver);
            this.job.logs.add("_<>Available version: " + avail_ver);

            this.report.setAppState(AppState.BLOCK);

            return;
        }
    
        // Checking for other available updates
        String avail[] = avail_ver.split("//.");
        String curr[] = curr_ver.split("//.");
    
        if (
            Integer.parseInt(avail[0]) > Integer.parseInt(curr[0])
            || Integer.parseInt(avail[1]) > Integer.parseInt(curr[1])
            || Integer.parseInt(avail[2]) > Integer.parseInt(curr[2])
        ){
            // New update available => Notify user and continue app execution

            this.logger.info(
                "bootstrap", 
                "New update available for installation"
            );

            this.job.logs.add("info<>New update available for installation");
            this.job.logs.add("_<>Current version: " + curr_ver);
            this.job.logs.add("_<>Available version: " + avail_ver);
        }
    }

    private void metadata()
    throws RuntimeException{
        /*
        It may be noted that this metadata is only for version validation and update check during
        bootstrap. Later when user wants to update the applcation or plugins, they will use update
        manager which will require another kind of metadata. Thus, there is no need to persist
        this out of this class.
        */

        this.logger.network("bootstrap", "Getting version metadata");
        
        try{
            // Data as written by local plugins available on user device
            this.plugins = FileIO.fileRead(DataClasses.Plugins.class);
            Map<String, DataClasses.Plugin> plg_data;

            if ((plg_data = this.plugins.plugins) == null)
                plg_data = new HashMap<>();

            // Final object to send to server
            Map<String, Map<String, String>> body = new HashMap<>();

            Map<String, String> appver = new HashMap<>();
            appver.put("current_version", ver.getProperty("app.version"));

            body.put("app", appver);

            body.put("plugins", new HashMap<>());

            for (String name : plg_data.keySet())
                body.get("plugins").put(
                    name,
                    plg_data.get(name).avai_ver
                );

            String json = FileIO.toJson(body);

            HttpResponse<String> res = server.post(
                "/version/check",
                new String[] { "Content-Type", "application/json" },
                json
            );
            
            this.logger.network("bootstrap", "Version metadata received");

            // Handle internal errors
            
            this.ver_meta = FileIO.toObject(res.body(), Network.UpdRes.class);
        } catch (IOException e){
            /*
            Means, failed to write/read from a file (user/environment related issue). In this case
            can't reliably move forward with plugins update checks, but can check for application
            updates.
            */
           
            this.logger.error(
                "bootstrap", 
                "Failed to persist plugin compatibility"
            );

            this.job.logs.add("error<>Failed to persist plugin compatibility");

            throw new RuntimeException();

        } catch (InterruptedException e){
            /*
            This implies that server request was interrupted while in process. So, will continue
            to application without update check
            */

            this.logger.network(
                "bootstrap", 
                "Server request for version meta was interrupted"
            );

            this.job.logs.add("error<>Server request for version meta was interrupted");

            Thread.currentThread().interrupt();

        } catch (NoSuchFieldException | IllegalAccessException e){
            /*
            This error is not user/environment caused. This is a development bug. Generally
            application is terminated because, this may cause unexpected behaviors
            */

            this.report.setAppState(AppState.TERMINATE);

            this.logger.error(
                "bootstrap", 
                "Reflection error @ DataClasses.plugins" + " | " + e.getMessage()
            );

            this.job.logs.add("critical<>" + e.getMessage());
        }
    }

    private boolean format() {
        /*
        A particular module's version is valid if it matches the RegEx below. If a particular
        version string is not valid, we abort the startup of the application (for now). In future
        versions, can suspend afected modules and continue to application.
        */

        for (String key : this.ver.stringPropertyNames()) {
            String version = this.ver.getProperty(key);

            if (!version.matches("^[0-9]+\\.[0-9]+\\.[0-9]$")) {
                // The version of this module is not in the valid form.

                this.logger.error(
                    "bootstrap", 
                    "Invalid version format | " + key + "=" + version
                );

                this.job.logs.add(
                    "critical<>Invalid version format | " + key +"=" + version
                );

                return false;
            }
        }

        return true;
    }

    @Override
    public void run(){
        this.logger.info("bootstrap", "Started version and update check");

        if (!this.format()){
            // Stop application startup
            
            this.logger.error(
                "bootstrap", 
                "Application startup terminated because of invalid version format"
            );

            this.job.logs.add(
                "error<>Application startup terminated because of invalid version format"
            );
        }

        this.metadata();
        this.appUpdate();
        this.pluginUpate();

        this.report.jobs.add(this.job);
    }
    
    private Version(Report report, Log logger, ProtoMet server, Properties ver){
        this.report = report;
        this.logger = logger;
        this.server = server;
        this.ver = ver;

        this.job = new Job();
        this.job.type = JobType.VERSION;
    }
}
