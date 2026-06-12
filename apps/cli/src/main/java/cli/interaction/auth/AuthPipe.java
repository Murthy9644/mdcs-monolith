package cli.interaction.auth;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import auth.AuthHandler;
import cli.helpers.HelperThreads;
import cli.utils.tools.ConsoleIO;
import models.PrintTask;
import models.auth.State.AuthState;
import network.ProtoMet;

public class AuthPipe {
    private ConsoleIO io;
    private ProtoMet server;
    private AuthHandler auth;
    private BlockingQueue<PrintTask> queue;

    public static boolean verifyAuthToken(){

        // Place holder for now

        return true;
    }
    
    public Boolean handleSignup(){
        try{
            this.queue.clear();
            HelperThreads.PrintToConsole print_helper = new HelperThreads.PrintToConsole(queue, io);
            Thread printer = new Thread(print_helper);

            printer.setDaemon(true);
            printer.start();
            
            // Start registration
            AuthState state = this.auth.registration();
            printer.interrupt();
            printer.join();

            if (state == AuthState.FAIL)
                return null;

            if (state == AuthState.TERMINATE)
                return false;

            // status = this.signup.validateAccount(getOtp());
        } catch (Exception e){
            return false;
        }

        return true;
    }

    public void handleSignin(){
        // io.muted("Email: "); String email = io.ask();
        // io.muted("Password: "); String password = io.ask();

        // boolean success = UserAuth.signin(email, password);

        // if (success) io.success("Authenticated successfully\n");
        // else io.error("Authentication failed\n");
    }

    public Boolean start(){
        this.io.info("Authentication required to continue\n");
        this.io.print("\nSelect:\n");
        this.io.print("1. Signup (If new to MDCS)\n");
        this.io.print("2. Signin (If already have an account)\n");
        this.io.print("3. Exit\n");
        
        while (true){
            try{
                this.io.specifier("\n> ");
                int choice = Integer.parseInt(this.io.ask());

                if (choice == 1) return handleSignup();

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

        return true; // Temporarily
    }
    
    public AuthPipe(ConsoleIO inou, ProtoMet server){
        this.io = inou;
        this.server = server;
        this.queue = new LinkedBlockingQueue<>();

        CLIAuthInteractor interactor = new CLIAuthInteractor(this.io);

        this.auth = new AuthHandler(this.server, this.queue, interactor);
    }
}
