package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import jakarta.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.resolvers.PubMedResolver;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.PIDResolutionResult;
import org.orcid.pojo.WorkExtended;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.togglz.junit.TogglzRule;


public class PubMedResolverTest {
    @Mock
    PIDNormalizationService normalizationService;

    @Mock
    PIDResolverCache cache;

    @Mock
    private IdentifierTypeManager identifierTypeManager;

    @Mock
    protected LocaleManager localeManager;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    private PubMedResolver resolver = new PubMedResolver();

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(resolver, "normalizationService", normalizationService);
        TargetProxyHelper.injectIntoProxy(resolver, "cache", cache);
        TargetProxyHelper.injectIntoProxy(resolver, "identifierTypeManager", identifierTypeManager);
        TargetProxyHelper.injectIntoProxy(resolver, "localeManager", localeManager);
        ReflectionTestUtils.setField(resolver, "maxContributorsForUI", 50);

        when(localeManager.getLocale()).thenReturn(Locale.ENGLISH);

        when(normalizationService.normalise(eq("pmc"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                String argument1 = invocation.getArgument(1).toString();
                if (argument1.contains("/")) {
                    return argument1.substring(argument1.lastIndexOf("/") + 1);
                }
                return argument1;
            }
        });

        when(normalizationService.normalise(eq("pmid"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                String argument1 = invocation.getArgument(1).toString();
                if (argument1.contains("/")) {
                    return argument1.substring(argument1.lastIndexOf("/") + 1);
                }
                return argument1;
            }
        });

        when(normalizationService.generateNormalisedURL(eq("pmc"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "https://europepmc.org/article/pmc/" + invocation.getArgument(1).toString();
            }
        });

        when(normalizationService.generateNormalisedURL(eq("pmid"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "https://www.ncbi.nlm.nih.gov/pubmed/" + invocation.getArgument(1).toString();
            }
        });

        when(identifierTypeManager.fetchIdentifierTypeByDatabaseName(eq("DOI"), Mockito.any(Locale.class))).thenAnswer(new Answer<IdentifierType>() {
            @Override
            public IdentifierType answer(InvocationOnMock invocation) throws Throwable {
                IdentifierType i = new IdentifierType();
                i.setResolutionPrefix("https://doi/");
                return i;
            }
        });

        when(identifierTypeManager.fetchIdentifierTypeByDatabaseName(eq("PMC"), Mockito.any(Locale.class))).thenAnswer(new Answer<IdentifierType>() {
            @Override
            public IdentifierType answer(InvocationOnMock invocation) throws Throwable {
                IdentifierType i = new IdentifierType();
                i.setResolutionPrefix("https://pmc/");
                return i;
            }
        });

        when(identifierTypeManager.fetchIdentifierTypeByDatabaseName(eq("PMID"), Mockito.any(Locale.class))).thenAnswer(new Answer<IdentifierType>() {
            @Override
            public IdentifierType answer(InvocationOnMock invocation) throws Throwable {
                IdentifierType i = new IdentifierType();
                i.setResolutionPrefix("https://pmid/");
                return i;
            }
        });

        when(identifierTypeManager.fetchIdentifierTypeByDatabaseName(eq("ISSN"), Mockito.any(Locale.class))).thenAnswer(new Answer<IdentifierType>() {
            @Override
            public IdentifierType answer(InvocationOnMock invocation) throws Throwable {
                IdentifierType i = new IdentifierType();
                i.setResolutionPrefix("https://issn/");
                return i;
            }
        });

        when(cache.get("https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=PMCID:PMC1&resultType=core&format=json", MediaType.APPLICATION_JSON))
                .thenAnswer(new Answer<InputStream>() {

                    @Override
                    public InputStream answer(InvocationOnMock invocation) throws Throwable {
                        return PubMedResolverTest.class.getResourceAsStream("/examples/works/form_autofill/pmc.json");
                    }

                });

        when(cache.get("https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=EXT_ID:pmid1&resultType=core&format=json", MediaType.APPLICATION_JSON))
                .thenAnswer(new Answer<InputStream>() {

                    @Override
                    public InputStream answer(InvocationOnMock invocation) throws Throwable {
                        return PubMedResolverTest.class.getResourceAsStream("/examples/works/form_autofill/pmid.json");
                    }

                });

