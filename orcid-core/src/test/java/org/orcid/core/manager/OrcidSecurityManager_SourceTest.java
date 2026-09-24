package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * The v2 half of the "you are not the source of this item" guard.
 *
 * <p>
 * This is the twin of {@code org.orcid.core.manager.v3.OrcidSecurityManager_SourceTest}
 * and exists for the same reason: until it did, the v2 rule was asserted only
 * indirectly, by the member v2 API delegator tests in {@code orcid-api-web}
 * driving the real security manager against database fixtures. Once those
 * delegator tests stub the manager, nothing at any layer fails when
 * {@link org.orcid.core.manager.impl.OrcidSecurityManagerImpl#checkSource(SourceAwareEntity)}
 * stops throwing.
 *
 * <p>
 * The v2 method is not the v3 one. v3 delegates the comparison to
 * {@code SourceEntityUtils.isTheSameSource}; v2 compares the acting source id
 * against the entity's two source columns itself
 * ({@code OrcidSecurityManagerImpl.java:194-200}), so these tests exercise the
 * real comparison, one disjunct at a time.
 *
 * <p>
 * The acting source is not stubbed directly. {@link OrcidSecurityManagerTestBase}
 * wires {@code sourceManager.retrieveSourceOrcid()} to the security context, so
 * each test installs an actor with {@link Actors} and the acting id is derived
 * the way {@code SourceManagerImpl} derives it in production.
 */
public class OrcidSecurityManager_SourceTest extends OrcidSecurityManagerTestBase {

    private static WorkEntity workSourcedBy(String sourceId, String clientSourceId) {
        WorkEntity entity = new WorkEntity();
        entity.setSourceId(sourceId);
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    /**
     * The updater is the user who created the item.
     *
     * <p>
     * Mutation caught: dropping {@code sourceIdOfUpdater.equals(existingEntity.getSourceId())}
     * from the disjunction at {@code OrcidSecurityManagerImpl.java:196}. The
     * client source id deliberately belongs to somebody else, so this passes
     * through the first disjunct alone.
     */
    @Test
    public void checkSource_updaterIsTheRecordThatCreatedTheItem_isAllowed() {
        Actors.user(ORCID_1);

        orcidSecurityManager.checkSource(workSourcedBy(ORCID_1, CLIENT_2));
    }

    /**
     * The updater is the client that created the item.
     *
     * <p>
     * Mutation caught: dropping {@code sourceIdOfUpdater.equals(existingEntity.getClientSourceId())}
     * from the disjunction at {@code OrcidSecurityManagerImpl.java:196}. The
     * record source id deliberately belongs to somebody else, so this passes
     * through the second disjunct alone.
     */
    @Test
    public void checkSource_updaterIsTheClientThatCreatedTheItem_isAllowed() {
        Actors.memberClient(CLIENT_1, ORCID_1, ScopePathType.ORCID_WORKS_UPDATE);

        orcidSecurityManager.checkSource(workSourcedBy(ORCID_2, CLIENT_1));
    }

    /**
     * The updater created neither: this is the rule itself.
     *
     * <p>
     * Mutation caught: deleting the {@code throw new WrongSourceException(params)}
     * at {@code OrcidSecurityManagerImpl.java:200}, or negating the condition at
     * {@code :196}. The params are asserted, not only the exception type,
     * because they are what the API renders back to the caller.
     */
    @Test
    public void checkSource_updaterCreatedNeither_isRefused() {
        Actors.memberClient(CLIENT_1, ORCID_1, ScopePathType.ORCID_WORKS_UPDATE);

        try {
            orcidSecurityManager.checkSource(workSourcedBy(ORCID_2, CLIENT_2));
            fail("client 1 must not be allowed to act on an item client 2 created");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }
    }

    /**
     * No active source at all.
     *
     * <p>
     * Documents current behaviour rather than endorsing it: the guard opens with
     * {@code sourceIdOfUpdater != null} ({@code OrcidSecurityManagerImpl.java:196}),
     * so with nothing authenticated it does not fire, and the caller above it is
     * what keeps an unauthenticated request out.
     *
     * <p>
     * Mutation caught: removing the null check would make this throw, which is a
     * behaviour change even though it looks like a tightening — it is recorded
     * here so the change shows up as a test change rather than silently.
     */
    @Test
    public void checkSource_noActiveSource_isAllowed() {
        Actors.clear();

        orcidSecurityManager.checkSource(workSourcedBy(ORCID_2, CLIENT_2));
    }
}
