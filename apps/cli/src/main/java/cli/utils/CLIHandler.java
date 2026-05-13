package cli.utils;

import java.util.Properties;

import cli.utils.tools.ConsoleIO;

public class CLIHandler {
    ConsoleIO io;
    Properties APP, VERSIONS;

    public void handleRestart() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public CLIHandler(ConsoleIO inou, Properties APP, Properties VERSIONS){
        this.io = inou;
        this.APP = APP;
        this.VERSIONS = VERSIONS;
    }
}
