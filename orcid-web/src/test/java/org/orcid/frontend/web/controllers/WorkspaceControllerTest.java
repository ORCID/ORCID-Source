package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class WorkspaceControllerTest {

    private String tenCharsStr = "0123456789";
    
    @Resource
    WorkspaceController workspaceController;
    
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
