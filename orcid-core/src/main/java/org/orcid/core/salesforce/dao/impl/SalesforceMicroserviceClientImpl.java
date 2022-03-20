package org.orcid.core.salesforce.dao.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.orcid.core.salesforce.dao.SalesforceMicroserviceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SalesforceMicroserviceClientImpl implements SalesforceMicroserviceClient {

    @Value("${org.orcid.microservice.salesforce.token}")
    private String salesforceReadToken;

    private final HttpClient httpClient;

    private final String membersListEndpoint;
    private final String memberDetailsEndpoint;

    public SalesforceMicroserviceClientImpl(@Value("${org.orcid.microservice.gateway.url}") String gatewayUrl,
            @Value("${org.orcid.salesforce.api.timeout:5000}") Integer timeout) {
        membersListEndpoint = gatewayUrl + "/members/list";
        memberDetailsEndpoint = gatewayUrl + "/member/%s/details";
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(timeout)).build();
    }

    @Override
    public String retrieveMembers() throws IOException, InterruptedException {
        // TODO: Handle exceptions
        return doGetRequest(membersListEndpoint);
    }

    @Override
    public String retrieveMemberDetails(String memberId) throws IOException, InterruptedException {
        // TODO: Handle exceptions
        return doGetRequest(String.format(memberDetailsEndpoint, memberId));
    }

    private String doGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).setHeader("Authorization", "Bearer " + salesforceReadToken).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        // TODO: Handle exceptions
        return response.body();
    }

}
