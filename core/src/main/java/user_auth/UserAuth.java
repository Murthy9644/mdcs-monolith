package user_auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserAuth{

    public static boolean signup(String username, String email, String password){
        HttpClient client = HttpClient.newHttpClient();
        
        try{
            String signup_data = String.format(
                "{\"username\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}",
                username, email, password
            );

            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:1097/mdcs/auth/signup"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(signup_data))
                .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            
            return true;
        } catch (Exception e){
            e.printStackTrace();

            return false;
        } finally{
            client.close();
        }
    }

    public static boolean signin(String email, String password){

        return true;
    }
}