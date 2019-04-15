package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceItemEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbResearchResourceAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbResearchResourceAdapterV3")
    private JpaJaxbResearchResourceAdapter jpaJaxbResearchResourceAdapter;

    private Date createdDate = new Date();
    
    @Test
    public void testEntityToModel() throws JAXBException{
        ResearchResourceEntity e = getResearchResourceEntity();
        ResearchResource m = jpaJaxbResearchResourceAdapter.toModel(e);
        assertNotNull(m.getCreatedDate().getValue());
        assertNotNull(m.getLastModifiedDate().getValue());
        assertEquals("title",m.getProposal().getTitle().getTitle().getContent());
        assertEquals("translatedTitle",m.getProposal().getTitle().getTranslatedTitle().getContent());
        assertEquals("en",m.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("2020",m.getProposal().getEndDate().getYear().getValue());
        assertEquals("2019",m.getProposal().getStartDate().getYear().getValue());
        assertEquals("http://blah.com",m.getProposal().getUrl().getValue());
        assertEquals("source-work-id",m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id",m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name",m.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("org:city",m.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
        //assertEquals("https://orcid.org/0000-0001-0002-0003/research-resource/1234",m.getPath());
        assertEquals(Long.valueOf(12345l),m.getPutCode());
        assertEquals("APP-000000001",m.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC,m.getVisibility());
        
        assertEquals(1,m.getResourceItems().size());
        assertEquals("resourceName",m.getResourceItems().get(0).getResourceName());
        assertEquals("equipment",m.getResourceItems().get(0).getResourceType().name());
        assertEquals("http://blah.com",m.getResourceItems().get(0).getUrl().getValue());
        assertEquals("source-work-id",m.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id",m.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name",m.getResourceItems().get(0).getHosts().getOrganization().get(0).getName());
        assertEquals("org:city",m.getResourceItems().get(0).getHosts().getOrganization().get(0).getAddress().getCity());
        
    }
    
    @Test
    public void testModelToEntity() throws JAXBException{
        ResearchResource r = getResearchResource();
        ResearchResourceEntity e = jpaJaxbResearchResourceAdapter.toEntity(r);
        StartDateEntity start = new StartDateEntity(1999,2,2);
        EndDateEntity end = new EndDateEntity(2012,2,2);
        assertEquals(start.getYear(),e.getStartDate().getYear());
        assertEquals(start.getMonth(),e.getStartDate().getMonth());
        assertEquals(start.getDay(),e.getStartDate().getDay());
        assertEquals(end.getYear(),e.getEndDate().getYear());
        assertEquals(end.getMonth(),e.getEndDate().getMonth());
        assertEquals(end.getDay(),e.getEndDate().getDay());
        assertEquals(Long.valueOf(1234l),e.getId());
        assertEquals(DateUtils.convertToDate("2015-06-25T16:01:12"),e.getDateCreated());
        assertEquals(DateUtils.convertToDate("2017-09-08T13:31:19"),e.getLastModified());
        assertEquals("proposal",e.getProposalType());
        assertEquals("Giant Laser Award",e.getTitle());
        assertEquals("Giant Laser Award2",e.getTranslatedTitle());
        assertEquals("de",e.getTranslatedTitleLanguageCode());
        assertEquals("PUBLIC",e.getVisibility());        
        assertEquals("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"PROPOSAL_ID\",\"workExternalIdentifierId\":{\"content\":\"123456\"}},{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"HANDLE\",\"workExternalIdentifierId\":{\"content\":\"https://grants.net/123456\"}}]}",e.getExternalIdentifiersJson());
        //assertEquals("",e.getProfile().getId());
        //assertEquals(Long.valueOf(1l),e.getDisplayIndex());
        //assertEquals("https://orcid.org/0000-0000-0000-0000",e.getSourceId());
        //assertEquals("https://orcid.org/0000-0000-0000-0000",e.getClientSourceId());

        //host 1
        assertEquals("XSEDE", e.getHosts().get(0).getName());
        assertEquals("city", e.getHosts().get(0).getCity());
        assertEquals("region", e.getHosts().get(0).getRegion());        
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name(), e.getHosts().get(0).getCountry());
        assertEquals("XX", e.getHosts().get(0).getOrgDisambiguated().getSourceId());
        assertEquals("grid", e.getHosts().get(0).getOrgDisambiguated().getSourceType()); 
        //host 2
        assertEquals("Lasers-R-Us", e.getHosts().get(1).getName());
        assertEquals("city", e.getHosts().get(1).getCity());
        assertEquals("region", e.getHosts().get(1).getRegion());        
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name(), e.getHosts().get(1).getCountry());
        assertEquals("XX", e.getHosts().get(1).getOrgDisambiguated().getSourceId());
        assertEquals("lei", e.getHosts().get(1).getOrgDisambiguated().getSourceType()); 

        //item1
        //assertEquals("",e.getResourceItems().get(0).getId());
        assertEquals("Giant Laser 1",e.getResourceItems().get(0).getResourceName());
        assertEquals("infrastructures",e.getResourceItems().get(0).getResourceType());
        assertEquals("http://blah.com",e.getResourceItems().get(0).getUrl());
        assertEquals("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"RRID\",\"workExternalIdentifierId\":{\"content\":\"rrid:giantLASER\"}},{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"https://doi.org/10.123/giantlaser\"}}]}",e.getResourceItems().get(0).getExternalIdentifiersJson());
        //assertEquals(Long.valueOf(1234l),e.getResourceItems().get(0).getResearchResourceEntity().getId());        
        assertEquals("Lasers-R-US", e.getResourceItems().get(0).getHosts().get(0).getName());
        assertEquals("city", e.getResourceItems().get(0).getHosts().get(0).getCity());
        assertEquals("region", e.getResourceItems().get(0).getHosts().get(0).getRegion());        
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name(), e.getResourceItems().get(0).getHosts().get(0).getCountry());
        assertEquals("XX", e.getResourceItems().get(0).getHosts().get(0).getOrgDisambiguated().getSourceId());
        assertEquals("grid", e.getResourceItems().get(0).getHosts().get(0).getOrgDisambiguated().getSourceType()); 
        //item2        
        //assertEquals("",e.getResourceItems().get(1).getId());
        assertEquals("Moon Targets",e.getResourceItems().get(1).getResourceName());
        assertEquals("infrastructures",e.getResourceItems().get(1).getResourceType());
        assertEquals("http://blah2.com",e.getResourceItems().get(1).getUrl());
        assertEquals("{\"workExternalIdentifier\":[{\"relationship\":\"PART_OF\",\"url\":null,\"workExternalIdentifierType\":\"URI\",\"workExternalIdentifierId\":{\"content\":\"https://moon.org/targetOnTheMoon\"}}]}",e.getResourceItems().get(1).getExternalIdentifiersJson());
        //assertEquals(Long.valueOf(1234l),e.getResourceItems().get(1).getResearchResourceEntity().getId());        
        assertEquals("Moon Holdings Ltd", e.getResourceItems().get(1).getHosts().get(0).getName());
        assertEquals("city", e.getResourceItems().get(1).getHosts().get(0).getCity());
        assertEquals("region", e.getResourceItems().get(1).getHosts().get(0).getRegion());        
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name(), e.getResourceItems().get(1).getHosts().get(0).getCountry());
        assertEquals("XX", e.getResourceItems().get(1).getHosts().get(0).getOrgDisambiguated().getSourceId());
        assertEquals("lei", e.getResourceItems().get(1).getHosts().get(0).getOrgDisambiguated().getSourceType()); 
        
    }

    @Test
    public void testEntityToSummary(){
        ResearchResourceSummary m = jpaJaxbResearchResourceAdapter.toSummary(getResearchResourceEntity());
        assertNotNull(m.getCreatedDate().getValue());
        assertNotNull(m.getLastModifiedDate().getValue());
        assertEquals("title",m.getProposal().getTitle().getTitle().getContent());
        assertEquals("translatedTitle",m.getProposal().getTitle().getTranslatedTitle().getContent());
        assertEquals("en",m.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("2020",m.getProposal().getEndDate().getYear().getValue());
        assertEquals("2019",m.getProposal().getStartDate().getYear().getValue());
        assertEquals("http://blah.com",m.getProposal().getUrl().getValue());
        assertEquals("source-work-id",m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("id",m.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("org:name",m.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("org:city",m.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
        //assertEquals("https://orcid.org/0000-0001-0002-0003/research-resource/1234",m.getPath());
        assertEquals(Long.valueOf(12345l),m.getPutCode());
        assertEquals("APP-000000001",m.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC,m.getVisibility());
    }

    private ResearchResource getResearchResource() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { ResearchResource.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/research-resource-3.0_rc1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (ResearchResource) unmarshaller.unmarshal(inputStream);
    }
    
    private ResearchResourceEntity getResearchResourceEntity() {
        ResearchResourceEntity rre = new ResearchResourceEntity();
        rre.setEndDate(new EndDateEntity(2020, 2, 2));
        rre.setStartDate(new StartDateEntity(2019, 1, 1));
        rre.setTitle("title");
        rre.setTranslatedTitle("translatedTitle");
        rre.setTranslatedTitleLanguageCode("en");
        rre.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        rre.setDateCreated(createdDate);
        rre.setLastModified(createdDate);
        rre.setDisplayIndex(1l);
        rre.setClientSourceId("APP-000000001");
        rre.setUrl("http://blah.com");
        rre.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"id\"}}]}");
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
        //ie.setId(id);
        ie.setHosts(new ArrayList<OrgEntity>());
        ie.getHosts().add(orgEntity);
        ie.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"id\"}}]}");
        rre.getResourceItems().add(ie);
        
        return rre;
    }
}
