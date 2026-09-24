package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Test;
import org.orcid.core.common.util.AuthenticationUtils;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.core.security.OrcidRoles;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests the test kit.
 *
 * <p>
 * {@link Actors} exists so that a security test cannot quietly authenticate the
 * wrong thing, so it needs its own proof: every actor here is asserted through
 * the same production code that reads the security context at runtime
 * ({@link AuthenticationUtils}), not through the fields Actors just set. An
 * actor that looks right but is invisible to that code would make every test
 * built on it vacuous.
 */
public class ActorsTest {

    @After
    public void after() {
        Actors.clear();
    }

    @Test
    public void userIsVisibleToTheCodeThatReadsTheContext() {
        Actors.user(Actors.USER_A);

        assertEquals(Actors.USER_A, AuthenticationUtils.retrieveEffectiveOrcid());
        assertEquals(Actors.USER_A, AuthenticationUtils.retrieveActiveSourceId());
        assertTrue(authorities().contains(OrcidRoles.ROLE_USER.name()));
        assertFalse(authorities().contains(OrcidRoles.ROLE_ADMIN.name()));
        assertFalse(AuthenticationUtils.isInDelegationMode());
    }

    @Test
    public void userDetailsArePopulated() {
        // BaseControllerUtil builds its UserDetails from the authentication, and
        // AuthenticationUtils.retrieveEffectiveOrcid() returns null when details
        // are absent -- so an actor without them is inert.
        Actors.user(Actors.USER_A);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication().getDetails());
    }

    @Test
    public void adminHoldsBothRoles() {
        Actors.admin();

        assertTrue(authorities().contains(OrcidRoles.ROLE_ADMIN.name()));
        assertTrue(authorities().contains(OrcidRoles.ROLE_USER.name()));
        assertEquals(Actors.ADMIN_ORCID, AuthenticationUtils.retrieveEffectiveOrcid());
    }

    @Test
    public void groupHoldsTheGroupRole() {
        Actors.group(Actors.GROUP_ORCID);

        assertTrue(authorities().contains(OrcidRoles.ROLE_GROUP.name()));
        assertFalse(authorities().contains(OrcidRoles.ROLE_USER.name()));
    }

    @Test
    public void delegateActsAsOneRecordWhileBeingAnother() {
        Actors.delegate(Actors.USER_B, Actors.USER_A);

        assertTrue(AuthenticationUtils.isInDelegationMode());
        assertEquals("the acting record is the one being managed", Actors.USER_A,
                AuthenticationUtils.retrieveEffectiveOrcid());
        assertEquals("the real user is the one who signed in", Actors.USER_B,
                AuthenticationUtils.retrieveRealUserOrcid());
        assertFalse(AuthenticationUtils.isDelegatedByAnAdmin());
    }

    @Test
    public void anAdminWhoSwitchedIntoARecordIsDistinguishable() {
        Actors.delegate(Actors.ADMIN_ORCID, Actors.USER_A, OrcidRoles.ROLE_USER, OrcidRoles.ROLE_ADMIN);

        assertTrue(AuthenticationUtils.isInDelegationMode());
        assertTrue(AuthenticationUtils.isDelegatedByAnAdmin());
    }

    @Test
    public void aPlainUserIsNotInDelegationMode() {
        Actors.user(Actors.USER_A);

        assertFalse(AuthenticationUtils.isInDelegationMode());
        assertFalse(AuthenticationUtils.isDelegatedByAnAdmin());
        assertEquals(Actors.USER_A, AuthenticationUtils.retrieveRealUserOrcid());
    }

    @Test
    public void memberClientCarriesBothTheClientAndTheRecordThatAuthorisedIt() {
        Actors.memberClient(Actors.CLIENT_A, Actors.USER_A, ScopePathType.ORCID_WORKS_UPDATE,
                ScopePathType.ORCID_WORKS_CREATE);

        OrcidBearerTokenAuthentication token = bearerToken();
        assertEquals(Actors.CLIENT_A, token.getClientId());
        assertEquals(Actors.USER_A, token.getUserOrcid());
        assertTrue(token.getScopes().contains(ScopePathType.ORCID_WORKS_UPDATE.value()));
        assertTrue(token.getScopes().contains(ScopePathType.ORCID_WORKS_CREATE.value()));
        assertEquals("an API call's source is the client, not the record", Actors.CLIENT_A,
                AuthenticationUtils.retrieveActiveSourceId());
    }

    @Test
    public void clientCredentialsHasNoUserBehindIt() {
        Actors.clientCredentials(Actors.CLIENT_B, ScopePathType.INTERNAL);

        OrcidBearerTokenAuthentication token = bearerToken();
        assertEquals(Actors.CLIENT_B, token.getClientId());
        assertNull("a machine token authorises no record", token.getUserOrcid());
        assertTrue(token.getScopes().contains(ScopePathType.INTERNAL.value()));
    }

    @Test
    public void memberClientWithNoScopesGrantsNothing() {
        Actors.memberClient(Actors.CLIENT_A, Actors.USER_A);

        assertTrue(bearerToken().getScopes().isEmpty());
    }

    @Test
    public void publicClientOnlyReadsPublicData() {
        Actors.publicClient();

        Set<String> scopes = bearerToken().getScopes();
        assertEquals(1, scopes.size());
        assertTrue(scopes.contains(ScopePathType.READ_PUBLIC.value()));
    }

    @Test
    public void anonymousIsAuthenticatedAsNobody() {
        Actors.anonymous();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(authentication instanceof AnonymousAuthenticationToken);
        assertTrue(authorities().contains("ROLE_ANONYMOUS"));
    }

    @Test
    public void clearEmptiesTheContext() {
        Actors.user(Actors.USER_A);
        Actors.clear();

        assertNull("a leftover actor authorises the next test in the same JVM",
                SecurityContextHolder.getContext().getAuthentication());
        assertNull(AuthenticationUtils.retrieveActiveSourceId());
    }

    @Test
    public void theTwoRecordConstantsDiffer() {
        // Cross-user tests are meaningless if the actor and the victim are the
        // same record, and that mistake is invisible at the call site.
        assertFalse(Actors.USER_A.equals(Actors.USER_B));
        assertFalse(Actors.CLIENT_A.equals(Actors.CLIENT_B));
    }

    private static OrcidBearerTokenAuthentication bearerToken() {
        return (OrcidBearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    private static Set<String> authorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}
