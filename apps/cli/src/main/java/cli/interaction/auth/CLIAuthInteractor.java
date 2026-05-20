package cli.interaction.auth;

import cli.utils.tools.ConsoleIO;
import models.auth.AuthInteractor;

public class CLIAuthInteractor implements AuthInteractor {
    private ConsoleIO io;
    
    public String getUsername(){
        this.io.muted("Username: ");
        String username = "";

        while (username.isEmpty()){
            username = this.io.ask();
        }
        
        return username;
    }
    
    public String getEmail(){
        this.io.muted("Email: ");
        String email = "";

        while (email.isEmpty())
            email = this.io.ask();

        // Regex test for email

        return email;
    }
    
    public String getPassword(){
        this.io.muted("Create password: ");
        String pswd = "";

        while (pswd.isEmpty())
            pswd = this.io.ask();
        
        return pswd;
    }
    
    public String confirmPassword(){
        this.io.muted("Confirm password: ");
        String conf = "";

        while (conf.isEmpty())
            conf = this.io.ask();
        
        return conf;
    }
    
    public String getOTP(){
        this.io.print("OTP has been sent to your email.\nPlease enter it here: ");
        String otp = "";

        while (otp.isEmpty())
            otp = this.io.ask();
        
        return otp;
    }
    
    public String getDeviceName(){
        this.io.muted("Set device name: ");
        String name = "";

        while (name.isEmpty())
            name = this.io.ask();
        
        return name;
    }
    
    public String getWorkspaceName(){
        this.io.muted("Set workspace name: ");
        String name = "";

        while (name.isEmpty())
            name = this.io.ask();
        
        return name;
    }

    public CLIAuthInteractor(ConsoleIO io){
        this.io = io;
    }
}
