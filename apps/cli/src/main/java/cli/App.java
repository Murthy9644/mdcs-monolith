package cli;

import java.io.IOException;
import java.util.Properties;

import cli.interaction.base.Interface;
import cli.utils.tools.ConfigLoader;
import cli.utils.tools.ConsoleIO;
import network.ProtoMet;

/**
 * Manager to control entire application flow, from the moment it is launched until it is closed.
 * It is responsible for initializing the application, loading configuration files, and managing
 * the user interface.
 */

public class App {
    private ConsoleIO io;
    private ProtoMet server;
    private Properties APP, VERSIONS;

    public void start() {
        String header_string = this.APP.getProperty("app.name");
        header_string += " v" + this.VERSIONS.getProperty("app.version") + "\n";
        this.io.heading(header_string);
        
        new Interface(this.io, this.APP, this.VERSIONS).begin();
    }

    /**
     * Runs when the application is launched without command line arguments.
     */
    public App(){
        this.io = new ConsoleIO();
        
        try{
            this.APP = new ConfigLoader("application.properties").property;
            this.VERSIONS = new ConfigLoader("versions.properties").property;
        } catch (IOException e){
            this.io.error("File not found: couldn't find or load config files\n");
            System.exit(0);
        }

        this.server = new ProtoMet(APP);
    }
}
