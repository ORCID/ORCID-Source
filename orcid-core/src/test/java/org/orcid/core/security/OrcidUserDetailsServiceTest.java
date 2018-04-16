package org.orcid.core.security;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.manager.SlackManager;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.TargetProxyHelper;

public class OrcidUserDetailsServiceTest {

    OrcidUserDetailsServiceImpl service = new OrcidUserDetailsServiceImpl();
    
    @Mock
    private ProfileDao profileDao;
    
    @Mock
    private EmailDao emailDao;

    @Mock
    private OrcidSecurityManager securityMgr;

    @Mock
    private SlackManager slackManager;

    @Mock
    private SalesForceManager salesForceManager;
    
    private static final String ORCID = "0000-0000-0000-0000";
    private static final String EMAIL = "email@test.orcid.org";
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(service, "profileDao", profileDao);
        TargetProxyHelper.injectIntoProxy(service, "emailDao", emailDao);
        TargetProxyHelper.injectIntoProxy(service, "securityMgr", securityMgr);
        TargetProxyHelper.injectIntoProxy(service, "slackManager", slackManager);
        TargetProxyHelper.injectIntoProxy(service, "salesForceManager", salesForceManager);
        
        ProfileEntity profile = getProfileEntity();
        EmailEntity email = getEmailEntity(profile);
        
        when(emailDao.findCaseInsensitive(anyString())).thenReturn(null);
        when(emailDao.findCaseInsensitive(EMAIL)).thenReturn(email);
        when(emailDao.findPrimaryEmail(ORCID)).thenReturn(email);
        
        when(profileDao.find(anyString())).thenReturn(null);
        when(profileDao.find(ORCID)).thenReturn(profile);
    }
    
    @Test
    public void loadUserByUsername_EmailTest() {
        service.loadUserByUsername(ORCID);
    }
    
    @Test
    public void loadUserByUsername_OrcidIdTest() {
        service.loadUserByUsername(EMAIL);
    }
    
    @Test
    public void loadUserByProfileTest() {
        service.loadUserByProfile(getProfileEntity());
    }
    
    private EmailEntity getEmailEntity(ProfileEntity profile) {
        EmailEntity result = new EmailEntity();
        result.setId(EMAIL);
        result.setProfile(profile);
        result.setVerified(true);
        return result;
    }
    
    private ProfileEntity getProfileEntity() {
        ProfileEntity result = new ProfileEntity();
        result.setId(ORCID);
        result.setOrcidType(OrcidType.USER);
        return result;
    }
}

