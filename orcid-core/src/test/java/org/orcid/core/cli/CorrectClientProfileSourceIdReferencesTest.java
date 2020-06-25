package org.orcid.core.cli;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class CorrectClientProfileSourceIdReferencesTest {
    
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
    
    @Mock
    private ProfileDao profileDao;
    
    @Mock
    private ClientDetailsDao clientDetailsDao;
    
    @Captor
    private ArgumentCaptor<List<BigInteger>> idsListCaptor;
    
    @Captor
    private ArgumentCaptor<List<String>> stringIdsListCaptor;
    
    @InjectMocks
    private CorrectClientProfileSourceIdReferences corrector;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test() {
        Mockito.when(profileDao.findByOrcidType("CLIENT")).thenReturn(getListOf126Profiles());
        Mockito.when(clientDetailsDao.find(Mockito.anyString())).thenReturn(new ClientDetailsEntity());
        
        Mockito.when(researcherUrlDao.getIdsOfResearcherUrlsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(166));
        Mockito.doNothing().when(researcherUrlDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(workDao.getIdsOfWorksReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(299));
        Mockito.doNothing().when(workDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(profileKeywordDao.getIdsOfKeywordsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(6000)).thenReturn(getListOfIds(6000)).thenReturn(getListOfIds(6000)).thenReturn(getListOfIds(6000)).thenReturn(getListOfIds(500));
        Mockito.doNothing().when(profileKeywordDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(profileFundingDao.getIdsOfFundingsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(3));
        Mockito.doNothing().when(profileFundingDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(otherNameDao.getIdsOfOtherNamesReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(44));
        Mockito.doNothing().when(otherNameDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(notificationDao.getIdsOfNotificationsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(901));
        Mockito.doNothing().when(notificationDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(emailDao.getIdsOfEmailsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfStringIds(233));
        Mockito.doNothing().when(emailDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(addressDao.getIdsOfAddressesReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(15));
        Mockito.doNothing().when(addressDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(orgAffiliationRelationDao.getIdsOfOrgAffiliationRelationsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(12));
        Mockito.doNothing().when(orgAffiliationRelationDao).correctClientSource(Mockito.anyList());
        
        Mockito.when(orgDao.getIdsOfOrgsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList())).thenReturn(getListOfIds(0));
        Mockito.doNothing().when(orgDao).correctClientSource(Mockito.anyList());
        
        ReflectionTestUtils.setField(corrector, "batchSize", 300);
        corrector.correctProfileReferences();
        
        Mockito.verify(researcherUrlDao, Mockito.times(2)).getIdsOfResearcherUrlsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(researcherUrlDao).correctClientSource(idsListCaptor.capture());
        List<BigInteger> idsArg = idsListCaptor.getAllValues().get(idsListCaptor.getAllValues().size() - 1);
        assertEquals(166, idsArg.size());
        
        Mockito.verify(workDao, Mockito.times(2)).getIdsOfWorksReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(workDao).correctClientSource(idsListCaptor.capture());
        idsArg = idsListCaptor.getAllValues().get(idsListCaptor.getAllValues().size() - 1);
        assertEquals(299, idsArg.size());
        
        Mockito.verify(profileKeywordDao, Mockito.times(6)).getIdsOfKeywordsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(profileKeywordDao, Mockito.times(82)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(profileFundingDao, Mockito.times(2)).getIdsOfFundingsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(profileFundingDao).correctClientSource(idsListCaptor.capture());
        idsArg = idsListCaptor.getAllValues().get(idsListCaptor.getAllValues().size() - 1);
        assertEquals(3, idsArg.size());
        
        Mockito.verify(otherNameDao, Mockito.times(2)).getIdsOfOtherNamesReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(otherNameDao).correctClientSource(Mockito.anyList());
        
        Mockito.verify(notificationDao, Mockito.times(2)).getIdsOfNotificationsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(notificationDao, Mockito.times(4)).correctClientSource(Mockito.anyList());
        
        Mockito.verify(emailDao, Mockito.times(2)).getIdsOfEmailsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(emailDao).correctClientSource(stringIdsListCaptor.capture());
        List<String> stringIdsArg = stringIdsListCaptor.getValue();
        assertEquals(233, stringIdsArg.size());
        
        Mockito.verify(addressDao, Mockito.times(2)).getIdsOfAddressesReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(addressDao).correctClientSource(idsListCaptor.capture());
        idsArg = idsListCaptor.getAllValues().get(idsListCaptor.getAllValues().size() - 1);
        assertEquals(15, idsArg.size());
        
        Mockito.verify(orgAffiliationRelationDao, Mockito.times(2)).getIdsOfOrgAffiliationRelationsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(orgAffiliationRelationDao).correctClientSource(idsListCaptor.capture());
        idsArg = idsListCaptor.getAllValues().get(idsListCaptor.getAllValues().size() - 1);
        assertEquals(12, idsArg.size());
        
        Mockito.verify(orgDao, Mockito.times(1)).getIdsOfOrgsReferencingClientProfiles(Mockito.eq(6000), Mockito.anyList());
        Mockito.verify(orgDao, Mockito.never()).correctClientSource(Mockito.anyList());
    }

    private List<ProfileEntity> getListOf126Profiles() {
        List<ProfileEntity> profiles = new ArrayList<>();
        for (int i = 1; i <= 126; i++) {
            ProfileEntity entity = new ProfileEntity();
            entity.setId(String.valueOf(i));
            profiles.add(entity);
        }
        return profiles;
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
