package org.orcid.core.context;

import org.springframework.test.annotation.DirtiesContext.HierarchyMode;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

public class ClearContextTestExecutionListener extends AbstractTestExecutionListener {
  
    /**
     * Returns {@code 1500}.
     */
    @Override
    public final int getOrder() {
            return 1500;
    }
    
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        dirtyContext(testContext, HierarchyMode.EXHAUSTIVE);
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        dirtyContext(testContext, HierarchyMode.EXHAUSTIVE);
    }
    
    protected void dirtyContext(TestContext testContext, HierarchyMode hierarchyMode) {
        testContext.markApplicationContextDirty(hierarchyMode);
        testContext.setAttribute(DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE, Boolean.TRUE);
    }
}
