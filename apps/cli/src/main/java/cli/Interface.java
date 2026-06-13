package cli;

import java.util.Properties;

import cli.utils.CLI;
import cli.utils.ConsoleIO;

public class Interface {
    private ConsoleIO io;
    private CLI handler;

    public void begin(){
        String command;
        this.io.highlight("Command Interface");

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
        this.handler = new CLI(io, APP, VERSIONS);
    }
}
