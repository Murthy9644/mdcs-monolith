package cli.interact.auth;

import auth.Register;
import bootstrap.UserState;
import cli.utils.ConsoleIO;
import models.auth.State;
import models.postals.Report;
import network.ProtoMet;

public class Onboard {
    private ConsoleIO io;
    private ProtoMet server;
    private CLIProvider callbacks;
    private Report report;

    private boolean access(){
        // Verify auth tokens.
        // true if valid; false if invalid.
        
        return true; // placeholder
    }

    private boolean analyze(){
        // Since auth returns the job of type State, we can type cast it.
        State job = (State) this.report.jobs.poll(); // Only one job will be there in the queue.

        for (String log : job.logs){
            String params[] = log.split("<>");

            this.io.map(params[0], params[1]);
        }

        if (job.get() == State.AuthState.SUCCESS)
            return true;

        if (job.get() == State.AuthState.TERMINATE)
            return false;

        if (job.get() == State.AuthState.RECOVER)
            return this.login(); // Login flow will recover the half set state

        if (job.get() == State.AuthState.RETRY){
            this.io.error("Signup process terminated. Please try again");

            return this.audit(); // Prompt the user again for signup but don't close application
        }

        return true;
    }

    private boolean registration(){
        Register register = new Register(this.server, this.report, this.callbacks);
        Thread process = new Thread(register);

        try{
            process.setDaemon(true);

            process.start();
            process.join();
        } catch (InterruptedException e){
            /*
            Not a direct error but the application can't safely proceed further. Because, the
            process is interrupted in between and we don't know where exactly. so, we will treat
            it as a failed process.
            */

            process.interrupt();
            return false;
        }

        return this.analyze();
    }

    private boolean login(){

        return true;
    }

    private boolean audit(){
        this.io.info("Authentication required to continue\n");
        this.io.print("\nSelect:\n");
        this.io.print("1. Signup (If new to MDCS)\n");
        this.io.print("2. Signin (If already have an account)\n");
        this.io.print("3. Exit\n");
        
        while (true){
            try{
                this.io.specifier("\n> ");
                int choice = Integer.parseInt(this.io.ask());

                if (choice == 1) return this.registration();

                else if (choice == 3) return false;

                else
                    this.io.info("Invalid choice. Enter 1, 2 or 3\n");
            }
            catch (NumberFormatException e){
                io.info("Invalid choice. Enter 1, 2 or 3\n");
            }
        }
    }

    public boolean init(){
        String state = new UserState().resolve();

        if (
            (state.equals("LOGGED_IN") && !this.access())
            ||
            state.equals("LOGGED_OUT")
        ){
            //
        }

        return true;
    }
    
    public Onboard(ConsoleIO io, ProtoMet server){
        this.io = io;
        this.server = server;

        this.callbacks = new CLIProvider(this.io);
        this.report = new Report();
    }
}
