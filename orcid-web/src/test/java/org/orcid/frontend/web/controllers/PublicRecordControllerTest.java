package org.orcid.frontend.web.controllers;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.locale.LocaleManager;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.pojo.PublicRecord;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class PublicRecordControllerTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/BiographyEntityData.xml", "/data/OrgsEntityData.xml",
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/WorksEntityData.xml");

    private String unclaimedUserOrcid = "0000-0000-0000-0001";
    private String userOrcid = "0000-0000-0000-0003";
    private String deprecatedUserOrcid = "0000-0000-0000-0004";
    private String lockedUserOrcid = "0000-0000-0000-0006";

    @Resource
    PublicRecordController publicRecordController;

    @Resource
    private LocaleManager localeManager;

    @Mock
    //private HttpServletRequest request;
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Mock
    //private HttpServletRequest request;
    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        assertNotNull(publicRecordController);
        Mockito.when(request.getRequestURI()).thenReturn("/");
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }


    @Test
    public void testGetPublicRecord() {
        PublicRecord record = publicRecordController.getPublicRecord(userOrcid);

        assertNotNull(record.getBiography());
        assertEquals("Biography for 0000-0000-0000-0003", record.getBiography().getBiography().getValue());

        assertNotNull(record.getDisplayName());
        assertEquals("Credit Name", record.getDisplayName());

        assertNotNull(record.getTitle());
        assertEquals((record.getDisplayName() + " (0000-0000-0000-0003) - " + localeManager.resolveMessage("layout.public-layout.title")), record.getTitle());
        assertNotNull(record.getOtherNames());
        assertEquals(1, record.getOtherNames().getOtherNames().size());

        assertNotNull(record.getCountries());
        assertEquals(1, record.getCountries().getAddresses().size());
        assertEquals(String.valueOf(9), record.getCountries().getAddresses().get(0).getPutCode());
        assertEquals(Iso3166Country.US.name(), record.getCountries().getAddresses().get(0).getIso2Country().getValue().value());
        assertEquals(Visibility.PUBLIC.value(), record.getCountries().getAddresses().get(0).getVisibility().getVisibility().value());

        assertNotNull(record.getKeyword());
        assertEquals(1, record.getKeyword().getKeywords().size());
        assertEquals(String.valueOf(9), record.getKeyword().getKeywords().get(0).getPutCode());
        assertEquals("PUBLIC", record.getKeyword().getKeywords().get(0).getContent());
        assertEquals(Visibility.PUBLIC.value(), record.getKeyword().getKeywords().get(0).getVisibility().getVisibility().value());

        assertNotNull(record.getWebsite());

        assertNotNull(record.getEmails());
        assertEquals(1, record.getEmails().getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", record.getEmails().getEmails().get(0).getValue());
        assertEquals(Visibility.PUBLIC, record.getEmails().getEmails().get(0).getVisibility());

        assertNotNull(record.getExternalIdentifier());
        assertEquals(1, record.getExternalIdentifier().getExternalIdentifiers().size());
        assertEquals(String.valueOf(13), record.getExternalIdentifier().getExternalIdentifiers().get(0).getPutCode());
        assertEquals("http://ext-id/public_ref", record.getExternalIdentifier().getExternalIdentifiers().get(0).getUrl());
        assertEquals(Visibility.PUBLIC.value(), record.getExternalIdentifier().getExternalIdentifiers().get(0).getVisibility().getVisibility().value());

        assertNotNull(record.getWebsite());
        assertEquals(1, record.getWebsite().getWebsites().size());
        assertEquals(String.valueOf(13), record.getWebsite().getWebsites().get(0).getPutCode());
        assertEquals("public_rurl", record.getWebsite().getWebsites().get(0).getUrlName());
        assertEquals(Visibility.PUBLIC.value(), record.getWebsite().getWebsites().get(0).getVisibility().getVisibility().value());
    }
}
