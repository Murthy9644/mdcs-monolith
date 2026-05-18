package cli.helpers;

import java.util.Properties;

import bootstrap.BootstrapHandler;
import cli.utils.command_util.AuthPipe;
import cli.utils.tools.ConsoleIO;
import models.bootstrap.BootstrapResponse.*;
import network.ServerRequest;

public class BootstrapTerminal {
    private Properties VERSIONS;
    private ServerRequest server;
    private GeneralResponse res;
    private ConsoleIO io;

    private boolean updateHandler() {

        // Critical update
        if (res.update_info != null
                &&
                res.update_info.update_type == UpdateType.CRITICAL) {
            io.critical("Update required to continue to the application\n");

            io.muted("Current version: ");
            io.print(res.update_info.app_curr_ver + "\n");
            io.muted("Available version: ");
            io.print(res.update_info.app_avail_ver + "\n");

            io.print("\nChanges:\n");

            for (String s : res.update_info.changes)
                io.print("- " + s + "\n");

            io.print("\n");
            io.info(res.update_info.message + "\n");

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
        if (res.update_info.update_type != null) {

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

            boolean plugin_updates_exist = res.update_info.plugin_ver != null
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

    private boolean appStatusCheck() {

        for (BootstrapIssue issue : res.reports) {
            io.info("Report: " + issue.phase + "\n");

            switch (issue.status) {
                case Status.CHECK:
                    break;

                case Status.CRITICAL_UPDATE:
                    break;

                case Status.INVALID_UPDATE_RESPONSE:
                    this.io.warn("Update check failed: Parsing issue\n");
                    
                    break;

                case Status.INVALID_VERSION_FORMAT:
                    this.io.critical("Invalid version format: " + issue.issues.get(0) + "\n");
                    this.io.info("Expected format: MAJOR.MINOR.PATCH (eg., 1.2.3)\n");

                    break;

                case Status.UPDATE_CHECK_FAILED:
                    this.io.error("Update check failed: Network issue\n");

                    break;

                case Status.INVALID_FILE_SCHEMA:
                    this.io.error("Invalid data values for:\n");

                    for (String s : issue.issues)
                        this.io.print(s + "\n");

                    this.io.print("\n");

                    break;

                case Status.FILE_WRITE_FAILURE:
                    this.io.critical("Failed to write to the file: ");
                    this.io.print(issue.issues.get(0) + "\n");

                    break;

                case Status.INVALID_FILE_FORMAT:

                    for (String s : issue.issues){
                        String ack = "Failed to parse file: " + s + "\n";

                        if (res.app_state == AppState.TERMINATE)
                            this.io.critical(ack);
                        else
                            this.io.error(ack);
                    }

                    break;
            }

            if (res.app_state == AppState.TERMINATE) {
                this.io.critical(issue.message + "\n");
                return false;
            }

            this.io.info(issue.message + "\n");
        }

        return true;
    }

    private void userState() {
        if (res.user_state == UserState.USER_AUTH_REQUIRED
                || (res.user_state == UserState.USER_LOGGED_IN
                        && !AuthPipe.verifyAuthToken())) {
            AuthPipe pipe = new AuthPipe(io, server);
            pipe.start();
        }
    }

    public boolean initiate() {
        this.io.info("Initializing application bootstrap\n");

        // Initialize bootstrap
        try {
            this.res = BootstrapHandler.run(this.server, VERSIONS);
        }

        catch (Exception e) {
            this.io.critical(e.getMessage() + "\n");
            return false;
        }

        if (this.updateHandler() && this.appStatusCheck())
            this.userState();

        this.io.info(res.summary);

        return false;
    }

    public BootstrapTerminal(ServerRequest server, Properties VERSIONS) {
        this.server = server;
        this.VERSIONS = VERSIONS;
        this.io = new ConsoleIO();
    }
}