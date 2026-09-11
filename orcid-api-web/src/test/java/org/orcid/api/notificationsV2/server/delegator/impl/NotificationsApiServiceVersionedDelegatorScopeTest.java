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
import org.orcid.api.notificationsV2.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * J21-010, the versioned v2 delegator, which serves /v2.0 and /v2.1.
 *
 * This one delegates its work to the plain v2 delegator, so the thing to prove is that the
 * refusal happens *before* it delegates. It is not redundant with the delegate's own check:
 * this class calls checkProfileStatus first, so without its own scope check an unscoped caller
 * would get a profile-status answer - possibly a 404 for a record that exists - rather than the
 * 403 the endpoint owes them, and the shape of the error would leak whether the record is there.
 */
public class NotificationsApiServiceVersionedDelegatorScopeTest {

    private static final String ORCID = "0000-0000-0000-0001";

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private NotificationsApiServiceDelegator<Object> delegate;

    private NotificationsApiServiceVersionedDelegatorImpl delegator;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        delegator = new NotificationsApiServiceVersionedDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(delegator, "notificationsApiServiceDelegator", delegate);
        doThrow(new OrcidAccessControlException())
                .when(orcidSecurityManager)
                .checkScopes(ScopePathType.PREMIUM_NOTIFICATION);
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
            fail(endpoint + " did not refuse on scope. The scope check has to come before "
                    + "checkProfileStatus, or an unscoped caller learns something about the "
                    + "record before being refused. Got: " + thrown);
        }
        verifyNoInteractions(delegate);
        // checkProfileStatus goes through the same mock, so a call to checkProfile means the
        // scope check was not first. This is the half that the delegate's own check cannot cover.
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
