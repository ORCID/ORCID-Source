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
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.dao.WorkDao;
import org.springframework.test.util.ReflectionTestUtils;

public class RevertUserOBODataForUserTest {
    
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
    private EmailDao emailDao;
    
    @Mock
    private AddressDao addressDao;
    
    @Mock
    private OrgAffiliationRelationDao orgAffiliationRelationDao;
    
    @InjectMocks
    private RevertUserOBODataForUser dataReverter;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test() {
        String clientId = "client-id";
        
        Mockito.when(researcherUrlDao.getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20))).thenReturn(getListOfIds(RevertUserOBODataForUser.BATCH_SIZE * 3));
        Mockito.doNothing().when(researcherUrlDao).revertUserOBODetails(Mockito.anyList());
        
        Mockito.when(workDao.getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20))).thenReturn(getListOfIds(RevertUserOBODataForUser.BATCH_SIZE * 9 + 3));
        Mockito.doNothing().when(workDao).revertUserOBODetails(Mockito.anyList());
        
        Mockito.when(profileKeywordDao.getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20))).thenReturn(getListOfIds(RevertUserOBODataForUser.BATCH_SIZE * 20)).thenReturn(getListOfIds(RevertUserOBODataForUser.BATCH_SIZE * 20)).thenReturn(getListOfIds(RevertUserOBODataForUser.BATCH_SIZE));
        Mockito.doNothing().when(profileKeywordDao).revertUserOBODetails(Mockito.anyList());
        
        Mockito.when(profileFundingDao.getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20))).thenReturn(getListOfIds(0));
        Mockito.doNothing().when(profileFundingDao).revertUserOBODetails(Mockito.anyList());
        
        Mockito.when(otherNameDao.getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20))).thenReturn(getListOfIds(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.doNothing().when(otherNameDao).revertUserOBODetails(Mockito.anyList());
        
        Mockito.when(emailDao.getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20))).thenReturn(getListOfStringIds(RevertUserOBODataForUser.BATCH_SIZE * 20)).thenReturn(getListOfStringIds(RevertUserOBODataForUser.BATCH_SIZE * 2));
        Mockito.doNothing().when(emailDao).revertUserOBODetails(Mockito.anyList());
        
        Mockito.when(addressDao.getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20))).thenReturn(getListOfIds(RevertUserOBODataForUser.BATCH_SIZE));
        Mockito.doNothing().when(addressDao).revertUserOBODetails(Mockito.anyList());
        
        Mockito.when(orgAffiliationRelationDao.getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20))).thenReturn(getListOfIds(1));
        Mockito.doNothing().when(orgAffiliationRelationDao).revertUserOBODetails(Mockito.anyList());
        
        ReflectionTestUtils.setField(dataReverter, "clientDetailsId", clientId);
        dataReverter.execute();
        
        Mockito.verify(researcherUrlDao, Mockito.times(2)).getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.verify(researcherUrlDao, Mockito.times(3)).revertUserOBODetails(Mockito.anyList());
        
        Mockito.verify(workDao, Mockito.times(2)).getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.verify(workDao, Mockito.times(10)).revertUserOBODetails(Mockito.anyList());
        
        Mockito.verify(profileKeywordDao, Mockito.times(4)).getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.verify(profileKeywordDao, Mockito.times(41)).revertUserOBODetails(Mockito.anyList());
        
        Mockito.verify(profileFundingDao, Mockito.times(1)).getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.verify(profileFundingDao, Mockito.times(0)).revertUserOBODetails(Mockito.anyList());
        
        Mockito.verify(otherNameDao, Mockito.times(2)).getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.verify(otherNameDao, Mockito.times(20)).revertUserOBODetails(Mockito.anyList());
        
        Mockito.verify(emailDao, Mockito.times(3)).getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.verify(emailDao, Mockito.times(22)).revertUserOBODetails(Mockito.anyList());
        
        Mockito.verify(addressDao, Mockito.times(2)).getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.verify(addressDao, Mockito.times(1)).revertUserOBODetails(Mockito.anyList());
        
        Mockito.verify(orgAffiliationRelationDao, Mockito.times(2)).getIdsForUserOBORecords(Mockito.eq(clientId), Mockito.eq(RevertUserOBODataForUser.BATCH_SIZE * 20));
        Mockito.verify(orgAffiliationRelationDao, Mockito.times(1)).revertUserOBODetails(Mockito.anyList());
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
