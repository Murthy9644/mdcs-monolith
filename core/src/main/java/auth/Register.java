package auth;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;

import fileio.FileIO;
import fileio.DataClasses.Accounts;
import fileio.DataClasses.Device;
import logger.Log;
import models.auth.Provider;
import models.auth.State;
import models.auth.Network.CreateUsrReq;
import models.auth.Network.CreateUsrRes;
import models.auth.Network.ValidateUsrReq;
import models.auth.Network.ValidateUsrRes;
import models.auth.State.AuthState;
import models.postals.Envelope.Mail;
import models.postals.Envelope.Print;
import network.ProtoMet;
import security.TokCipher;
import utils.NetErrors;

/**
 * Manages account creation, user validation, and enrollment of the user's first device.
 *
 * The first device is automatically trusted and marked as the primary device,
 * which is later used for enrolling additional devices and other account operations.
 *
 * Registration phases:
 * - Account creation
 * - Account validation
 * - First device enrollment
 */

public class Register implements Runnable{

    /*
    What if user creation and validation succeeded but first device enrollment failed? User
    won't be able to register the device during signup.

    In such cases, we have two ideas.
        1. Immediately trigger the login state and let user sign in to trust their device.
        2. Mark user as VERIFIED and let them manually enroll the device later.

    2nd one is good as it gives full control of which device to make primary to user. It will
    be implemented in future versions.

    For now, we maintain 3 states for a user account.
        UNVERIFIED -> After user creation
        VERIFIED   -> After user validation
        ONBOARDED  -> After first device enrollment

    So that if:
        User was created and not validated => OTP verification in next login.
        User created and validated but device not enrolled => Device enrollment in next login.

    That means, we should make the login flow capable of getting the state codes from server for
    this case :)
    */

    private ProtoMet server;
    private BlockingQueue<Mail> mails;
    private Provider callbacks;
    private State state;
    private Log logger;
    private Accounts user;
    private Device device;

    /**
     * Validate user account with OTP and set the user as verified after successful validation.
     * Assumes account has been created previously (ofcourse bro)
     * 
     * @return AuthState
     * @throws IOException
     * @throws InterruptedException
     */
    private AuthState validateUsr()
    throws IOException, InterruptedException{
        this.logger.info("auth.validateUsr", "Starting user account validation");

        String otp = this.callbacks.otp();

        ValidateUsrReq message = new ValidateUsrReq();

        message.body = new ValidateUsrReq.Body(
            this.user.user_id, 
            this.user.email, 
            otp
        );

        HttpResponse<String> res = this.server.post(message);

        this.logger.network(
            "auth.validateUsr", 
            "Request has been sent to the server"
        );

        if (res.statusCode() >= 500){
            // Some internal server error has occured. Return termination code

            this.logger.network(
                "auth.validateUsr", 
                "Internal server error has occured"
            );

            Print mail = new Print();

            mail.sender_id = Thread.currentThread().threadId();

            mail.line = 
                "critical<>Signup: An internal server error occurred. Please try again later.";

            this.mails.offer(mail);

            return AuthState.RECOVER;
        }

        ValidateUsrRes payload = FileIO.toObject(
            res.body().toString(), 
            ValidateUsrRes.class
        );

        /*
        If the OTP is incorrect, need to let the user try again. Current sequence discards the
        OTP entirely, requests new one. That is bad UX. Need to work on that.
        */

        if (!payload.status){
            // Auth failed due to some user / environment related issue

            this.logger.network(
                "auth.validateUsr", 
                "User validation failed due to user or environment issue"
            );

            Print mail = new Print();

            mail.sender_id = Thread.currentThread().threadId();
            mail.line = "error<>Signup: " + NetErrors.err.get(payload.error);

            this.mails.offer(mail);

            return AuthState.RECOVER;
        }

        this.user.auth_token = TokCipher.encrypt(payload.body.auth_tok);
        this.user.refresh_token = TokCipher.encrypt(payload.body.refresh_tok);

        this.logger.info(
            "auth.validateUsr",
            "User account verified successfully | User validated"
        );

        return AuthState.SUCCESS;
    }

