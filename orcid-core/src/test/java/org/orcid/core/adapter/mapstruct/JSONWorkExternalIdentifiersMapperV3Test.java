package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.core.utils.v3.identifiers.normalizers.CaseSensitiveNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.Normalizer;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.IdentifierType;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JSONWorkExternalIdentifiersMapperV3Test {

    /**
     * This test asserts on normalised values, so the normalisation chain must be REAL - a mocked
     * PIDNormalizationService would make every assertion below an assertion about the mock. Only its
     * two leaves are faked: the identifier_type reference data (which lives in the database and is
     * asserted by org.orcid.core.manager.IdentifierTypeManagerTest) and the message bundle. The values
     * used here mirror the shipped data: DOI carries the resolution prefix set by the Liquibase change
     * set identifier-types/update-doi-https.xml, and the two messages are the ones in
     * orcid-core/src/main/resources/i18n/api_en.properties.
     *
     * The mapper is the production MapStruct bean, built without Spring the way main's own
     * SourceMapperV2Test does it, with its four @Autowired collaborators set directly. Its @PostConstruct
     * init() is what resolves the two error messages, so it must be called by hand here.
     */
    @Mock
    private IdentifierTypeManager identifierTypeManager;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private PIDResolverService resolver;

    private final PIDNormalizationService norm = new PIDNormalizationService();

    private JSONWorkExternalIdentifiersMapperV3 converter;

    @Before
    public void initMocks() throws Exception {
        Map<String, IdentifierType> types = new HashMap<String, IdentifierType>();
        types.put("doi", identifierType("doi", "https://doi.org/"));
        types.put("agr", identifierType("agr", null));
        when(identifierTypeManager.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH)).thenReturn(types);

        when(localeManager.resolveMessage("transientError.normalization_failed.code")).thenReturn("8001");
        when(localeManager.resolveMessage("transientError.normalization_failed.message")).thenReturn("Cannot normalize identifier value {0}:{1}");

        CaseSensitiveNormalizer caseSensitiveNormalizer = new CaseSensitiveNormalizer();
        ReflectionTestUtils.setField(caseSensitiveNormalizer, "idman", identifierTypeManager);

        List<Normalizer> normalizers = new ArrayList<Normalizer>();
        normalizers.add(caseSensitiveNormalizer);
        normalizers.add(new DOINormalizer());
        ReflectionTestUtils.setField(norm, "normalizers", normalizers);
        ReflectionTestUtils.setField(norm, "idman", identifierTypeManager);
        norm.init();

        converter = Mappers.getMapper(JSONWorkExternalIdentifiersMapperV3.class);
        ReflectionTestUtils.setField(converter, "typeMapper", ExternalIdentifierTypeMapper.INSTANCE);
        ReflectionTestUtils.setField(converter, "norm", norm);
        ReflectionTestUtils.setField(converter, "resolverService", resolver);
        ReflectionTestUtils.setField(converter, "localeManager", localeManager);
        converter.init();
    }

    private IdentifierType identifierType(String name, String resolutionPrefix) {
        IdentifierType type = new IdentifierType();
        type.setName(name);
        type.setCaseSensitive(Boolean.FALSE);
        type.setResolutionPrefix(resolutionPrefix);
        return type;
    }

    @Test
    public void testConvertTo() throws JAXBException {
        Work work = getWork();
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
                converter.convertTo(work.getExternalIdentifiers()));
    }

    @Test
    public void testConvertFrom() throws IllegalAccessException {
        WorkEntity workEntity = getWorkEntity();
        ExternalIDs entityIDs = converter.convertFrom(workEntity.getExternalIdentifiersJson());
        assertEquals(1, entityIDs.getExternalIdentifier().size());

        ExternalID externalID = entityIDs.getExternalIdentifier().get(0);
        assertEquals("123", externalID.getValue());
        assertNotNull(externalID.getType());
        assertEquals("123",externalID.getNormalized().getValue());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), externalID.getType());
    }

    @Test
    public void testConvertFromNormalize() throws IllegalAccessException {
        WorkEntity workEntity = getWorkEntity();
        workEntity.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"doi:10.1/123\"}}]}");
        ExternalIDs entityIDs = converter.convertFrom(workEntity.getExternalIdentifiersJson());
        assertEquals(1, entityIDs.getExternalIdentifier().size());
        ExternalID externalID = entityIDs.getExternalIdentifier().get(0);
        assertEquals("doi:10.1/123", externalID.getValue());
        assertEquals("10.1/123",externalID.getNormalized().getValue());
        assertNotNull(externalID.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value(), externalID.getType());
    }
    
    @Test
    public void testConvertFromNormalizeError() throws IllegalAccessException {
        WorkEntity workEntity = getWorkEntity();
        workEntity.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"123\"}}]}");
        ExternalIDs entityIDs = converter.convertFrom(workEntity.getExternalIdentifiersJson());
        assertEquals(1, entityIDs.getExternalIdentifier().size());
        ExternalID externalID = entityIDs.getExternalIdentifier().get(0);
        assertEquals("123", externalID.getValue());
        assertNull(externalID.getNormalized());
        assertEquals("8001",externalID.getNormalizedError().getErrorCode());
        assertEquals("Cannot normalize identifier value doi:123",externalID.getNormalizedError().getErrorMessage());
        assertNotNull(externalID.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value(), externalID.getType());
    }
    
    @Test
    public void testConverFromtWithIdThatBreaksUrlValidation() {
        String extIds = "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"10.00000/test.v%vi%i.0000\"}}]}";
        ExternalIDs entityIDs = converter.convertFrom(extIds);
        assertNotNull(entityIDs.getExternalIdentifier());
        ExternalID eid0 = entityIDs.getExternalIdentifier().get(0);
        assertNotNull(eid0);
        assertNull(eid0.getUrl());
        assertEquals("doi", eid0.getType());
        assertEquals("10.00000/test.v%vi%i.0000", eid0.getValue());
        assertNotNull(eid0.getNormalized());
        assertEquals("10.00000/test.v%vi%i.0000", eid0.getNormalized().getValue());
        assertNull(eid0.getNormalizedUrl());
        assertNotNull(eid0.getNormalizedUrlError());
        assertEquals("Cannot normalize identifier value doi:10.00000/test.v%vi%i.0000", eid0.getNormalizedUrlError().getErrorMessage());
    }
    
    @Test
    public void testConverFromtWithUrlThatBreaksUrlValidation() {
        String extIds = "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://doi.org/10.00000/test.v%vi%i.0000\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"10.00000/test.v%vi%i.0000\"}}]}";
        ExternalIDs entityIDs = converter.convertFrom(extIds);
        assertNotNull(entityIDs.getExternalIdentifier());
        ExternalID eid0 = entityIDs.getExternalIdentifier().get(0);
        assertNotNull(eid0);
        assertNotNull(eid0.getUrl());
        assertEquals("doi", eid0.getType());
        assertEquals("10.00000/test.v%vi%i.0000", eid0.getValue());
        assertNotNull(eid0.getNormalized());
        assertEquals("10.00000/test.v%vi%i.0000", eid0.getNormalized().getValue());
        assertNull(eid0.getNormalizedUrl());
        assertNotNull(eid0.getNormalizedUrlError());
        assertEquals("Cannot normalize identifier value doi:10.00000/test.v%vi%i.0000", eid0.getNormalizedUrlError().getErrorMessage());
    }
    
    @Test
    public void testConvertToWithIdThatBreaksUrlValidation() {
        ExternalID eid0 = new ExternalID();
        eid0.setRelationship(Relationship.SELF);
        eid0.setType("doi");
        eid0.setValue("10.00000/test.v%vi%i.0000");
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(eid0);
        String expected1 = "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"10.00000/test.v%vi%i.0000\"}}]}";
        String expected2 = "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://doi.org/10.00000/test.v%vi%i.0000\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"10.00000/test.v%vi%i.0000\"}}]}";
        assertEquals(expected1, converter.convertTo(ids));
        // Set the URL
        eid0.setUrl(new Url("http://doi.org/10.00000/test.v%vi%i.0000"));
        assertEquals(expected2, converter.convertTo(ids));        
    }

    private Work getWork() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Work.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/work-full-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Work) unmarshaller.unmarshal(inputStream);
    }

    private WorkEntity getWorkEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        WorkEntity work = new WorkEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(work, date);
        work.setOrcid("0000-0000-0000-0001");
        work.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        work.setDisplayIndex(1234567890L);
        work.setClientSourceId("APP-5555555555555555");
        work.setCitation("work:citation");
        work.setCitationType(org.orcid.jaxb.model.record_v2.CitationType.BIBTEX.name());
        work.setDescription("work:description");
        work.setId(12345L);
        work.setIso2Country(org.orcid.jaxb.model.common_v2.Iso3166Country.CR.name());
        work.setJournalTitle("work:journalTitle");
        work.setLanguageCode("EN");
        work.setPublicationDate(new PublicationDateEntity(2000, 1, 1));
        work.setSubtitle("work:subtitle");
        work.setTitle("work:title");
        work.setTranslatedTitle("work:translatedTitle");
        work.setTranslatedTitleLanguageCode("ES");
        work.setWorkType(org.orcid.jaxb.model.record_v2.WorkType.ARTISTIC_PERFORMANCE.name());
        work.setWorkUrl("work:url");
        work.setContributorsJson("{\"contributor\":[]}");
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}}]}");
        return work;
    }

}
