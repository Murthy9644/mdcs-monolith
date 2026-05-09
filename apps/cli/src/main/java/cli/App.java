package cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import bootstrap.BootstrapHandler;
import cli.cli_utils.CLIHandler;
import cli.cli_utils.ConfigLoader;
import cli.cli_utils.ConsoleIO;

public class App {
    private ConsoleIO io;
    private CLIHandler handler;
    private Properties APP, VERSIONS;

    private boolean bootstrap(){
        io.info("Application bootstrap started\n");
        
        try{
            HashMap<String, String> res = BootstrapHandler.run(APP, VERSIONS);

            switch (res.get("status")) {
                case "INVALID_VERSION_FORMAT":
                    io.critical("Invalid version format: " + res.get("body") + "\n");
                    io.print("Expected format: MAJOR.MINOR.PATCH (eg., 1.2.3)\n");
                    
                    break;

                case "INCOMPATIBLE_MODULE":
                    io.critical(
                        "Module compatibility check failed: "
                        + res.get("body").split("<>")[0]
                        + " ("
                        + res.get("body").split("<>")[1]
                        + ")\n"
                    );

                    break;

                case "UPDATE_CHECK_FAILED":
                    io.error("Failed to check for updates\n");

                    break;

                case "INVALID_FILE_SCHEMA":
                    io.error("Invalid data values for:\n\n");

                    for (String s : res.get("body").split("<>"))
                        io.print(s + "\n");

                    io.print("\n");

                    break;

                case "FILE_WRITE_FAILURE":
                    io.critical("Failed to write to the file: ");
                    io.print(res.get("body") + "\n");

                    break;

                case "INVALID_FILE_FORMAT":
                    String ack = "Failed to parse file : " + res.get("body") + "\n";

                    if (res.get("app_state").equals("terminate"))
                        io.critical(ack);
                    else
                        io.error(ack);

                    break;
            }

            io.info(res.get("message") + "\n");

            if (res.get("app_state").equals("terminate")){

                return false;
            }

        } catch (Exception e){
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public void start() {
        String header_string = APP.getProperty("app.name");
        header_string += " " + VERSIONS.getProperty("app.version") + "\n";
        io.heading(header_string);

        if (!bootstrap()) return;
        
        String command;

        while (true) {
            io.specifier("> ");
            command = io.ask();

            switch (command){
                case "signup":
                    handler.handleSignup();
                    break;

                case "signin":
                    handler.handleSignin();
                    break;

                case "restart":
                    handler.handleRestart();
                    break;

                case "exit":
                    return;

                default:
                    io.error("unknown command\n");
            }
        }
    }

    public App(){
        io = new ConsoleIO();

        try{
            APP = new ConfigLoader("application.properties").property;
            VERSIONS = new ConfigLoader("versions.properties").property;
        } catch (IOException e){
            io.error("File not found: couldn't find or load config files\n");
            System.exit(0);
        }
        
        handler = new CLIHandler(io, APP, VERSIONS);
    }
}
