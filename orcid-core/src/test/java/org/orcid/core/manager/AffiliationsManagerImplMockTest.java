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
import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.core.adapter.JpaJaxbEmploymentAdapter;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.impl.AffiliationsManagerImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.OrganizationHolder;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * The two rules {@link AffiliationsManagerImpl} enforces on an API write, on
 * mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code AffiliationsManagerTest}, which is left
 * alone. {@link ActivityValidator} is used for real here rather than mocked:
 * {@code validateEducation} and {@code validateEmployment} touch none of its
 * injected collaborators when {@code createFlag} is false and no dates are set,
 * so the visibility check that runs is the production one.
 *
 * <p>
 * Affiliations differ from the person elements in how the stored visibility
 * survives an update: the validator never repairs a null, the manager re-applies
 * it with {@code setVisibility(originalVisibility)} after the adapter has run.
 * That is why the null-visibility tests capture the entity reaching the DAO
 * rather than the model reaching the adapter.
 */
@RunWith(MockitoJUnitRunner.class)
public class AffiliationsManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private AffiliationsManagerImpl affiliationsManager = new AffiliationsManagerImpl();

    @Mock
    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private OrgManager orgManager;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private JpaJaxbEducationAdapter jpaJaxbEducationAdapter;

    @Mock
    private JpaJaxbEmploymentAdapter jpaJaxbEmploymentAdapter;

    @Before
    public void before() {
        // Deliberately real: the rule under test lives inside it, and neither
        // validateEducation nor validateEmployment reaches its collaborators on
        // this path.
        ReflectionTestUtils.setField(affiliationsManager, "activityValidator", new ActivityValidator());
    }


    private static OrgAffiliationRelationEntity storedAffiliation(AffiliationType type, Visibility visibility, String clientSourceId) {
        OrgAffiliationRelationEntity entity = new OrgAffiliationRelationEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setAffiliationType(type.name());
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static Education education(Visibility visibility) {
        Education education = new Education();
        education.setPutCode(PUT_CODE);
        education.setVisibility(visibility);
        return education;
    }

    private static Employment employment(Visibility visibility) {
        Employment employment = new Employment();
        employment.setPutCode(PUT_CODE);
        employment.setVisibility(visibility);
        return employment;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    /** An org with a name, which {@code createItemList} dereferences after a merge. */
    private OrgEntity anOrg() {
        OrgEntity org = new OrgEntity();
        org.setName("An institution");
        return org;
    }

    // ------------------------------------------------- P1, the source guard

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(educationEntity)}
     * ({@code AffiliationsManagerImpl.java:107}) below
     * {@code orgAffiliationRelationDao.merge(...)} ({@code :124}), or deleting it.
     */
    @Test
    public void educationUpdateRefusesAnAffiliationSourcedByAnotherClient() {
        OrgAffiliationRelationEntity stored = storedAffiliation(AffiliationType.EDUCATION, Visibility.PUBLIC, Actors.CLIENT_B);
        when(orgAffiliationRelationDao.getOrgAffiliationRelation(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            affiliationsManager.updateEducationAffiliation(ORCID, education(Visibility.PUBLIC), true);
            fail("a client must not update an education another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(orgAffiliationRelationDao, never()).merge(any(OrgAffiliationRelationEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(employmentEntity)}
     * ({@code AffiliationsManagerImpl.java:188}) below
     * {@code orgAffiliationRelationDao.merge(...)} ({@code :205}), or deleting it.
     */
    @Test
    public void employmentUpdateRefusesAnAffiliationSourcedByAnotherClient() {
        OrgAffiliationRelationEntity stored = storedAffiliation(AffiliationType.EMPLOYMENT, Visibility.PUBLIC, Actors.CLIENT_B);
        when(orgAffiliationRelationDao.getOrgAffiliationRelation(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            affiliationsManager.updateEmploymentAffiliation(ORCID, employment(Visibility.PUBLIC), true);
            fail("a client must not update an employment another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(orgAffiliationRelationDao, never()).merge(any(OrgAffiliationRelationEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(affiliationEntity)}
     * ({@code AffiliationsManagerImpl.java:224}) below
     * {@code orgAffiliationRelationDao.removeOrgAffiliationRelation(...)}
     * ({@code :225}), or deleting it.
     */
    @Test
    public void deleteRefusesAnAffiliationSourcedByAnotherClient() {
        OrgAffiliationRelationEntity stored = storedAffiliation(AffiliationType.EDUCATION, Visibility.PUBLIC, Actors.CLIENT_B);
        when(orgAffiliationRelationDao.getOrgAffiliationRelation(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            affiliationsManager.checkSourceAndDelete(ORCID, PUT_CODE);
            fail("a client must not delete an affiliation another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(orgAffiliationRelationDao, never()).removeOrgAffiliationRelation(ORCID, PUT_CODE);
    }

    // ----------------------------------------------- P4, the stored visibility

    /**
     * Mutation caught: deleting the {@code activityValidator.validateEducation(...)}
     * call at {@code AffiliationsManagerImpl.java:109}, or passing something
     * other than the stored visibility read at {@code :106}.
     */
    @Test
    public void educationApiUpdateWithADifferentVisibilityIsRefused() {
        when(orgAffiliationRelationDao.getOrgAffiliationRelation(ORCID, PUT_CODE))
                .thenReturn(storedAffiliation(AffiliationType.EDUCATION, Visibility.PUBLIC, Actors.CLIENT_A));

        try {
            affiliationsManager.updateEducationAffiliation(ORCID, education(Visibility.PRIVATE), true);
            fail("an API update must not move a public education to private");
        } catch (VisibilityMismatchException expected) {
            // the API renders this as 9035
        }

        verify(orgAffiliationRelationDao, never()).merge(any(OrgAffiliationRelationEntity.class));
    }

    /**
     * Mutation caught: deleting {@code educationEntity.setVisibility(originalVisibility)}
     * at {@code AffiliationsManagerImpl.java:112}. The adapter is stubbed to null
     * the entity's visibility, which is what the real update mapper does with a
     * model that carries none, so only the re-apply can put it back.
     */
    @Test
    public void educationApiUpdateWithNoVisibilityKeepsTheStoredOne() {
        OrgAffiliationRelationEntity stored = storedAffiliation(AffiliationType.EDUCATION, Visibility.PUBLIC, Actors.CLIENT_A);
        when(orgAffiliationRelationDao.getOrgAffiliationRelation(ORCID, PUT_CODE)).thenReturn(stored);
        when(orgManager.getOrgEntity(any(OrganizationHolder.class))).thenReturn(anOrg());
        when(orgAffiliationRelationDao.merge(any(OrgAffiliationRelationEntity.class))).thenAnswer(returnsFirstArg());
        doAnswer(invocation -> {
            OrgAffiliationRelationEntity target = invocation.getArgument(1);
            target.setVisibility(null);
            return target;
        }).when(jpaJaxbEducationAdapter).toOrgAffiliationRelationEntity(any(Education.class), any(OrgAffiliationRelationEntity.class));

        affiliationsManager.updateEducationAffiliation(ORCID, education(null), true);

        ArgumentCaptor<OrgAffiliationRelationEntity> merged = ArgumentCaptor.forClass(OrgAffiliationRelationEntity.class);
        verify(orgAffiliationRelationDao).merge(merged.capture());
        assertEquals(Visibility.PUBLIC.name(), merged.getValue().getVisibility());
    }

    /**
     * Mutation caught: deleting the {@code activityValidator.validateEmployment(...)}
     * call at {@code AffiliationsManagerImpl.java:190}, or passing something
     * other than the stored visibility read at {@code :181}.
     */
    @Test
    public void employmentApiUpdateWithADifferentVisibilityIsRefused() {
        when(orgAffiliationRelationDao.getOrgAffiliationRelation(ORCID, PUT_CODE))
                .thenReturn(storedAffiliation(AffiliationType.EMPLOYMENT, Visibility.PUBLIC, Actors.CLIENT_A));

        try {
            affiliationsManager.updateEmploymentAffiliation(ORCID, employment(Visibility.LIMITED), true);
            fail("an API update must not move a public employment to limited");
        } catch (VisibilityMismatchException expected) {
            // the API renders this as 9035
        }

        verify(orgAffiliationRelationDao, never()).merge(any(OrgAffiliationRelationEntity.class));
    }

    /**
     * Mutation caught: deleting {@code employmentEntity.setVisibility(originalVisibility)}
     * at {@code AffiliationsManagerImpl.java:193}.
     */
    @Test
    public void employmentApiUpdateWithNoVisibilityKeepsTheStoredOne() {
        OrgAffiliationRelationEntity stored = storedAffiliation(AffiliationType.EMPLOYMENT, Visibility.PUBLIC, Actors.CLIENT_A);
        when(orgAffiliationRelationDao.getOrgAffiliationRelation(ORCID, PUT_CODE)).thenReturn(stored);
        when(orgManager.getOrgEntity(any(OrganizationHolder.class))).thenReturn(anOrg());
        when(orgAffiliationRelationDao.merge(any(OrgAffiliationRelationEntity.class))).thenAnswer(returnsFirstArg());
        doAnswer(invocation -> {
            OrgAffiliationRelationEntity target = invocation.getArgument(1);
            target.setVisibility(null);
            return target;
        }).when(jpaJaxbEmploymentAdapter).toOrgAffiliationRelationEntity(any(Employment.class), any(OrgAffiliationRelationEntity.class));

        affiliationsManager.updateEmploymentAffiliation(ORCID, employment(null), true);

        ArgumentCaptor<OrgAffiliationRelationEntity> merged = ArgumentCaptor.forClass(OrgAffiliationRelationEntity.class);
        verify(orgAffiliationRelationDao).merge(merged.capture());
        assertEquals(Visibility.PUBLIC.name(), merged.getValue().getVisibility());
    }
}
