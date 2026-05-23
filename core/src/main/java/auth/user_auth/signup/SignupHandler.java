package auth.user_auth.signup;

import java.io.IOException;
import java.net.http.HttpResponse;
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

        this.queue.offer("info<>Starting OTP verification");

        HttpResponse<String> res = this.server.post(
            "/auth/user/verify-otp", 
            new String[] {"Content-type", "application/json"}, 
            FileIO.toJson(validation_data)
        );

        String res_body = res.body().toString();
        
        ValidateAccResponse response = FileIO.toObject(res_body, ValidateAccResponse.class);

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

        this.interactor.pswdRules();
        String password = this.interactor.getPassword();

        while (!password.equals(this.interactor.confirmPassword())){
            this.interactor.pswdsMismatch();
            password = this.interactor.getPassword();
        }
        
        CreateAccRequest signup_data = new CreateAccRequest(
            this.username, 
            this.email, 
            password
        );

        this.queue.offer("info<>Registration request has been submitted to the server");
        
        HttpResponse<String> res = this.server.post(
            "/auth/user/signup", 
            new String[] {"Content-type", "application/json"}, 
            FileIO.toJson(signup_data)
        );

        if (res.statusCode() >= 500){
            this.queue.offer("critical<>Internal server error");
            return AuthState.TERMINATE;
        }

        String res_body = res.body().toString();

        CreateAccResponse response = FileIO.toObject(res_body, CreateAccResponse.class);

        if (!response.status){
            this.queue.offer("error<>" + Utils.err.get(response.error));
            this.queue.offer("error<>" + response.message);
            
            return AuthState.FAIL;
        }

        this.queue.offer("success<>Account created successfully");

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
