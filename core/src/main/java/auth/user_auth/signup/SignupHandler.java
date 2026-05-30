package auth.user_auth.signup;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import fileio.DataClasses;
import fileio.FileIO;
import models.PrintTask;
import models.auth.AuthInteractor;
import models.auth.ServerResponseClasses.*;
import models.auth.SignupResponse.AuthState;
import network.ProtoMet;
import security.TokenCipher;

public class SignupHandler {
    private ProtoMet server;
    private BlockingQueue<PrintTask> queue;
    private String user_id, username, email;
    private AuthInteractor interactor;

    public AuthState validateAccount(DataClasses.Accounts body)
    throws IOException, InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        PrintTask task = new PrintTask("<>", latch);
        this.queue.offer(task);

        // latch.await();

        String otp = this.interactor.getOTP();
        ValidateAccRequest validation_data = new ValidateAccRequest(this.user_id, this.email, otp);        

        this.queue.offer(
            new PrintTask(
                "info<>Starting OTP verification",
                new CountDownLatch(1)
            )
        );

        HttpResponse<String> res = this.server.post(
            "/auth/user/verify-otp", 
            new String[] {"Content-type", "application/json"}, 
            FileIO.toJson(validation_data)
        );

        if (res.statusCode() >= 500){
            this.queue.offer(
                new PrintTask(
                    "critical<>Internal Server Error",
                    new CountDownLatch(1)
                )
            );
            return AuthState.TERMINATE;
        }

        String res_body = res.body().toString();
        
        ValidateAccResponse response = FileIO.toObject(res_body, ValidateAccResponse.class);

        if (!response.status){
            this.queue.offer(
                new PrintTask(
                    "error<>" + Utils.err.get(response.error),
                    new CountDownLatch(1)
                )
            );
            this.queue.offer(
                new PrintTask(
                    "error<>" + response.message,
                    new CountDownLatch(1)
                )
            );
            
            return AuthState.FAIL;
        }

        // Encrypt tokens
        body.auth_token = TokenCipher.encrypt(response.body.auth_token);
        body.refresh_token = TokenCipher.encrypt(response.body.refresh_token);

        return AuthState.SUCCESS;
    }

    public AuthState createAccount(DataClasses.Accounts body)
    throws IOException, InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        PrintTask task = new PrintTask("info<>Enter your details", latch);
        this.queue.offer(task);

        latch.await();

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

        task = new PrintTask(
            "info<>Registration request has been submitted to the server",
            new CountDownLatch(1)
        );
        this.queue.offer(task);
        
        HttpResponse<String> res = this.server.post(
            "/auth/user/signup", 
            new String[] {"Content-type", "application/json"}, 
            FileIO.toJson(signup_data)
        );

        if (res.statusCode() >= 500){
            task = new PrintTask(
                "critical<>Internal server error",
                new CountDownLatch(1)
            );
            this.queue.offer(task);
            return AuthState.TERMINATE;
        }

        String res_body = res.body().toString();

        CreateAccResponse response = FileIO.toObject(res_body, CreateAccResponse.class);

        if (!response.status){
            this.queue.offer(
                new PrintTask(
                    "error<>" + Utils.err.get(response.error),
                    new CountDownLatch(1)
                )
            );
            this.queue.offer(
                new PrintTask(
                    "error<>" + response.message,
                    new CountDownLatch(1)
                )
            );
            
            return AuthState.FAIL;
        }

        this.queue.offer(
            new PrintTask(
                "success<>Account created successfully",
                new CountDownLatch(1)
            )
        );

        body.user_id = response.body.user_id;
        body.username = response.body.username;
        body.email = response.body.email;

        this.user_id = response.body.user_id;

        return AuthState.SUCCESS;
    }
    
    public SignupHandler(ProtoMet server, BlockingQueue<PrintTask> queue, AuthInteractor interactor){
        this.server = server;
        this.queue = queue;
        this.interactor = interactor;
    }
}
