package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
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
import org.orcid.jaxb.model.common.PeerReviewSubjectType;
import org.orcid.jaxb.model.common.PeerReviewType;
import org.orcid.jaxb.model.common.Role;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.persistence.dao.RecordNameDao;
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
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbPeerReviewAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbPeerReviewAdapterV3")
    private JpaJaxbPeerReviewAdapter jpaJaxbPeerReviewAdapter;

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
    public void testToOrgAffiliationRelationEntity() throws JAXBException {
        PeerReview e = getPeerReview(true);
        assertNotNull(e);
        PeerReviewEntity pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        assertNotNull(pe);
        // General info
        assertEquals(Long.valueOf(12345), pe.getId());
        assertNull(pe.getDateCreated());
        assertNull(pe.getLastModified());
        assertEquals(Visibility.PRIVATE.name(), pe.getVisibility());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
                pe.getExternalIdentifiersJson());
        assertEquals("REVIEWER", pe.getRole());
        assertEquals("REVIEW", pe.getType());
        assertEquals("peer-review:url", pe.getUrl());

        // Dates
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getDay());
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getMonth());
        assertEquals(Integer.valueOf(1948), pe.getCompletionDate().getYear());

        // Source
        assertNull(pe.getSourceId());
        assertNull(pe.getClientSourceId());
        assertNull(pe.getElementSourceId());
        
        // org should be null, it will be found / created based on model object of incoming data
        assertNull(pe.getOrg());

        // Check subject
        assertEquals(
                "{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}",
                pe.getSubjectExternalIdentifiersJson());
        assertEquals("peer-review:subject-container-name", pe.getSubjectContainerName());
        assertEquals("peer-review:subject-name", pe.getSubjectName());
        assertEquals("peer-review:subject-translated-name", pe.getSubjectTranslatedName());
        assertEquals("en", pe.getSubjectTranslatedNameLanguageCode());
        assertEquals("peer-review:subject-url", pe.getSubjectUrl());
        assertEquals(org.orcid.jaxb.model.record_v2.WorkType.JOURNAL_ARTICLE.name(), pe.getSubjectType());

        // Check group id
        assertEquals("orcid-generated:12345", pe.getGroupId());
    }

    @Test
    public void clearPeerReviewEntityFieldsTest() throws JAXBException {
        PeerReview e = getPeerReview(true);
        assertNotNull(e);
        PeerReviewEntity pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(e);
        assertNotNull(pe);
        // General info
        assertEquals(Long.valueOf(12345), pe.getId());
        assertNull(pe.getDateCreated());
        assertNull(pe.getLastModified());
        assertEquals(Visibility.PRIVATE.name(), pe.getVisibility());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"SOURCE_WORK_ID\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
                pe.getExternalIdentifiersJson());
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

        // Dates
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getDay());
        assertEquals(Integer.valueOf(2), pe.getCompletionDate().getMonth());
        assertEquals(Integer.valueOf(1948), pe.getCompletionDate().getYear());

        // Source
        assertNull(pe.getSourceId());
        assertNull(pe.getClientSourceId());
        assertNull(pe.getElementSourceId());

        // org should be null, it will be found / created based on model object of incoming data
        assertNull(pe.getOrg());

        // Check subject
        assertEquals(
                "{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"peer-review:subject-external-identifier-id\"}}",
                pe.getSubjectExternalIdentifiersJson());
        assertEquals("peer-review:subject-name", pe.getSubjectName());
        assertEquals(org.orcid.jaxb.model.record_v2.WorkType.JOURNAL_ARTICLE.name(), pe.getSubjectType());

        // Check group id
        assertEquals("orcid-generated:12345", pe.getGroupId());
    }

    @Test
    public void fromPeerReviewEntityToPeerReview() throws IllegalAccessException {
        PeerReviewEntity entity = getPeerReviewEntity();
        assertNotNull(entity);
        PeerReview peerReview = jpaJaxbPeerReviewAdapter.toPeerReview(entity);
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(12345), peerReview.getPutCode());
        assertNotNull(peerReview.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReview.getCreatedDate().getValue()));
        assertNotNull(peerReview.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReview.getLastModifiedDate().getValue()));

        assertEquals("private", peerReview.getVisibility().value());
        assertEquals("orcid-generated:12345", peerReview.getGroupId());
        // Subject
        assertNotNull(peerReview.getSubjectExternalIdentifier());
        assertEquals("peer-review:subject-external-identifier-id", peerReview.getSubjectExternalIdentifier().getValue());
        assertEquals("source-work-id", peerReview.getSubjectExternalIdentifier().getType());
        assertEquals("peer-review:subject-container-name", peerReview.getSubjectContainerName().getContent());
        assertEquals("peer-review:subject-name", peerReview.getSubjectName().getTitle().getContent());
        assertEquals("peer-review:subject-translated-name", peerReview.getSubjectName().getTranslatedTitle().getContent());
        assertEquals("en", peerReview.getSubjectName().getTranslatedTitle().getLanguageCode());
        assertEquals(WorkType.BOOK_REVIEW.value(), peerReview.getSubjectType().value());
        assertEquals("peer-review:subject-url", peerReview.getSubjectUrl().getValue());
        // Fields
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

        // no user obo
        assertNull(peerReview.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromPeerReviewEntityToPeerReviewSummary() throws IllegalAccessException {
        PeerReviewEntity entity = getPeerReviewEntity();
        assertNotNull(entity);
        PeerReviewSummary peerReviewSummary = jpaJaxbPeerReviewAdapter.toPeerReviewSummary(entity);
        assertNotNull(peerReviewSummary);
        assertEquals(Long.valueOf(12345), peerReviewSummary.getPutCode());
        assertNotNull(peerReviewSummary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReviewSummary.getCreatedDate().getValue()));
        assertNotNull(peerReviewSummary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReviewSummary.getLastModifiedDate().getValue()));
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

        // no user obo
        assertNull(peerReviewSummary.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromPeerReviewEntityToUserOBOPeerReview() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(userOBOClient);

        PeerReviewEntity entity = getPeerReviewEntity();
        assertNotNull(entity);
        PeerReview peerReview = jpaJaxbPeerReviewAdapter.toPeerReview(entity);
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(12345), peerReview.getPutCode());
        assertNotNull(peerReview.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReview.getCreatedDate().getValue()));
        assertNotNull(peerReview.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReview.getLastModifiedDate().getValue()));
        assertEquals("private", peerReview.getVisibility().value());
        assertEquals("orcid-generated:12345", peerReview.getGroupId());
        // Subject
        assertNotNull(peerReview.getSubjectExternalIdentifier());
        assertEquals("peer-review:subject-external-identifier-id", peerReview.getSubjectExternalIdentifier().getValue());
        assertEquals("source-work-id", peerReview.getSubjectExternalIdentifier().getType());
        assertEquals("peer-review:subject-container-name", peerReview.getSubjectContainerName().getContent());
        assertEquals("peer-review:subject-name", peerReview.getSubjectName().getTitle().getContent());
        assertEquals("peer-review:subject-translated-name", peerReview.getSubjectName().getTranslatedTitle().getContent());
        assertEquals("en", peerReview.getSubjectName().getTranslatedTitle().getLanguageCode());
        assertEquals(WorkType.BOOK_REVIEW.value(), peerReview.getSubjectType().value());
        assertEquals("peer-review:subject-url", peerReview.getSubjectUrl().getValue());
        // Fields
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

        // user obo
        assertNotNull(peerReview.getSource().getAssertionOriginOrcid());
    }

    @Test
    public void fromPeerReviewEntityToUserOBOPeerReviewSummary() throws IllegalAccessException {
        // set client source to user obo enabled client
        ClientDetailsEntity userOBOClient = new ClientDetailsEntity();
        userOBOClient.setUserOBOEnabled(true);
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(userOBOClient);

        PeerReviewEntity entity = getPeerReviewEntity();
        assertNotNull(entity);
        PeerReviewSummary peerReviewSummary = jpaJaxbPeerReviewAdapter.toPeerReviewSummary(entity);
        assertNotNull(peerReviewSummary);
        assertEquals(Long.valueOf(12345), peerReviewSummary.getPutCode());
        assertNotNull(peerReviewSummary.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReviewSummary.getCreatedDate().getValue()));
        assertNotNull(peerReviewSummary.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(peerReviewSummary.getLastModifiedDate().getValue()));
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

        // user obo
        assertNotNull(peerReviewSummary.getSource().getAssertionOriginOrcid());
    }

    private PeerReview getPeerReview(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { PeerReview.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/peer-review-3.0_rc1.xml";
        if (full) {
            name = "/record_3.0_rc1/samples/read_samples/peer-review-full-3.0_rc1.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (PeerReview) unmarshaller.unmarshal(inputStream);
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
        result.setClientSourceId(CLIENT_SOURCE_ID);
        result.setGroupId("orcid-generated:12345");
        result.setId(12345L);

        return result;
    }

    @Test
    public void dissertationThesisModelToEntityTest() throws JAXBException {
        PeerReview p = getPeerReview(true);
        p.setSubjectType(PeerReviewSubjectType.DISSERTATION_THESIS);

        PeerReviewEntity pe = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(p);
        assertNotNull(pe);
        assertEquals(PeerReviewSubjectType.DISSERTATION_THESIS.name(), pe.getSubjectType());
    }
}
