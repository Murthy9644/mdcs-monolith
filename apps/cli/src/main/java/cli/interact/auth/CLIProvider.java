package cli.interact.auth;

import cli.utils.ConsoleIO;
import models.auth.Provider;

public class CLIProvider implements Provider {
    private ConsoleIO io;
    
    public String username(){
        this.io.muted("Username: ");
        String username = "";

        while (username.isEmpty()){
            username = this.io.ask();
        }
        
        return username;
    }
    
    public String email(){
        this.io.muted("Email: ");
        String email = "";

        while (email.isEmpty())
            email = this.io.ask();

        // Regex test for email

        return email;
    }

    public void pswdRules(){
        this.io.highlight("Password rules:");
        this.io.print("Password must contain atleast one\n");
        this.io.print("\t- Uppercase alphabet\t(A-Z)\n");
        this.io.print("\t- Lowercase alphabet\t(a-z)\n");
        this.io.print("\t- Special character\t(!@#$%^&*_)\n");
        this.io.print("\t- Digit\t(0-9)\n");
        this.io.print("Password must be atleast 6 digits long\n");
    }
    
    public String pswd(){
        this.io.muted("Create password: ");
        String pswd = "";

        while (pswd.isEmpty())
            pswd = this.io.ask();
        
        return pswd;
    }
    
    public String confirmPswd(){
        this.io.muted("Confirm password: ");
        String conf = "";

        while (conf.isEmpty())
            conf = this.io.ask();
        
        return conf;
    }

    public void pswdsMismatch(){
        this.io.error("Passwords DO NOT match\n");
    }
    
    public String otp(){
        this.io.print("OTP has been sent to your email.\nPlease enter it here: ");
        String otp = "";

        while (otp.isEmpty())
            otp = this.io.ask();
        
        return otp;
    }
    
    public String deviceName(){
        this.io.muted("Set device name: ");
        String name = "";

        while (name.isEmpty())
            name = this.io.ask();
        
        return name;
    }
    
    public String workspaceName(){
        this.io.muted("Set workspace name: ");
        String name = "";

        while (name.isEmpty())
            name = this.io.ask();
        
        return name;
    }

    public CLIProvider(ConsoleIO io){
        this.io = io;
    }
}
