package cli.cli_utils;

import user_auth.UserAuth;

public class CLIHandler {
    IO io;
    
    public void handleSignup(){
        String username = io.ask("muted", "username: ");
        String email = io.ask("muted", "email: ");
        String password;
        String conf_password;

        while (true){
            password = io.ask("muted", "create password: ");
            conf_password = io.ask("muted", "confirm password: ");

            if (password.equals(conf_password)) break;
            else
                io.say("error", "passwords do not match\n");
        }

        boolean success = UserAuth.signup(username, email, password);

        if (success) io.say("success", "registered successfully\n");
        else io.say("error", "registration failed\n");
    }

    public void handleSignin(){
        String email = io.ask("muted", "email: ");
        String password = io.ask("muted", "password: ");

        boolean success = UserAuth.signin(email, password);

        if (success) io.say("success", "authenticated successfully\n");
        else io.say("error", "authentication failed\n");
    }

    public void handleRestart() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public CLIHandler(IO inou){
        io = inou;
    }
}
