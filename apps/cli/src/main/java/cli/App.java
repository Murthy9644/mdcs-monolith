package cli;

import java.io.IOException;
import java.util.Properties;

import cli.interaction.base.Interface;
import cli.interaction.bootstrap.BootstrapTerminal;
import cli.utils.tools.ConfigLoader;
import cli.utils.tools.ConsoleIO;
import network.ServerRequest;

public class App {
    private ConsoleIO io;
    private ServerRequest server;
    private Properties APP, VERSIONS;

    public void start() {
        String header_string = this.APP.getProperty("app.name");
        header_string += " V" + this.VERSIONS.getProperty("app.version") + "\n";
        this.io.heading(header_string);
        
        if (!(new BootstrapTerminal(this.server, this.VERSIONS).initiate()))
            return;
        
        new Interface(this.io, this.APP, this.VERSIONS).begin();
    }

    public App(){
        this.io = new ConsoleIO();
        this.server = new ServerRequest(APP);

        try{
            this.APP = new ConfigLoader("application.properties").property;
            this.VERSIONS = new ConfigLoader("versions.properties").property;
        } catch (IOException e){
            this.io.error("File not found: couldn't find or load config files\n");
            System.exit(0);
        }
    }
}
