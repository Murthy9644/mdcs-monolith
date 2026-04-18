package cli;

import cli.cli_utils.CLIHandler;
import cli.cli_utils.IO;

public class App {
    private IO io;
    private CLIHandler handler;

    public void start() {
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
