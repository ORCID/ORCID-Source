package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

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
import org.orcid.core.utils.v3.identifiers.resolvers.DOIResolver;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.pojo.IdentifierType;
import org.orcid.test.TargetProxyHelper;

public class DOIResolverTest {
    @Mock
    PIDNormalizationService normalizationService;

    @Mock
    PIDResolverCache cache;
    
    @Mock
    private IdentifierTypeManager identifierTypeManager;

    @Mock
    protected LocaleManager localeManager;

    private DOIResolver resolver = new DOIResolver();

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(resolver, "normalizationService", normalizationService);
        TargetProxyHelper.injectIntoProxy(resolver, "cache", cache);
        TargetProxyHelper.injectIntoProxy(resolver, "identifierTypeManager", identifierTypeManager);
        TargetProxyHelper.injectIntoProxy(resolver, "localeManager", localeManager);

        when(localeManager.getLocale()).thenReturn(Locale.ENGLISH);
        
        when(normalizationService.normalise(eq("doi"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1).toString();
            }
        });

        when(normalizationService.generateNormalisedURL(eq("doi"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "https://doi.org/" + invocation.getArgument(1).toString();
            }
        });
        
        when(identifierTypeManager.fetchIdentifierTypeByDatabaseName(eq("DOI"), Mockito.any(Locale.class))).thenAnswer(new Answer<IdentifierType>() {
            @Override
            public IdentifierType answer(InvocationOnMock invocation) throws Throwable {
                IdentifierType i = new IdentifierType();
                i.setResolutionPrefix("https://doi.org/");
                return i;
            }
        });

        when(normalizationService.normalise(eq("isbn"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                String argument1 = invocation.getArgument(1).toString();
                if (argument1.contains("/")) {
                    return argument1.substring(argument1.lastIndexOf("/") + 1);
                }
                return argument1;
            }
        });

        when(identifierTypeManager.fetchIdentifierTypeByDatabaseName(eq("ISBN"), Mockito.any(Locale.class))).thenAnswer(new Answer<IdentifierType>() {
            @Override
            public IdentifierType answer(InvocationOnMock invocation) throws Throwable {
                IdentifierType i = new IdentifierType();
                i.setResolutionPrefix("https://www.worldcat.org/isbn/");
                return i;
            }
        });

        when(identifierTypeManager.fetchIdentifierTypeByDatabaseName(eq("ISSN"), Mockito.any(Locale.class))).thenAnswer(new Answer<IdentifierType>() {
            @Override
            public IdentifierType answer(InvocationOnMock invocation) throws Throwable {
                IdentifierType i = new IdentifierType();
                i.setResolutionPrefix("https://www.test.org/issn/");
                return i;
            }
        });
        
        when(cache.isValidDOI(anyString())).thenReturn(true);       

        when(cache.get("https://doi.org/10.000/0000.0000", "application/vnd.citationstyles.csl+json")).thenAnswer(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return DOIResolverTest.class.getResourceAsStream("/examples/works/form_autofill/doi.json");
            }

        });
        
        when(cache.get("https://doi.org/10.000/0000.0001", "application/vnd.citationstyles.csl+json")).thenAnswer(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return DOIResolverTest.class.getResourceAsStream("/examples/works/form_autofill/doi-no-published-date.json");
            }

        });
        
        when(cache.get("https://doi.org/10.000/0000.0002", "application/vnd.citationstyles.csl+json")).thenAnswer(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return DOIResolverTest.class.getResourceAsStream("/examples/works/form_autofill/doi-journal-title-in-container-title.json");
            }

        });
        
        when(cache.get("https://doi.org/10.000/0000.0003", "application/vnd.citationstyles.csl+json")).thenAnswer(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return DOIResolverTest.class.getResourceAsStream("/examples/works/form_autofill/doi-journal-title-in-container-title-short.json");
            }

        });
    }

    @Test
    public void resolveMetadataTest() {
        Work work = resolver.resolveMetadata("doi", "10.000/0000.0000");
        assertEquals("Journal title", work.getJournalTitle().getContent());
        verifyDoi(work, true);        
    }
    
    /** checks published date populated by 'issued' field */
    @Test
    public void resolveMetadataTestNoPublishedDate() {
        Work work = resolver.resolveMetadata("doi", "10.000/0000.0001");
        assertNotNull(work.getPublicationDate());
        assertEquals("2018", work.getPublicationDate().getYear().getValue());
        assertEquals("01", work.getPublicationDate().getMonth().getValue());
        assertEquals("01", work.getPublicationDate().getDay().getValue());
        verifyDoi(work, false);        
    }
    
    /** 
     * checks journal title is populated by 'container-title' field 
     * */
    @Test
    public void resolveMetadataJournalTitleInContainerTitleTest() {
        Work work = resolver.resolveMetadata("doi", "10.000/0000.0002");
        assertEquals("journal-title-in-container-title", work.getJournalTitle().getContent());
        verifyDoi(work, true);
    } 
    
    /** 
     * checks journal title is populated by 'container-title-short' field 
     * */
    @Test
    public void resolveMetadataJournalTitleInContainerTitleShortTest() {
        Work work = resolver.resolveMetadata("doi", "10.000/0000.0003");
        assertEquals("journal-title-in-container-title-short", work.getJournalTitle().getContent());
        verifyDoi(work, true);
    }
    
    private void verifyDoi(Work work, boolean checkPublicationDate) {
        assertNotNull(work);
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals("DOI title", work.getWorkTitle().getTitle().getContent());
        assertEquals("Subtitle", work.getWorkTitle().getSubtitle().getContent());        
        assertEquals("This is a description", work.getShortDescription());
        if(checkPublicationDate) {
            assertEquals("2018", work.getPublicationDate().getYear().getValue());
            assertEquals("12", work.getPublicationDate().getMonth().getValue());
            assertEquals("31", work.getPublicationDate().getDay().getValue());
            assertEquals("http://dx.doi.org/10.000/0000.0000", work.getUrl().getValue());
        }
        
        assertEquals(5, work.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("https://doi.org/10.000/0000.0000", work.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("10.000/0000.0000", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        
        assertEquals("isbn", work.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("https://www.worldcat.org/isbn/ISBN-0000-0000", work.getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("ISBN-0000-0000", work.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
    
        assertEquals("isbn", work.getExternalIdentifiers().getExternalIdentifier().get(2).getType());
        assertEquals("https://www.worldcat.org/isbn/ISBN-0000-0001", work.getExternalIdentifiers().getExternalIdentifier().get(2).getUrl().getValue());
        assertEquals("ISBN-0000-0001", work.getExternalIdentifiers().getExternalIdentifier().get(2).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(2).getRelationship());
    
        assertEquals("issn", work.getExternalIdentifiers().getExternalIdentifier().get(3).getType());
        assertEquals("https://www.test.org/issn/ISSN-0000-0000", work.getExternalIdentifiers().getExternalIdentifier().get(3).getUrl().getValue());
        assertEquals("ISSN-0000-0000", work.getExternalIdentifiers().getExternalIdentifier().get(3).getValue());
        assertEquals(Relationship.PART_OF, work.getExternalIdentifiers().getExternalIdentifier().get(3).getRelationship());
    
        assertEquals("issn", work.getExternalIdentifiers().getExternalIdentifier().get(4).getType());
        assertEquals("https://www.test.org/issn/ISSN-0000-0001", work.getExternalIdentifiers().getExternalIdentifier().get(4).getUrl().getValue());
        assertEquals("ISSN-0000-0001", work.getExternalIdentifiers().getExternalIdentifier().get(4).getValue());
        assertEquals(Relationship.PART_OF, work.getExternalIdentifiers().getExternalIdentifier().get(4).getRelationship());
    }
}
