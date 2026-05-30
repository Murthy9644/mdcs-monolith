package auth.device_auth;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;

import fileio.DataClasses;
import fileio.FileIO;
import models.auth.AuthInteractor;
import models.auth.ServerResponseClasses.FDRRequest;
import models.auth.ServerResponseClasses.FDRResponse;
import models.auth.SignupResponse.AuthState;
import network.ProtoMet;

public class DeviceAuthHandler {
    private ProtoMet server;
    private BlockingQueue<String> queue;
    private AuthInteractor interactor;

    public AuthState firstDeviceRegistration(DataClasses.Device body)
    throws IOException, InterruptedException{
        String device_name = this.interactor.getDeviceName();
        String workspace_name = this.interactor.getWorkspaceName();

        FDRRequest req = new FDRRequest(device_name, workspace_name);

        this.queue.offer("info<>Register device request has been submitted\n");

        HttpResponse<String> res = this.server.post(
            "/auth/device/register",
            new String[] {"Content-type", "application/json"},
            FileIO.toJson(req)
        );

        String res_body = res.body().toString();

        this.queue.offer("success<>Device registered as MAIN successfully\n");
        FDRResponse response = FileIO.toObject(res_body, FDRResponse.class);

        body.device_id = response.body.device_id;
        body.device_name = response.body.device_name;
        body.workspace_id = response.body.workspace_id;
        body.workspace_name = response.body.workspace_name;

        return AuthState.SUCCESS;
    }
    
    public DeviceAuthHandler(ProtoMet server, BlockingQueue<String> queue, AuthInteractor interactor){
        this.server = server;
        this.queue = queue;
        this.interactor = interactor;
    }
}
