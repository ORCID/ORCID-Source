package org.orcid.core.manager.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
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
        
        Mockito.when(mockMemberChosenOrgDisambiguatedDao.merge(Mockito.any(MemberChosenOrgDisambiguatedEntity.class))).thenAnswer((Answer<MemberChosenOrgDisambiguatedEntity>) invocation -> {
            MemberChosenOrgDisambiguatedEntity memberChosenOrgDisambiguatedEntity = (MemberChosenOrgDisambiguatedEntity) invocation.getArgument(0);
            OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
            orgDisambiguatedEntity.setSourceType(memberChosenOrgDisambiguatedEntity.getSourceType());
            orgDisambiguatedEntity.setSourceId(memberChosenOrgDisambiguatedEntity.getSourceId());
            memberChosenOrgDisambiguatedEntity.setOrgDisambiguatedEntity(orgDisambiguatedEntity);
            return memberChosenOrgDisambiguatedEntity;
        });
        Mockito.when(mockOrgDisambiguatedDao.merge(Mockito.any(OrgDisambiguatedEntity.class))).thenReturn(null);        
    }

    @Test
    public void testRefreshMemberChosenOrgDisambiguatedEntities() {
        memberChosenOrgDisambiguatedManagerImpl.refreshMemberChosenOrgs();
        
        Mockito.verify(mockMemberChosenOrgDisambiguatedDao, Mockito.times(1)).remove(Mockito.any(MemberChosenOrgDisambiguatedEntity.class));
        Mockito.verify(mockMemberChosenOrgDisambiguatedDao, Mockito.times(1)).merge(Mockito.any(MemberChosenOrgDisambiguatedEntity.class));
        Mockito.verify(mockOrgDisambiguatedDao, Mockito.times(2)).merge(Mockito.any(OrgDisambiguatedEntity.class));
    }

    private List<MemberChosenOrgDisambiguatedEntity> getExistingChosenMemberChosenOrgDisambiguatedEntityList() {
        MemberChosenOrgDisambiguatedEntity first = getMemberChosenOrgDisambiguatedEntity("first");
        MemberChosenOrgDisambiguatedEntity second = getMemberChosenOrgDisambiguatedEntity("second");
        MemberChosenOrgDisambiguatedEntity third = getMemberChosenOrgDisambiguatedEntity("third");
        return Arrays.asList(first, second, third);
    }

    private MemberChosenOrgDisambiguatedEntity getMemberChosenOrgDisambiguatedEntity(String s) {
        MemberChosenOrgDisambiguatedEntity e = new MemberChosenOrgDisambiguatedEntity();
        e.setSourceType(s);
        e.setSourceId(s);
        
        OrgDisambiguatedEntity o = new OrgDisambiguatedEntity();
        o.setSourceId(s);
        o.setSourceType(s);
        e.setOrgDisambiguatedEntity(o);
        return e;
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
