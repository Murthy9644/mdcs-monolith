package com.mdcs.core.auth;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.mdcs.shared.fileio.FileIO;
import com.mdcs.shared.fileio.DataClasses.Device;
import com.mdcs.shared.logger.Log;
import com.mdcs.shared.models.auth.Provider;
import com.mdcs.shared.models.auth.State;
import com.mdcs.shared.models.auth.Network.EnrollDeviceReq;
import com.mdcs.shared.models.auth.Network.EnrollDeviceRes;
import com.mdcs.shared.models.auth.State.AuthState;
import com.mdcs.shared.network.ProtoMet;
import com.mdcs.shared.utils.NetErrors;

/**
 * Manages device enrollment, first device enrollment, device trusting, primary device tagging
 */

public class Enroll{
    private ProtoMet server;
    private State job;
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
     * Populates the supplied Device instance with the enrolled device details.
     * 
     * @param device
     * @param logger
     * @return AuthState
     * @throws IOException
     * @throws InterruptedException
     */
    public void firstDevice(Device device, Log logger)
    throws IOException, InterruptedException{
        logger.info("auth.firstDevice", "Starting first device enrollment");

        device.device_name = this.callbacks.deviceName();
        device.workspace_name = this.callbacks.workspaceName();

        EnrollDeviceReq message = new EnrollDeviceReq();

        message.body = new EnrollDeviceReq.Body(
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

            this.job.logs.add(
                "critical<>An internal server error occurred. Please try again later."
            );

            this.job.set(AuthState.RECOVER);

            return;
        }

        EnrollDeviceRes payload = FileIO.toObject(
            res.body().toString(), 
            EnrollDeviceRes.class
        );

        if (!payload.status){
            // Device enrollment failed due to user / environment related issue

            logger.network(
                "auth.firstDevice", 
                "Device enrollment failed due to user or environment issue"
            );

            this.job.logs.add("error<>" + NetErrors.err.get(payload.error));
            this.job.set(AuthState.RETRY);
        }

        device.device_id = payload.body.device_id;
        device.workspace_id = payload.body.workspace_id;

        logger.info(
            "auth.firstDevice", 
            "Device enrolled successfully and marked as primary"
        );
    }
    
    public Enroll(ProtoMet server, State job, Provider callbacks){
        this.server = server;
        this.job = job;
        this.callbacks = callbacks;
    }
}
