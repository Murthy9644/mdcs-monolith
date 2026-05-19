package auth;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import auth.user_auth.signup.SignupHandler;
import models.auth.AuthInteractor;
import network.ServerRequest;

public class AuthHandler {
    private ServerRequest server;
    private BlockingQueue<String> queue;
    private AuthInteractor interactor;

    public void registration()
    throws IOException, InterruptedException{
        SignupHandler signup = new SignupHandler(this.server, this.queue, this.interactor);

        // Need a response DTO.
        // Right now assuming void return type.
        
        // Step 1 -> Create account
        signup.createAccount();

        // Step 2 -> Validate email (with OTP)
        signup.validateAccount();
    }

    public void login(){
        //
    }

    public void validateAuthToken(){
        //
    }
    
    public AuthHandler(ServerRequest server, BlockingQueue<String> queue, AuthInteractor interactor){
        this.server = server;
        this.queue = queue;
        this.interactor = interactor;
    }
}
