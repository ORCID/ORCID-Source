/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PeerReviewType;
import org.orcid.jaxb.model.record_rc2.Role;
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
public class JpaJaxbPeerReviewAdapterTest {

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
        assertEquals(Visibility.PRIVATE.value(), pe.getVisibility().value());        
        assertEquals("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}", pe.getExternalIdentifiersJson());
        assertEquals("reviewer", pe.getRole().value());
        assertEquals("review", pe.getType().value());
        assertEquals("peer-review:url", pe.getUrl());
        
        //Dates
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getDay());        
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getMonth());
        assertEquals(Integer.valueOf(1848), pe.getCompletionDate().getYear());        
        
        //Source
        assertEquals("8888-8888-8888-8880", pe.getSource().getSourceId());
        
        //Check org values
        assertEquals("common:name", pe.getOrg().getName());
        assertEquals("common:city", pe.getOrg().getCity());
        assertEquals("common:region", pe.getOrg().getRegion());        
        assertEquals(Iso3166Country.AF.value(), pe.getOrg().getCountry().value());
        assertEquals("common:disambiguated-organization-identifier", pe.getOrg().getOrgDisambiguated().getSourceId());
        assertEquals("common:disambiguation-source", pe.getOrg().getOrgDisambiguated().getSourceType()); 
        
        //Check subject        
        assertEquals("{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}", pe.getSubjectExternalIdentifiersJson());
        assertEquals("peer-review:subject-container-name", pe.getSubjectContainerName());
        assertEquals("peer-review:subject-name", pe.getSubjectName());
        assertEquals("peer-review:subject-translated-name", pe.getSubjectTranslatedName());
        assertEquals("en", pe.getSubjectTranslatedNameLanguageCode());
        assertEquals("peer-review:subject-url", pe.getSubjectUrl());
        assertEquals(WorkType.JOURNAL_ARTICLE, pe.getSubjectType());
        
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
        String name = "/record_2.0_rc2/samples/peer-review-2.0_rc2.xml";
        if(full) {
            name = "/record_2.0_rc2/samples/peer-review-full-2.0_rc2.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (PeerReview) unmarshaller.unmarshal(inputStream);
    }
    
    private PeerReviewEntity getPeerReviewEntity() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setCity("org:city");
        orgEntity.setCountry(org.orcid.jaxb.model.message.Iso3166Country.US);
        orgEntity.setName("org:name");
        orgEntity.setRegion("org:region");
        orgEntity.setUrl("org:url");
        orgEntity.setSource(new SourceEntity("APP-000000001"));
        
        PeerReviewEntity result = new PeerReviewEntity();
        result.setOrg(orgEntity);
        result.setCompletionDate(new CompletionDateEntity(2015, 1, 1));
        result.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"peer-review:external-identifier-id\"}}]}");
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setRole(Role.MEMBER);
        result.setType(PeerReviewType.EVALUATION);
        result.setUrl("peer-review:url");        
        result.setSubjectExternalIdentifiersJson("{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}");
        result.setSubjectContainerName("peer-review:subject-container-name");
        result.setSubjectName("peer-review:subject-name");
        result.setSubjectTranslatedName("peer-review:subject-translated-name");
        result.setSubjectTranslatedNameLanguageCode("en");
        result.setSubjectUrl("peer-review:subject-url");                
        result.setSubjectType(WorkType.BOOK_REVIEW);        
        result.setVisibility(org.orcid.jaxb.model.message.Visibility.PRIVATE);   
        result.setSource(new SourceEntity("APP-000000001"));
        result.setGroupId("orcid-generated:12345");
        result.setId(12345L);
        
        return result;
    }
}
