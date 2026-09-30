package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;
import org.togglz.junit.TogglzRule;

/**
 * Unit tests for HomeController config.json / togglz feature value logic.
 * getFeatureValue returns either "true", "false", or a percentage 1-99 as string.
 */
public class HomeControllerTest {

    private FeatureManager featureManager;
    private HomeControllerTestable controller;

    /** Use a feature that exists in the enum (EVENTS) for testing the logic. */
    private static final Features TEST_FEATURE = Features.EVENTS;

    @Before
    public void setUp() {
        featureManager = mock(FeatureManager.class);
        controller = new HomeControllerTestable();
    }

    @Test
    public void getFeatureValue_returnsFalseWhenStateNull() {
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(null);
        assertEquals("false", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Test
    public void getFeatureValue_returnsFalseWhenDisabled() {
        FeatureState state = mock(FeatureState.class);
        when(state.isEnabled()).thenReturn(false);
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(state);
        assertEquals("false", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Test
    public void getFeatureValue_returnsTrueWhenEnabledNoPercentage() {
        FeatureState state = mock(FeatureState.class);
        when(state.isEnabled()).thenReturn(true);
        when(state.getParameter("percentage")).thenReturn(null);
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(state);
        assertEquals("true", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Test
    public void getFeatureValue_returnsTrueWhenEnabledEmptyPercentage() {
        FeatureState state = mock(FeatureState.class);
        when(state.isEnabled()).thenReturn(true);
        when(state.getParameter("percentage")).thenReturn("");
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(state);
        assertEquals("true", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Test
    public void getFeatureValue_returnsTrueWhenEnabledPercentage100() {
        FeatureState state = mock(FeatureState.class);
        when(state.isEnabled()).thenReturn(true);
        when(state.getParameter("percentage")).thenReturn("100");
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(state);
        assertEquals("true", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Test
    public void getFeatureValue_returnsFalseWhenEnabledPercentage0() {
        FeatureState state = mock(FeatureState.class);
        when(state.isEnabled()).thenReturn(true);
        when(state.getParameter("percentage")).thenReturn("0");
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(state);
        assertEquals("false", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Test
    public void getFeatureValue_returnsNumberWhenEnabledPercentage1To99() {
        FeatureState state = mock(FeatureState.class);
        when(state.isEnabled()).thenReturn(true);
        when(state.getParameter("percentage")).thenReturn("25");
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(state);
        assertEquals("25", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Test
    public void getFeatureValue_clampsPercentageOver100ToTrue() {
        FeatureState state = mock(FeatureState.class);
        when(state.isEnabled()).thenReturn(true);
        when(state.getParameter("percentage")).thenReturn("150");
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(state);
        assertEquals("true", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Test
    public void getFeatureValue_invalidPercentageFallsBackToTrue() {
        FeatureState state = mock(FeatureState.class);
        when(state.isEnabled()).thenReturn(true);
        when(state.getParameter("percentage")).thenReturn("not-a-number");
        when(featureManager.getFeatureState(TEST_FEATURE)).thenReturn(state);
        assertEquals("true", controller.getFeatureValue(featureManager, TEST_FEATURE));
    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    private static final String ORCID = "0000-0000-0000-0001";

    private static final String DELEGATE_ORCID = "0000-0000-0000-0002";

    private final ProfileEntityManagerReadOnly profileEntityManagerReadOnly = mock(ProfileEntityManagerReadOnly.class);

    @Test
    public void getUserInfo_reportsAMandatoryPasswordResetToTheRecordsOwnUser() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        when(profileEntityManagerReadOnly.isPasswordResetRequired(ORCID)).thenReturn(true);

        Map<String, String> info = signedIn(ORCID).getUserInfo(new MockHttpServletRequest());

        assertEquals("true", info.get("FORCE_PASSWORD_RESET"));
    }

    @Test
    public void getUserInfo_reportsFalseForARecordThatIsNotFlagged() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);

        Map<String, String> info = signedIn(ORCID).getUserInfo(new MockHttpServletRequest());

        assertEquals("false", info.get("FORCE_PASSWORD_RESET"));
    }

    @Test
    public void getUserInfo_saysNothingWhileTheFeatureIsOff() {
        when(profileEntityManagerReadOnly.isPasswordResetRequired(ORCID)).thenReturn(true);

        Map<String, String> info = signedIn(ORCID).getUserInfo(new MockHttpServletRequest());

        assertFalse(info.containsKey("FORCE_PASSWORD_RESET"));
        verify(profileEntityManagerReadOnly, never()).isPasswordResetRequired(anyString());
    }

    @Test
    public void getUserInfo_saysNothingToADelegate() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        when(profileEntityManagerReadOnly.isPasswordResetRequired(ORCID)).thenReturn(true);

        Map<String, String> info = signedIn(DELEGATE_ORCID).getUserInfo(new MockHttpServletRequest());

        assertEquals("true", info.get("IN_DELEGATION_MODE"));
        assertFalse(info.containsKey("FORCE_PASSWORD_RESET"));
        verify(profileEntityManagerReadOnly, never()).isPasswordResetRequired(anyString());
    }

    /** A controller signed in as ORCID's record, by its own user or by a delegate. */
    private HomeController signedIn(String realUserOrcid) {
        HomeController controller = spy(new HomeController());
        doReturn(new User(ORCID, "password", List.of(new SimpleGrantedAuthority("ROLE_USER")))).when(controller).getCurrentUser();
        doReturn(realUserOrcid).when(controller).getRealUserOrcid();

        ProfileEntity profile = new ProfileEntity(ORCID);
        profile.setClaimed(true);
        ProfileEntityCacheManager profileEntityCacheManager = mock(ProfileEntityCacheManager.class);
        when(profileEntityCacheManager.retrieve(ORCID)).thenReturn(profile);

        ReflectionTestUtils.setField(controller, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(controller, "emailManagerReadOnly", mock(EmailManagerReadOnly.class));
        ReflectionTestUtils.setField(controller, "sourceManager", mock(SourceManager.class));
        ReflectionTestUtils.setField(controller, "profileEntityManagerReadOnly", profileEntityManagerReadOnly);
        return controller;
    }

    /**
     * Subclass to expose protected getFeatureValue for testing.
     */
    private static class HomeControllerTestable extends HomeController {
        @Override
        protected String getFeatureValue(org.togglz.core.manager.FeatureManager featureManager, Features feature) {
            return super.getFeatureValue(featureManager, feature);
        }
    }
}
