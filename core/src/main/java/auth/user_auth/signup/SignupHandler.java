package auth.user_auth.signup;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import file_io.DataClasses;
import file_io.FileIO;
import models.auth.AuthInteractor;
import models.auth.ServerResponseClasses.*;
import models.auth.SignupResponse.AuthState;
import network.ServerRequest;
import security.TokenCipher;

public class SignupHandler {
    private ServerRequest server;
    private BlockingQueue<String> queue;
    private String username, email;
    private AuthInteractor interactor;

    public AuthState validateAccount(DataClasses.Accounts body)
    throws IOException, InterruptedException{
        int otp = Integer.parseInt(this.interactor.getOTP());
        ValidateAccRequest validation_data = new ValidateAccRequest(this.email, otp);        

        this.queue.offer("info<>Starting OTP verification\n");

        String res = this.server.post(
            "/auth/user/verify-otp", 
            new String[] {"Content-type", "application/json"}, 
            FileIO.toJson(validation_data)
        );
        
        ValidateAccResponse response = FileIO.toObject(res, ValidateAccResponse.class);

        // Need to add functionality to handle, account creation / OTP errors

        this.queue.offer("success<>Account validated successfully");

        // Encrypt tokens
        body.auth_token = TokenCipher.encrypt(response.body.auth_token);
        body.refresh_token = TokenCipher.encrypt(response.body.refresh_token);

        return AuthState.SUCCESS;
    }

    public AuthState createAccount(DataClasses.Accounts body)
    throws IOException, InterruptedException{
        this.username = this.interactor.getUsername();
        this.email = this.interactor.getEmail();

        String password = this.interactor.getPassword();

        while (!password.equals(this.interactor.confirmPassword())){
            this.queue.offer("error<>Passwords DO NOT match\n");
            password = this.interactor.getPassword();
        }

        // (Optional) Regex test for password strength
        // Implement regex test at backend also.
        
        CreateAccRequest signup_data = new CreateAccRequest(
            this.username, 
            this.email, 
            password
        );

        this.queue.offer("info<>Registration request has been submitted to the server\n");
        
        String res = this.server.post(
            "/auth/user/signup", 
            new String[] {"Content-type", "application/json"}, 
            FileIO.toJson(signup_data)
        );

        CreateAccResponse response = FileIO.toObject(res, CreateAccResponse.class);

        // Need to add functionality to handle account creation errors

        this.queue.offer("success<>Account created successfully\n");

        body.user_id = response.body.user_id;
        body.username = response.body.username;
        body.email = response.body.email;

        return AuthState.SUCCESS;
    }
    
    public SignupHandler(ServerRequest server, BlockingQueue<String> queue, AuthInteractor interactor){
        this.server = server;
        this.queue = queue;
        this.interactor = interactor;
    }
}
