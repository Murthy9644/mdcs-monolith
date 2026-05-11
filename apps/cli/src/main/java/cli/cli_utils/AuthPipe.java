package cli.cli_utils;

import user_auth.UserAuth;

public class AuthPipe {
    private ConsoleIO io;

    public static boolean verifyAuthToken(){

        // Place holder for now

        return true;
    }
    
    public void handleSignup(){
        io.muted("Username: "); String username = io.ask();
        io.muted("Email: "); String email = io.ask();

        String password;
        String conf_password;

        while (true){
            io.muted("Create password: "); password = io.ask();
            io.muted("Confirm password: "); conf_password = io.ask();

            if (password.equals(conf_password)) break;
            else
                io.error("Passwords do not match\n");
        }

        boolean success = UserAuth.signup(username, email, password);

        if (success) io.success("Registered successfully\n");
        else io.error("Registration failed\n");
    }

    public void handleSignin(){
        io.muted("Email: "); String email = io.ask();
        io.muted("Password: "); String password = io.ask();

        boolean success = UserAuth.signin(email, password);

        if (success) io.success("Authenticated successfully\n");
        else io.error("Authentication failed\n");
    }

    public void start(){
        this.io.info("Authentication required to continue.\n");
        this.io.print("\nSelect:\n");
        this.io.print("1. Signup (If new to MDCS)\n");
        this.io.print("2. Signin (If already have an account)\n");
        this.io.print("3. exit\n");
        
        while (true){
            try{
                this.io.specifier("\n> ");
                int choice = Integer.parseInt(this.io.ask());

                if (choice == 1) handleSignup();

                else if (choice == 2) handleSignin();

                else if (choice == 3) System.exit(0);

                else{
                    this.io.info("Invalid choice. Select 1 or 2\n");
                    continue;
                }

                break;
            }
            catch (NumberFormatException e){ io.info("Invalid choice. Select 1 or 2\n"); }
        }
    }
    
    public AuthPipe(ConsoleIO inou){
        this.io = inou;
    }
}
