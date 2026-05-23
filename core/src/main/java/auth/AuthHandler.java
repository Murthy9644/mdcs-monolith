package auth;

import java.util.concurrent.BlockingQueue;

// import auth.device_auth.DeviceAuthHandler;
import auth.user_auth.signup.SignupHandler;
import file_io.DataClasses;
import file_io.FileIO;
import models.auth.AuthInteractor;
import models.auth.SignupResponse;
import models.auth.SignupResponse.*;
import network.ServerRequest;

public class AuthHandler {
    private ServerRequest server;
    private BlockingQueue<String> queue;
    private AuthInteractor interactor;
    private AuthState sres = AuthState.SUCCESS;

    public AuthState registration()
    throws Exception{
        DataClasses.Accounts user_data = new DataClasses.Accounts();
        // DataClasses.Device device_data = new DataClasses.Device();

        SignupHandler signup = new SignupHandler(this.server, this.queue, this.interactor);
        // DeviceAuthHandler device = new DeviceAuthHandler(this.server, this.queue, this.interactor);
        
        // Step 1 -> Create account
        this.sres = SignupResponse.setAuthState(this.sres, signup.createAccount(user_data));

        // Step 2 -> Validate email (with OTP)
        // this.sres = SignupResponse.setAuthState(this.sres, signup.validateAccount(user_data));

        // Step 3 -> First device registration
        // this.sres = SignupResponse.setAuthState(this.sres, device.firstDeviceRegistration(device_data));

        if (this.sres == AuthState.SUCCESS){
            user_data.login_status = true;

            FileIO.fileWrite(user_data);
            // FileIO.fileWrite(device_data);
        }

        return sres;
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
