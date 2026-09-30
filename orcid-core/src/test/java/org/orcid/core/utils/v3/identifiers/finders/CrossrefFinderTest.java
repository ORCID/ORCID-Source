package org.orcid.core.utils.v3.identifiers.finders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.jaxb.model.v3.release.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffResult;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * As with {@link DataciteFinderTest}, the network was already faked at the
 * {@link PIDResolverCache} boundary and the Spring context was booted only to
 * obtain the bean, so the finder is now built directly. The Crossref search
 * JSON ({@code /examples/works/finder/crossref.json}, in orcid-test) is still
 * parsed by the real Jackson mapper inside the production class.
 *
 * <p>
 * The {@link DOINormalizer} is a real instance, not a mock, and unlike in
 * DataciteFinderTest it is load-bearing. CrossrefFinder deduplicates with
 * {@code CrossrefItem.getExternalID(norm)}: the normalizer produces the
 * normalized form of each DOI in the response, and it is that form -- not the
 * raw value -- which decides whether the item counts as one the record already
 * holds. {@code testAlreadyHaveAll} depends on it: two of the three existing
 * identifiers it passes in carry {@code https://doi.org/...} and
 * {@code http://dx.doi.org/...} values whose normalized forms are the bare
 * DOIs, and they match the three bare DOIs in the fixture only because both
 * sides are compared normalized. A mock would leave the candidate side
 * unnormalized and the expected count of zero would be settled by the stub
 * rather than by the finder.
 *
 * <p>
 * The {@code cache.isHttp200} stub in the old setup is gone: {@code find} never
 * calls it.
 */
@RunWith(MockitoJUnitRunner.class)
public class CrossrefFinderTest {

    private static final String ENDPOINT = "https://api.crossref.org/works?filter=orcid:";

    private static final String ORCID = "0000-0003-1419-2405";

    @Mock
    private PIDResolverCache cache;

    @InjectMocks
    private CrossrefFinder finder = new CrossrefFinder();

    @Before
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(finder, "norm", new DOINormalizer());
        ReflectionTestUtils.setField(finder, "isEnabled", Boolean.TRUE);
        ReflectionTestUtils.setField(finder, "clientId", "APP-9999999999999901");
        ReflectionTestUtils.setField(finder, "metadataEndpoint", ENDPOINT);

        when(cache.get(ENDPOINT + ORCID, "application/json")).thenAnswer(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return CrossrefFinderTest.class.getResourceAsStream("/examples/works/finder/crossref.json");
            }

        });
    }
    
    
    @Test
    public void testSimple(){
        FindMyStuffResult result = finder.find("0000-0003-1419-2405", new ExternalIDs());
        assertEquals("CrossrefFinder",result.getFinderName());
        assertEquals(3,result.getResults().size());
        assertEquals(3,result.getTotal());
    }
    
    @Test
    public void testAlreadyHaveOne(){
        ExternalID id = new ExternalID();
        id.setType("doi");
        id.setValue("10.7287/peerj.preprints.26505");
        id.setNormalized(new TransientNonEmptyString("10.7287/peerj.preprints.26505"));
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id);
        
        FindMyStuffResult result = finder.find("0000-0003-1419-2405", ids);
        assertEquals("CrossrefFinder",result.getFinderName());
        assertEquals(2,result.getResults().size());
    }
    
    @Test
    public void testAlreadyHaveAll(){
        ExternalID id = new ExternalID();
        id.setType("doi");
        id.setValue("10.7287/peerj.preprints.26505");
        id.setNormalized(new TransientNonEmptyString("10.7287/peerj.preprints.26505"));
        ExternalID id2 = new ExternalID();
        id2.setType("doi");
        id2.setValue("https://doi.org/10.7287/peerj.preprints.26505v1");
        id2.setNormalized(new TransientNonEmptyString("10.7287/peerj.preprints.26505v1"));
        ExternalID id3 = new ExternalID();
        id3.setType("doi");
        id3.setValue("http://dx.doi.org/10.1101/097196");
        id3.setNormalized(new TransientNonEmptyString("10.1101/097196"));
        
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id);
        ids.getExternalIdentifier().add(id2);
        ids.getExternalIdentifier().add(id3);
        FindMyStuffResult result = finder.find("0000-0003-1419-2405", ids);
        assertEquals("CrossrefFinder",result.getFinderName());
        assertEquals(0,result.getResults().size());
    }
}
