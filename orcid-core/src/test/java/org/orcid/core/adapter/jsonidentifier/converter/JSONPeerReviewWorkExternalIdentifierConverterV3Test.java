package org.orcid.core.adapter.jsonidentifier.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class JSONPeerReviewWorkExternalIdentifierConverterV3Test {

    private JSONPeerReviewWorkExternalIdentifierConverterV3 converter = new JSONPeerReviewWorkExternalIdentifierConverterV3();

    @Test
    public void testConvertTo() throws JAXBException {
        PeerReview peerReview = getPeerReview();
        assertEquals(
                "{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}",
                converter.convertTo(peerReview.getSubjectExternalIdentifier(), null));
    }

    @Test
    public void testConvertFrom() {
        PeerReviewEntity peerReview = getPeerReviewEntity();
        ExternalID externalID = converter.convertFrom(peerReview.getSubjectExternalIdentifiersJson(), null);
        assertNotNull(externalID);
        
        assertEquals("source-work-id", externalID.getType());
        assertEquals("peer-review:subject-external-identifier-id", externalID.getValue());
        assertEquals("http://orcid.org", externalID.getUrl().getValue());
    }

    private PeerReview getPeerReview() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { PeerReview.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_dev1/samples/read_samples/peer-review-full-3.0_dev1.xml";
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
        orgEntity.setSource(new SourceEntity("APP-000000001"));

        PeerReviewEntity result = new PeerReviewEntity();
        result.setOrg(orgEntity);
        result.setCompletionDate(new CompletionDateEntity(2015, 1, 1));
        result.setExternalIdentifiersJson(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"peer-review:external-identifier-id\"}}]}");
        result.setProfile(new ProfileEntity("0000-0001-0002-0003"));
        result.setRole(org.orcid.jaxb.model.record_v2.Role.MEMBER.name());
        result.setType(org.orcid.jaxb.model.record_v2.PeerReviewType.EVALUATION.name());
        result.setUrl("peer-review:url");
        result.setSubjectExternalIdentifiersJson(
                "{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}");
        result.setSubjectContainerName("peer-review:subject-container-name");
        result.setSubjectName("peer-review:subject-name");
        result.setSubjectTranslatedName("peer-review:subject-translated-name");
        result.setSubjectTranslatedNameLanguageCode("en");
        result.setSubjectUrl("peer-review:subject-url");
        result.setSubjectType(org.orcid.jaxb.model.record_v2.WorkType.BOOK_REVIEW.name());
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        result.setClientSourceId("APP-000000001");
        result.setGroupId("orcid-generated:12345");
        result.setId(12345L);

        return result;
    }

}
