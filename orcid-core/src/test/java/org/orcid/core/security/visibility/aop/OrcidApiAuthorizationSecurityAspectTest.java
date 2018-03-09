package org.orcid.core.security.visibility.aop;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;

public class OrcidApiAuthorizationSecurityAspectTest extends BaseTest {
    private final String clientId = "APP-0001";
    private final String userOrcid = "0000-0000-0000-0001";

    OrcidApiAuthorizationSecurityAspect orcidApiAuthorizationSecurityAspect;

    @Mock
    private OrcidOauth2TokenDetailDao mockedOrcidOauth2TokenDetailDao;

    @Before
    public void setup() {
        if(orcidApiAuthorizationSecurityAspect == null) {
            orcidApiAuthorizationSecurityAspect = new OrcidApiAuthorizationSecurityAspect();
        }
        MockitoAnnotations.initMocks(this);
        orcidApiAuthorizationSecurityAspect.setOrcidOauth2TokenDetailDao(mockedOrcidOauth2TokenDetailDao);
    }
    
    @Test
    public void testActivitiesReadLimitedScopes() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/activities/read-limited"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
    }
    
    @Test
    public void testActivitiesUpdateScopes() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/activities/update"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
    }
    
    @Test
    public void testWorksReadLimitedScopes() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/orcid-works/read-limited"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
    }
    
    @Test
    public void testFundingReadLimitedScopes() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/funding/read-limited"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));        
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
    }
    
    @Test
    public void testAffiliationsReadLimitedScopes() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/affiliations/read-limited"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));                
    }
    
    @Test
    public void testOrcidBioScopes() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/orcid-bio/read-limited"));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/orcid-bio/external-identifiers/create"));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
    }
    
    @Test
    public void testOrcidProfileReadLimitedScope() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/orcid-profile/read-limited"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
    }
    
    @Test
    public void testPersonReadLimitedScope() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/person/read-limited"));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
    }
    
    @Test
    public void testPersonUpdateScope() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/person/update"));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
    }
    
    @Test
    public void testCombineSomeScopes() {
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/person/update", "/orcid-works/update"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
        
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/person/update", "/orcid-works/update", "/funding/read-limited"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertFalse(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
        
        when(mockedOrcidOauth2TokenDetailDao.findAvailableScopesByUserAndClientId(clientId, userOrcid)).thenReturn(Arrays.asList("/person/update", "/orcid-works/update", "/funding/read-limited", "/activities/read-limited"));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.ORCID_WORKS_READ_LIMITED.getContent(), ScopePathType.ORCID_WORKS_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.FUNDING_READ_LIMITED.getContent(), ScopePathType.FUNDING_UPDATE.getContent()));
        assertTrue(orcidApiAuthorizationSecurityAspect.hasScopeEnabled(clientId, userOrcid, ScopePathType.AFFILIATIONS_READ_LIMITED.getContent(), ScopePathType.AFFILIATIONS_UPDATE.getContent()));
    }
}
