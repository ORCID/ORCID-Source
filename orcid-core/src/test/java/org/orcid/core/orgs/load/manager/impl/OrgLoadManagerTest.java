package org.orcid.core.orgs.load.manager.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.orgs.load.source.OrgLoadSource;
import org.orcid.persistence.dao.OrgImportLogDao;
import org.orcid.persistence.jpa.entities.OrgImportLogEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class OrgLoadManagerTest {

    @Mock
    private OrgImportLogDao importLogDao;

    @Mock
    private OrgLoadSource ringgoldOrgLoadSource;
    
    @Mock
    private OrgLoadSource disabledOrgLoadSource;

    @Mock
    private SlackManager slackManager;

    @InjectMocks
    private OrgLoadManagerImpl orgLoadManager;

    @Captor
    private ArgumentCaptor<OrgImportLogEntity> captor;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(ringgoldOrgLoadSource.getSourceName()).thenReturn("RINGGOLD");
        Mockito.when(ringgoldOrgLoadSource.isEnabled()).thenReturn(true);
        Mockito.when(disabledOrgLoadSource.getSourceName()).thenReturn("DISABLED");
        Mockito.when(disabledOrgLoadSource.isEnabled()).thenReturn(false);
        ReflectionTestUtils.setField(orgLoadManager, "orgLoadSources", Arrays.asList(ringgoldOrgLoadSource, disabledOrgLoadSource));
        ReflectionTestUtils.setField(orgLoadManager, "slackChannel", "#slack-channel");
        ReflectionTestUtils.setField(orgLoadManager, "slackUser", "slack-user");
    }

    @Test
    public void testLoadOrgs() {
        Mockito.when(importLogDao.getImportSourceOrder()).thenReturn(getListStartingWith("RINGGOLD"));
        Mockito.when(ringgoldOrgLoadSource.loadOrgData()).thenReturn(true);
        Mockito.doNothing().when(importLogDao).persist(Mockito.any(OrgImportLogEntity.class));

        orgLoadManager.loadOrgs();

        Mockito.verify(ringgoldOrgLoadSource, Mockito.times(1)).loadOrgData();
        Mockito.verify(importLogDao, Mockito.times(1)).persist(captor.capture());
        OrgImportLogEntity importLog = captor.getValue();
        assertNotNull(importLog);
        assertNotNull(importLog.getStart());
        assertNotNull(importLog.getEnd());
        assertTrue(importLog.isSuccessful());

        ArgumentCaptor<String> slackMessageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(slackManager, Mockito.times(1)).sendAlert(slackMessageCaptor.capture(), Mockito.eq("#slack-channel"), Mockito.eq("slack-user"));
        String slackMessage = slackMessageCaptor.getValue();
        assertTrue(slackMessage.contains("success"));
    }
    
    @Test
    public void testLoadOrgsNextSourceDisabled() {
        List<String> nextSources = getListStartingWith("RINGGOLD");
        nextSources.add(0, "DISABLED");
        
        Mockito.when(importLogDao.getImportSourceOrder()).thenReturn(nextSources);
        Mockito.when(ringgoldOrgLoadSource.loadOrgData()).thenReturn(true);
        Mockito.doNothing().when(importLogDao).persist(Mockito.any(OrgImportLogEntity.class));

        orgLoadManager.loadOrgs();

        // ringgold source chosen because DISABLED was not enabled
        Mockito.verify(disabledOrgLoadSource, Mockito.times(1)).isEnabled();
        Mockito.verify(disabledOrgLoadSource, Mockito.never()).loadOrgData();
        Mockito.verify(ringgoldOrgLoadSource, Mockito.times(1)).isEnabled();
        Mockito.verify(ringgoldOrgLoadSource, Mockito.times(1)).loadOrgData();
        Mockito.verify(importLogDao, Mockito.times(1)).persist(captor.capture());
        OrgImportLogEntity importLog = captor.getValue();
        assertNotNull(importLog);
        assertNotNull(importLog.getStart());
        assertNotNull(importLog.getEnd());
        assertTrue(importLog.isSuccessful());

        ArgumentCaptor<String> slackMessageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(slackManager, Mockito.times(1)).sendAlert(slackMessageCaptor.capture(), Mockito.eq("#slack-channel"), Mockito.eq("slack-user"));
        String slackMessage = slackMessageCaptor.getValue();
        assertTrue(slackMessage.contains("success"));
    }

    @Test
    public void testLoadOrgsFailure() {
        Mockito.when(importLogDao.getImportSourceOrder()).thenReturn(getListStartingWith("RINGGOLD"));
        Mockito.when(ringgoldOrgLoadSource.loadOrgData()).thenReturn(false);
        Mockito.doNothing().when(importLogDao).persist(Mockito.any(OrgImportLogEntity.class));

        orgLoadManager.loadOrgs();

        Mockito.verify(ringgoldOrgLoadSource, Mockito.times(1)).loadOrgData();
        Mockito.verify(importLogDao, Mockito.times(1)).persist(captor.capture());
        OrgImportLogEntity importLog = captor.getValue();
        assertNotNull(importLog);
        assertNotNull(importLog.getStart());
        assertNotNull(importLog.getEnd());
        assertFalse(importLog.isSuccessful());

        ArgumentCaptor<String> slackMessageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(slackManager, Mockito.times(1)).sendAlert(slackMessageCaptor.capture(), Mockito.eq("#slack-channel"), Mockito.eq("slack-user"));
        String slackMessage = slackMessageCaptor.getValue();
        assertTrue(slackMessage.contains("FAILURE"));
    }
    
    private List<String> getListStartingWith(String startElement) {
        List<String> list = new ArrayList<>();
        list.add(startElement);
        list.add("second");
        list.add("third");
        list.add("fourth");
        return list;
    }

}
