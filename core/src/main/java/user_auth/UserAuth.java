package user_auth;

import network.ServerRequest;

public class UserAuth{

    public static boolean signup(String username, String email, String password){        
        try{
            String signup_data = String.format(
                "{\"username\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}",
                username, email, password
            );

            String res = ServerRequest.post("http://localhost:1097/mdcs/auth/signup", null, signup_data);
            
            return res.equals("hello");
        } catch (Exception e){
            e.printStackTrace();

            return false;
        }
    }

    public static boolean signin(String email, String password){

        return true;
    }
}