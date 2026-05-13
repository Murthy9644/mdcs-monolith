package cli.helpers;

import java.util.Properties;

import bootstrap.BootstrapHandler;
import cli.utils.command_util.AuthPipe;
import cli.utils.tools.ConsoleIO;
import response_classes.BootstrapResponse.*;

public class BootstrapTerminal {
    private Properties APP, VERSIONS;
    private GeneralResponse res;
    private ConsoleIO io;

    private boolean updateHandler() {

        // Critical update
        if (res.status == Status.CRITICAL_UPDATE) {
            io.critical("Update required to continue to the application\n");

            io.muted("Current version: ");
            io.print(res.update_info.app_curr_ver + "\n");
            io.muted("Available version: ");
            io.print(res.update_info.app_avail_ver + "\n");

            io.print("\nChanges:\n");

            for (String s : res.update_info.changes)
                io.print("\t- " + s + "\n");

            io.info(res.message);

            io.print("Do you want to continue downloading the updates? (y/n) ");
            String choice = io.ask();

            // Place holder for update sequence (later)
            if (choice.equalsIgnoreCase("y")) {
                this.io.info("Updated successfully. Re-launching the application.");
                System.exit(0);
            } 
            
            else 
                return false;
        }

        // Normal updates
        if (res.update_info != null) {

            if (res.update_info.app_update_avail) {

                if (res.update_info.update_type == UpdateType.OPTIONAL)
                    io.warn("Optional update is available to download\n");
                else
                    io.info("Update is available to download\n");

                io.muted("Current version: ");
                io.print(res.update_info.app_curr_ver + "\n");
                io.muted("Available version: ");
                io.print(res.update_info.app_avail_ver + "\n");

            } 
            
            boolean plugin_updates_exist = 
                res.update_info.plugin_ver != null 
                && !res.update_info.plugin_ver.isEmpty();
            
            if (plugin_updates_exist) {
                io.info("These plugin(s) can be updated:\n");

                for (String plugin_name : res.update_info.plugin_ver.keySet()) {
                    PluginInfo plugin_info = res.update_info.plugin_ver.get(plugin_name);

                    io.print("\n- " + plugin_name + "\n");
                    io.muted("Current version: ");
                    io.print(plugin_info.installed_ver + "\n");
                    io.muted("Available version: ");
                    io.print(plugin_info.available_ver + "\n");
                }
            }

            io.print("\nChanges:\n");

            for (String s : res.update_info.changes)
                io.print("\t- " + s + "\n");

            io.print("Do you want to continue downloading the updates? (y/n) ");
            String choice = io.ask();

            // Place holder for update sequence (later)
            if (choice.equalsIgnoreCase("y")) {
                this.io.info("Updated successfully. Re-launching the application.");
                System.exit(0);
            }
        }

        return true;
    }

    public boolean appStatusCheck() {

        switch (res.status) {
            case Status.CHECK:
                break;

            case Status.CRITICAL_UPDATE:
                break;

            case Status.INVALID_UPDATE_RESPONSE:
                this.io.warn("Failed to validate update response\n");

            case Status.INVALID_VERSION_FORMAT:
                this.io.critical("Invalid version format: " + res.body.get(0) + "\n");
                this.io.print("Expected format: MAJOR.MINOR.PATCH (eg., 1.2.3)\n");

                break;

            case Status.UPDATE_CHECK_FAILED:
                this.io.error("Failed to check for updates\n");

                break;

            case Status.INVALID_FILE_SCHEMA:
                this.io.error("Invalid data values for:\n\n");

                for (String s : res.body)
                    this.io.print(s + "\n");

                this.io.print("\n");

                break;

            case Status.FILE_WRITE_FAILURE:
                this.io.critical("Failed to write to the file: ");
                this.io.print(res.body.get(0) + "\n");

                break;

            case Status.INVALID_FILE_FORMAT:
                String ack = "Failed to parse file: " + res.body.get(0) + "\n";

                if (res.app_state == AppState.TERMINATE)
                    this.io.critical(ack);
                else
                    this.io.error(ack);

                break;
        }

        if (res.app_state == AppState.TERMINATE){
            this.io.critical(res.message + "\n");
            return false;
        }

        this.io.info(res.message + "\n");

        return true;
    }

    public void userState(){
        if (
            res.user_state == UserState.USER_AUTH_REQUIRED
            || (
                res.user_state == UserState.USER_LOGGED_IN
                && !AuthPipe.verifyAuthToken()
            )
        ){
            AuthPipe pipe = new AuthPipe(io);
            pipe.start();
        }
    }

    public boolean initiate() {
        this.io.info("Initializing application bootstrap\n");

        // Initialize bootstrap
        try {
            this.res = BootstrapHandler.run(APP, VERSIONS);
        }

        catch (Exception e) {
            this.io.critical(e.getMessage() + "\n");
            return false;
        }

        if (this.updateHandler() && this.appStatusCheck()){
            this.userState();
            return true;
        }

        return false;
    }

    public BootstrapTerminal(Properties APP, Properties VERSIONS) {
        this.APP = APP;
        this.VERSIONS = VERSIONS;
        this.io = new ConsoleIO();
    }
}
