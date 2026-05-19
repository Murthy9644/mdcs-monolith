package auth.user_auth.signup;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import file_io.FileIO;
import models.auth.AuthInteractor;
import models.auth.ServerResponseClasses.CreateAccRequest;
import models.auth.ServerResponseClasses.CreateAccResponse;
import network.ServerRequest;

public class SignupHandler {
    private ServerRequest server;
    private BlockingQueue<String> queue;
    private String username, email;
    private AuthInteractor interactor;

    public void validateAccount()
    throws IOException, InterruptedException{
        String otp = this.interactor.getOTP();

        HashMap<String, String> validation_data = new HashMap<>();
        validation_data.put("email", this.email);
        validation_data.put("otp", otp);
        
        this.queue.offer("info<>Starting OTP verification\n");

        String res = this.server.post(
            "/auth/verify-otp", 
            new String[] {"Content-type", "application/json"}, 
            FileIO.toJson(validation_data)
        );

        /*
        Expected server output:
        {
            "status": true/false,
            "body": {
                "user_id": int(...),
                "auth_token": "..."
            }
            "message": "..."
        }
        */
    }

    public void createAccount()
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
            "/auth/signup", 
            new String[] {"Content-type", "application/json"}, 
            FileIO.toJson(signup_data)
        );

        /*
        Expected output from server:
        {
            "status": true/false,
            "body": {
                "user_id": int(...),
                "username": "...",
                "email": "..."
            },
            "error": "...",
            "message": "..."
        }
        */

        CreateAccResponse response = FileIO.toObject(res, CreateAccResponse.class);

        // Need to add functionality to handle account creation errors

        //
    }
    
    public SignupHandler(ServerRequest server, BlockingQueue<String> queue, AuthInteractor interactor){
        this.server = server;
        this.queue = queue;
        this.interactor = interactor;
    }
}
