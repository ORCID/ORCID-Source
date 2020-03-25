package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.v3.release.record.SourceType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class SpamControllerTest extends BaseControllerTest {

    private static String USER_ORCID = "4444-4444-4444-4497";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4499";   
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml",
            "/data/SpamEntityData.xml", "/data/Oauth2TokenDetailsData.xml");

    @Resource
    private SpamController spamController;

    @Mock
    private HttpServletRequest servletRequest;

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }

    @Test
    public void testReadOne() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        org.orcid.pojo.Spam spam = spamController.getSpam("0000-0000-0000-0004");

        assertNotNull(spam);
        assertEquals(Integer.valueOf(1), spam.getCount());
        assertEquals(SourceType.USER, SourceType.fromValue(spam.getSourceType()));                

    }

    @Test
    public void testCreateSpam() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        spamController.reportSpam(USER_ORCID);        
        
        org.orcid.pojo.Spam spam = spamController.getSpam(USER_ORCID);
        assertNotNull(spam);
        assertEquals(Integer.valueOf(1), spam.getCount());
        assertEquals(SourceType.USER, SourceType.fromValue(spam.getSourceType()));        

    }

    @Test
    public void testUpdateSpam() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        spamController.reportSpam(OTHER_USER_ORCID);
        
        
        org.orcid.pojo.Spam spam = spamController.getSpam(OTHER_USER_ORCID);
        assertNotNull(spam);
        assertEquals(Integer.valueOf(2), spam.getCount());
        assertEquals(SourceType.USER, SourceType.fromValue(spam.getSourceType()));        
    }
    
    @Test
    public void testDeleteSpam() {
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        spamController.removeSpam(OTHER_USER_ORCID);
        org.orcid.pojo.Spam spamDeleted = spamController.getSpam(OTHER_USER_ORCID);            
        assertNull(spamDeleted);
    }

}
