package models.auth;

import models.ServerResponse;

public class ServerResponseClasses {

    // Body for CreateAccResponse
    public static class CreateAccResBody{
        public int user_id;
        public String username;
        public String email;
    }

    public static class CreateAccResponse extends ServerResponse{
        public CreateAccResBody body;
    }

    // Body for ValidateAccResponse
    public static class ValidateAccResBody{
        public String auth_token;
        public String refresh_token;
    }

    public static class ValidateAccResponse extends ServerResponse{
        public ValidateAccResBody body;
    }

    // Body for First Device Reg Response
    public static class FDRBody{
        public int device_id;
        public String device_name;
        public int workspace_id;
        public String workspace_name;
    }

    public static class FDRResponse extends ServerResponse{
        public FDRBody body;
    }

    // DTO for register request
    public static class CreateAccRequest{
        public String username;
        public String email;
        public String password;

        public CreateAccRequest(String uname, String email, String pswd){
            this.username = uname;
            this.email = email;
            this.password = pswd;
        }
    }

    // DTO for validate request
    public static class ValidateAccRequest{
        public String email;
        public int otp;

        public ValidateAccRequest(String email, int otp){
            this.email = email;
            this.otp = otp;
        }
    }

    // DTO for first device register
    public static class FDRRequest{
        public String device_name;
        public String workspace_name;

        public FDRRequest(String device_name, String workspace_name){
            this.device_name = device_name;
            this.workspace_name = workspace_name;
        }
    }
}
