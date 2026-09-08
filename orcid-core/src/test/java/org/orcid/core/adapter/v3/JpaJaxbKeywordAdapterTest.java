package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.orcid.core.adapter.v3.JpaJaxbKeywordAdapter;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbKeywordAdapterImpl;
import org.orcid.core.adapter.MockedMapStructAdapters;

/**
 *
 * @author Angel Montenegro
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaJaxbKeywordAdapterTest {

    private static final String CLIENT_SOURCE_ID = MockedMapStructAdapters.CLIENT_SOURCE_ID;

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    private JpaJaxbKeywordAdapter adapter;

    @Before
    public void setUpAdapter() throws Exception {
        // REAL MapStruct mapper: the whole behaviour under test is the mapping configuration,
        // so a mocked mapper would make every assertion below an assertion about a mock.
        adapter = adapters.get(JpaJaxbKeywordAdapterImpl.class);
    }

    @Test
    public void fromKeywordToProfileKeywordEntityTest() throws JAXBException {
        Keyword keyword = getKeyword();
        ProfileKeywordEntity entity = adapter.toProfileKeywordEntity(keyword);
        assertNotNull(entity);
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertEquals(Long.valueOf(1), entity.getId());
        assertEquals("keyword1", entity.getKeywordName());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name(), entity.getVisibility());

        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());
    }

    @Test
    public void fromProfileKeywordEntityToKeywordTest() throws IllegalAccessException {
        ProfileKeywordEntity entity = getProfileKeywordEntity();
        Keyword keyword = adapter.toKeyword(entity);
        assertNotNull(keyword);
        assertNotNull(keyword.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getCreatedDate().getValue()));
        assertNotNull(keyword.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getLastModifiedDate().getValue()));
        assertEquals("keyword-1", keyword.getContent());
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getCreatedDate().getValue());
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertNotNull(keyword.getSource());
        assertEquals(CLIENT_SOURCE_ID, keyword.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());

        // no user obo
        assertNull(keyword.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromProfileKeywordEntityToUserOBOKeywordTest() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(Mockito.anyString())).thenReturn(userOBOClient);

        ProfileKeywordEntity entity = getProfileKeywordEntity();
        Keyword keyword = adapter.toKeyword(entity);
        assertNotNull(keyword);
        assertNotNull(keyword.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getCreatedDate().getValue()));
        assertNotNull(keyword.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getLastModifiedDate().getValue()));
        assertEquals("keyword-1", keyword.getContent());
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getCreatedDate().getValue());
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertNotNull(keyword.getSource());
        assertEquals(CLIENT_SOURCE_ID, keyword.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());

        // user obo
        assertNotNull(keyword.getSource().getAssertionOriginOrcid());
    }

    private Keyword getKeyword() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Keyword.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0/samples/read_samples/keyword-3.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Keyword) unmarshaller.unmarshal(inputStream);
    }

    private ProfileKeywordEntity getProfileKeywordEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date);
        entity.setId(Long.valueOf(1));
        entity.setKeywordName("keyword-1");
        entity.setOrcid("0000-0000-0000-0000");
        entity.setClientSourceId(CLIENT_SOURCE_ID);
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        return entity;
    }
}
