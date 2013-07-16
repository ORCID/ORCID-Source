/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.t2.server;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-t2-web-context.xml" })
public class T2OrcidApiServiceImplLatestPutAndPostMetricsTest {

    @Resource
    private T2OrcidApiServiceImplLatest t2OrcidApiService;

    // mock the search results so that we don't need a db setub to check the
    // counter works as expected

    private final Response successResponse = Response.ok().build();

    private T2OrcidApiServiceDelegator mockServiceDelegator;

    private OrcidMessage orcidMessage;

    @Before
    public void setupMocks() {
        mockServiceDelegator = mock(T2OrcidApiServiceDelegator.class);
        // view status is always fine
        t2OrcidApiService.setServiceDelegator(mockServiceDelegator);
        when(mockServiceDelegator.viewStatusText()).thenReturn(successResponse);

    }

    @After
    @Before
    public void resetVals() {
        T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.clear();
        T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.clear();
    }

    @Test
    public void testCreateProfileXML() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.createProfileXML(orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);

    }

    @Test
    public void testCreateProfileJson() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.createProfileJson(orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
    }

    @Test
    public void testUpdateBioDetailsXml() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.updateBioDetailsXml("", orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 1);

    }

    @Test
    public void testUpdateBioDetailsJson() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.updateBioDetailsJson("", orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 1);
    }

    @Test
    public void testAddWorksXml() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.addWorksXml("", orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
    }

    @Test
    public void testAddWorksJson() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.addWorksJson("", orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
    }

    @Test
    public void testUpdateWorksXml() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.updateWorksXml("", orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 1);
    }

    @Test
    public void testUpdateWorksJson() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.updateWorksJson("", orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 1);
    }

    @Test
    public void testAddExternalIdentifiersXml() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.addExternalIdentifiersXml("", orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
    }

    @Test
    public void testAddExternalIdentifiersJson() {
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 0);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
        t2OrcidApiService.addExternalIdentifiersJson("", orcidMessage);
        assertTrue(T2OrcidApiServiceImplLatest.T2_POST_REQUESTS.count() == 1);
        assertTrue(T2OrcidApiServiceImplLatest.T2_PUT_REQUESTS.count() == 0);
    }

}
