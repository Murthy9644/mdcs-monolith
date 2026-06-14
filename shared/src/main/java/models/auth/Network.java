package models.auth;

import models.network.Http.Request;
import models.network.Http.Response;

public class Network {
    
    public static class CreateUsrReq extends Request<CreateUsrReq.Body>{

        public static class Body{
            public String username;
            public String email;
            public String password;

            public Body(String uname, String email, String pswd){
                this.username = uname;
                this.email = email;
                this.password = pswd;
            }
        }

        public CreateUsrReq(){
            this.endpoint = "/auth/user/signup";
            this.addHeader("Content-type", "application/json");
        }
    }

    public static class CreateUsrRes extends Response<CreateUsrRes.Body>{
        
        public static class Body{
            public String user_id;
            public String username;
            public String email;
        }
    }

    public static class ValidateUsrReq extends Request<ValidateUsrReq.Body>{

        public static class Body{
            public String user_id;
            public String email;
            public String otp;

            public Body(String id, String email, String otp){
                this.user_id = id;
                this.email = email;
                this.otp = otp;
            }
        }

        public ValidateUsrReq(){
            this.endpoint = "/auth/user/verify-otp";
            this.addHeader("Content-type", "application/json");
        }
    }

    public static class ValidateUsrRes extends Response<ValidateUsrRes.Body>{

        public static class Body{
            public String auth_tok;
            public String refresh_tok;
        }
    }

    public static class RegisterDeviceReq extends Request<RegisterDeviceReq.Body>{

        public static class Body{
            public String device_name;
            public String workspace_name;

            public Body(String dname, String wname){
                this.device_name = dname;
                this.workspace_name = wname;
            }
        }

        public RegisterDeviceReq(){
            this.endpoint = "/auth/device/register";
            this.addHeader("Content-type", "application/json");
        }
    }

    public static class RegisterDeviceRes extends Response<RegisterDeviceRes.Body>{

        public static class Body{
            public String device_id;
            public String device_name;
            public String workspace_id;
            public String workspace_name;
        }
    }
}
