package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceItemEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbResearchResourceAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbResearchResourceAdapterV3")
    private JpaJaxbResearchResourceAdapter jpaJaxbResearchResourceAdapter;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManager;

    @Mock
    private ClientDetailsManager mockClientDetailsManager;

    @Mock
    private RecordNameDao mockRecordNameDao;

    @Mock
    private RecordNameManagerReadOnly mockRecordNameManager;

    private Date createdDate = DateUtils.convertToDate("2015-06-05T10:15:20");

    @Before
    public void setUp() {
        // by default return client details entity with user obo disabled
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(new ClientDetailsEntity());
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", mockClientDetailsManager);

        Mockito.when(mockRecordNameDao.exists(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockRecordNameManager.fetchDisplayablePublicName(Mockito.anyString())).thenReturn("test");
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameDao", mockRecordNameDao);
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameManagerReadOnlyV3", mockRecordNameManager);
    }

    @After
    public void tearDown() {
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", clientDetailsManager);        
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameDao", recordNameDao);        
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameManagerReadOnlyV3", recordNameManager);   
    }
    
    @Test
    public void testEntityToModel() throws JAXBException, IllegalAccessException {
        ResearchResourceEntity e = getResearchResourceEntity();
        ResearchResource m = jpaJaxbResearchResourceAdapter.toModel(e);
        assertNotNull(m.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(m.getCreatedDate().getValue()));
        assertNotNull(m.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(m.getLastModifiedDate().getValue()));
        assertEquals("title", m.getProposal().getTitle().getTitle().getContent());
        assertEquals("translatedTitle", m.getProposal().getTitle().getTranslatedTitle().getContent());
        assertEquals("en", m.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("2020", m.getProposal().getEndDate().getYear().getValue());
        assertEquals("2019", m.getProposal().getStartDate().getYear().getValue());
        assertEquals("http://blah.com", m.getProposal().getUrl().getValue());
        assertEquals("source-work-id", m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id", m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name", m.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("org:city", m.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
        // assertEquals("https://orcid.org/0000-0001-0002-0003/research-resource/1234",m.getPath());
        assertEquals(Long.valueOf(12345l), m.getPutCode());
        assertEquals(CLIENT_SOURCE_ID, m.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, m.getVisibility());

        assertEquals(1, m.getResourceItems().size());
        assertEquals("resourceName", m.getResourceItems().get(0).getResourceName());
        assertEquals("equipment", m.getResourceItems().get(0).getResourceType().name());
        assertEquals("http://blah.com", m.getResourceItems().get(0).getUrl().getValue());
        assertEquals("source-work-id", m.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id", m.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name", m.getResourceItems().get(0).getHosts().getOrganization().get(0).getName());
        assertEquals("org:city", m.getResourceItems().get(0).getHosts().getOrganization().get(0).getAddress().getCity());

        // no user obo
        assertNull(m.getSource().getAssertionOriginOrcid());
        
        assertEquals(1, m.getProposal().getHosts().getOrganization().size());
        assertNotNull(m.getProposal().getHosts().getOrganization().get(0));
        assertNotNull(m.getProposal().getHosts().getOrganization().get(0).getAddress());
        assertEquals("org:city", m.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
    }

    @Test
    public void testEntityToUserOBOModel() throws JAXBException, IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(userOBOClient);

        ResearchResourceEntity e = getResearchResourceEntity();
        ResearchResource m = jpaJaxbResearchResourceAdapter.toModel(e);
        assertNotNull(m.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(m.getCreatedDate().getValue()));
        assertNotNull(m.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(m.getLastModifiedDate().getValue()));
        assertEquals("title", m.getProposal().getTitle().getTitle().getContent());
        assertEquals("translatedTitle", m.getProposal().getTitle().getTranslatedTitle().getContent());
        assertEquals("en", m.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("2020", m.getProposal().getEndDate().getYear().getValue());
        assertEquals("2019", m.getProposal().getStartDate().getYear().getValue());
        assertEquals("http://blah.com", m.getProposal().getUrl().getValue());
        assertEquals("source-work-id", m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id", m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name", m.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("org:city", m.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
        // assertEquals("https://orcid.org/0000-0001-0002-0003/research-resource/1234",m.getPath());
        assertEquals(Long.valueOf(12345l), m.getPutCode());
        assertEquals(CLIENT_SOURCE_ID, m.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, m.getVisibility());

        assertEquals(1, m.getResourceItems().size());
        assertEquals("resourceName", m.getResourceItems().get(0).getResourceName());
        assertEquals("equipment", m.getResourceItems().get(0).getResourceType().name());
        assertEquals("http://blah.com", m.getResourceItems().get(0).getUrl().getValue());
        assertEquals("source-work-id", m.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id", m.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name", m.getResourceItems().get(0).getHosts().getOrganization().get(0).getName());
        assertEquals("org:city", m.getResourceItems().get(0).getHosts().getOrganization().get(0).getAddress().getCity());

        // user obo
        assertNotNull(m.getSource().getAssertionOriginOrcid());

    }

    @Test
    public void testModelToEntity() throws JAXBException {
        ResearchResource r = getResearchResource();
        ResearchResourceEntity e = jpaJaxbResearchResourceAdapter.toEntity(r);
        assertNull(e.getDateCreated());
        assertNull(e.getLastModified());
        StartDateEntity start = new StartDateEntity(1999, 2, 2);
        EndDateEntity end = new EndDateEntity(2012, 2, 2);
        assertEquals(start.getYear(), e.getStartDate().getYear());
        assertEquals(start.getMonth(), e.getStartDate().getMonth());
        assertEquals(start.getDay(), e.getStartDate().getDay());
        assertEquals(end.getYear(), e.getEndDate().getYear());
        assertEquals(end.getMonth(), e.getEndDate().getMonth());
        assertEquals(end.getDay(), e.getEndDate().getDay());
        assertEquals(Long.valueOf(1234l), e.getId());
        assertEquals("proposal", e.getProposalType());
        assertEquals("Giant Laser Award", e.getTitle());
        assertEquals("Giant Laser Award2", e.getTranslatedTitle());
        assertEquals("de", e.getTranslatedTitleLanguageCode());
        assertEquals("PUBLIC", e.getVisibility());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"PROPOSAL_ID\",\"workExternalIdentifierId\":{\"content\":\"123456\"}},{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"HANDLE\",\"workExternalIdentifierId\":{\"content\":\"https://grants.net/123456\"}}]}",
                e.getExternalIdentifiersJson());
        // assertEquals("",e.getProfile().getId());
        // assertEquals(Long.valueOf(1l),e.getDisplayIndex());
        // assertEquals("https://orcid.org/0000-0000-0000-0000",e.getSourceId());
        // assertEquals("https://orcid.org/0000-0000-0000-0000",e.getClientSourceId());

        // item1
        assertEquals("Giant Laser 1", e.getResourceItems().get(0).getResourceName());
        assertEquals("infrastructures", e.getResourceItems().get(0).getResourceType());
        assertEquals("http://blah.com", e.getResourceItems().get(0).getUrl());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"RRID\",\"workExternalIdentifierId\":{\"content\":\"rrid:giantLASER\"}},{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"https://doi.org/10.123/giantlaser\"}}]}",
                e.getResourceItems().get(0).getExternalIdentifiersJson());
      
        // item2
        // assertEquals("",e.getResourceItems().get(1).getId());
        assertEquals("Moon Targets", e.getResourceItems().get(1).getResourceName());
        assertEquals("infrastructures", e.getResourceItems().get(1).getResourceType());
        assertEquals("http://blah2.com", e.getResourceItems().get(1).getUrl());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"PART_OF\",\"url\":null,\"workExternalIdentifierType\":\"URI\",\"workExternalIdentifierId\":{\"content\":\"https://moon.org/targetOnTheMoon\"}}]}",
                e.getResourceItems().get(1).getExternalIdentifiersJson());
    }

    @Test
    public void testEntityToSummary() throws IllegalAccessException {
        ResearchResourceSummary m = jpaJaxbResearchResourceAdapter.toSummary(getResearchResourceEntity());
        assertNotNull(m.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(m.getCreatedDate().getValue()));
        assertNotNull(m.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(m.getLastModifiedDate().getValue()));
        assertEquals("title", m.getProposal().getTitle().getTitle().getContent());
        assertEquals("translatedTitle", m.getProposal().getTitle().getTranslatedTitle().getContent());
        assertEquals("en", m.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("2020", m.getProposal().getEndDate().getYear().getValue());
        assertEquals("2019", m.getProposal().getStartDate().getYear().getValue());
        assertEquals("http://blah.com", m.getProposal().getUrl().getValue());
        assertEquals("source-work-id", m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id", m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name", m.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("org:city", m.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
        // assertEquals("https://orcid.org/0000-0001-0002-0003/research-resource/1234",m.getPath());
        assertEquals(Long.valueOf(12345l), m.getPutCode());
        assertEquals(CLIENT_SOURCE_ID, m.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, m.getVisibility());

        // no user obo
        assertNull(m.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void testEntityToUserOBOSummary() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(userOBOClient);

        ResearchResourceSummary m = jpaJaxbResearchResourceAdapter.toSummary(getResearchResourceEntity());
        assertNotNull(m.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(m.getCreatedDate().getValue()));
        assertNotNull(m.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(m.getLastModifiedDate().getValue()));
        assertEquals("title", m.getProposal().getTitle().getTitle().getContent());
        assertEquals("translatedTitle", m.getProposal().getTitle().getTranslatedTitle().getContent());
        assertEquals("en", m.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("2020", m.getProposal().getEndDate().getYear().getValue());
        assertEquals("2019", m.getProposal().getStartDate().getYear().getValue());
        assertEquals("http://blah.com", m.getProposal().getUrl().getValue());
        assertEquals("source-work-id", m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id", m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name", m.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("org:city", m.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
        // assertEquals("https://orcid.org/0000-0001-0002-0003/research-resource/1234",m.getPath());
        assertEquals(Long.valueOf(12345l), m.getPutCode());
        assertEquals(CLIENT_SOURCE_ID, m.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, m.getVisibility());

        // user obo
        assertNotNull(m.getSource().getAssertionOriginOrcid());
    }

    private ResearchResource getResearchResource() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { ResearchResource.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0/samples/read_samples/research-resource-3.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (ResearchResource) unmarshaller.unmarshal(inputStream);
    }

    private ResearchResourceEntity getResearchResourceEntity() throws IllegalAccessException {
        ResearchResourceEntity rre = new ResearchResourceEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(rre, createdDate);
        rre.setEndDate(new EndDateEntity(2020, 2, 2));
        rre.setStartDate(new StartDateEntity(2019, 1, 1));
        rre.setTitle("title");
        rre.setTranslatedTitle("translatedTitle");
        rre.setTranslatedTitleLanguageCode("en");
        rre.setOrcid("0000-0001-0002-0003");
        rre.setDisplayIndex(1l);
        rre.setClientSourceId(CLIENT_SOURCE_ID);
        rre.setUrl("http://blah.com");
        rre.setExternalIdentifiersJson(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"id\"}}]}");
        rre.setId(12345L);
        rre.setVisibility("PUBLIC");

        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US.name());
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");
        rre.setHosts(new ArrayList<OrgEntity>());
        rre.getHosts().add(orgEntity);

        rre.setResourceItems(new ArrayList<ResearchResourceItemEntity>());
        ResearchResourceItemEntity ie = new ResearchResourceItemEntity();
        ie.setResourceName("resourceName");
        ie.setResourceType("equipment");
        ie.setUrl("http://blah.com");
        // ie.setId(id);
        ie.setHosts(new ArrayList<OrgEntity>());
        ie.getHosts().add(orgEntity);
        ie.setExternalIdentifiersJson(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"id\"}}]}");
        rre.getResourceItems().add(ie);

        return rre;
    }
}
