package com.mdcs.core.auth;

import java.io.IOException;
import java.net.http.HttpResponse;
import com.mdcs.shared.fileio.FileIO;
import com.mdcs.core.Stream;
import com.mdcs.core.Stream.LogAct;
import com.mdcs.core.Stream.Message;
import com.mdcs.shared.fileio.DataClasses.Device;
import com.mdcs.shared.models.State;
import com.mdcs.shared.models.State.AuthState;
import com.mdcs.shared.models.auth.Network.EnrollDeviceReq;
import com.mdcs.shared.models.auth.Network.EnrollDeviceRes;
import com.mdcs.shared.network.ProtoMet;

/**
 * Manages device enrollment, first device enrollment, device trusting, primary device tagging
 */

public class Enroll{
    private ProtoMet server;
    private State state;
    private Callbacks.Register callbacks;

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
     */
    public void firstDevice(Device device, Stream stream)
    throws IOException, InterruptedException{
        stream.send(
            new Message(
                LogAct.INFO,
                null,
                "Initiating first device enrollment...\n"
            )
        );

        device.device_name = this.callbacks.deviceName();
        device.workspace_name = this.callbacks.workspaceName();

        EnrollDeviceReq msg = new EnrollDeviceReq();
        msg.body = new EnrollDeviceReq.Body(device.device_name, device.workspace_name);

        HttpResponse<String> res = this.server.post(msg);

        if (res.statusCode() >= 500){
            /*
            Internal server error has occured. Account is created but device is not enrolled.
            So return RECOVER code so that login is triggered.
            */

            stream.send(
                new Message(
                    LogAct.CRITICAL,
                    null,
                    "Device enrollment failed due to an internal server error.\n"
                )
            );

            this.state.set(AuthState.RECOVER);

            return;
        }

        EnrollDeviceRes payload = FileIO.toObject(
            res.body().toString(), 
            EnrollDeviceRes.class
        );

        if (!payload.status){
            // Device enrollment failed due to user / environment related issue

            stream.send(
                new Message(
                    LogAct.CRITICAL,
                    null,
                    "Device enrollment failed due to user or environment issue.\n"
                )
            );
            
            this.state.set(AuthState.RETRY);
        }

        device.device_id = payload.body.device_id;
        device.workspace_id = payload.body.workspace_id;

        stream.send(
            new Message(
                LogAct.INFO,
                null,
                "Device enrolled successfully and marked as primary.\n"
            )
        );
    }
    
    public Enroll(ProtoMet server, State state, Callbacks.Register callbacks){
        this.server = server;
        this.state = state;
        this.callbacks = callbacks;
    }
}
