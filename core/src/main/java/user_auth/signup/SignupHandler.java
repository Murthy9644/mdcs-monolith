package user_auth.signup;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import file_io.FileIO;
import models.user_auth.ServerResponseClasses.*;
import network.ServerRequest;

public class SignupHandler {
    private ServerRequest server;
    private BlockingQueue<String> queue;
    private String username, email;

    public boolean validateAccount(String otp)
    throws IOException, InterruptedException{
        HashMap<String, String> validation_data = new HashMap<>();
        validation_data.put("email", this.email);
        validation_data.put("otp", otp);
        
        this.queue.offer("info<>Starting OTP verification");

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
        
        return true;
    }

    public CreateAccResponse createAccount(String username, String email, String password)
    throws IOException, InterruptedException{
        this.username = username;
        this.email = email;

        HashMap<String, String> signup_data = new HashMap<>();
        signup_data.put("username", username);
        signup_data.put("email", email);
        signup_data.put("password", password);

        this.queue.offer("info<>Registration request has been submitted to the server");
        
        String res = this.server.post(
            "/auth/signup", 
            new String[]{"Content-type", "application/json"}, 
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
            "message": "..."
        }
        */

        return FileIO.toObject(res, CreateAccResponse.class);
    }
    
    public SignupHandler(ServerRequest server, BlockingQueue<String> queue){
        this.server = server;
        this.queue = queue;
    }
}
