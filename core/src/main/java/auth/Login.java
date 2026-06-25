package auth;

import java.io.IOException;
import java.net.http.HttpResponse;

import fileio.FileIO;
import fileio.DataClasses.Accounts;
import fileio.DataClasses.Device;
import logger.Log;
import models.auth.Provider;
import models.auth.State;
import models.auth.State.AuthState;
import models.auth.Network.LoginReq;
import models.auth.Network.LoginRes;
import models.postals.Report;
import network.ProtoMet;
import utils.NetErrors;

public class Login implements Runnable{
    private ProtoMet server;
    private Provider callbacks;
    private Report report;
    private Log logger;
    private State job;
    private Accounts user;
    private Device device;

    private void conclude(){
        //
    }

    // private void deviceAuth(){
    //     //
    // }

    private void usrAuth()
    throws IOException, InterruptedException{
        this.user.email = this.callbacks.email();
        String pswd = this.callbacks.pswd();

        LoginReq message = new LoginReq();
        
        message.body = new LoginReq.Body(
            this.user.email, 
            pswd,
            this.device.workspace_id,
            this.device.device_id
        );

        HttpResponse<String> res = this.server.post(message);

        if (res.statusCode() >= 500){
            /*
            Some sort of internal server error has occured. User must be notified that this action
            cannot be performed now or till server has recovered.
            In this case, the response message from server doesn't conatin the payload. So, need
            to return early.
            */

            this.logger.network(
                "auth.usrAuth", 
                "Internal server error has occured"
            );

            this.job.logs.add(
                "critical<>An internal server error occurred. Please try again later."
            );

            this.job.set(AuthState.TERMINATE);

            return;
        }

        LoginRes payload = FileIO.toObject(res.body().toString(), LoginRes.class);

        if (!payload.status){
            /*
            Means, login was failed due to some user or environment related error. In
            such cases, show the error message and prompt user to try again.
            */

            this.logger.network(
                "auth.usrAuth", 
                "Login failed due to user or environment issue"
            );

            this.job.logs.add("error<>" + NetErrors.err.get(payload.error));
            this.job.set(AuthState.RETRY);
        }

        /*
        Some details might already be available in user but, it is better to write all of them
        again. Because, in some cases when user is logging in on a new device, file may not have
        all the data.
        */
        this.user.user_id = payload.body.user_id;
        this.user.username = payload.body.username;

        if (payload.body.phase == "UNVERIFIED"){
            //
        }

        // this.user.auth_token = payload.body.auth_tok;
        // this.user.refresh_token = payload.body.refresh_tok;
        
        /*
        But the device and workspace details are not required to write again. Because, if the
        details were not available, the device enrollment would be triggered.
        */
    }

    @Override
    public void run(){
        //
    }
    
    public Login(ProtoMet server, Report report, Provider callbacks){
        this.server = server;
        this.callbacks = callbacks;
        this.report = report;

        this.logger = new Log();

        this.job = new State();

        /*
        User data is overwritten in absolutely every scenario of login. Because, we can't say
        whether the user intends to login to another account or is logging in because of expired
        auth tokens.
        */
        this.user = new Accounts();

        /*
        For device data, read the file first and if the file doesn't exist or throwing some error
        (which ususally don't happen because of bootstrap process) then default the objects.

        This helps us in two cases. If device is enrolled for the account user is trying to log
        into, we can skip device enrollment and if device id is absent or is not associated with
        the account, we will trigger the enrollment process.
        */
        try{
            this.device = FileIO.fileRead(Device.class);
        } catch (Exception e){
            this.device = new Device();
        }
    }
}
