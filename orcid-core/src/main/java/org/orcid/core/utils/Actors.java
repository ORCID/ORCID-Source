package org.orcid.core.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.core.security.OrcidRoles;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

/**
 * Puts a named actor into the {@link SecurityContextHolder} for a unit test.
 *
 * <p>
 * Every way into the Registry authenticates differently -- a browser session is
 * a {@link UsernamePasswordAuthenticationToken} carrying roles, an API call is
 * an {@link OrcidBearerTokenAuthentication} carrying a client id and scopes, and
 * a trusted individual acting for someone else is the former wrapped in a
 * {@link SwitchUserGrantedAuthority}. Getting any of those subtly wrong produces
 * a test that passes while proving nothing, which is exactly the failure this
 * class exists to prevent.
 *
 * <p>
 * One actor per call, and always clear afterwards, because the security context
 * is static and leaks into the next test in the same JVM:
 *
 * <pre>
 * &#64;After
 * public void after() {
 *     Actors.clear();
 * }
 *
 * &#64;Test
 * public void aUserCannotDeleteAnotherUsersWork() {
 *     Actors.user(Actors.USER_A);
 *     ...
 * }
 * </pre>
 *
 * <p>
 * Two record identifiers are provided as constants so that cross-user tests read
 * the same way everywhere: {@link #USER_A} acts, {@link #USER_B} is the victim
 * whose data must be untouched. Likewise {@link #CLIENT_A} and {@link #CLIENT_B}
 * for the "item was created by another member" case.
 *
 * <p>
 * This class lives in {@code src/main} rather than a test source tree for the
 * same reason {@link SecurityContextTestUtils} does: it needs
 * {@link OrcidBearerTokenAuthentication} from this module, and {@code orcid-test}
 * cannot depend on {@code orcid-core} because the dependency already runs the
 * other way.
 *
 * @see SecurityContextTestUtils the older, narrower helper this generalises
 */
public final class Actors {

    /** The record that acts in a cross-user test. */
    public static final String USER_A = "0000-0000-0000-0003";

    /** The record that must be left untouched in a cross-user test. */
    public static final String USER_B = "0000-0000-0000-0004";

    /** An administrator's own record. */
    public static final String ADMIN_ORCID = "1111-1111-1111-1111";

    /** A member account (group) record. */
    public static final String GROUP_ORCID = "4444-4444-4444-4441";

    /** The member API client that acts. */
    public static final String CLIENT_A = "APP-5555555555555555";

    /** A different member API client -- the source of items {@link #CLIENT_A} may not touch. */
    public static final String CLIENT_B = "APP-5555555555555556";

    private Actors() {
    }

    // ---------------------------------------------------------------- browser

    /**
     * A signed-in user with {@code ROLE_USER}, the ordinary Registry actor.
     */
    public static void user(String orcid) {
        user(orcid, OrcidRoles.ROLE_USER);
    }

    /**
     * A signed-in user with an explicit set of roles.
     */
    public static void user(String orcid, OrcidRoles... roles) {
        setWebAuthentication(orcid, authorities(roles), null);
    }

    /**
     * An administrator: {@code ROLE_ADMIN} in addition to {@code ROLE_USER},
     * which is how the security configuration and {@code isAdmin()} see one.
     */
    public static void admin() {
        admin(ADMIN_ORCID);
    }

    public static void admin(String orcid) {
        user(orcid, OrcidRoles.ROLE_USER, OrcidRoles.ROLE_ADMIN);
    }

    /**
     * A member account, which is what reaches the member-facing web endpoints.
     */
    public static void group(String orcid) {
        user(orcid, OrcidRoles.ROLE_GROUP);
    }

    /**
     * A trusted individual acting for another record: the effective user is
     * {@code effectiveOrcid}, while {@code realOrcid} is who actually signed in.
     *
     * <p>
     * This is the shape {@code AuthenticationUtils.isInDelegationMode()} and
     * {@code retrieveRealUserOrcid()} look for, so a controller under test sees
     * the same delegation it would in production.
     */
    public static void delegate(String realOrcid, String effectiveOrcid) {
        delegate(realOrcid, effectiveOrcid, OrcidRoles.ROLE_USER);
    }

