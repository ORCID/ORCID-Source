package org.orcid.core.orgs.load.manager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.After;
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
    private RinggoldOrgLoadSource ringgoldOrgLoader;
    
    @InjectMocks
    private OrgLoadManagerImpl orgLoadManager;
    
    @Captor
    private ArgumentCaptor<OrgImportLogEntity> captor;
    
    private File loadBaseDir;
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        
        loadBaseDir = File.createTempFile("orgs-imports", "-test");
        loadBaseDir.delete();
        
        Mockito.when(ringgoldOrgLoader.getSourceName()).thenReturn("RINGGOLD");
        
        ReflectionTestUtils.setField(orgLoadManager, "loadBaseDir", loadBaseDir.getAbsolutePath());
        ReflectionTestUtils.setField(orgLoadManager, "orgLoaders", Arrays.asList(ringgoldOrgLoader));
    }
    
    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(loadBaseDir);
    }
    
    @Test
    public void testLoadOrgs() {
        Mockito.when(importLogDao.getNextImportSourceName()).thenReturn("RINGGOLD");
        Mockito.when(ringgoldOrgLoader.loadLatestOrgs(Mockito.any(File.class))).thenReturn(Boolean.TRUE);
        Mockito.doNothing().when(importLogDao).persist(Mockito.any(OrgImportLogEntity.class));
        
        orgLoadManager.loadOrgs();
        
        Mockito.verify(ringgoldOrgLoader, Mockito.times(1)).loadLatestOrgs(Mockito.any(File.class));
        Mockito.verify(importLogDao, Mockito.times(1)).persist(captor.capture());
        OrgImportLogEntity importLog = captor.getValue();
        assertNotNull(importLog);
        assertNotNull(importLog.getStart());
        assertNotNull(importLog.getEnd());
        assertTrue(importLog.isSuccessful());
        
    }

}
