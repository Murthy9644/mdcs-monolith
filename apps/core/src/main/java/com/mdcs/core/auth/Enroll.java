package com.mdcs.core.auth;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.mdcs.shared.fileio.FileIO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mdcs.core.Stream;
import com.mdcs.core.Stream.AuthAct;
import com.mdcs.core.Stream.LogAct;
import com.mdcs.core.Stream.Message;
import com.mdcs.core.Stream.Response;
import com.mdcs.shared.fileio.DataClasses.Device;
import com.mdcs.shared.models.State;
import com.mdcs.shared.models.State.AuthState;
import com.mdcs.shared.models.auth.Network.EnrollReq;
import com.mdcs.shared.models.auth.Network.EnrollRes;
import com.mdcs.shared.network.ProtoMet;

/**
 * Manages device enrollment, first device enrollment, device trusting, primary device tagging
 */

public class Enroll{
    private ProtoMet server;
    private Device device;
    private State state;
    private Stream stream;
    private Callbacks.Enroll callbacks;

    public void getCallbacks(){
        this.stream.send(
            new Message(
                LogAct.INFO,
                null,
                "Requesting device & workspace information for enrollment workflow...\n"
            )
        );

        CompletableFuture<Response> promise;

        try {
            promise = this.stream.request(
                new Message(AuthAct.ENROLL, null, "")
            );

            this.callbacks = FileIO.toObject(
                promise.get().getPayload(),
                Callbacks.Enroll.class
            );

            this.stream.send(
                new Message(
                    LogAct.INFO,
                    null,
                    "Received device & workspace information successfully.\n"
                )
            );
        } catch (InterruptedException e) {
            // Will decide what to do later
        } catch (JsonProcessingException e) {
            // Will decide what to do later
        } catch (ExecutionException e) {
            // Will decide what to do later
        }
    }

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
    public Device firstEnroll()
    throws IOException, InterruptedException{
        stream.send(
            new Message(
                LogAct.INFO,
                null,
                "Initiating first device enrollment...\n"
            )
        );

        this.getCallbacks();

        this.device.device_name = this.callbacks.deviceName();
        this.device.workspace_name = this.callbacks.workspaceName();

        EnrollReq msg = new EnrollReq();
        msg.body = new EnrollReq.Body(this.device.device_name, this.device.workspace_name);

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

            return device;
        }

        EnrollRes payload = FileIO.toObject(
            res.body().toString(), 
            EnrollRes.class
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

            return this.device;
        }

        this.device.device_id = payload.body.device_id;
        this.device.workspace_id = payload.body.workspace_id;

        stream.send(
            new Message(
                LogAct.INFO,
                null,
                "Device enrolled successfully and marked as primary.\n"
            )
        );

        return this.device;
    }
    
    public Enroll(ProtoMet server, State state, Stream stream){
        this.server = server;
        this.state = state;
        this.stream = stream;

        this.device = new Device();
    }
}
