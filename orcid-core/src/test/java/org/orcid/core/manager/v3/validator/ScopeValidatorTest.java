package org.orcid.core.manager.v3.validator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.jaxb.model.message.ScopePathType;

public class ScopeValidatorTest {

    private OrcidBearerTokenAuthentication authWithScopes(String... scopes) {
        OrcidBearerTokenAuthentication authentication = mock(OrcidBearerTokenAuthentication.class);
        when(authentication.getScopes()).thenReturn(Set.of(scopes));
        return authentication;
    }

    @Test
    public void eachScopeWorksSeparatelyTest() {
        for (ScopePathType scope : ScopePathType.values()) {
            ScopeValidator.validateRequiredScopes(authWithScopes(scope.value()), scope);
        }
    }

    @Test
    public void allScopesWorkCombinedTest() {
        OrcidBearerTokenAuthentication authentication = mock(OrcidBearerTokenAuthentication.class);
        Set<String> allScopes = Arrays.stream(ScopePathType.values()).map(ScopePathType::value).collect(Collectors.toSet());
        when(authentication.getScopes()).thenReturn(allScopes);

        for (ScopePathType scope : ScopePathType.values()) {
            ScopeValidator.validateRequiredScopes(authentication, scope);
        }
    }

    @Test
    public void anyOfMultipleRequiredScopesIsEnoughTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ORCID_WORKS_READ_LIMITED.value()), ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_WORKS_CREATE);
    }

    @Test
    public void emailAndProfileAreIgnoredWhenCombinedTest() {
        for (ScopePathType scope : ScopePathType.values()) {
            ScopeValidator.validateRequiredScopes(authWithScopes(scope.value(), "email", "profile"), scope);
        }
    }

    @Test(expected = OrcidAccessControlException.class)
    public void emailAloneThrowsTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes("email"), ScopePathType.READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void profileAloneThrowsTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes("profile"), ScopePathType.READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void emailAndProfileTogetherThrowTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes("email", "profile"), ScopePathType.PERSON_UPDATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void missingRequiredScopeThrowsTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes("/person/read-limited"), ScopePathType.PERSON_UPDATE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownScopeThrowsTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes("/not-a-real-scope"), ScopePathType.READ_LIMITED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownScopeCombinedWithEmailAndProfileThrowsTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes("email", "profile", "invalid"), ScopePathType.READ_LIMITED);
    }

    @Test
    public void readLimitedScopesHierarchyTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.READ_LIMITED.value()), ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void readLimitedScopesHierarchy_LowerPrivilegesFailTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ORCID_WORKS_READ_LIMITED.value()), ScopePathType.READ_LIMITED);
    }

    @Test
    public void updateScopesHierarchyTest() {
        // Check works create
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ACTIVITIES_UPDATE.value()), ScopePathType.ORCID_WORKS_CREATE);
        // Check works update
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ACTIVITIES_UPDATE.value()), ScopePathType.ORCID_WORKS_UPDATE);
        // Check affiliation update
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ACTIVITIES_UPDATE.value()), ScopePathType.AFFILIATIONS_UPDATE);
        // Check affiliations create
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ACTIVITIES_UPDATE.value()), ScopePathType.AFFILIATIONS_CREATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void updateScopesHierarchy_LowerPrivilegesFail_CreateTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ORCID_WORKS_CREATE.value()), ScopePathType.ACTIVITIES_UPDATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void updateScopesHierarchy_LowerPrivilegesFail_UpdateTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ORCID_WORKS_UPDATE.value()), ScopePathType.ACTIVITIES_UPDATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void readLimitedCannotUpdate1Test() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.READ_LIMITED.value()), ScopePathType.ACTIVITIES_UPDATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void readLimitedCannotUpdate2Test() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.READ_LIMITED.value()), ScopePathType.ORCID_WORKS_CREATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void readLimitedCannotUpdate3Test() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.READ_LIMITED.value()), ScopePathType.ORCID_WORKS_UPDATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void readLimitedCannotUpdate4Test() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.READ_LIMITED.value()), ScopePathType.AFFILIATIONS_CREATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void readLimitedCannotUpdate5Test() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.READ_LIMITED.value()), ScopePathType.AFFILIATIONS_UPDATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void worksCreateCannotUpdateAffiliationsTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ORCID_WORKS_CREATE.value()), ScopePathType.AFFILIATIONS_CREATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void worksUpdateCannotUpdateAffiliationsTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.ORCID_WORKS_UPDATE.value()), ScopePathType.AFFILIATIONS_UPDATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void affiliationsCreateCannotUpdateWorksTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.AFFILIATIONS_CREATE.value()), ScopePathType.ORCID_WORKS_CREATE);
    }

    @Test(expected = OrcidAccessControlException.class)
    public void affiliationsUpdateCannotUpdateWorksTest() {
        ScopeValidator.validateRequiredScopes(authWithScopes(ScopePathType.AFFILIATIONS_UPDATE.value()), ScopePathType.ORCID_WORKS_UPDATE);
    }

}
