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
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.MockedMapStructAdapters;
import org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbResearcherUrlAdapterImpl;

/**
 *
 * @author Angel Montenegro
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaJaxbResearcherUrlAdapterTest {

    private static final String CLIENT_SOURCE_ID = MockedMapStructAdapters.CLIENT_SOURCE_ID;

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    private JpaJaxbResearcherUrlAdapter jpaJaxbResearcherUrlAdapter;

    @Before
    public void setUpAdapter() throws Exception {
        // REAL MapStruct mapper: the whole behaviour under test is the mapping configuration,
        // so a mocked mapper would make every assertion below an assertion about a mock.
        jpaJaxbResearcherUrlAdapter = adapters.get(JpaJaxbResearcherUrlAdapterImpl.class);
    }

    @Test
    public void testToResearcherUrlEntity() throws JAXBException {
        ResearcherUrls rUrls = getResearcherUrls();
        assertNotNull(rUrls);
        assertNotNull(rUrls.getResearcherUrls());
        assertEquals(1, rUrls.getResearcherUrls().size());
        ResearcherUrlEntity entity = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(rUrls.getResearcherUrls().get(0));
        assertNotNull(entity);
        //General info
        assertEquals(Long.valueOf(1248), entity.getId());
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertEquals(Visibility.PUBLIC.name(), entity.getVisibility());
        assertEquals("http://site1.com/", entity.getUrl());
        assertEquals("Site # 1", entity.getUrlName());
        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());
    }

    @Test
    public void fromResearcherUrlEntityToResearcherUrl() throws IllegalAccessException {
        ResearcherUrlEntity entity = getResearcherUrlEntity();
        ResearcherUrl r = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);
        //General info
        assertNotNull(r);
        assertNotNull(r.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getCreatedDate().getValue()));
        assertNotNull(r.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getLastModifiedDate().getValue()));
        assertEquals(Long.valueOf(13579), r.getPutCode());
        assertEquals("http://orcid.org", r.getUrl().getValue());
        assertEquals("Orcid URL", r.getUrlName());
        assertEquals(Visibility.LIMITED, r.getVisibility());
        //Source
        assertEquals(CLIENT_SOURCE_ID, r.getSource().retrieveSourcePath());

        // no user obo
        assertNull(r.getSource().getAssertionOriginOrcid());
    }      

    @Test
    public void fromResearcherUrlEntityWithBlankUrlDoesNotCreateUrlWrapper() throws IllegalAccessException {
        ResearcherUrlEntity entity = getResearcherUrlEntity();
        entity.setUrl(" ");

        ResearcherUrl researcherUrl = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);

        assertNull(researcherUrl.getUrl());
    }

    @Test
    public void fromResearcherUrlEntityToUserOBOResearcherUrl() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(adapters.clientDetailsEntityCacheManager.retrieve(Mockito.anyString())).thenReturn(userOBOClient);

        ResearcherUrlEntity entity = getResearcherUrlEntity();
        entity.setOrcid("orcid");

        ResearcherUrl r = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);
        //General info
        assertNotNull(r);
        assertNotNull(r.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getCreatedDate().getValue()));
        assertNotNull(r.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getLastModifiedDate().getValue()));
        assertEquals(Long.valueOf(13579), r.getPutCode());
        assertEquals("http://orcid.org", r.getUrl().getValue());
        assertEquals("Orcid URL", r.getUrlName());
        assertEquals(Visibility.LIMITED, r.getVisibility());
        //Source
        assertEquals(CLIENT_SOURCE_ID, r.getSource().retrieveSourcePath());

        // user obo
        assertNotNull(r.getSource().getAssertionOriginOrcid());
    }

    private ResearcherUrls getResearcherUrls() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { ResearcherUrls.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/researcher-urls-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (ResearcherUrls) unmarshaller.unmarshal(inputStream);
    }

    private ResearcherUrlEntity getResearcherUrlEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ResearcherUrlEntity entity = new ResearcherUrlEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date);
        entity.setId(13579L);
        entity.setClientSourceId(CLIENT_SOURCE_ID);
        entity.setUrl("http://orcid.org");
        entity.setUrlName("Orcid URL");
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        return entity;
    }
}