    /**
     * Create new user account with unverified state. This process will automatically send an OTP
     * to user email without the need of separate api.
     * 
     * In cases of business failures or internal server errors, the OTP is not sent, so it is fine
     * to neglect that case.
     * 
     * @return AuthState
     * @throws IOException
     * @throws InterruptedException
     */
    private AuthState createUsr()
    throws IOException, InterruptedException{
        this.logger.info("auth.createUsr", "Creating user account");

        this.user.username = this.callbacks.username();
        this.user.email = this.callbacks.email();

        String password = this.callbacks.pswd();

        while (!password.equals(this.callbacks.confirmPswd())){
            this.callbacks.pswdsMismatch();
            password = this.callbacks.pswd();
        }

        CreateUsrReq message = new CreateUsrReq(); 

        message.body = new CreateUsrReq.Body(
            this.user.username, 
            this.user.email, 
            password
        );

        HttpResponse<String> res = this.server.post(message);

        this.logger.network(
            "auth.createUsr", 
            "Request has been sent to the server"
        );

        if (res.statusCode() >= 500){
            /*
            Some sort of internal server error has occured. User must be notified that this action
            cannot be performed now or till server has recovered.
            In this case, the response message from server doesn't conatin the payload. So, need
            to return early.
            */

            this.logger.network(
                "auth.createUsr", 
                "Internal server error has occured"
            );

            Print mail = new Print();

            mail.sender_id = Thread.currentThread().threadId();

            mail.line = 
                "critical<>Signup: An internal server error occurred. Please try again later.";

            this.mails.offer(mail);

            return AuthState.TERMINATE;
        }

        CreateUsrRes payload = FileIO.toObject(
            res.body().toString(), 
            CreateUsrRes.class
        );

        if (!payload.status){
            /*
            Means, account creation was failed due to some user or environment related error. In
            such cases, show the error message and prompt user to try again.
            */

            this.logger.network(
                "auth.createUsr", 
                "User creation failed due to user or environment issue"
            );

            Print mail = new Print();

            mail.sender_id = Thread.currentThread().threadId();
            mail.line = "error<>Signup: " + NetErrors.err.get(payload.error);

            this.mails.offer(mail);

            return AuthState.RETRY;
        }

        this.user.user_id = payload.body.user_id;

        this.logger.info("auth.createusr", "User account created successfully");

        return AuthState.SUCCESS;
    }

    @Override
    public void run(){
        this.logger.info("auth-signup", "Starting user registration");
        
        try{
            Enroll enroll = new Enroll(server, mails, callbacks);

            this.state.set(this.createUsr());

            if (this.state.get() == AuthState.SUCCESS)
                this.state.set(this.validateUsr());

            if (this.state.get() == AuthState.SUCCESS)
                this.state.set(enroll.firstDevice(this.device, this.logger));

            if (this.state.get() == AuthState.RECOVER){
                this.user.logged_in = false;

                return;
            }
        } catch (IOException e){
            /*
            This means, server couldn't be contacted. This can cause due to:
                No internet connection
                Connection timed out
                DNS lookup failed
                etc.

            In such cases, prompt user and send the termination code.
            */

            this.logger.network(
                "auth-signup", 
                "Unable to contact the server | " + e.getMessage()
            );

            Print mail = new Print();

            mail.sender_id = Thread.currentThread().threadId();
            mail.line = "critical<>Unable to contact the server. Please try again later";

            this.mails.offer(mail);

            this.state.set(AuthState.TERMINATE);

            return;
        } catch (InterruptedException e){
            /*
            This is more of an internal / system event. No need to prompt user about it. Because,
            it happens when another thread has interrrupted this thread (happens when application
            is being shut down).
            */

            this.logger.error("auth-signup", "Thread was interrupted");

            Thread.currentThread().interrupt();
            this.state.set(AuthState.TERMINATE);
            
            return;
        }

        this.user.logged_in = true;

        try {
            // Write the user and device details into the file

            FileIO.fileWrite(this.user);
            FileIO.fileWrite(this.device);

            this.logger.info("auth-signup", "User and device data persisted");
        } catch (Exception e) {
            /*
            User is signed in but we can't persist the data for the next time. In such cases,
            treat user as signed out but account creation is suucessful and ask for log in using
            email and password.
            */

            this.logger.error(
                "auth-signup", 
                "Failed to persist user and device data"
            );

            this.user.logged_in = false;
            this.state.set(AuthState.RECOVER);
        }
    }

    /*
    As this is a worker (thread), i mean, run() can't take parameters or return values right, so
    we will get the State object from caller and fill it with state. This will also allow us
    to not worry about creating objects locally (-_-)
    */
    
    public Register(
        ProtoMet server, 
        BlockingQueue<Mail> mails, 
        Provider callbacks,
        State state
    ){
        this.server = server;
        this.mails = mails;
        this.callbacks = callbacks;
        this.state = state;

        this.logger = new Log();

        this.user = new Accounts();
        this.device = new Device();
    }
}
