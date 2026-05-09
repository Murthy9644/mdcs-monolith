package network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerRequest {
    private static HttpClient client;

    static{
        client = HttpClient.newHttpClient();
    }
    
    public static String post(String api, String headers[], String body)
    throws IOException, InterruptedException{
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(api))
            .headers(headers)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        
        return res.toString();
    }

    public static String get(String api, String headers[])
    throws IOException, InterruptedException{
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(api))
            .headers(headers)
            .GET()
            .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        
        return res.toString();
    }
}
