/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.analytics.client.AnalyticsClient;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

public class AnalyticsProcessTest {

    @Mock
    private AnalyticsClient analyticsClient;
    
    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAnalyticsProcessForPublicClient() throws InterruptedException {
        String clientDetailsId = "some-client-details-id";
        Mockito.when(clientDetailsEntityCacheManager.retrieve(Mockito.eq(clientDetailsId))).thenReturn(getPublicClient());
        
        ContainerRequest request = getRequest();
        ContainerResponse response = getResponse(request);

        AnalyticsProcess process = new AnalyticsProcess();
        process.setRequest(request);
        process.setResponse(response);
        process.setClientDetailsId(clientDetailsId);
        process.setAnalyticsClient(analyticsClient);
        process.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        process.setPublicApi(true);

        Thread t = new Thread(process);
        t.start();
        t.join();

        ArgumentCaptor<AnalyticsData> captor = ArgumentCaptor.forClass(AnalyticsData.class);
        Mockito.verify(analyticsClient).sendAnalyticsData(captor.capture());

        AnalyticsData data = captor.getValue();
        assertNotNull(data);
        assertEquals("POST", data.getMethod());
        assertEquals("works", data.getCategory());
        assertEquals("Public API v2.0", data.getApiVersion());
        assertEquals(ClientType.PUBLIC_CLIENT.value() + " | a public client - some-client-details-id", data.getClientDetailsString());
        assertEquals("37.14.150.83", data.getIpAddress());
        assertEquals(Integer.valueOf(200), data.getResponseCode());
        assertEquals("https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321/works", data.getUrl());
        assertEquals("blah", data.getUserAgent());
        assertEquals("application/xml", data.getContentType());
    }

    @Test
    public void testAnalyticsProcessForMemberClient() throws InterruptedException {
        String clientDetailsId = "some-client-details-id";
        Mockito.when(clientDetailsEntityCacheManager.retrieve(Mockito.eq(clientDetailsId))).thenReturn(getMemberClient());
        
        ContainerRequest request = getRequest();
        ContainerResponse response = getResponse(request);

        AnalyticsProcess process = new AnalyticsProcess();
        process.setRequest(request);
        process.setResponse(response);
        process.setClientDetailsId(clientDetailsId);
        process.setAnalyticsClient(analyticsClient);
        process.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        process.setPublicApi(false);

        Thread t = new Thread(process);
        t.start();
        t.join();

        ArgumentCaptor<AnalyticsData> captor = ArgumentCaptor.forClass(AnalyticsData.class);
        Mockito.verify(analyticsClient).sendAnalyticsData(captor.capture());

        AnalyticsData data = captor.getValue();
        assertNotNull(data);
        assertEquals("POST", data.getMethod());
        assertEquals("works", data.getCategory());
        assertEquals("Member API v2.0", data.getApiVersion());
        assertEquals(ClientType.CREATOR.value() + " | a member client - some-client-details-id", data.getClientDetailsString());
        assertEquals("37.14.150.83", data.getIpAddress());
        assertEquals(Integer.valueOf(200), data.getResponseCode());
        assertEquals("https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321/works", data.getUrl());
        assertEquals("blah", data.getUserAgent());
        assertEquals("application/xml", data.getContentType());
    }
    
    @Test
    public void testAnalyticsProcessForAnonymous() throws InterruptedException {
        ContainerRequest request = getRequest();
        ContainerResponse response = getResponse(request);

        AnalyticsProcess process = new AnalyticsProcess();
        process.setRequest(request);
        process.setResponse(response);
        process.setAnalyticsClient(analyticsClient);
        process.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        process.setPublicApi(true);

        Thread t = new Thread(process);
        t.start();
        t.join();

        ArgumentCaptor<AnalyticsData> captor = ArgumentCaptor.forClass(AnalyticsData.class);
        Mockito.verify(analyticsClient).sendAnalyticsData(captor.capture());

        AnalyticsData data = captor.getValue();
        assertNotNull(data);
        assertEquals("POST", data.getMethod());
        assertEquals("works", data.getCategory());
        assertEquals("Public API v2.0", data.getApiVersion());
        assertEquals("Public API user", data.getClientDetailsString());
        assertEquals("37.14.150.83", data.getIpAddress());
        assertEquals(Integer.valueOf(200), data.getResponseCode());
        assertEquals("https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321/works", data.getUrl());
        assertEquals("blah", data.getUserAgent());
        assertEquals("application/xml", data.getContentType());
    }

    private ClientDetailsEntity getMemberClient() {
        ClientDetailsEntity client = new ClientDetailsEntity();
        client.setClientName("a member client");
        client.setClientType(ClientType.CREATOR);
        return client;
    }
    
    private ClientDetailsEntity getPublicClient() {
        ClientDetailsEntity client = new ClientDetailsEntity();
        client.setClientName("a public client");
        client.setClientType(ClientType.PUBLIC_CLIENT);
        return client;
    }

    private ContainerResponse getResponse(ContainerRequest request) {
        ContainerResponse response = new ContainerResponse(new WebApplicationImpl(), request, null);
        response.setStatus(200);
        return response;
    }

    private ContainerRequest getRequest() {
        InBoundHeaders headers = new InBoundHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/xml");
        headers.add(HttpHeaders.USER_AGENT, "blah");
        headers.add("X-FORWARDED-FOR", "37.14.150.83");
        return new ContainerRequest(new WebApplicationImpl(), "POST", URI.create("https://localhost:8443/orcid-api-web/"),
                URI.create("https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321/works"), headers, null);
    }

}
