package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.Role;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
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
    public void fromPeerReviewToPeerReviewEntity() throws JAXBException {
        PeerReview e = getPeerReview(true);        
        assertNotNull(e);
        
        PeerReviewEntity pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        assertNotNull(pe);

        // Source should be null, it is not set by the mapper
        assertNull(pe.getSourceId());
        assertNull(pe.getClientSourceId());
        assertNull(pe.getElementSourceId());
        assertNull(pe.getDateCreated());
        assertNull(pe.getLastModified());
        
        // General info
        assertEquals(Long.valueOf(12345), pe.getId());
        assertEquals(Visibility.PRIVATE.name(), pe.getVisibility());
        assertEquals("REVIEWER", pe.getRole());
        assertEquals("REVIEW", pe.getType());
        assertEquals("peer-review:url", pe.getUrl());
        
        // Dates
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getDay());
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getMonth());
        assertEquals(Integer.valueOf(1948), pe.getCompletionDate().getYear());        
        
        // Group id
        assertEquals("orcid-generated:12345", pe.getGroupId());
        
        // Subject data
        assertEquals("peer-review:subject-container-name", pe.getSubjectContainerName());
        assertEquals("peer-review:subject-name", pe.getSubjectName());
        assertEquals("peer-review:subject-translated-name", pe.getSubjectTranslatedName());
        assertEquals("en", pe.getSubjectTranslatedNameLanguageCode());
        assertEquals("JOURNAL_ARTICLE", pe.getSubjectType());
        assertEquals("peer-review:subject-url", pe.getSubjectUrl());
        
        // Org data
        assertNotNull(pe.getOrg());
        assertEquals("common:city", pe.getOrg().getCity());
        assertEquals("common:region", pe.getOrg().getRegion());
        assertEquals("AF", pe.getOrg().getCountry());
        
        // Identifiers
        assertEquals("{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}", pe.getSubjectExternalIdentifiersJson());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
                pe.getExternalIdentifiersJson());        
    }
    
    @Test
    public void fromPeerReviewEntityFullToPeerReviewEntityWithOnlyRequiredFields() throws JAXBException {
        // Get full peer review
        PeerReview e = getPeerReview(true);        
        assertNotNull(e);
        
        // Generate the entity
        PeerReviewEntity pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        assertNotNull(pe);
        
        // Clear fields
        e.setCompletionDate(null);        
        e.setExternalIdentifiers(null);
        e.setGroupId(null);
        e.setLastModifiedDate(null);
        e.setOrganization(null);
        e.setPath(null);
        e.setPutCode(null);
        e.setRole(null);
        e.setSource(null);
        e.setSubjectContainerName(null);
        e.setSubjectExternalIdentifier(null);
        e.setSubjectName(null);
        e.setSubjectType(null);
        e.setSubjectUrl(null);
        e.setType(null);
        e.setUrl(null);
        e.setVisibility(null);
        
        // Convert again
        pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        assertNotNull(pe);
        
        // Verify fields has been removed
        assertNull(pe.getCompletionDate());
        assertNull(pe.getExternalIdentifiersJson());
        assertNull(pe.getGroupId());
        assertNull(pe.getOrg());
        assertNull(pe.getRole());
        assertNull(pe.getSubjectContainerName());
        assertNull(pe.getSubjectExternalIdentifiersJson());
        assertNull(pe.getSubjectName());
        assertNull(pe.getSubjectTranslatedName());
        assertNull(pe.getSubjectTranslatedNameLanguageCode());
        assertNull(pe.getSubjectType());
        assertNull(pe.getSubjectUrl());
        assertNull(pe.getType());
        assertNull(pe.getUrl());
        assertNull(pe.getVisibility());
        
        // Map existing entity to updated entity
        e = getPeerReview(true);        
        assertNotNull(e);
        pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        
        // Verify fields exists
        assertNotNull(pe.getCompletionDate());
        assertNotNull(pe.getExternalIdentifiersJson());
        assertNotNull(pe.getGroupId());
        assertNotNull(pe.getRole());
        assertNotNull(pe.getSubjectContainerName());
        assertNotNull(pe.getSubjectExternalIdentifiersJson());
        assertNotNull(pe.getSubjectName());
        assertNotNull(pe.getSubjectTranslatedName());
        assertNotNull(pe.getSubjectTranslatedNameLanguageCode());
        assertNotNull(pe.getSubjectType());
        assertNotNull(pe.getSubjectUrl());
        assertNotNull(pe.getType());
        assertNotNull(pe.getUrl());
        assertNotNull(pe.getVisibility());
        
        // Clear fields
        e.setCompletionDate(null);        
        e.setExternalIdentifiers(null);
        e.setGroupId(null);
        e.setLastModifiedDate(null);
        e.setPath(null);
        e.setPutCode(null);
        e.setRole(null);
        e.setSource(null);
        e.setSubjectContainerName(null);
        e.setSubjectExternalIdentifier(null);
        e.setSubjectName(null);
        e.setSubjectType(null);
        e.setSubjectUrl(null);
        e.setType(null);
        e.setUrl(null);
        e.setVisibility(null);
        
        pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e, pe);
        // Verify fields has been removed
        assertNull(pe.getCompletionDate());
        assertNull(pe.getExternalIdentifiersJson());
        assertNull(pe.getGroupId());
        assertNull(pe.getRole());
        assertNull(pe.getSubjectContainerName());
        assertNull(pe.getSubjectExternalIdentifiersJson());
        assertNull(pe.getSubjectName());
        assertNull(pe.getSubjectTranslatedName());
        assertNull(pe.getSubjectTranslatedNameLanguageCode());
        assertNull(pe.getSubjectType());
        assertNull(pe.getSubjectUrl());
        assertNull(pe.getType());
        assertNull(pe.getUrl());
        assertNull(pe.getVisibility());
    }
    
    @Test
    public void fromOrgAffiliationRelationEntityToEducation() throws IllegalAccessException {
        PeerReviewEntity entity = getPeerReviewEntity();
        assertNotNull(entity);
        PeerReview peerReview= jpaJaxbPeerReviewAdapter.toPeerReview(entity);
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(12345), peerReview.getPutCode());
        assertNotNull(peerReview.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReview.getCreatedDate().getValue()));
        assertNotNull(peerReview.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReview.getLastModifiedDate().getValue()));
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
        assertEquals(CLIENT_SOURCE_ID, peerReview.getSource().retrieveSourcePath());
    }
    
    @Test
    public void fromPeerReviewEntityToPeerReviewSummary() throws IllegalAccessException {
        PeerReviewEntity entity = getPeerReviewEntity();
        assertNotNull(entity);
        PeerReviewSummary peerReviewSummary = jpaJaxbPeerReviewAdapter.toPeerReviewSummary(entity);
        assertNotNull(peerReviewSummary);
        assertNotNull(peerReviewSummary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReviewSummary.getCreatedDate().getValue()));
        assertNotNull(peerReviewSummary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReviewSummary.getLastModifiedDate().getValue()));
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
        assertEquals(CLIENT_SOURCE_ID, peerReviewSummary.getSource().retrieveSourcePath());
    }

    private PeerReview getPeerReview(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { PeerReview.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/peer-review-2.0.xml";
        if(full) {
            name = "/record_2.0/samples/read_samples/peer-review-full-2.0.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        PeerReview p = (PeerReview) unmarshaller.unmarshal(inputStream);
        if(full) {
            // Set the put code
            p.setPutCode(Long.valueOf(12345));
            // Set the visibility
            p.setVisibility(Visibility.PRIVATE);
            // Set the source
            Source s = new Source();
            s.setSourceClientId(new SourceClientId("APP-0000000000000000"));
            p.setSource(s);
        }
        
        assertNotNull(p.getCompletionDate());
        assertNotNull(p.getCompletionDate().getDay());
        assertNotNull(p.getCompletionDate().getMonth());
        assertNotNull(p.getCompletionDate().getYear());
        assertNotNull(p.getExternalIdentifiers());
        assertNotNull(p.getExternalIdentifiers().getExternalIdentifier().size());
        assertNotNull(p.getGroupId());        
        assertNotNull(p.getOrganization());
        assertNotNull(p.getOrganization().getDisambiguatedOrganization());
        assertNotNull(p.getOrganization().getAddress());
        assertNotNull(p.getPutCode());
        assertNotNull(p.getRole());        
        assertNotNull(p.getSubjectContainerName());
        assertNotNull(p.getSubjectExternalIdentifier());
        assertNotNull(p.getSubjectName());
        assertNotNull(p.getSubjectType());
        assertNotNull(p.getSubjectUrl());
        assertNotNull(p.getType());
        assertNotNull(p.getUrl());
        assertNotNull(p.getVisibility());
        
        return p;
    }
    
    private PeerReviewEntity getPeerReviewEntity() throws IllegalAccessException {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US.name());
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");
        
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId(CLIENT_SOURCE_ID);

        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(clientDetailsEntity);
        orgEntity.setSource(sourceEntity);
        
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        PeerReviewEntity result = new PeerReviewEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(result, date);
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
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setGroupId("orcid-generated:12345");
        result.setId(12345L);
        
        return result;
    }
}
