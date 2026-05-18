package network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class ServerRequest {
    private HttpClient client;
    private String url_base;
    
    public String post(String api, String headers[], String body)
    throws IOException, InterruptedException{
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(this.url_base + api))
            .headers(headers)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> res = this.client.send(req, HttpResponse.BodyHandlers.ofString());
        
        return res.body().toString();
    }

    public String get(String api, String headers[])
    throws IOException, InterruptedException{
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(this.url_base + api))
            .headers(headers)
            .GET()
            .build();

        HttpResponse<String> res = this.client.send(req, HttpResponse.BodyHandlers.ofString());
        
        return res.toString();
    }

    public ServerRequest(Properties APP){
        String host = APP.getProperty("server.host");
        String port = APP.getProperty("server.port");

        this.client = HttpClient.newHttpClient();
        this.url_base = "http://" + host + ":" + port + "/mdcs";
    }
}
