package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.resolvers.PubMedResolver;
import org.orcid.jaxb.model.v3.rc2.record.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.pojo.IdentifierType;
import org.orcid.test.TargetProxyHelper;

public class PubMedResolverTest {
    @Mock
    PIDNormalizationService normalizationService;

    @Mock
    PIDResolverCache cache;

    @Mock
    private IdentifierTypeManager identifierTypeManager;

    @Mock
    protected LocaleManager localeManager;

    private PubMedResolver resolver = new PubMedResolver();

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(resolver, "normalizationService", normalizationService);
        TargetProxyHelper.injectIntoProxy(resolver, "cache", cache);
        TargetProxyHelper.injectIntoProxy(resolver, "identifierTypeManager", identifierTypeManager);
        TargetProxyHelper.injectIntoProxy(resolver, "localeManager", localeManager);

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
                return "https://europepmc.org/articles/" + invocation.getArgument(1).toString();
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

        when(cache.isHttp200(anyString())).thenReturn(true);

        when(cache.get("https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=PMCID:pmc1&resultType=core&format=json", MediaType.APPLICATION_JSON))
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
    }

    @Test
    public void resolvePMCMetadataTest() {
        Work work = resolver.resolveMetadata("pmc", "pmc1");
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
    }
}
