package org.orcid.core.manager.v3;

import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.impl.OrcidSecurityManagerImpl;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class OrcidSecurityManagerTest {
    
    private static final String ORCID = "0000-0000-0000-0000";
    private static final String CLIENT_ID = "APP-0000000000000001";

    /**
     * A second member client, so that "the profile was created by someone
     * else" can be modelled without reusing the acting client's id.
     */
    private static final String OTHER_CLIENT_ID = "APP-0000000000000002";

    /**
     * Matches the @Value default on OrcidSecurityManagerImpl. It has to be
     * non-zero, or DateUtils.olderThan(justCreatedDate, 0) is already true a
     * millisecond later and checkProfile_NotClaimed_NotOldEnough_NotSourceTest
     * stops throwing.
     */
    private static final int CLAIM_WAIT_PERIOD_DAYS = 10;

    @InjectMocks
    protected OrcidSecurityManagerImpl orcidSecurityManager = new OrcidSecurityManagerImpl();

    @Mock
    protected ProfileEntityCacheManager profileEntityCacheManagerMock;

    @Mock
    protected SourceManager sourceManagerMock;
    
    @Before
    public void before() {
        // @InjectMocks does not resolve @Value fields, so they are set here.
        ReflectionTestUtils.setField(orcidSecurityManager, "claimWaitPeriodDays", CLAIM_WAIT_PERIOD_DAYS);
        ReflectionTestUtils.setField(orcidSecurityManager, "baseUrl", "https://testserver.orcid.org");
        SourceEntity source = new SourceEntity();
        source.setSourceClient(new ClientDetailsEntity(CLIENT_ID));
        when(sourceManagerMock.retrieveActiveSourceEntity()).thenReturn(source);        
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
        cal.add(Calendar.DAY_OF_YEAR, -(CLAIM_WAIT_PERIOD_DAYS + 1));
        entity.setSubmissionDate(cal.getTime());
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);
        orcidSecurityManager.checkProfile(ORCID);
    }  

    @Test(expected = OrcidNotClaimedException.class)
    public void checkProfile_NotClaimed_NotOldEnough_DifferentSourceTest() {
        // Catches: replacing !Objects.equals(profileSource, currentSource)
        // with false in OrcidSecurityManagerImpl.checkProfile. The creator
        // only exemption would then admit any client to somebody else's
        // unclaimed record. checkProfile_NotClaimed_NotOldEnough_NotSourceTest
        // covers only the profileSource == null half of that condition and
        // stays green under the mutation, and
        // checkProfile_NotClaimed_NotOldEnough_SourceTest covers only the
        // matching source case, which the mutation also leaves passing.
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(false);
        entity.setSubmissionDate(new Date());
        SourceEntity createdByAnotherClient = new SourceEntity();
        createdByAnotherClient.setSourceClient(new ClientDetailsEntity(OTHER_CLIENT_ID));
        entity.setSource(createdByAnotherClient);
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(entity);

        // The active source stubbed in before() is CLIENT_ID, not
        // OTHER_CLIENT_ID, so this is the source mismatch half of the rule.
        orcidSecurityManager.checkProfile(ORCID);
    }
}
