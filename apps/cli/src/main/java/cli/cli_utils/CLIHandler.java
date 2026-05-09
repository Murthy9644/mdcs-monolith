package cli.cli_utils;

import java.util.Properties;

import user_auth.UserAuth;

public class CLIHandler {
    ConsoleIO io;
    Properties APP, VERSIONS;
    
    public void handleSignup(){
        io.muted("username: "); String username = io.ask();
        io.muted("email: "); String email = io.ask();

        String password;
        String conf_password;

        while (true){
            io.muted("create password: "); password = io.ask();
            io.muted("confirm password: "); conf_password = io.ask();

            if (password.equals(conf_password)) break;
            else
                io.error("passwords do not match\n");
        }

        boolean success = UserAuth.signup(username, email, password);

        if (success) io.success("registered successfully\n");
        else io.error("registration failed\n");
    }

    public void handleSignin(){
        io.muted("email: "); String email = io.ask();
        io.muted("password: "); String password = io.ask();

        boolean success = UserAuth.signin(email, password);

        if (success) io.success("authenticated successfully\n");
        else io.error("authentication failed\n");
    }

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
