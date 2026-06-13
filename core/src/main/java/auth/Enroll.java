package auth;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;

import fileio.FileIO;
import fileio.DataClasses.Device;
import logger.Log;
import models.auth.Provider;
import models.auth.Network.RegisterDeviceReq;
import models.auth.Network.RegisterDeviceRes;
import models.auth.State.AuthState;
import models.jobs.Envelope.Mail;
import models.jobs.Envelope.Print;
import network.ProtoMet;
import utils.NetErrors;

/**
 * Manages device enrollment, first device enrollment, device trusting, primary device tagging
 */

public class Enroll{
    private ProtoMet server;
    private BlockingQueue<Mail> mails;
    private Provider callbacks;

    /*
    First device enrollment process is not being implemented as a new worker now because, later
    when general enrollment process differs in the process from this, we may need to split the
    classes or keep as separate method as required.
    */

    /**
     * If this method has been provoked, it is assumed that user has been already created and
     * validated.
     * 
     * Populates the supplied Device instance with the registered device details.
     * 
     * @param device
     * @param logger
     * @return AuthState
     * @throws IOException
     * @throws InterruptedException
     */
    public AuthState firstDevice(Device device, Log logger)
    throws IOException, InterruptedException{
        logger.info("auth.firstDevice", "Starting first device enrollment");

        device.device_name = this.callbacks.deviceName();
        device.workspace_name = this.callbacks.workspaceName();

        RegisterDeviceReq message = new RegisterDeviceReq();

        message.body = new RegisterDeviceReq.Body(
            device.device_name, 
            device.workspace_name
        );

        HttpResponse<String> res = this.server.post(message);

        logger.network(
            "auth.firstDevice", 
            "Request has been sent to the server"
        );

        if (res.statusCode() >= 500){
            /*
            Internal server error has occured. Account is created but device is not enrolled.
            So return RECOVER code so that login is triggered.
            */

            logger.network(
                "auth.firstDevice", 
                "Internal server error has occured"
            );

            Print mail = new Print();

            mail.sender_id = Thread.currentThread().threadId();

            mail.line = 
                "critical<>Signup: An internal server error occurred. Please login again.";

            this.mails.offer(mail);

            return AuthState.RECOVER;
        }

        RegisterDeviceRes payload = FileIO.toObject(
            res.body().toString(), 
            RegisterDeviceRes.class
        );

        if (!payload.status){
            // Device enrollment failed due to user / environment related issue

            logger.network(
                "auth.firstDevice", 
                "Device enrollment failed due to user or environment issue"
            );

            Print mail = new Print();

            mail.sender_id = Thread.currentThread().threadId();
            mail.line = "error<>Signup: " + NetErrors.err.get(payload.error);

            this.mails.offer(mail);

            return AuthState.RECOVER;
        }

        device.device_id = payload.body.device_id;
        device.workspace_id = payload.body.workspace_id;

        logger.info(
            "auth.firstDevice", 
            "Device enrolled successfully and marked as primary"
        );
        
        return AuthState.SUCCESS;
    }
    
    public Enroll(ProtoMet server, BlockingQueue<Mail> mails, Provider callbacks){
        this.server = server;
        this.mails = mails;
        this.callbacks = callbacks;
    }
}
