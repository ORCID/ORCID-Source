package org.orcid.listener.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.core.MediaType;

@Component
public class OrcidAPIClient {

    private final HttpClient client;
    
    @Value("${org.orcid.messaging.api.requestTimeout:60}")
    private int requestTimeout = 60;
    
    @Autowired
    public OrcidAPIClient(@Value("${org.orcid.messaging.api.connectionTimeout:60}") Integer connectionTimeout,
            @Value("${org.orcid.messaging.api.requestTimeout:60}") Integer requestTimeout) {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(connectionTimeout)).build();
        if (requestTimeout != null) {
            this.requestTimeout = requestTimeout;
        }
    }

    public OrcidAPIClient(@Value("${org.orcid.messaging.api.connectionTimeout:60}") Integer connectionTimeout) {
        this(connectionTimeout, 60);
    }
    
    public HttpResponse<byte[]> getActivity(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(requestTimeout))
                .header("Accept", MediaType.APPLICATION_XML_TYPE.toString())
                .header("User-Agent","orcid/message-listener")
                .build();
        return client.send(request, BodyHandlers.ofByteArray());        
    }
}
