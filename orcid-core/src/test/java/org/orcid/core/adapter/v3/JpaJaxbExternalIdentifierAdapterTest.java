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
import org.orcid.core.adapter.v3.JpaJaxbExternalIdentifierAdapter;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.MockedMapStructAdapters;
import org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbExternalIdentifierAdapterImpl;

/**
 *
 * @author Angel Montenegro
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaJaxbExternalIdentifierAdapterTest {

    private static final String CLIENT_SOURCE_ID = MockedMapStructAdapters.CLIENT_SOURCE_ID;

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    private JpaJaxbExternalIdentifierAdapter jpaJaxbExternalIdentifierAdapter;

    @Before
    public void setUpAdapter() throws Exception {
        // REAL MapStruct mapper: the whole behaviour under test is the mapping configuration,
        // so a mocked mapper would make every assertion below an assertion about a mock.
        jpaJaxbExternalIdentifierAdapter = adapters.get(JpaJaxbExternalIdentifierAdapterImpl.class);
    }

    @Test
    public void testToExternalIdentifierEntity() throws JAXBException {
        ExternalIdentifierEntity entity = jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(getExternalIdentifier());
        assertNotNull(entity);
        assertEquals("A-0003", entity.getExternalIdCommonName());
        assertEquals("A-0003", entity.getExternalIdReference());
        assertEquals("http://ext-id/A-0003", entity.getExternalIdUrl());
        assertEquals(Long.valueOf(1), entity.getId());
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());

        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());

        // Check url get removed on entity when it comes null in model object
        PersonExternalIdentifier pei = getExternalIdentifier();
        pei.setUrl(null);

        jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(pei, entity);
        assertNotNull(entity);
        assertNull(entity.getExternalIdUrl());
        assertEquals("A-0003", entity.getExternalIdCommonName());
        assertEquals("A-0003", entity.getExternalIdReference());
        assertEquals(Long.valueOf(1), entity.getId());
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());

        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());
    }

    @Test
    public void fromExternalIdentifierEntityToExternalIdentifier() throws IllegalAccessException {
        ExternalIdentifierEntity entity = getExternalIdentifierEntity();
        PersonExternalIdentifier extId = jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
        assertNotNull(extId);
        assertNotNull(extId.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(extId.getCreatedDate().getValue()));
        assertNotNull(extId.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(extId.getLastModifiedDate().getValue()));
        assertEquals("common-name", extId.getType());
        assertEquals("id-reference", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://myurl.com", extId.getUrl().getValue());
        assertEquals(Long.valueOf(123), extId.getPutCode());
        assertNotNull(extId.getSource());
        assertEquals(CLIENT_SOURCE_ID, extId.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());

        // no user obo
        assertNull(extId.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromExternalIdentifierEntityToUserOBOExternalIdentifier() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(CLIENT_SOURCE_ID)).thenReturn(userOBOClient);

        ExternalIdentifierEntity entity = getExternalIdentifierEntity();
        PersonExternalIdentifier extId = jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
        assertNotNull(extId);
        assertNotNull(extId.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(extId.getCreatedDate().getValue()));
        assertNotNull(extId.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(extId.getLastModifiedDate().getValue()));
        assertEquals("common-name", extId.getType());
        assertEquals("id-reference", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://myurl.com", extId.getUrl().getValue());
        assertEquals(Long.valueOf(123), extId.getPutCode());
        assertNotNull(extId.getSource());
        assertEquals(CLIENT_SOURCE_ID, extId.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());

        // user obo
        assertNotNull(extId.getSource().getAssertionOriginOrcid());
    }

    private PersonExternalIdentifier getExternalIdentifier() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { PersonExternalIdentifier.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/external-identifier-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (PersonExternalIdentifier) unmarshaller.unmarshal(inputStream);
    }

    private ExternalIdentifierEntity getExternalIdentifierEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date);
        entity.setExternalIdCommonName("common-name");
        entity.setExternalIdReference("id-reference");
        entity.setExternalIdUrl("http://myurl.com");
        entity.setId(123L);
        entity.setClientSourceId(CLIENT_SOURCE_ID);
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        entity.setOrcid("orcid");

        return entity;
    }
}
