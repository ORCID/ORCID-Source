package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.orcid.core.adapter.mapstruct.ExternalIdentifierTypeMapper;
import org.orcid.core.adapter.mapstruct.JSONPeerReviewWorkExternalIdentifierMapperV3;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.Role;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class JSONPeerReviewWorkExternalIdentifierMapperV3Test {

    private final JSONPeerReviewWorkExternalIdentifierMapperV3 converter = newConverter();

    private static JSONPeerReviewWorkExternalIdentifierMapperV3 newConverter() {
        JSONPeerReviewWorkExternalIdentifierMapperV3 mapper = Mappers.getMapper(JSONPeerReviewWorkExternalIdentifierMapperV3.class);
        ReflectionTestUtils.setField(mapper, "typeMapper", ExternalIdentifierTypeMapper.INSTANCE);
        return mapper;
    }

    @Test
    public void testConvertTo() throws JAXBException {
        PeerReview peerReview = getPeerReview();
        assertEquals(
                "{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}",
                converter.convertTo(peerReview.getSubjectExternalIdentifier()));
    }

    @Test
    public void testConvertFrom() {
        PeerReviewEntity peerReview = getPeerReviewEntity();
        ExternalID externalID = converter.convertFrom(peerReview.getSubjectExternalIdentifiersJson());
        assertNotNull(externalID);

        assertEquals("source-work-id", externalID.getType());
        assertEquals("peer-review:subject-external-identifier-id", externalID.getValue());
        assertEquals("http://orcid.org", externalID.getUrl().getValue());
    }

    private PeerReview getPeerReview() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PeerReview.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0/samples/read_samples/peer-review-full-3.0.xml";
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
        result.setExternalIdentifiersJson(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"peer-review:external-identifier-id\"}}]}");
        result.setOrcid("0000-0001-0002-0003");
        result.setRole(Role.MEMBER.name());
        result.setType(PeerReviewType.EVALUATION.name());
        result.setUrl("peer-review:url");
        result.setSubjectExternalIdentifiersJson(
                "{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}");
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