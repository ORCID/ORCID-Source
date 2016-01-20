/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common.CreditName;
import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.common.SourceClientId;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Work;

import com.sun.tools.javac.util.List;

import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.FamilyName;
import org.orcid.jaxb.model.record_rc2.GivenNames;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.OtherName;

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
        try {
            orcidSecurityManager.checkVisibility(work);
            fail();
        } catch (OrcidUnauthorizedException e) {

        }
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadPublicScopeAndActivityIsPrivateAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        Work work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        try {
            orcidSecurityManager.checkVisibility(work);
            fail();
        } catch (OrcidUnauthorizedException e) {

        }
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
        } catch (OrcidVisibilityException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadLimitedScopeAndActivityIsPublicAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_LIMITED);
        Work work = createWork();
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions

        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.ACTIVITIES_READ_LIMITED);
        work = createWork();
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadLimitedScopeAndActivityIsLimitedAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_LIMITED);
        Work work = createWork();
        work.setVisibility(Visibility.LIMITED);
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions

        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.ACTIVITIES_READ_LIMITED);
        work = createWork();
        work.setVisibility(Visibility.LIMITED);
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadLimitedScopeAndActivityIsPrivateAndIsSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_LIMITED);
        Work work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions

        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.ACTIVITIES_READ_LIMITED);
        work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        orcidSecurityManager.checkVisibility(work);
        // There should be no exceptions
    }

    @Test
    public void testCheckVisibilityOfActivityWhenUsingReadLimitedScopeAndActivityIsPrivateAndIsNotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_LIMITED);
        Work work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        work.getSource().setSourceClientId(new SourceClientId("APP-1111111111111111"));
        boolean caughtException = false;
        try {
            orcidSecurityManager.checkVisibility(work);
        } catch (OrcidVisibilityException e) {
            caughtException = true;
        }
        assertTrue(caughtException);

        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.ACTIVITIES_READ_LIMITED);
        work = createWork();
        work.setVisibility(Visibility.PRIVATE);
        work.getSource().setSourceClientId(new SourceClientId("APP-1111111111111111"));
        caughtException = false;
        try {
            orcidSecurityManager.checkVisibility(work);
        } catch (OrcidVisibilityException e) {
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

    @Test
    public void testCheckVisibilityOfNameUsingReadLimited() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        Name name = createName();
        // Check public with any scope
        orcidSecurityManager.checkVisibility(name);
        name.setVisibility(Visibility.LIMITED);
        try {
            // Check limited with any scope
            orcidSecurityManager.checkVisibility(name);
            fail();
        } catch (OrcidUnauthorizedException ua) {

        } catch (Exception e) {
            fail();
        }

        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_LIMITED);
        // Check limited with read_limited scope
        orcidSecurityManager.checkVisibility(name);

        name.setVisibility(Visibility.PRIVATE);
        try {
            // Check private always fail
            orcidSecurityManager.checkVisibility(name);
        } catch (OrcidVisibilityException ua) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCheckVisibilityOfBiography() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        Biography bio = createBiography();
        // Check public with any scope
        orcidSecurityManager.checkVisibility(bio);
        bio.setVisibility(Visibility.LIMITED);
        try {
            // Check limited with any scope
            orcidSecurityManager.checkVisibility(bio);
            fail();
        } catch (OrcidUnauthorizedException ua) {

        } catch (Exception e) {
            fail();
        }

        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_LIMITED);
        // Check limited with read_limited scope
        orcidSecurityManager.checkVisibility(bio);

        bio.setVisibility(Visibility.PRIVATE);
        try {
            // Check private always fail
            orcidSecurityManager.checkVisibility(bio);
        } catch (OrcidVisibilityException ua) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testOtherName() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_PUBLIC);
        OtherName otherName = createOtherName();

        // Check public with any scope
        orcidSecurityManager.checkVisibility(otherName);
        otherName.setVisibility(Visibility.LIMITED);
        try {
            // Check limited with any scope
            orcidSecurityManager.checkVisibility(otherName);
            fail();
        } catch (OrcidUnauthorizedException ua) {

        } catch (Exception e) {
            fail();
        }

        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.READ_LIMITED);
        // Check limited with read_limited scope
        orcidSecurityManager.checkVisibility(otherName);

        otherName.setVisibility(Visibility.PRIVATE);
        try {
            // Check private always fail
            orcidSecurityManager.checkVisibility(otherName);
        } catch (OrcidVisibilityException ua) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCheckPermissionsOnEveryScope() {
        String userOrcid = "4444-4444-4444-4441";
        for (ScopePathType scopeToTest : ScopePathType.values()) {
            SecurityContextTestUtils.setUpSecurityContext(userOrcid, scopeToTest);
            checkScopes(userOrcid, scopeToTest);
        }
    }

    public void checkScopes(String userOrcid, ScopePathType scopeThatShouldWork) {
        if (ScopePathType.READ_PUBLIC.equals(scopeThatShouldWork)) {
            System.out.println("Debug here");
        }
        for (ScopePathType scope : ScopePathType.values()) {
            if (scopeThatShouldWork.combined().contains(scope)) {
                try {
                    orcidSecurityManager.checkPermissions(scope, userOrcid);
                } catch (Exception e) {
                    fail("Testing scope '" + scopeThatShouldWork.value() + "' scope '" + scope.value() + "' should work");
                }
            } else if (scope.isReadOnlyScope()) {
                // TODO: check if we still want this behavior
                // Check at the DefaultPermissionsChecker.hasRequiredScope, if
                // the client have the READ_PUBLIC scope and the
                // request requires a read only scope, the
                // DefaultPermissionsChecker will allow the request and
                // delegate filtering the result to others code
                if (ScopePathType.hasStringScope(scopeThatShouldWork.getContent(), ScopePathType.READ_PUBLIC)) {
                    try {
                        orcidSecurityManager.checkPermissions(scope, userOrcid);
                    } catch (Exception e) {
                        fail("Testing scope '" + scopeThatShouldWork.value() + "' scope '" + scope.value() + "' should work");
                    }
                }
            } else {
                try {
                    orcidSecurityManager.checkPermissions(scope, userOrcid);
                    fail("Testing scope '" + scopeThatShouldWork.value() + "' scope '" + scope.value() + "' should fail");
                } catch (AccessControlException ace) {

                } catch (Exception e) {
                    fail("Testing scope '" + scopeThatShouldWork.value() + "' Invalid exception thrown for scope '" + scope.value());
                }
            }
        }
    }

    private Name createName() {
        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setVisibility(Visibility.PUBLIC);
        return name;
    }

    private Biography createBiography() {
        return new Biography("Biography", Visibility.PUBLIC);
    }

    private OtherName createOtherName() {
        OtherName otherName = new OtherName();
        otherName.setContent("other-name");
        otherName.setVisibility(Visibility.PUBLIC);
        return otherName;
    }

}
