package org.orcid.core.manager.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.persistence.dao.MemberChosenOrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;

public class MemberChosenOrgDisambiguatedManagerImplTest {
    
    @Mock
    private MemberChosenOrgDisambiguatedDao mockMemberChosenOrgDisambiguatedDao;
    
    @Mock
    private SalesForceManager mockSalesForceManager;
    
    @Mock
    private OrgDisambiguatedDao mockOrgDisambiguatedDao;

    @InjectMocks
    private MemberChosenOrgDisambiguatedManagerImpl memberChosenOrgDisambiguatedManagerImpl;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockMemberChosenOrgDisambiguatedDao.getAll()).thenReturn(getExistingChosenMemberChosenOrgDisambiguatedEntityList());
        Mockito.when(mockSalesForceManager.retrieveAllOrgIds()).thenReturn(getUpdatedChosenOrgIdList());
        
        Mockito.when(mockMemberChosenOrgDisambiguatedDao.merge(Mockito.any(MemberChosenOrgDisambiguatedEntity.class))).thenReturn(null);
        Mockito.when(mockOrgDisambiguatedDao.merge(Mockito.any(OrgDisambiguatedEntity.class))).thenReturn(null);   
        Mockito.when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.eq("first"), Mockito.eq("first"))).thenReturn(getOrgDisambiguatedEntity(1L));
        Mockito.when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.eq("second"), Mockito.eq("second"))).thenReturn(getOrgDisambiguatedEntity(2L));
        Mockito.when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.eq("third"), Mockito.eq("third"))).thenReturn(getOrgDisambiguatedEntity(3L));
        Mockito.when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.eq("fourth"), Mockito.eq("fourth"))).thenReturn(getOrgDisambiguatedEntity(4L));
        Mockito.when(mockOrgDisambiguatedDao.find(Mockito.eq(3L))).thenReturn(getOrgDisambiguatedEntity(3L));
    }

    @Test
    public void testRefreshMemberChosenOrgDisambiguatedEntities() {
        memberChosenOrgDisambiguatedManagerImpl.refreshMemberChosenOrgs();
        
        Mockito.verify(mockMemberChosenOrgDisambiguatedDao, Mockito.times(1)).remove(Mockito.any(MemberChosenOrgDisambiguatedEntity.class));
        Mockito.verify(mockMemberChosenOrgDisambiguatedDao, Mockito.times(1)).merge(Mockito.any(MemberChosenOrgDisambiguatedEntity.class));
        Mockito.verify(mockOrgDisambiguatedDao, Mockito.times(2)).merge(Mockito.any(OrgDisambiguatedEntity.class));
    }

    private List<MemberChosenOrgDisambiguatedEntity> getExistingChosenMemberChosenOrgDisambiguatedEntityList() {
        MemberChosenOrgDisambiguatedEntity first = getMemberChosenOrgDisambiguatedEntity(1L);
        MemberChosenOrgDisambiguatedEntity second = getMemberChosenOrgDisambiguatedEntity(2L);
        MemberChosenOrgDisambiguatedEntity third = getMemberChosenOrgDisambiguatedEntity(3L);
        return Arrays.asList(first, second, third);
    }
    
    private MemberChosenOrgDisambiguatedEntity getMemberChosenOrgDisambiguatedEntity(Long id) {
        MemberChosenOrgDisambiguatedEntity chosen = new MemberChosenOrgDisambiguatedEntity();
        chosen.setOrgDisambiguatedId(id);
        return chosen;
    }
    
    private OrgDisambiguatedEntity getOrgDisambiguatedEntity(Long id) {
        OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
        entity.setId(id);
        return entity;
    }

    private List<OrgId> getUpdatedChosenOrgIdList() {
        OrgId first = getOrgId("first");
        OrgId second = getOrgId("second");
        OrgId fourth = getOrgId("fourth");
        return Arrays.asList(first, second, fourth);
    }

    private OrgId getOrgId(String s) {
        OrgId o = new OrgId();
        o.setOrgIdType(s);
        o.setOrgIdValue(s);
        return o;
    }
    
    

}
