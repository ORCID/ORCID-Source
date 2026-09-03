package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * The "you are not the source of this item" guard.
 *
 * <p>
 * This is the rule that stops one member client from modifying or deleting an
 * item another member created on the same record. Until this class existed the
 * rule was asserted only indirectly, by the member API delegator tests in
 * {@code orcid-api-web} driving the real security manager against database
 * fixtures — so the guard's own behaviour had no test at this layer.
 *
 * <p>
 * That gap was found by mutation: disabling
 * {@link org.orcid.core.manager.v3.impl.OrcidSecurityManagerImpl#checkSourceAndThrow}
 * entirely left all 190 tests in this family green, both before and after the
 * family was migrated off the Spring context. These tests close it. Disabling
 * either guard now fails here.
 *
 * <p>
 * The two methods are deliberately tested differently, because they enforce the
 * rule differently. {@code checkSourceAndThrow} delegates the comparison to
 * {@code SourceEntityUtils.isTheSameSource}, which has its own tests in
 * {@code SourceEntityUtilsTest}; what is proved here is the branch — that a
 * mismatch throws and a match does not. {@code checkSource} compares client
 * identifiers itself, so those tests exercise the real comparison.
 */
public class OrcidSecurityManager_SourceTest extends OrcidSecurityManagerTestBase {

    private static Source sourceOf(String clientId) {
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(clientId));
        return source;
    }

    private static IdentifierTypeEntity identifierTypeSourcedBy(String clientId) {
        IdentifierTypeEntity entity = new IdentifierTypeEntity();
        if (clientId != null) {
            entity.setSourceClient(new ClientDetailsEntity(clientId));
        }
        return entity;
    }

    // ------------------------------------------------ checkSourceAndThrow

    @Test
    public void checkSourceAndThrow_sameSource_isAllowed() {
        WorkEntity existing = new WorkEntity();
        when(sourceManager.retrieveActiveSource()).thenReturn(sourceOf(CLIENT_1));
        when(sourceEntityUtils.isTheSameSource(sourceOf(CLIENT_1), existing)).thenReturn(true);

        orcidSecurityManager.checkSourceAndThrow(existing);
    }

    @Test
    public void checkSourceAndThrow_differentSource_isRefused() {
        WorkEntity existing = new WorkEntity();
        when(sourceManager.retrieveActiveSource()).thenReturn(sourceOf(CLIENT_2));
        when(sourceEntityUtils.isTheSameSource(sourceOf(CLIENT_2), existing)).thenReturn(false);

        try {
            orcidSecurityManager.checkSourceAndThrow(existing);
            fail("a client must not be allowed to act on an item it is not the source of");
        } catch (WrongSourceException expected) {
            // the message names the activity, which the API surfaces to the caller
            assertEquals("work", expected.getParams().get("activity"));
        }
    }

    @Test
    public void checkSourceAndThrow_noActiveSource_isAllowed() {
        // Documents current behaviour rather than endorsing it: with no active
        // source the guard does not fire, and the caller above it is what keeps
        // an unauthenticated request out.
        WorkEntity existing = new WorkEntity();
        when(sourceManager.retrieveActiveSource()).thenReturn(null);

        orcidSecurityManager.checkSourceAndThrow(existing);
    }

    // -------------------------------------------------------- checkSource

    @Test
    public void checkSource_sameClient_isAllowed() {
        when(sourceManager.retrieveActiveSourceId()).thenReturn(CLIENT_1);

        orcidSecurityManager.checkSource(identifierTypeSourcedBy(CLIENT_1));
    }

    @Test
    public void checkSource_differentClient_isRefused() {
        when(sourceManager.retrieveActiveSourceId()).thenReturn(CLIENT_1);

        try {
            orcidSecurityManager.checkSource(identifierTypeSourcedBy(CLIENT_2));
            fail("client 1 must not be allowed to act on an identifier type sourced by client 2");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }
    }

    @Test
    public void checkSource_entityWithNoSource_isRefusedForAClient() {
        when(sourceManager.retrieveActiveSourceId()).thenReturn(CLIENT_1);

        try {
            orcidSecurityManager.checkSource(identifierTypeSourcedBy(null));
            fail("an entity with no source must not be treated as belonging to the acting client");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }
    }

    @Test
    public void checkSource_noActiveClient_matchesAnUnsourcedEntity() {
        // Both sides null compare equal, so this passes. Recorded so that a
        // change to either side of that comparison shows up as a test change.
        when(sourceManager.retrieveActiveSourceId()).thenReturn(null);

        orcidSecurityManager.checkSource(identifierTypeSourcedBy(null));
    }
}
