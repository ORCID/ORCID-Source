package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.Role;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbPeerReviewAdapterTest extends MockSourceNameCache {

    @Resource
    private JpaJaxbPeerReviewAdapter jpaJaxbPeerReviewAdapter;

    @Test
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        PeerReview e = getPeerReview(true);
        assertNotNull(e);
        PeerReviewEntity pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        assertNotNull(pe);
        //General info
        assertEquals(Long.valueOf(12345), pe.getId());
        assertEquals(Visibility.PRIVATE.name(), pe.getVisibility());        
        assertEquals("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}", pe.getExternalIdentifiersJson());
        assertEquals("REVIEWER", pe.getRole());
        assertEquals("REVIEW", pe.getType());
        assertEquals("peer-review:url", pe.getUrl());
        
        //Dates
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getDay());        
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getMonth());
        assertEquals(Integer.valueOf(1948), pe.getCompletionDate().getYear());        
        
        // Source
        assertNull(pe.getSourceId());        
        assertNull(pe.getClientSourceId());        
        assertNull(pe.getElementSourceId());
        
        //Check org values
        assertEquals("common:name", pe.getOrg().getName());
        assertEquals("common:city", pe.getOrg().getCity());
        assertEquals("common:region", pe.getOrg().getRegion());        
        assertEquals(Iso3166Country.AF.name(), pe.getOrg().getCountry());
        assertEquals("http://dx.doi.org/10.13039/100000001", pe.getOrg().getOrgDisambiguated().getSourceId());
        assertEquals("FUNDREF", pe.getOrg().getOrgDisambiguated().getSourceType()); 
        
        //Check subject        
        assertEquals("{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}", pe.getSubjectExternalIdentifiersJson());
        assertEquals("peer-review:subject-container-name", pe.getSubjectContainerName());
        assertEquals("peer-review:subject-name", pe.getSubjectName());
        assertEquals("peer-review:subject-translated-name", pe.getSubjectTranslatedName());
        assertEquals("en", pe.getSubjectTranslatedNameLanguageCode());
        assertEquals("peer-review:subject-url", pe.getSubjectUrl());
        assertEquals(WorkType.JOURNAL_ARTICLE.name(), pe.getSubjectType());
        
        //Check group id
        assertEquals("orcid-generated:12345", pe.getGroupId());
    }
    
    @Test
    public void clearPeerReviewEntityFieldsTest() throws JAXBException {
        PeerReview e = getPeerReview(true);
        assertNotNull(e);
        PeerReviewEntity pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        assertNotNull(pe);
        //General info
        assertEquals(Long.valueOf(12345), pe.getId());
        assertEquals(Visibility.PRIVATE.name(), pe.getVisibility());        
        assertEquals("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}", pe.getExternalIdentifiersJson());
        assertEquals("REVIEWER", pe.getRole());
        assertEquals("REVIEW", pe.getType());
        assertEquals("peer-review:url", pe.getUrl());

        // Clear fields
        e.setUrl(null);
        e.setSubjectUrl(null);
        e.getSubjectName().setTranslatedTitle(null);
        e.setSubjectContainerName(null);
        
        pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        assertNotNull(pe);
        
        // Check fields are null
        assertNull(pe.getUrl());
        assertNull(pe.getSubjectUrl());
        assertNull(pe.getSubjectTranslatedName());
        assertNull(pe.getSubjectTranslatedNameLanguageCode());
        assertNull(pe.getSubjectContainerName());
                
        //Dates
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getDay());        
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getMonth());
        assertEquals(Integer.valueOf(1948), pe.getCompletionDate().getYear());        
        
        // Source
        assertNull(pe.getSourceId());        
        assertNull(pe.getClientSourceId());        
        assertNull(pe.getElementSourceId());
        
        //Check org values
        assertEquals("common:name", pe.getOrg().getName());
        assertEquals("common:city", pe.getOrg().getCity());
        assertEquals("common:region", pe.getOrg().getRegion());        
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.AF.name(), pe.getOrg().getCountry());
        assertEquals("http://dx.doi.org/10.13039/100000001", pe.getOrg().getOrgDisambiguated().getSourceId());
        assertEquals("FUNDREF", pe.getOrg().getOrgDisambiguated().getSourceType()); 
        
        //Check subject        
        assertEquals("{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}", pe.getSubjectExternalIdentifiersJson());
        assertEquals("peer-review:subject-name", pe.getSubjectName());
        assertEquals(org.orcid.jaxb.model.record_v2.WorkType.JOURNAL_ARTICLE.name(), pe.getSubjectType());
        
        //Check group id
        assertEquals("orcid-generated:12345", pe.getGroupId());    
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToEducation() {
        PeerReviewEntity entity = getPeerReviewEntity();
        assertNotNull(entity);
        PeerReview peerReview= jpaJaxbPeerReviewAdapter.toPeerReview(entity);
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(12345), peerReview.getPutCode());
        assertEquals("private", peerReview.getVisibility().value());    
        assertEquals("orcid-generated:12345", peerReview.getGroupId());
        //Subject
        assertNotNull(peerReview.getSubjectExternalIdentifier());
        assertEquals("peer-review:subject-external-identifier-id", peerReview.getSubjectExternalIdentifier().getValue());
        assertEquals("source-work-id", peerReview.getSubjectExternalIdentifier().getType());
        assertEquals("peer-review:subject-container-name", peerReview.getSubjectContainerName().getContent());
        assertEquals("peer-review:subject-name", peerReview.getSubjectName().getTitle().getContent());
        assertEquals("peer-review:subject-translated-name", peerReview.getSubjectName().getTranslatedTitle().getContent());
        assertEquals("en", peerReview.getSubjectName().getTranslatedTitle().getLanguageCode());
        assertEquals(WorkType.BOOK_REVIEW.value(), peerReview.getSubjectType().value());
        assertEquals("peer-review:subject-url", peerReview.getSubjectUrl().getValue());        
        //Fields
        assertNotNull(peerReview.getExternalIdentifiers());
        assertNotNull(peerReview.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, peerReview.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review:external-identifier-id", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("source-work-id", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals(Role.MEMBER.value(), peerReview.getRole().value());
        assertEquals(PeerReviewType.EVALUATION.value(), peerReview.getType().value());
        assertEquals("peer-review:url", peerReview.getUrl().getValue());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertNotNull(peerReview.getOrganization());
        assertEquals("org:name", peerReview.getOrganization().getName());
        assertNotNull(peerReview.getOrganization().getAddress());
        assertEquals("org:city", peerReview.getOrganization().getAddress().getCity());
        assertEquals("org:region", peerReview.getOrganization().getAddress().getRegion());
        assertNotNull(peerReview.getSource());        
        assertEquals("APP-000000001", peerReview.getSource().retrieveSourcePath());
    }
    
    @Test
    public void fromPeerReviewEntityToPeerReviewSummary() {
        PeerReviewEntity entity = getPeerReviewEntity();
        assertNotNull(entity);
        PeerReviewSummary peerReviewSummary = jpaJaxbPeerReviewAdapter.toPeerReviewSummary(entity);
        assertNotNull(peerReviewSummary);
        assertEquals(Long.valueOf(12345), peerReviewSummary.getPutCode());
        assertEquals("private", peerReviewSummary.getVisibility().value());
        assertNotNull(peerReviewSummary.getCompletionDate());
        assertEquals("2015", peerReviewSummary.getCompletionDate().getYear().getValue());
        assertEquals("01", peerReviewSummary.getCompletionDate().getMonth().getValue());
        assertEquals("01", peerReviewSummary.getCompletionDate().getDay().getValue());
        assertNotNull(peerReviewSummary.getExternalIdentifiers());
        assertNotNull(peerReviewSummary.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, peerReviewSummary.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review:external-identifier-id", peerReviewSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("source-work-id", peerReviewSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());        
        assertNotNull(peerReviewSummary.getSource());
        assertEquals("APP-000000001", peerReviewSummary.getSource().retrieveSourcePath());
    }

    private PeerReview getPeerReview(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { PeerReview.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/peer-review-2.0.xml";
        if(full) {
            name = "/record_2.0/samples/read_samples/peer-review-full-2.0.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (PeerReview) unmarshaller.unmarshal(inputStream);
    }
    
    private PeerReviewEntity getPeerReviewEntity() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US.name());
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");
        
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId("APP-000000001");

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(clientDetailsEntity);
        orgEntity.setSource(sourceEntity);
        
        PeerReviewEntity result = new PeerReviewEntity();
        result.setOrg(orgEntity);
        result.setCompletionDate(new CompletionDateEntity(2015, 1, 1));
        result.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"peer-review:external-identifier-id\"}}]}");
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setRole(Role.MEMBER.name());
        result.setType(PeerReviewType.EVALUATION.name());
        result.setUrl("peer-review:url");        
        result.setSubjectExternalIdentifiersJson("{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}");
        result.setSubjectContainerName("peer-review:subject-container-name");
        result.setSubjectName("peer-review:subject-name");
        result.setSubjectTranslatedName("peer-review:subject-translated-name");
        result.setSubjectTranslatedNameLanguageCode("en");
        result.setSubjectUrl("peer-review:subject-url");                
        result.setSubjectType(WorkType.BOOK_REVIEW.name());        
        result.setVisibility(Visibility.PRIVATE.name());   
        result.setClientSourceId("APP-000000001");
        result.setGroupId("orcid-generated:12345");
        result.setId(12345L);
        
        return result;
    }
}
