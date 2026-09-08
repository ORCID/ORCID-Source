package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.JpaJaxbFundingAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.impl.ProfileFundingManagerImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.manager.validator.ExternalIDValidator;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.OrganizationHolder;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingTitle;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * The two rules {@link ProfileFundingManagerImpl} enforces on an API write, on
 * mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code ProfileFundingManagerTest}, which is
 * left alone. {@link ActivityValidator} is real, with only its
 * {@link ExternalIDValidator} collaborator mocked, so the visibility check that
 * runs is the production one; the identifier vocabulary check it ends with is
 * not what this class is about and has its own tests.
 *
 * <p>
 * Like affiliations and unlike the person elements, funding does not have its
 * null visibility repaired by the validator — the manager re-applies the stored
 * value after the adapter, so the null-visibility test captures the entity
 * reaching the DAO.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileFundingManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private ProfileFundingManagerImpl profileFundingManager = new ProfileFundingManagerImpl();

    @Mock
    private ProfileFundingDao profileFundingDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private OrgManager orgManager;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private JpaJaxbFundingAdapter jpaJaxbFundingAdapter;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    @Mock
    private ExternalIDValidator externalIDValidator;

    @Before
    public void before() {
        ActivityValidator activityValidator = new ActivityValidator();
        ReflectionTestUtils.setField(activityValidator, "externalIDValidator", externalIDValidator);
        ReflectionTestUtils.setField(profileFundingManager, "activityValidator", activityValidator);
    }


    private static ProfileFundingEntity storedFunding(Visibility visibility, String clientSourceId) {
        ProfileFundingEntity entity = new ProfileFundingEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setTitle("Funding title");
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static Funding funding(Visibility visibility) {
        Funding funding = new Funding();
        funding.setPutCode(PUT_CODE);

        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Funding title"));
        funding.setTitle(title);

        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("grant_number");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setValue("ext-id-value");
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        funding.setExternalIdentifiers(extIds);

        funding.setVisibility(visibility);
        return funding;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    private OrgEntity anOrg() {
        OrgEntity org = new OrgEntity();
        org.setName("A funder");
        return org;
    }

    // ------------------------------------------------- P1, the source guard

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(pfe)}
     * ({@code ProfileFundingManagerImpl.java:183}) below
     * {@code profileFundingDao.merge(pfe)}, or deleting it.
     */
    @Test
    public void updateRefusesAFundingSourcedByAnotherClient() {
        ProfileFundingEntity stored = storedFunding(Visibility.PUBLIC, Actors.CLIENT_B);
        when(profileFundingDao.getProfileFunding(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            profileFundingManager.updateFunding(ORCID, funding(Visibility.PUBLIC), true);
            fail("a client must not update a funding another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(profileFundingDao, never()).merge(any(ProfileFundingEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(pfe)}
     * ({@code ProfileFundingManagerImpl.java:219}) below
     * {@code profileFundingDao.removeProfileFunding(...)}, or deleting it.
     */
    @Test
    public void deleteRefusesAFundingSourcedByAnotherClient() {
        ProfileFundingEntity stored = storedFunding(Visibility.PUBLIC, Actors.CLIENT_B);
        when(profileFundingDao.getProfileFunding(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            profileFundingManager.checkSourceAndDelete(ORCID, PUT_CODE);
            fail("a client must not delete a funding another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(profileFundingDao, never()).removeProfileFunding(ORCID, PUT_CODE);
    }

    // ----------------------------------------------- P4, the stored visibility

    /**
     * Mutation caught: deleting the {@code activityValidator.validateFunding(...)}
     * call at {@code ProfileFundingManagerImpl.java:172}, or passing something
     * other than the stored visibility read at {@code :166}.
     */
    @Test
    public void apiUpdateWithADifferentVisibilityIsRefused() {
        when(profileFundingDao.getProfileFunding(ORCID, PUT_CODE)).thenReturn(storedFunding(Visibility.PUBLIC, Actors.CLIENT_A));

        try {
            profileFundingManager.updateFunding(ORCID, funding(Visibility.PRIVATE), true);
            fail("an API update must not move a public funding to private");
        } catch (VisibilityMismatchException expected) {
            // the API renders this as 9035
        }

        verify(profileFundingDao, never()).merge(any(ProfileFundingEntity.class));
    }

    /**
     * Mutation caught: deleting {@code pfe.setVisibility(originalVisibility.name())}
     * at {@code ProfileFundingManagerImpl.java:186}. The adapter is stubbed to
     * null the entity's visibility, which is what the real update mapper does
     * with a model that carries none, so only the re-apply can put it back.
     */
    @Test
    public void apiUpdateWithNoVisibilityKeepsTheStoredOne() {
        ProfileFundingEntity stored = storedFunding(Visibility.PUBLIC, Actors.CLIENT_A);
        when(profileFundingDao.getProfileFunding(ORCID, PUT_CODE)).thenReturn(stored);
        when(orgManager.getOrgEntity(any(OrganizationHolder.class))).thenReturn(anOrg());
        when(profileFundingDao.merge(any(ProfileFundingEntity.class))).thenAnswer(returnsFirstArg());
        doAnswer(invocation -> {
            ProfileFundingEntity target = invocation.getArgument(1);
            target.setVisibility(null);
            return target;
        }).when(jpaJaxbFundingAdapter).toProfileFundingEntity(any(Funding.class), any(ProfileFundingEntity.class));

        profileFundingManager.updateFunding(ORCID, funding(null), true);

        ArgumentCaptor<ProfileFundingEntity> merged = ArgumentCaptor.forClass(ProfileFundingEntity.class);
        verify(profileFundingDao).merge(merged.capture());
        assertEquals(Visibility.PUBLIC.name(), merged.getValue().getVisibility());
    }
}
