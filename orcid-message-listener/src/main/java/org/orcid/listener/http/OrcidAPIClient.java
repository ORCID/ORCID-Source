package org.orcid.listener.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
public class OrcidAPIClient {

    private final HttpClient client;
    
    @Value("${org.orcid.messaging.api.requestTimeout:60}")
    private int requestTimeout;
    
    public OrcidAPIClient(@Value("${org.orcid.messaging.api.connectionTimeout:60}") Integer connectionTimeout) {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(connectionTimeout)).build();
    }
    
    public HttpResponse<byte[]> getActivity(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(requestTimeout))
                .header("Content-Type", MediaType.APPLICATION_XML_TYPE.toString())
                .header("User-Agent","orcid/message-listener")
                .build();
        return client.send(request, BodyHandlers.ofByteArray());        
    }
}
