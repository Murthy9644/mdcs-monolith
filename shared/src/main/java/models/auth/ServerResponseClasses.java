package models.auth;

import models.network.Http;

public class ServerResponseClasses {

    // Body for CreateAccResponse
    public static class CreateAccResBody{
        public String user_id;
        public String username;
        public String email;
    }

    public static class CreateAccResponse extends Http{
        public CreateAccResBody body;
    }

    // Body for ValidateAccResponse
    public static class ValidateAccResBody{
        public String auth_token;
        public String refresh_token;
    }

    public static class ValidateAccResponse extends Http{
        public ValidateAccResBody body;
    }

    // Body for First Device Reg Response
    public static class FDRBody{
        public String device_id;
        public String device_name;
        public String workspace_id;
        public String workspace_name;
    }

    public static class FDRResponse extends Http{
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
        public String user_id;
        public String email;
        public String otp;

        public ValidateAccRequest(String user_id, String email, String otp){
            this.user_id = user_id;
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
