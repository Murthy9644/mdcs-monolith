package cli.interaction.auth;

import cli.utils.tools.ConsoleIO;
import models.auth.AuthInteractor;

public class CLIAuthInteractor implements AuthInteractor {
    private ConsoleIO io;
    
    public String getUsername(){
        this.io.muted("Username: ");
        
        return this.io.ask();
    }
    
    public String getEmail(){
        this.io.muted("Email: ");
        String email = this.io.ask();

        // Regex test for email

        return email;
    }
    
    public String getPassword(){
        this.io.muted("Create password: ");
        
        return this.io.ask();
    }
    
    public String confirmPassword(){
        this.io.muted("Confirm password: ");
        
        return this.io.ask();
    }
    
    public String getOTP(){
        this.io.print("OTP has been sent to your email.\nPlease enter it here: ");
        
        return this.io.ask();
    }
    
    public String getDeviceName(){
        this.io.muted("Set device name: ");
        
        return this.io.ask();
    }
    
    public String getWorkspaceName(){
        this.io.muted("Set workspace name: ");
        
        return this.io.ask();
    }

    public CLIAuthInteractor(ConsoleIO io){
        this.io = io;
    }
}
