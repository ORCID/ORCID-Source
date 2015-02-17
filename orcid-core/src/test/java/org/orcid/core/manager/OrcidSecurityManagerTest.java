package org.orcid.core.manager;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidForbiddenException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Source;
import org.orcid.jaxb.model.record.SourceClientId;
import org.orcid.jaxb.model.record.Visibility;
import org.orcid.jaxb.model.record.Work;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManagerTest extends BaseTest {

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadPublicScopeAndActivityIsPublicAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        Work work = createWork();
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadPublicScopeAndActivityIsLimitedAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        Work work = createWork();
        work.setVisibility(Visibility.LIMITED);
        boolean caughtException = false;
        try {
            orcidSecurityManager.checkVisibility(work);
        } catch (OrcidUnauthorizedException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadPublicScopeAndActivityIsPrivateAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        Work work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        boolean caughtException = false;
        try {
            orcidSecurityManager.checkVisibility(work);
        } catch (OrcidUnauthorizedException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadPublicScopeAndActivityIsPrivateAndIsNotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        Work work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        work.getSource().setSourceClientId(new SourceClientId("APP-1111111111111111"));
        boolean caughtException = false;
        try {
            orcidSecurityManager.checkVisibility(work);
        } catch (OrcidForbiddenException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadLimitedScopeAndActivityIsPublicAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.ACTIVITIES_READ_LIMITED);
        Work work = createWork();
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadLimitedScopeAndActivityIsLimitedAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.ACTIVITIES_READ_LIMITED);
        Work work = createWork();
        work.setVisibility(Visibility.LIMITED);
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadLimitedScopeAndActivityIsPrivateAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.ACTIVITIES_READ_LIMITED);
        Work work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadLimitedScopeAndActivityIsPrivateAndIsNotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.ACTIVITIES_READ_LIMITED);
        Work work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        work.getSource().setSourceClientId(new SourceClientId("APP-1111111111111111"));
        boolean caughtException = false;
        try {
            orcidSecurityManager.checkVisibility(work);
        } catch (OrcidForbiddenException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    private Work createWork() {
        Work work = new Work();
        work.setVisibility(Visibility.PUBLIC);
        Source source = new Source();
        work.setSource(source);
        source.setSourceClientId(new SourceClientId("APP-5555555555555555"));
        return work;
    }

}
