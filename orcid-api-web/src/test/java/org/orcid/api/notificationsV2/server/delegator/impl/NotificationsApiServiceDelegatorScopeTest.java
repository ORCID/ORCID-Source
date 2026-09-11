package org.orcid.api.notificationsV2.server.delegator.impl;

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
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * J21-010, the v2 delegator. See the v3 test of the same name for why this asserts the effect of
 * the scope check rather than the presence of an annotation: the requirement used to live on an
 * `@AccessControl` annotation whose reading advice the migration deleted, so it was stated on
 * twelve methods and enforced on none, silently.
 *
 * Note the manager type. This package injects `org.orcid.core.manager.OrcidSecurityManager`,
 * the v2 one, not `org.orcid.core.manager.v3.OrcidSecurityManager`. Mocking the wrong type
 * leaves the real field null and the test proves nothing.
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
        inject("orcidSecurityManager", orcidSecurityManager);
        inject("notificationManager", notificationManager);
        doThrow(new OrcidAccessControlException())
                .when(orcidSecurityManager)
                .checkScopes(ScopePathType.PREMIUM_NOTIFICATION);
    }

    /**
     * Reports a missing field as what it is. Before the fix this delegator had no
     * orcidSecurityManager at all, so a plain setField fails here with "could not find field"
     * and reads like a broken fixture rather than a missing guard.
     */
    private void inject(String field, Object value) {
        try {
            ReflectionTestUtils.setField(delegator, field, value);
        } catch (IllegalArgumentException e) {
            fail("This delegator has no '" + field + "' field, so it cannot be enforcing "
                    + "premium-notification scope on anything: " + e.getMessage());
        }
    }

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
                throw new AssertionError("reached the archive path without a scope check", e);
            }
        }, "archiving a notification");
    }

    @Test
    public void testAddPermissionNotificationRequiresTheScope() {
        expectRefusal(() -> delegator.addPermissionNotification(null, ORCID, null), "creating a notification");
    }
}
