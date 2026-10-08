package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.NoResultException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.web.controllers.PasswordResetStatusController.PasswordResetStatusRequest;
import org.togglz.junit.TogglzRule;

public class PasswordResetStatusControllerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    private static final String EMAIL = "user@orcid.org";

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Mock
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @InjectMocks
    private PasswordResetStatusController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isInertWhileTheFeatureIsOff() {
        when(profileEntityManagerReadOnly.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertFalse(status(ORCID));
        verify(profileEntityManagerReadOnly, never()).isPasswordResetRequired(anyString());
        verify(emailManagerReadOnly, never()).findOrcidIdByEmail(anyString());
    }

    @Test
    public void reportsAFlaggedOrcidId() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        when(profileEntityManagerReadOnly.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertTrue(status(ORCID));
        assertTrue(status("  " + ORCID + " "));
        verify(emailManagerReadOnly, never()).findOrcidIdByEmail(anyString());
    }

    @Test
    public void reportsAFlaggedEmailAddress() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        when(emailManagerReadOnly.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityManagerReadOnly.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertTrue(status(EMAIL));
    }

    @Test
    public void anUnflaggedRecordIsNotReported() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        when(emailManagerReadOnly.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(profileEntityManagerReadOnly.isPasswordResetRequired(ORCID)).thenReturn(false);

        assertFalse(status(EMAIL));
        assertFalse(status(ORCID));
    }

    @Test
    public void anUnknownEmailAddressAnswersLikeAnUnflaggedRecord() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        when(emailManagerReadOnly.findOrcidIdByEmail(EMAIL)).thenThrow(new NoResultException());

        assertFalse(status(EMAIL));
        verify(profileEntityManagerReadOnly, never()).isPasswordResetRequired(anyString());
    }

    @Test
    public void somethingThatIsNeitherAnIdNorAnAddressIsNotLookedUp() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);

        assertFalse(status("not-an-identifier"));
        assertFalse(status("0000-0000-0000"));
        assertFalse(status(""));
        assertFalse(status(null));
        assertFalse(status(repeat('a', 400) + "@orcid.org"));
        assertFalse(controller.getPasswordResetStatus(null).isPasswordResetRequired());
        verify(emailManagerReadOnly, never()).findOrcidIdByEmail(anyString());
        verify(profileEntityManagerReadOnly, never()).isPasswordResetRequired(anyString());
    }

    private boolean status(String username) {
        PasswordResetStatusRequest request = new PasswordResetStatusRequest();
        request.setUsername(username);
        return controller.getPasswordResetStatus(request).isPasswordResetRequired();
    }

    private static String repeat(char c, int times) {
        return String.valueOf(c).repeat(times);
    }
}
