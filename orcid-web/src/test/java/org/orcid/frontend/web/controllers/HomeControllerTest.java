package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.orcid.core.togglz.Features;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

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
