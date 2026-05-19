package cli.interaction.auth;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import auth.AuthHandler;
import cli.helpers.HelperThreads;
import cli.utils.tools.ConsoleIO;
import network.ServerRequest;

public class AuthPipe {
    private ConsoleIO io;
    private ServerRequest server;
    private AuthHandler auth;
    private BlockingQueue<String> queue;

    public static boolean verifyAuthToken(){

        // Place holder for now

        return true;
    }
    
    public void handleSignup(){
        try{
            HelperThreads.PrintToConsole print_helper = new HelperThreads.PrintToConsole(queue, io);
            Thread printer = new Thread(print_helper);

            printer.setDaemon(true);
            printer.start();
            
            // Start registration
            this.auth.registration();
            printer.interrupt();

            // if (!res.status){
            //     this.io.critical("Signup attempt failed\n");
            //     this.io.critical(res.message);
            //     return;
            // }

            // status = this.signup.validateAccount(getOtp());
        } catch (IOException e){
            //
        } catch (InterruptedException e){
            //
        }

        // if (success) io.success("Registered successfully\n");
        // else io.error("Registration failed\n");
    }

    public void handleSignin(){
        // io.muted("Email: "); String email = io.ask();
        // io.muted("Password: "); String password = io.ask();

        // boolean success = UserAuth.signin(email, password);

        // if (success) io.success("Authenticated successfully\n");
        // else io.error("Authentication failed\n");
    }

    public void start(){
        this.io.info("Authentication required to continue\n");
        this.io.print("\nSelect:\n");
        this.io.print("1. Signup (If new to MDCS)\n");
        this.io.print("2. Signin (If already have an account)\n");
        this.io.print("3. Exit\n");
        
        while (true){
            try{
                this.io.specifier("\n> ");
                int choice = Integer.parseInt(this.io.ask());

                if (choice == 1) handleSignup();

                else if (choice == 2) handleSignin();

                else if (choice == 3) System.exit(0);

                else{
                    this.io.info("Invalid choice. Select 1, 2 or 3\n");
                    continue;
                }

                break;
            }
            catch (NumberFormatException e){ io.info("Invalid choice. Select 1, 2 or 3\n"); }
        }
    }
    
    public AuthPipe(ConsoleIO inou, ServerRequest server){
        this.io = inou;
        this.server = server;
        this.queue = new LinkedBlockingQueue<>();

        CLIAuthInteractor interactor = new CLIAuthInteractor(this.io);

        this.auth = new AuthHandler(this.server, this.queue, interactor);
    }
}
