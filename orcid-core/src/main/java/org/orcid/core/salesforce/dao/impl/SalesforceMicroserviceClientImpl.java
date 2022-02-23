package org.orcid.core.salesforce.dao.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.orcid.core.salesforce.dao.SalesforceMicroserviceClient;
import org.springframework.beans.factory.annotation.Value;

public class SalesforceMicroserviceClientImpl implements SalesforceMicroserviceClient {

	@Value("${org.orcid.microservice.gateway.url}")
	private String gatewayUrl;
	
	@Value("${org.orcid.microservice.salesforce.token}")
	private String salesforceReadToken;
	
	private final HttpClient httpClient;
	
	private final String retrieveMembersEndpoint;
	
	public SalesforceMicroserviceClientImpl(@Value("${org.orcid.salesforce.api.timeout:5000}") Integer timeout) {
		retrieveMembersEndpoint = gatewayUrl + "/members/list";
		httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_1_1)
	            .connectTimeout(Duration.ofSeconds(timeout))
	            .build();
	}
	
	@Override
	public String retrieveMembers() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(retrieveMembersEndpoint))
                .setHeader("Authorization", "Bearer " + salesforceReadToken)
                .build();
		
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		//TODO: Handle exceptions
		return response.body();
	}

}
