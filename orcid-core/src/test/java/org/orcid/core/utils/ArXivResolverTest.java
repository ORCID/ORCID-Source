package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.resolvers.ArXivResolver;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.test.TargetProxyHelper;

public class ArXivResolverTest {
    @Mock
    PIDNormalizationService normalizationService;

    @Mock
    PIDResolverCache cache;

    private ArXivResolver resolver = new ArXivResolver();

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(resolver, "normalizationService", normalizationService);
        TargetProxyHelper.injectIntoProxy(resolver, "cache", cache);

        when(normalizationService.normalise(eq("doi"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1).toString();
            }
        });

        when(normalizationService.generateNormalisedURL(eq("doi"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return getUrl("doi", invocation.getArgument(1));
            }
        });

        when(normalizationService.normalise(eq("arxiv"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                String argument1 = invocation.getArgument(1).toString();
                if (argument1.contains("/")) {
                    return argument1.substring(argument1.lastIndexOf("/") + 1);
                }
                return argument1;
            }
        });

        when(normalizationService.generateNormalisedURL(eq("arxiv"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return getUrl("arxiv", invocation.getArgument(1));
            }
        });

        when(cache.isHttp200(anyString())).thenReturn(true);

        when(cache.get("https://export.arxiv.org/api/query?id_list=0000.0000", MediaType.APPLICATION_ATOM_XML)).thenAnswer(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return ArXivResolverTest.class.getResourceAsStream("/examples/works/form_autofill/arxiv.xml");
            }

        });
    }

    private String getUrl(String identifier, String value) {
        switch (identifier) {
        case "doi":
            return "http://dx.doi.org/" + value;
        case "arxiv":
            return "https://arxiv.org/abs/" + value;
        }
        return null;
    }

    @Test
    public void resolveMetadataTest() {
        Work work = resolver.resolveMetadata("arxiv", "0000.0000");
        assertNotNull(work);
        assertEquals("Multiline Title Example", work.getWorkTitle().getTitle().getContent());
        assertEquals("This is a multiline summary example for testing purposes", work.getShortDescription());
        assertEquals("2018", work.getPublicationDate().getYear().getValue());
        assertEquals("01", work.getPublicationDate().getMonth().getValue());
        assertEquals("02", work.getPublicationDate().getDay().getValue());
        assertEquals("Journal title", work.getJournalTitle().getContent());
        assertEquals(2, work.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("arxiv", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("https://arxiv.org/abs/0000.0000", work.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("0000.0000", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://dx.doi.org/10.0000/test/a0000-00000-x", work.getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("10.0000/test/a0000-00000-x", work.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals(WorkType.PREPRINT, work.getWorkType());
    }
}
