package org.orcid.api.notificationsV3.server.delegator.impl;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * J21-010. Every notification-permission endpoint requires the premium-notification scope. That
 * requirement used to be carried by an `@AccessControl` annotation and an AspectJ advice that
 * read it; the migration deleted the advice and left the annotations, so the requirement was
 * stated on twelve methods and enforced on none. Nothing failed, nothing logged, and a token
 * without the scope was simply served.
 *
 * A test that only asserted the annotation is present would have passed throughout that. So this
 * asserts the effect instead: make the scope check refuse, and require that the refusal comes
 * back out of the method and that the data layer was never reached. That fails if the call is
 * removed, and it also fails if it is moved below the work it is meant to guard.
 *
 * Pure Mockito on purpose - no Spring context and no database, so it runs in milliseconds and
 * cannot be defeated by the orcid-core DBUnit harness being unavailable.
 */
public class NotificationsApiServiceDelegatorScopeTest {

    private static final String ORCID = "0000-0000-0000-0001";

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private NotificationManager notificationManager;

    private NotificationsApiServiceDelegatorImpl delegator;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        delegator = new NotificationsApiServiceDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(delegator, "notificationManager", notificationManager);
        doThrow(new OrcidAccessControlException())
                .when(orcidSecurityManager)
                .checkScopes(ScopePathType.PREMIUM_NOTIFICATION);
    }

    /**
     * Catches Throwable rather than the expected type on purpose. When the scope check is
     * missing the method does not simply return - it runs on and throws whatever the mocked
     * data layer provokes, which the first version of this test reported as
     * `OrcidNotificationNotFoundException`. That reads like a broken fixture, not like a
     * deleted access control check, and it is the same trap that produced two tests earlier in
     * this campaign that passed against unfixed code and guarded nothing.
     */
    private void expectRefusal(Runnable call, String endpoint) {
        Throwable thrown = null;
        try {
            call.run();
        } catch (Throwable t) {
            thrown = t;
        }
        if (thrown == null) {
            fail(endpoint + " was served to a token with no premium-notification scope: the "
                    + "scope check did not run");
        }
        if (!(thrown instanceof OrcidAccessControlException)) {
            fail(endpoint + " did not refuse on scope. The scope check has to be the first "
                    + "statement; something else failed first, which means the request had "
                    + "already started doing the work the check exists to prevent. Got: "
                    + thrown);
        }
        verifyNoInteractions(notificationManager);
        // Same mock serves checkProfile, so any call to it means the scope check was not first.
        verify(orcidSecurityManager, never()).checkProfile(anyString());
    }

    @Test
    public void testFindPermissionNotificationsRequiresTheScope() {
        expectRefusal(() -> delegator.findPermissionNotifications(ORCID), "the permission list");
    }

    @Test
    public void testFindPermissionNotificationRequiresTheScope() {
        expectRefusal(() -> delegator.findPermissionNotification(ORCID, 1L), "a permission by put-code");
    }

    @Test
    public void testFlagNotificationAsArchivedRequiresTheScope() {
        expectRefusal(() -> {
            try {
                delegator.flagNotificationAsArchived(ORCID, 1L);
            } catch (org.orcid.core.exception.OrcidNotificationAlreadyReadException e) {
                // Declared but unreachable: the scope check refuses before any read happens.
                throw new AssertionError("reached the archive path without a scope check", e);
            }
        }, "archiving a notification");
    }

    @Test
    public void testAddPermissionNotificationRequiresTheScope() {
        expectRefusal(() -> delegator.addPermissionNotification(null, ORCID, null), "creating a notification");
    }
}
