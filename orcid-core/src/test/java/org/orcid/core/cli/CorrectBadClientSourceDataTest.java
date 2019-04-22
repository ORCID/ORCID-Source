package org.orcid.core.cli;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.dao.WorkDao;

public class CorrectBadClientSourceDataTest {
    
    @Mock
    private ResearcherUrlDao researcherUrlDao;
    
    @Mock
    private WorkDao workDao;
    
    @Mock
    private ProfileKeywordDao profileKeywordDao;
    
    @Mock
    private ProfileFundingDao profileFundingDao;
    
    @Mock
    private OtherNameDao otherNameDao;
    
    @Mock
    private NotificationDao notificationDao;
    
    @Mock
    private EmailDao emailDao;
    
    @Mock
    private AddressDao addressDao;
    
    @Mock
    private OrgAffiliationRelationDao orgAffiliationRelationDao;
    
    @Mock
    private OrgDao orgDao;
    
    @InjectMocks
    private CorrectBadClientSourceData corrector;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test() {
        Mockito.when(researcherUrlDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(500));
        Mockito.doNothing().when(researcherUrlDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(workDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(60));
        Mockito.doNothing().when(workDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(profileKeywordDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(2000)).thenReturn(getListOfIds(2000)).thenReturn(getListOfIds(2000)).thenReturn(getListOfIds(2000)).thenReturn(getListOfIds(500));
        Mockito.doNothing().when(profileKeywordDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(profileFundingDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(3));
        Mockito.doNothing().when(profileFundingDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(otherNameDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(700));
        Mockito.doNothing().when(otherNameDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(notificationDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(2000)).thenReturn(getListOfIds(2000)).thenReturn(getListOfIds(1837));
        Mockito.doNothing().when(notificationDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(emailDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(2000)).thenReturn(getListOfStringIds(1223));
        Mockito.doNothing().when(emailDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(addressDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(332));
        Mockito.doNothing().when(addressDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(orgAffiliationRelationDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(12));
        Mockito.doNothing().when(orgAffiliationRelationDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(orgDao.getIdsForClientSourceCorrection(Mockito.eq(2000))).thenReturn(getListOfIds(0));
        Mockito.doNothing().when(orgDao).correctClientSource(Mockito.anyList());
        
        corrector.correct();
        
        Mockito.verify(researcherUrlDao, Mockito.times(2)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(researcherUrlDao, Mockito.times(5)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(workDao, Mockito.times(2)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(workDao, Mockito.times(1)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(profileKeywordDao, Mockito.times(6)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(profileKeywordDao, Mockito.times(85)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(profileFundingDao, Mockito.times(2)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(profileFundingDao, Mockito.times(1)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(otherNameDao, Mockito.times(2)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(otherNameDao, Mockito.times(7)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(notificationDao, Mockito.times(4)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(notificationDao, Mockito.times(59)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(emailDao, Mockito.times(23)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(emailDao, Mockito.times(433)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(addressDao, Mockito.times(2)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(addressDao, Mockito.times(4)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(orgAffiliationRelationDao, Mockito.times(2)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(orgAffiliationRelationDao, Mockito.times(1)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(orgDao, Mockito.times(1)).getIdsForClientSourceCorrection(Mockito.eq(2000));
        Mockito.verify(orgDao, Mockito.never()).correctClientSource(Mockito.anyList());
    }

    private List<String> getListOfStringIds(int size) {
        List<String> ids = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            ids.add(String.valueOf(i));
        }
        return ids;
    }

    private List<BigInteger> getListOfIds(int size) {
        List<BigInteger> ids = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            ids.add(BigInteger.valueOf(i));
        }
        return ids;
    }

    
    
}
