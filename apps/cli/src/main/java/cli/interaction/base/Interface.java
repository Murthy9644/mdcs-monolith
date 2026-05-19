package cli.interaction.base;

import java.util.Properties;

import cli.utils.command_util.CLIHandler;
import cli.utils.tools.ConsoleIO;

public class Interface {
    private ConsoleIO io;
    private CLIHandler handler;

    public void begin(){
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
    
    public Interface(ConsoleIO io, Properties APP, Properties VERSIONS){
        this.io = io;
        this.handler = new CLIHandler(io, APP, VERSIONS);
    }
}
