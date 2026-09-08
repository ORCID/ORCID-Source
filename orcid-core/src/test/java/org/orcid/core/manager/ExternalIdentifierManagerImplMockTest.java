package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.JpaJaxbExternalIdentifierAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.impl.ExternalIdentifierManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

/**
 * The two rules {@link ExternalIdentifierManagerImpl} enforces on an API write,
 * on mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code ExternalIdentifierManagerTest}, which is
 * left alone. Its one update test passes {@code isApiRequest = false}, the
 * branch that skips both rules below.
 *
 * <p>
 * {@code PersonValidator} is static and therefore real here.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExternalIdentifierManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private ExternalIdentifierManagerImpl externalIdentifierManager = new ExternalIdentifierManagerImpl();

    @Mock
    private ExternalIdentifierDao externalIdentifierDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private JpaJaxbExternalIdentifierAdapter jpaJaxbExternalIdentifierAdapter;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;


    private static ExternalIdentifierEntity storedExternalIdentifier(Visibility visibility, String clientSourceId) {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static PersonExternalIdentifier externalIdentifier(Visibility visibility) {
        PersonExternalIdentifier extId = new PersonExternalIdentifier();
        extId.setPutCode(PUT_CODE);
        extId.setRelationship(Relationship.SELF);
        extId.setType("person-ext-id-type");
        extId.setValue("person-ext-id-value");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setVisibility(visibility);
        return extId;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    // ------------------------------------------------- P1, the source guard

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(updatedExternalIdentifierEntity)}
     * ({@code ExternalIdentifierManagerImpl.java:97}) below
     * {@code externalIdentifierDao.merge(...)} ({@code :104}), or deleting it.
     */
    @Test
    public void updateRefusesAnIdentifierSourcedByAnotherClient() {
        ExternalIdentifierEntity stored = storedExternalIdentifier(Visibility.PUBLIC, Actors.CLIENT_B);
        when(externalIdentifierDao.getExternalIdentifierEntity(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            externalIdentifierManager.updateExternalIdentifier(ORCID, externalIdentifier(Visibility.PUBLIC), true);
            fail("a client must not update an external identifier another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(externalIdentifierDao, never()).merge(any(ExternalIdentifierEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(extIdEntity)}
     * ({@code ExternalIdentifierManagerImpl.java:144-146}) below
     * {@code externalIdentifierDao.removeExternalIdentifier(...)} ({@code :148}),
     * or deleting it.
     */
    @Test
    public void deleteRefusesAnIdentifierSourcedByAnotherClient() {
        ExternalIdentifierEntity stored = storedExternalIdentifier(Visibility.PUBLIC, Actors.CLIENT_B);
        when(externalIdentifierDao.getExternalIdentifierEntity(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            externalIdentifierManager.deleteExternalIdentifier(ORCID, PUT_CODE, true);
            fail("a client must not delete an external identifier another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(externalIdentifierDao, never()).removeExternalIdentifier(anyString(), anyLong());
    }

    // ----------------------------------------------- P4, the stored visibility

    /**
     * Mutation caught: deleting the {@code PersonValidator.validateExternalIdentifier(...)}
     * call at {@code ExternalIdentifierManagerImpl.java:86}.
     */
    @Test
    public void apiUpdateWithADifferentVisibilityIsRefused() {
        when(externalIdentifierDao.getExternalIdentifierEntity(ORCID, PUT_CODE))
                .thenReturn(storedExternalIdentifier(Visibility.PUBLIC, Actors.CLIENT_A));

        try {
            externalIdentifierManager.updateExternalIdentifier(ORCID, externalIdentifier(Visibility.LIMITED), true);
            fail("an API update must not move a public external identifier to limited");
        } catch (VisibilityMismatchException expected) {
            // the API renders this as 9035
        }

        verify(externalIdentifierDao, never()).merge(any(ExternalIdentifierEntity.class));
    }

    /**
     * Mutation caught: passing {@code null} instead of {@code originalVisibility}
     * at {@code ExternalIdentifierManagerImpl.java:86} (the value read at
     * {@code :84}). What reaches the adapter is what gets mapped onto the row.
     */
    @Test
    public void apiUpdateWithNoVisibilityKeepsTheStoredOne() {
        ExternalIdentifierEntity stored = storedExternalIdentifier(Visibility.PUBLIC, Actors.CLIENT_A);
        when(externalIdentifierDao.getExternalIdentifierEntity(ORCID, PUT_CODE)).thenReturn(stored);

        externalIdentifierManager.updateExternalIdentifier(ORCID, externalIdentifier(null), true);

        ArgumentCaptor<PersonExternalIdentifier> submitted = ArgumentCaptor.forClass(PersonExternalIdentifier.class);
        verify(jpaJaxbExternalIdentifierAdapter).toExternalIdentifierEntity(submitted.capture(), same(stored));
        assertEquals(Visibility.PUBLIC, submitted.getValue().getVisibility());
    }
}
