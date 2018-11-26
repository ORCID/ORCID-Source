package org.orcid.core.manager.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.MemberChosenOrgDisambiguatedManagerImpl;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.persistence.dao.MemberChosenOrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.MemberChosenOrgDisambiguatedEntity;

public class MemberChosenOrgDisambiguatedManagerImplTest extends BaseTest {
    
    @Mock
    private MemberChosenOrgDisambiguatedDao mockMemberChosenOrgDisambiguatedDao;

    @InjectMocks
    private MemberChosenOrgDisambiguatedManagerImpl memberChosenOrgDisambiguatedManagerImpl;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.doNothing().when(mockMemberChosenOrgDisambiguatedDao).removeAll();
        Mockito.doNothing().when(mockMemberChosenOrgDisambiguatedDao).persist(Mockito.any(MemberChosenOrgDisambiguatedEntity.class));
    }

    @Test
    public void testRefreshMemberChosenOrgDisambiguatedEntities() {
        List<OrgId> chosenOrgs = getChosenOrgIdList();
        memberChosenOrgDisambiguatedManagerImpl.refreshMemberChosenOrgs(chosenOrgs);

        Mockito.verify(mockMemberChosenOrgDisambiguatedDao, Mockito.times(1)).removeAll();
        Mockito.verify(mockMemberChosenOrgDisambiguatedDao, Mockito.times(4)).persist(Mockito.any(MemberChosenOrgDisambiguatedEntity.class));
    }

    private List<OrgId> getChosenOrgIdList() {
        OrgId first = new OrgId();
        first.setOrgIdType("first");
        first.setOrgIdValue("first");
        
        OrgId second = new OrgId();
        second.setOrgIdType("second");
        second.setOrgIdValue("second");
        
        OrgId third = new OrgId();
        third.setOrgIdType("third");
        third.setOrgIdValue("third");
        
        OrgId fourth = new OrgId();
        fourth.setOrgIdType("fourth");
        fourth.setOrgIdValue("fourth");
        
        return Arrays.asList(first, second, third, fourth);
    }

}
