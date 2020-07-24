package org.orcid.core.orgs.load.manager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.orgs.load.source.impl.RinggoldOrgLoadSource;
import org.orcid.persistence.dao.OrgImportLogDao;
import org.orcid.persistence.jpa.entities.OrgImportLogEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class OrgLoadManagerTest {

    @Mock
    private OrgImportLogDao importLogDao;

    @Mock
    private RinggoldOrgLoadSource ringgoldOrgLoadSource;
    
    @InjectMocks
    private OrgLoadManagerImpl orgLoadManager;
    
    @Captor
    private ArgumentCaptor<OrgImportLogEntity> captor;
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(ringgoldOrgLoadSource.getSourceName()).thenReturn("RINGGOLD");
        ReflectionTestUtils.setField(orgLoadManager, "orgLoadSources", Arrays.asList(ringgoldOrgLoadSource));
    }
    
    @Test
    public void testLoadOrgs() {
        Mockito.when(importLogDao.getNextImportSourceName()).thenReturn("RINGGOLD");
        Mockito.when(ringgoldOrgLoadSource.loadLatestOrgs()).thenReturn(Boolean.TRUE);
        Mockito.doNothing().when(importLogDao).persist(Mockito.any(OrgImportLogEntity.class));
        
        orgLoadManager.loadOrgs();
        
        Mockito.verify(ringgoldOrgLoadSource, Mockito.times(1)).loadLatestOrgs();
        Mockito.verify(importLogDao, Mockito.times(1)).persist(captor.capture());
        OrgImportLogEntity importLog = captor.getValue();
        assertNotNull(importLog);
        assertNotNull(importLog.getStart());
        assertNotNull(importLog.getEnd());
        assertTrue(importLog.isSuccessful());
    }

}
