package org.orcid.core.utils.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class HttpRequestUtils {

    @Value("${org.orcid.http.timeout:15}")
    private int connectionTimeout;
    
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
    
    public HttpResponse<String> doPost(String url) throws IOException, InterruptedException, URISyntaxException {
        Duration timeout = Duration.ofSeconds(connectionTimeout);  
        HttpRequest request = HttpRequest.newBuilder(new URI(url))
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .timeout(timeout)
                    .build();
        
        HttpResponse<String> response = HttpClient
                .newBuilder()
                .connectTimeout(timeout)
                .build()
                .send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
        
        return response;
    }

    public HttpResponse<String> doPost(String url, Map<String, String> parameters) throws IOException, InterruptedException, URISyntaxException {

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        Duration timeout = Duration.ofSeconds(connectionTimeout);
        HttpRequest request = HttpRequest.newBuilder(new URI(url))
                .version(HttpClient.Version.HTTP_1_1)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .timeout(timeout)
                .build();

        HttpResponse<String> response = HttpClient
                .newBuilder()
                .connectTimeout(timeout)
                .build()
                .send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));

        return response;
    }
}