    /**
     * A delegate whose real user holds specific roles -- pass
     * {@link OrcidRoles#ROLE_ADMIN} to model an administrator who has switched
     * into a record, which {@code isDelegatedByAnAdmin()} treats differently
     * from an ordinary trusted individual.
     */
    public static void delegate(String realOrcid, String effectiveOrcid, OrcidRoles... realUserRoles) {
        UsernamePasswordAuthenticationToken source = new UsernamePasswordAuthenticationToken(realOrcid, "password",
                authorities(realUserRoles));
        source.setDetails(new User(realOrcid, "password", authorities(realUserRoles)));

        List<GrantedAuthority> granted = new ArrayList<>(authorities(OrcidRoles.ROLE_USER));
        granted.add(new SwitchUserGrantedAuthority(OrcidRoles.ROLE_PREVIOUS_ADMINISTRATOR.name(), source));
        setWebAuthentication(effectiveOrcid, granted, null);
    }

    // -------------------------------------------------------------------- API

    /**
     * A member API client acting with a user's token: it has both a client id
     * and the record that authorised it, plus the scopes that token carries.
     */
    public static void memberClient(String clientId, String userOrcid, ScopePathType... scopes) {
        setBearerAuthentication(clientId, userOrcid, scopeValues(scopes));
    }

    /**
     * The default member client acting on {@link #USER_A}'s behalf.
     */
    public static void memberClient(ScopePathType... scopes) {
        memberClient(CLIENT_A, USER_A, scopes);
    }

    /**
     * A client-credentials token: a machine client with no user behind it.
     * This is what reaches {@code orcid-internal-api} and the read-public
     * endpoints, and it is the actor for which {@code getUserOrcid()} is null.
     */
    public static void clientCredentials(String clientId, ScopePathType... scopes) {
        setBearerAuthentication(clientId, null, scopeValues(scopes));
    }

    public static void clientCredentials(ScopePathType... scopes) {
        clientCredentials(CLIENT_A, scopes);
    }

    /**
     * A public API client: a client id with only the read-public scope.
     */
    public static void publicClient(String clientId) {
        setBearerAuthentication(clientId, null, scopeValues(ScopePathType.READ_PUBLIC));
    }

    public static void publicClient() {
        publicClient(CLIENT_A);
    }

    /**
     * An unauthenticated caller, which is how the public API is reached without
     * a token at all.
     */
    public static void anonymous() {
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(new AnonymousAuthenticationToken("testKey", "anonymousUser",
                authorities(new OrcidRoles[0], "ROLE_ANONYMOUS")));
        SecurityContextHolder.setContext(context);
    }

    /**
     * Empties the security context. Call this from {@code @After} in every test
     * that set an actor: the holder is static, so a leftover actor silently
     * authorises the next test in the same JVM.
     */
    public static void clear() {
        SecurityContextHolder.clearContext();
    }

    // ---------------------------------------------------------------- private

    private static void setWebAuthentication(String orcid, Collection<GrantedAuthority> granted, String password) {
        String credentials = password == null ? "password" : password;
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(orcid,
                credentials, granted);
        // AuthenticationUtils.retrieveEffectiveOrcid() returns null unless
        // details are set, and BaseControllerUtil builds its User from the
        // authorities -- so both have to be present or the actor is inert.
        authentication.setDetails(new User(orcid, credentials, granted));

        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private static void setBearerAuthentication(String clientId, String userOrcid, Set<String> scopes) {
        OrcidBearerTokenAuthentication authentication = mock(OrcidBearerTokenAuthentication.class);
        when(authentication.getPrincipal()).thenReturn(clientId);
        when(authentication.getName()).thenReturn(clientId);
        when(authentication.getClientId()).thenReturn(clientId);
        when(authentication.getUserOrcid()).thenReturn(userOrcid);
        when(authentication.getScopes()).thenReturn(scopes);

        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private static Set<String> scopeValues(ScopePathType... scopes) {
        if (scopes == null) {
            return new HashSet<>();
        }
        return Arrays.stream(scopes).map(ScopePathType::value).collect(Collectors.toCollection(HashSet::new));
    }

    private static Collection<GrantedAuthority> authorities(OrcidRoles... roles) {
        return authorities(roles, (String[]) null);
    }

    private static List<GrantedAuthority> authorities(OrcidRoles[] roles, String... extra) {
        List<GrantedAuthority> granted = new ArrayList<>();
        if (roles != null) {
            for (OrcidRoles role : roles) {
                granted.add(new SimpleGrantedAuthority(role.getAuthority()));
            }
        }
        if (extra != null) {
            for (String authority : extra) {
                granted.add(new SimpleGrantedAuthority(authority));
            }
        }
        return granted;
    }
}