        when(cache.get("https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=EXT_ID:pmid404&resultType=core&format=json", MediaType.APPLICATION_JSON))
                .thenAnswer(new Answer<InputStream>() {

                    @Override
                    public InputStream answer(InvocationOnMock invocation) throws Throwable {
                        return PubMedResolverTest.class.getResourceAsStream("/examples/works/form_autofill/pmid-no-results.json");
                    }

                });
    }

    @Test
    public void resolvePMCMetadataTest() {
        Work work = resolver.resolveMetadata("pmc", "1");
        assertNotNull(work);
        assertEquals("PMC title", work.getWorkTitle().getTitle().getContent());
        assertEquals("Journal title", work.getJournalTitle().getContent());
        assertEquals("2018", work.getPublicationDate().getYear().getValue());
        assertEquals("01", work.getPublicationDate().getMonth().getValue());
        assertEquals("01", work.getPublicationDate().getDay().getValue());

        assertEquals(3, work.getExternalIdentifiers().getExternalIdentifier().size());

        assertEquals("pmid", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("https://pmid/pmid1", work.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("pmid1", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());

        assertEquals("pmc", work.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("https://pmc/pmc1", work.getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("pmc1", work.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());

        assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(2).getType());
        assertEquals("https://doi/doi1", work.getExternalIdentifiers().getExternalIdentifier().get(2).getUrl().getValue());
        assertEquals("doi1", work.getExternalIdentifiers().getExternalIdentifier().get(2).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(2).getRelationship());
        
        assertNotNull(work.getWorkType());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
    }

    @Test
    public void resolvePMIDMetadataTest() {
        Work work = resolver.resolveMetadata("pmid", "pmid1");
        assertNotNull(work);
        assertEquals("PMID title", work.getWorkTitle().getTitle().getContent());
        assertEquals("Journal title", work.getJournalTitle().getContent());
        assertEquals("2018", work.getPublicationDate().getYear().getValue());
        assertEquals("01", work.getPublicationDate().getMonth().getValue());
        assertEquals("01", work.getPublicationDate().getDay().getValue());

        assertEquals(3, work.getExternalIdentifiers().getExternalIdentifier().size());

        assertEquals("pmid", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("https://pmid/pmid1", work.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("pmid1", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());

        assertEquals("pmc", work.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("https://pmc/pmc1", work.getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("pmc1", work.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());

        assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(2).getType());
        assertEquals("https://doi/doi1", work.getExternalIdentifiers().getExternalIdentifier().get(2).getUrl().getValue());
        assertEquals("doi1", work.getExternalIdentifiers().getExternalIdentifier().get(2).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(2).getRelationship());
        
        assertNotNull(work.getWorkType());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
    }

    @Test
    public void resolvePMIDMetadataContributorsTest() {
        WorkExtended work = resolver.resolveMetadata("pmid", "pmid1");
        List<ContributorsRolesAndSequences> contributors = work.getContributorsGroupedByOrcid();
        assertNotNull(work);
        assertNotNull(contributors);
        assertEquals(3, contributors.size());
        assertEquals("author", getRole(contributors, 0));
        assertEquals("Author One", getName(contributors, 0));
        assertEquals(getRole(contributors, 1), "author");
        assertEquals("Author Two", getName(contributors, 1));
        assertEquals("ORCID Consortium", getName(contributors, 2));
    }

    /**
     * PD-6180: NCBI answers HTTP 203 for pubmed.ncbi.nlm.nih.gov landing pages, so
     * resolution must not depend on that page returning 200.
     */
    @Test
    public void resolvePMIDMetadataWhenLandingPageIsNotHttp200Test() {
        when(cache.isHttp200(anyString())).thenReturn(false);

        WorkExtended work = resolver.resolveMetadata("pmid", "pmid1");
        assertNotNull(work);
        assertEquals("PMID title", work.getWorkTitle().getTitle().getContent());
    }

    @Test
    public void resolvePMIDWhenLandingPageIsNotHttp200Test() {
        when(cache.isHttp200(anyString())).thenReturn(false);

        PIDResolutionResult result = resolver.resolve("pmid", "pmid1");
        assertTrue(result.isResolved());
        assertTrue(result.getAttemptedResolution());
        assertTrue(result.isValidFormat());
        assertEquals("https://www.ncbi.nlm.nih.gov/pubmed/pmid1", result.getGeneratedUrl());
    }

    @Test
    public void resolvePMIDUnknownToEuropePMCTest() {
        PIDResolutionResult result = resolver.resolve("pmid", "pmid404");
        assertFalse(result.isResolved());
        assertTrue(result.getAttemptedResolution());
        assertTrue(result.isValidFormat());
        assertNull(result.getGeneratedUrl());
    }

    @Test
    public void resolveMetadataPMIDUnknownToEuropePMCTest() {
        assertNull(resolver.resolveMetadata("pmid", "pmid404"));
    }

    @Test
    public void resolvePMIDNotAttemptedForEmptyValueTest() {
        PIDResolutionResult result = resolver.resolve("pmid", "");
        assertFalse(result.isResolved());
        assertFalse(result.getAttemptedResolution());
    }

    @Test
    public void resolveMetadataCallsEuropePMCOnlyOnceTest() throws IOException {
        assertNotNull(resolver.resolveMetadata("pmid", "pmid1"));
        Mockito.verify(cache, Mockito.times(1)).get(anyString(), anyString());
    }

    private static final String PMC_SEARCH = "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=PMCID:PMC11195135&resultType=core&format=json";
    private static final String PMID_SEARCH = "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=EXT_ID:38915692&resultType=core&format=json";
    private static final String PMC_CONVERTER = "https://pmc.ncbi.nlm.nih.gov/tools/idconv/api/v1/articles/?ids=PMC11195135&format=json";
    private static final String PMID_SUMMARY = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=38915692&retmode=json";
    private static final String DOI_SEARCH = "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=DOI%3A%2210.1101%2F2024.06.10.597395%22%20AND%20SRC%3APPR&resultType=core&format=json";
    private static final String NO_RESULTS = "{\"version\":\"6.9\",\"hitCount\":0,\"resultList\":{\"result\":[]}}";
    private static final String PMC_WITH_DOI = "{\"status\":\"ok\",\"records\":[{\"doi\":\"10.1101/2024.06.10.597395\",\"pmcid\":\"PMC11195135\",\"pmid\":38915692,\"requested-id\":\"PMC11195135\"}]}";
    private static final String PMID_WITH_DOI = "{\"header\":{\"type\":\"esummary\",\"version\":\"0.3\"},\"result\":{\"uids\":[\"38915692\"],\"38915692\":{\"uid\":\"38915692\",\"articleids\":[{\"idtype\":\"pubmed\",\"idtypen\":1,\"value\":\"38915692\"},{\"idtype\":\"doi\",\"idtypen\":3,\"value\":\"10.1101/2024.06.10.597395\"}]}}}";
    private static final String PREPRINT = "{\"version\":\"6.9\",\"hitCount\":1,\"resultList\":{\"result\":[{\"id\":\"PPR865278\",\"source\":\"PPR\",\"doi\":\"10.1101/2024.06.10.597395\",\"title\":\"C-LTMRs mediate wet dog shakes via the spinoparabrachial pathway\",\"firstPublicationDate\":\"2024-06-11\"}]}}";

    @Test
    public void resolvePMCMetadataUsingDoiFallbackTest() throws IOException {
        stubJson(PMC_SEARCH, NO_RESULTS);
        stubJson(PMC_CONVERTER, PMC_WITH_DOI);
        stubJson(DOI_SEARCH, PREPRINT);

        Work work = resolver.resolveMetadata("pmc", "11195135");
        assertPreprint(work);
        assertEquals("pmc", work.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("PMC11195135", work.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        Mockito.verify(cache).get(PMC_CONVERTER, MediaType.APPLICATION_JSON);
        Mockito.verify(cache).get(DOI_SEARCH, MediaType.APPLICATION_JSON);
    }

    @Test
    public void resolvePMIDMetadataUsingDoiFallbackTest() throws IOException {
        stubJson(PMID_SEARCH, NO_RESULTS);
        stubJson(PMID_SUMMARY, PMID_WITH_DOI);
        stubJson(DOI_SEARCH, PREPRINT);

        Work work = resolver.resolveMetadata("pmid", "38915692");
        assertPreprint(work);
        assertEquals("pmid", work.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("38915692", work.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        Mockito.verify(cache).get(PMID_SUMMARY, MediaType.APPLICATION_JSON);
        Mockito.verify(cache).get(DOI_SEARCH, MediaType.APPLICATION_JSON);
    }

    @Test
    public void resolveMetadataWithoutDoiDoesNotSearchPreprintsTest() throws IOException {
        stubJson(PMC_SEARCH, NO_RESULTS);
        stubJson(PMC_CONVERTER, "{\"status\":\"ok\",\"records\":[{\"pmcid\":\"PMC11195135\",\"requested-id\":\"PMC11195135\"}]}");
        stubJson(PMID_SEARCH, NO_RESULTS);
        stubJson(PMID_SUMMARY, "{\"result\":{\"uids\":[\"38915692\"],\"38915692\":{\"uid\":\"38915692\",\"articleids\":[{\"idtype\":\"pubmed\",\"value\":\"38915692\"}]}}}");

        assertNull(resolver.resolveMetadata("pmc", "11195135"));
        assertNull(resolver.resolveMetadata("pmid", "38915692"));
        Mockito.verify(cache).get(PMC_CONVERTER, MediaType.APPLICATION_JSON);
        Mockito.verify(cache).get(PMID_SUMMARY, MediaType.APPLICATION_JSON);
        Mockito.verify(cache, Mockito.never()).get(DOI_SEARCH, MediaType.APPLICATION_JSON);
    }

    @Test
    public void resolveMetadataWhenDoiSearchFailsTest() throws IOException {
        stubJson(PMC_SEARCH, NO_RESULTS);
        stubJson(PMC_CONVERTER, PMC_WITH_DOI);
        when(cache.get(DOI_SEARCH, MediaType.APPLICATION_JSON)).thenThrow(new IOException("Europe PMC unavailable"));

        assertNull(resolver.resolveMetadata("pmc", "11195135"));
        Mockito.verify(cache).get(DOI_SEARCH, MediaType.APPLICATION_JSON);
    }

    @Test
    public void resolveMetadataWhenDoiSearchHasNoResultsTest() throws IOException {
        stubJson(PMID_SEARCH, NO_RESULTS);
        stubJson(PMID_SUMMARY, PMID_WITH_DOI);
        stubJson(DOI_SEARCH, NO_RESULTS);

        assertNull(resolver.resolveMetadata("pmid", "38915692"));
        Mockito.verify(cache).get(DOI_SEARCH, MediaType.APPLICATION_JSON);
    }

    @Test
    public void resolveIdentifiersUsingDoiFallbackTest() throws IOException {
        stubJson(PMC_SEARCH, NO_RESULTS);
        stubJson(PMC_CONVERTER, PMC_WITH_DOI);
        stubJson(PMID_SEARCH, NO_RESULTS);
        stubJson(PMID_SUMMARY, PMID_WITH_DOI);
        stubJson(DOI_SEARCH, PREPRINT);

        assertTrue(resolver.resolve("pmc", "11195135").isResolved());
        assertTrue(resolver.resolve("pmid", "38915692").isResolved());
    }

    private void stubJson(String url, String json) throws IOException {
        when(cache.get(url, MediaType.APPLICATION_JSON))
                .thenAnswer(invocation -> new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }

    private void assertPreprint(Work work) {
        assertNotNull(work);
        assertEquals("C-LTMRs mediate wet dog shakes via the spinoparabrachial pathway", work.getWorkTitle().getTitle().getContent());
        assertEquals("10.1101/2024.06.10.597395", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
    }

    private String getRole(List<ContributorsRolesAndSequences> contributors, Integer contributorsIndex) {
        return contributors.get(contributorsIndex).getRolesAndSequences().get(0).getContributorRole();
    }

    private String getName(List<ContributorsRolesAndSequences> contributors, Integer contributorsIndex) {
        return contributors.get(contributorsIndex).getCreditName().getContent();
    }
}
