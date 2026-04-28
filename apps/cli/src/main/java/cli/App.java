package cli;

import java.io.IOException;
import java.util.Properties;

import cli.cli_utils.CLIHandler;
import cli.cli_utils.ConfigLoader;
import cli.cli_utils.ConsoleIO;

public class App {
    private ConsoleIO io;
    private CLIHandler handler;
    private Properties APP, VERSIONS;

    public void start() {
        String header_string = APP.getProperty("app.name");
        header_string += " " + VERSIONS.getProperty("app.version") + "\n";
        io.say("heading", header_string);
        
        String command;

        while (true) {
            command = io.ask("specifier", "> ");

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
                    io.say("error", "unknown command\n");
            }
        }
    }

    public App(){
        io = new ConsoleIO();

        try{
            APP = new ConfigLoader("application.properties").property;
            VERSIONS = new ConfigLoader("versions.properties").property;
        } catch (IOException e){
            io.say("error", "File not found: couldn't find or load config files\n");
        }
        
        handler = new CLIHandler(io, APP, VERSIONS);
    }
}
