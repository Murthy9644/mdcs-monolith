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
    }

    public static class ValidateAccResponse extends ServerResponse{
        public ValidateAccResBody body;
    }

    // Body for FirstDeviceRegResponse
    public static class FirstDeviceRegBody{
        public int device_id;
        public String device_name;
        public int workspace_id;
        public String workspace_name;
    }

    public static class FirstDeviceRegResponse extends ServerResponse{
        public FirstDeviceRegBody body;
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
}
