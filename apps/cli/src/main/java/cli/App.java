package cli;

import cli.cli_utils.CLIHandler;
import cli.cli_utils.IO;

public class App {
    private IO io;
    private CLIHandler handler;

    public void start() {
        io.say("heading", "MDCS v1.0.0\n");
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
        io = new IO();
        handler = new CLIHandler(io);
    }
}
