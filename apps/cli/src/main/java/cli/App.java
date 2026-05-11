package cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import bootstrap.BootstrapHandler;
import cli.cli_utils.AuthPipe;
import cli.cli_utils.CLIHandler;
import cli.cli_utils.ConfigLoader;
import cli.cli_utils.ConsoleIO;

public class App {
    private ConsoleIO io;
    private CLIHandler handler;
    private Properties APP, VERSIONS;

    private boolean bootstrap(){
        this.io.info("Initializing application bootstrap\n");
        
        try{
            HashMap<String, String> res = BootstrapHandler.run(this.APP, this.VERSIONS);

            switch (res.get("status")) {
                case "INVALID_VERSION_FORMAT":
                    this.io.critical("Invalid version format: " + res.get("body") + "\n");
                    this.io.print("Expected format: MAJOR.MINOR.PATCH (eg., 1.2.3)\n");
                    
                    break;

                case "INCOMPATIBLE_MODULE":
                    this.io.critical(
                        "Module compatibility check failed: "
                        + res.get("body").split("<>")[0]
                        + " ("
                        + res.get("body").split("<>")[1]
                        + ")\n"
                    );

                    break;

                case "UPDATE_CHECK_FAILED":
                    this.io.error("Failed to check for updates\n");

                    break;

                case "INVALID_FILE_SCHEMA":
                    this.io.error("Invalid data values for:\n\n");

                    for (String s : res.get("body").split("<>"))
                        this.io.print(s + "\n");

                    this.io.print("\n");

                    break;

                case "FILE_WRITE_FAILURE":
                    this.io.critical("Failed to write to the file: ");
                    this.io.print(res.get("body") + "\n");

                    break;

                case "INVALID_FILE_FORMAT":
                    String ack = "Failed to parse file: " + res.get("body") + "\n";

                    if (res.get("app_state").equals("terminate"))
                        this.io.critical(ack);
                    else
                        this.io.error(ack);

                    break;
            }

            if (res.get("app_state").equals("terminate")){
                this.io.critical(res.get("message") + "\n");

                return false;
            }

            this.io.info(res.get("message") + "\n");

            if (res.get("user_state").equals("USER_AUTH_REQUIRED")){
                AuthPipe pipe = new AuthPipe(io);
                pipe.start();
            } else if (
                res.get("user_state").equals("USER_LOGGED_IN")
                && !AuthPipe.verifyAuthToken()
            ){
                AuthPipe pipe = new AuthPipe(io);
                pipe.start();
            }
        } catch (Exception e){
            e.printStackTrace();

            return false;
        }

        return true;
    }

    private void commandInterface(){
        String command;

        while (true) {
            this.io.specifier("> ");
            command = this.io.ask();

            switch (command){
                case "refresh":
                    this.handler.handleRestart();
                    break;

                case "exit": return;

                default: this.io.error("Invalid command. Use 'help' for more info.\n");
            }
        }
    }

    public void start() {
        String header_string = this.APP.getProperty("app.name");
        header_string += " V" + this.VERSIONS.getProperty("app.version") + "\n";
        this.io.heading(header_string);

        if (!this.bootstrap()) return;

        this.commandInterface();
    }

    public App(){
        this.io = new ConsoleIO();

        try{
            this.APP = new ConfigLoader("application.properties").property;
            this.VERSIONS = new ConfigLoader("versions.properties").property;
        } catch (IOException e){
            this.io.error("File not found: couldn't find or load config files\n");
            System.exit(0);
        }
        
        this.handler = new CLIHandler(io, this.APP, this.VERSIONS);
    }
}
