package models.auth.user_auth;

public class ServerResponseClasses {

    public static class CreateAccResBody{
        public int user_id;
        public String username;
        public String email;
    }
    
    public static class CreateAccResponse{
        public boolean status;
        public String message;
        public CreateAccResBody body;
    }
}
