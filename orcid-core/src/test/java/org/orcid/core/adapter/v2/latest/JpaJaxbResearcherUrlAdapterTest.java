package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.junit.Before;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.mapstruct.impl.JpaJaxbResearcherUrlAdapterImpl;
import org.orcid.core.adapter.MockedMapStructAdapters;

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
        assertNotNull(rUrls.getResearcherUrls().get(0).getCreatedDate());
        assertNotNull(rUrls.getResearcherUrls().get(0).getLastModifiedDate());
        ResearcherUrlEntity entity = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(rUrls.getResearcherUrls().get(0));
        assertNotNull(entity);
        //General info
        assertEquals(Long.valueOf(1248), entity.getId());
        assertEquals(Visibility.PUBLIC.name(), entity.getVisibility());
        assertEquals("http://site1.com/", entity.getUrl());
        assertEquals("Site # 1", entity.getUrlName());
        // Source
        assertNull(entity.getSourceId());
        assertNull(entity.getClientSourceId());
        assertNull(entity.getElementSourceId());
        // Dates are null
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
    }

    @Test
    public void fromResearcherUrlEntityToResearcherUrl() throws IllegalAccessException {
        ResearcherUrlEntity entity = getResearcherUrlEntity();
        ResearcherUrl r = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);
        //General info
        assertNotNull(r);
        assertEquals(Long.valueOf(13579), r.getPutCode());
        assertEquals("http://orcid.org", r.getUrl().getValue());
        assertEquals("Orcid URL", r.getUrlName());
        assertEquals(Visibility.LIMITED, r.getVisibility());
        assertNotNull(r.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getCreatedDate().getValue()));
        assertNotNull(r.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(r.getLastModifiedDate().getValue()));

        //Source
        assertEquals(CLIENT_SOURCE_ID, r.getSource().retrieveSourcePath());
    }      

    @Test
    public void fromResearcherUrlEntityWithBlankUrlDoesNotCreateUrlWrapper() throws IllegalAccessException {
        ResearcherUrlEntity entity = getResearcherUrlEntity();
        entity.setUrl(" ");

        ResearcherUrl researcherUrl = jpaJaxbResearcherUrlAdapter.toResearcherUrl(entity);

        assertNull(researcherUrl.getUrl());
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
        entity.setVisibility(Visibility.LIMITED.name());
        return entity;
    }
}
