package cli.utils;

import java.util.Properties;

public class CLI {
    ConsoleIO io;
    Properties APP, VERSIONS;

    public void handleRestart() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public CLI(ConsoleIO inou, Properties APP, Properties VERSIONS){
        this.io = inou;
        this.APP = APP;
        this.VERSIONS = VERSIONS;
    }
}
