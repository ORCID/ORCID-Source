package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
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
import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.impl.OtherNameManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

/**
 * The source guard on {@link OtherNameManagerImpl}, on mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code OtherNameManagerTest}, which is left
 * alone.
 */
@RunWith(MockitoJUnitRunner.class)
public class OtherNameManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private OtherNameManagerImpl otherNameManager = new OtherNameManagerImpl();

    @Mock
    private OtherNameDao otherNameDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private JpaJaxbOtherNameAdapter jpaJaxbOtherNameAdapter;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;


    private static OtherNameEntity storedOtherName(Visibility visibility, String clientSourceId) {
        OtherNameEntity entity = new OtherNameEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setDisplayName("other-name");
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static OtherName otherName(Visibility visibility) {
        OtherName otherName = new OtherName();
        otherName.setPutCode(PUT_CODE);
        otherName.setContent("other-name");
        otherName.setVisibility(visibility);
        return otherName;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(updatedOtherNameEntity)}
     * ({@code OtherNameManagerImpl.java:115}) below
     * {@code otherNameDao.merge(updatedOtherNameEntity)}, or deleting it.
     */
    @Test
    public void updateRefusesAnOtherNameSourcedByAnotherClient() {
        OtherNameEntity stored = storedOtherName(Visibility.PUBLIC, Actors.CLIENT_B);
        when(otherNameDao.getOtherName(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            otherNameManager.updateOtherName(ORCID, PUT_CODE, otherName(Visibility.PUBLIC), true);
            fail("a client must not update an other name another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(otherNameDao, never()).merge(any(OtherNameEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(otherNameEntity)}
     * ({@code OtherNameManagerImpl.java:45}) below
     * {@code otherNameDao.deleteOtherName(otherNameEntity)}, or deleting it.
     */
    @Test
    public void deleteRefusesAnOtherNameSourcedByAnotherClient() {
        OtherNameEntity stored = storedOtherName(Visibility.PUBLIC, Actors.CLIENT_B);
        when(otherNameDao.getOtherName(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            otherNameManager.deleteOtherName(ORCID, PUT_CODE, true);
            fail("a client must not delete an other name another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(otherNameDao, never()).deleteOtherName(any(OtherNameEntity.class));
    }
}
