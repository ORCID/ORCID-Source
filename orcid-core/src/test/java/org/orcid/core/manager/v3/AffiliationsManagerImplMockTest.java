package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.v3.impl.AffiliationsManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * The v3 affiliation delete refuses a client that is not the source of the
 * affiliation. One guard covers all seven affiliation types -- distinction,
 * education, employment, invited position, membership, qualification and
 * service all reach {@code AffiliationsManagerImpl.checkSourceAndDelete}.
 *
 * <p>
 * The method reads the affiliation, then calls
 * {@code orcidSecurityManager.checkSourceAndThrow(affiliationEntity)}
 * <em>before</em> {@code orgAffiliationRelationDao.removeOrgAffiliationRelation}.
 *
 * <p>
 * Seven real-chain member API delegator tests used to prove this
 * ({@code _EducationsTest}, {@code _EmploymentsTest}, {@code _DistinctionsTest},
 * {@code _InvitedPositionsTest}, {@code _MembershipsTest},
 * {@code _QualificationsTest}, {@code _ServicesTest}); all seven now run against
 * a mocked manager and cannot see the guard.
 *
 * <p>
 * No actor is placed in the security context. At this layer the security manager
 * is a mock, so nothing under test reads {@code SecurityContextHolder}; the
 * fixture that carries the rule is the entity's source -- {@code Actors.CLIENT_B}
 * created it, and the client the guard refuses is the one acting. Setting an
 * actor here would also fail the class: {@code Actors.memberClient} builds a
 * stubbed token mock, and the strict Mockito runner reports stubbings nothing
 * reads as unnecessary.
 *
 * @see OrcidSecurityManager_SourceTest the other half of TESTING.md R2
 */
@RunWith(MockitoJUnitRunner.class)
public class AffiliationsManagerImplMockTest {

    private static final Long AFFILIATION_ID = 20L;

    @Mock
    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private NotificationManager notificationManager;

    @InjectMocks
    private AffiliationsManagerImpl affiliationsManager = new AffiliationsManagerImpl();

    /**
     * Catches: deleting
     * {@code orcidSecurityManager.checkSourceAndThrow(affiliationEntity)} at
     * {@code manager/v3/impl/AffiliationsManagerImpl.java:449}, and moving it
     * below {@code orgAffiliationRelationDao.removeOrgAffiliationRelation} at
     * {@code :450}.
     */
    @Test
    public void checkSourceAndDeleteRefusesAnAffiliationSourcedByAnotherClient() {
        OrgAffiliationRelationEntity affiliation = new OrgAffiliationRelationEntity();
        affiliation.setId(AFFILIATION_ID);
        affiliation.setAffiliationType(AffiliationType.EDUCATION.name());
        affiliation.setClientSourceId(Actors.CLIENT_B);
        when(orgAffiliationRelationDao.getOrgAffiliationRelation(Actors.USER_A, AFFILIATION_ID)).thenReturn(affiliation);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(affiliation);

        try {
            affiliationsManager.checkSourceAndDelete(Actors.USER_A, AFFILIATION_ID);
            fail("a client must not be able to delete an affiliation another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(orgAffiliationRelationDao, never()).removeOrgAffiliationRelation(anyString(), anyLong());
        verify(notificationManager, never()).sendAmendEmail(anyString(), any(AmendedSection.class), anyCollection());
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
