package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.locale.LocaleManager;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.Silent.class)
public class WorkspaceControllerTest {

    private String tenCharsStr = "0123456789";

    @Mock
    private LocaleManager localeManager;

    @InjectMocks
    private WorkspaceController workspaceController = new WorkspaceController();

    @Before
    public void before() {
        // WorkspaceController re-declares `localeManager` (line 101) over the
        // copy BaseController declares (line 117). Spring's @Resource fills
        // both; a single-field injection fills only the most derived one and
        // BaseController.getMessage() then throws NullPointerException. Set the
        // field once per declaring class.
        ReflectionTestUtils.setField(workspaceController, WorkspaceController.class, "localeManager", localeManager, LocaleManager.class);
        ReflectionTestUtils.setField(workspaceController, BaseController.class, "localeManager", localeManager, LocaleManager.class);
        when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void validateSmallerThanTest() {
        Text text = new Text();
        workspaceController.validateNoLongerThan(10, text);
        assertTrue(text.getErrors().isEmpty());
        text.setValue(tenCharsStr);
        workspaceController.validateNoLongerThan(10, text);
        assertTrue(text.getErrors().isEmpty());
        text.setValue(tenCharsStr + '!');
        workspaceController.validateNoLongerThan(10, text);
        assertEquals(workspaceController.getMessage("manualWork.length_less_X", 10), text.getErrors().get(0));
        text.setValue(tenCharsStr);
        text.setErrors(new ArrayList<String>());
        workspaceController.validateNoLongerThan(10, text);
        assertTrue(text.getErrors().isEmpty());
    }
}
