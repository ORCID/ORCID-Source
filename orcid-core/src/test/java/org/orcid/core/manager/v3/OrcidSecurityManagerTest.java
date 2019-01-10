package org.orcid.core.manager.v3;

import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.security.aop.LockedException;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidSecurityManagerTest {
    
    private static final String ORCID = "0000-0000-0000-0000";
    private static final String CLIENT_ID = "APP-0000000000000001";

    @Resource(name = "orcidSecurityManagerV3")
    protected OrcidSecurityManager orcidSecurityManager;

    @Value("${org.orcid.core.claimWaitPeriodDays:10}")
    private int claimWaitPeriodDays;
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;
    
    @Resource 
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Mock
    protected ProfileEntityCacheManager profileEntityCacheManagerMock;

    @Mock
    protected SourceManager sourceManagerMock;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "profileEntityCacheManager", profileEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", sourceManagerMock);
        SourceEntity source = new SourceEntity();
        source.setSourceClient(new ClientDetailsEntity(CLIENT_ID));
        when(sourceManagerMock.retrieveActiveSourceEntity()).thenReturn(source);        
    }
   
    @After
    public void after() {
        //Restore the original beans
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", sourceManager);
    }
    
    @Test(expected = NoResultException.class)
    public void checkProfile_InvalidOrcidTest() {
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenThrow(NoResultException.class);
        orcidSecurityManager.checkProfile(ORCID);
    }
    
    @Test(expected = OrcidDeprecatedException.class)
    public void checkProfile_DeprecatedTest() {
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(true);
        entity.setPrimaryRecord(new ProfileEntity());
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);
        orcidSecurityManager.checkProfile(ORCID);
    }
    
    @Test(expected = OrcidNotClaimedException.class)
    public void checkProfile_NotClaimed_NotOldEnough_NotSourceTest() {
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(false);
        entity.setSubmissionDate(new Date());
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);
        orcidSecurityManager.checkProfile(ORCID);
    }        
    
    @Test(expected = LockedException.class)
    public void checkProfile_LockedTest() {
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(true);
        entity.setRecordLocked(true);
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);
        orcidSecurityManager.checkProfile(ORCID);
    }
        
    @Test(expected = DeactivatedException.class)
    public void checkProfile_DeactivatedTest() {
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(true);
        entity.setDeactivationDate(new Date());
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);
        orcidSecurityManager.checkProfile(ORCID);
    }
    
    @Test
    public void checkProfile_OkTest() {
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(true);
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);
        orcidSecurityManager.checkProfile(ORCID);
    }
     
    @Test
    public void checkProfile_NotClaimed_NotOldEnough_SourceTest() {
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(false);
        entity.setSubmissionDate(new Date());
        SourceEntity source = new SourceEntity();
        source.setSourceClient(new ClientDetailsEntity(CLIENT_ID));
        entity.setSource(source);
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);
        orcidSecurityManager.checkProfile(ORCID);
    }  
    
    @Test
    public void checkProfile_NotClaimed_OldEnoughTest() {
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(false);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -(claimWaitPeriodDays + 1));
        entity.setSubmissionDate(cal.getTime());
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);
        orcidSecurityManager.checkProfile(ORCID);
    }  
}
