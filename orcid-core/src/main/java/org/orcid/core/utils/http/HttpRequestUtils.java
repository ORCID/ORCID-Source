package org.orcid.core.utils.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

@Component
public class HttpRequestUtils {

    public HttpResponse<String> doGet(String url) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder(new URI(url)).GET().build();        
        HttpResponse<String> response = HttpClient
                .newBuilder()
                .build()
                .send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
        
        return response;
    }
    
    public HttpResponse<String> doGet(String url, String accept, Redirect redirectPolicy) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder(new URI(url))
                .header("accept", accept)                
                .GET()
                .build();        
        HttpResponse<String> response = HttpClient
                .newBuilder()
                .followRedirects(redirectPolicy)
                .build()
                .send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
        
        return response;
    }
}
