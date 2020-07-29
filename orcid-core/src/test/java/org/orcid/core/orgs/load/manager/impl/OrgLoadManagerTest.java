package org.orcid.core.orgs.load.manager.impl;

import static org.junit.Assert.assertFalse;
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
import org.orcid.core.manager.SlackManager;
import org.orcid.core.orgs.load.source.impl.RinggoldOrgLoadSource;
import org.orcid.persistence.dao.OrgImportLogDao;
import org.orcid.persistence.jpa.entities.OrgImportLogEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class OrgLoadManagerTest {

    @Mock
    private OrgImportLogDao importLogDao;

    @Mock
    private RinggoldOrgLoadSource ringgoldOrgLoadSource;

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
        ReflectionTestUtils.setField(orgLoadManager, "orgLoadSources", Arrays.asList(ringgoldOrgLoadSource));
        ReflectionTestUtils.setField(orgLoadManager, "slackChannel", "#slack-channel");
        ReflectionTestUtils.setField(orgLoadManager, "slackUser", "slack-user");
    }

    @Test
    public void testLoadOrgs() {
        Mockito.when(importLogDao.getNextImportSourceName()).thenReturn("RINGGOLD");
        Mockito.when(ringgoldOrgLoadSource.loadLatestOrgs()).thenReturn(true);
        Mockito.doNothing().when(importLogDao).persist(Mockito.any(OrgImportLogEntity.class));

        orgLoadManager.loadOrgs();

        Mockito.verify(ringgoldOrgLoadSource, Mockito.times(1)).loadLatestOrgs();
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
        Mockito.when(importLogDao.getNextImportSourceName()).thenReturn("RINGGOLD");
        Mockito.when(ringgoldOrgLoadSource.loadLatestOrgs()).thenReturn(false);
        Mockito.doNothing().when(importLogDao).persist(Mockito.any(OrgImportLogEntity.class));

        orgLoadManager.loadOrgs();

        Mockito.verify(ringgoldOrgLoadSource, Mockito.times(1)).loadLatestOrgs();
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

}
