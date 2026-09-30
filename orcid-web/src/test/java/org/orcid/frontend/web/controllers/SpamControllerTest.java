package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.v3.SpamManager;
import org.orcid.jaxb.model.v3.release.record.SourceType;

/**
 * The controller is an isValidOrcid guard plus delegation. The 1 -> 2
 * increment-versus-insert branch the old testCreateSpam/testUpdateSpam relied on
 * lives in SpamManagerImpl.createOrUpdateSpam against /data/SpamEntityData.xml,
 * and belongs to a SpamManagerImpl test; here the counts are stub values and
 * what is asserted is that the controller passes the orcid through and maps the
 * model onto the pojo.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SpamControllerTest {

    private static String USER_ORCID = "4444-4444-4444-4497";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4499";
    private static String INVALID_ORCID = "not-an-orcid";

    @Mock(name = "spamManager")
    private SpamManager spamManager;

    @InjectMocks
    private SpamController spamController = new SpamController();

    @Test
    public void testReadOne() {
        when(spamManager.getSpam("0000-0000-0000-0004")).thenReturn(spam(1));

        org.orcid.pojo.Spam spam = spamController.getSpam("0000-0000-0000-0004");

        assertNotNull(spam);
        assertEquals(Integer.valueOf(1), spam.getCount());
        assertEquals(SourceType.USER, SourceType.fromValue(spam.getSourceType()));
    }

    @Test
    public void testCreateSpam() {
        when(spamManager.createOrUpdateSpam(USER_ORCID)).thenReturn(true);
        when(spamManager.getSpam(USER_ORCID)).thenReturn(spam(1));

        assertTrue(spamController.reportSpam(USER_ORCID));
        verify(spamManager).createOrUpdateSpam(USER_ORCID);

        org.orcid.pojo.Spam spam = spamController.getSpam(USER_ORCID);
        assertNotNull(spam);
        assertEquals(Integer.valueOf(1), spam.getCount());
        assertEquals(SourceType.USER, SourceType.fromValue(spam.getSourceType()));
    }

    @Test
    public void testUpdateSpam() {
        when(spamManager.createOrUpdateSpam(OTHER_USER_ORCID)).thenReturn(true);
        when(spamManager.getSpam(OTHER_USER_ORCID)).thenReturn(spam(2));

        assertTrue(spamController.reportSpam(OTHER_USER_ORCID));
        verify(spamManager).createOrUpdateSpam(OTHER_USER_ORCID);

        org.orcid.pojo.Spam spam = spamController.getSpam(OTHER_USER_ORCID);
        assertNotNull(spam);
        assertEquals(Integer.valueOf(2), spam.getCount());
        assertEquals(SourceType.USER, SourceType.fromValue(spam.getSourceType()));
    }

    @Test
    public void testDeleteSpam() {
        when(spamManager.removeSpam(OTHER_USER_ORCID)).thenReturn(true);
        when(spamManager.getSpam(OTHER_USER_ORCID)).thenReturn(null);

        assertTrue(spamController.removeSpam(OTHER_USER_ORCID));
        verify(spamManager).removeSpam(OTHER_USER_ORCID);

        org.orcid.pojo.Spam spamDeleted = spamController.getSpam(OTHER_USER_ORCID);
        assertNull(spamDeleted);
    }

    /**
     * The only thing between the request body and SpamManager is
     * OrcidStringUtils.isValidOrcid.
     */
    @Test
    public void reportSpam_invalidOrcidTest() {
        assertFalse(spamController.reportSpam(INVALID_ORCID));
        verify(spamManager, never()).createOrUpdateSpam(anyString());
    }

    @Test
    public void removeSpam_invalidOrcidTest() {
        assertFalse(spamController.removeSpam(INVALID_ORCID));
        verify(spamManager, never()).removeSpam(anyString());
    }

    private org.orcid.jaxb.model.v3.release.record.Spam spam(int counter) {
        org.orcid.jaxb.model.v3.release.record.Spam spam = new org.orcid.jaxb.model.v3.release.record.Spam();
        spam.setSpamCounter(counter);
        spam.setSourceType(SourceType.USER);
        return spam;
    }
}
