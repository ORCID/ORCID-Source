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
 * The network was already faked here: {@link PIDResolverCache} is the only
 * thing in {@link DataciteFinder} that opens a connection, and the old test
 * mocked it and fed it a canned response. The Spring context was booted purely
 * to obtain the bean, so this now builds the finder directly.
 *
 * <p>
 * What is under test is the parse-and-deduplicate step: read the DataCite
 * search JSON, drop the DOIs the record already holds, report the total. The
 * fixture ({@code /examples/works/finder/datacite.json}, in orcid-test) is a
 * real captured DataCite response and is what makes the assertions mean
 * something, so it is still parsed by a real Jackson {@code ObjectMapper}
 * inside the production class.
 *
 * <p>
 * Two stubs from the old setup are gone because they were dead.
 * {@code cache.isHttp200} is never called by {@code find}. And the
 * {@code DOINormalizer} stub was never consulted either: DataciteFinder builds
 * its comparison ExternalID from the raw {@code doi} attribute
 * ({@code DataciteSimpleWorkAttributes.getExternalID()} takes no normalizer),
 * unlike CrossrefFinder which does normalise. A real DOINormalizer is set on
 * the field anyway, so the test does not start passing for the wrong reason if
 * the production class ever begins to use it.
 *
 * <p>
 * The three {@code @Value} settings are the ones in
 * {@code orcid-test/.../test-core.properties}, which is where the Spring run
 * read them from; {@code find} returns an empty result unless all three are
 * present, so they are not decoration.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataciteFinderTest {

    private static final String ENDPOINT = "https://api.datacite.org/works?query=";

    private static final String ORCID = "0000-0003-1419-2405";

    @Mock
    private PIDResolverCache cache;

    @InjectMocks
    private DataciteFinder finder = new DataciteFinder();

    @Before
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(finder, "norm", new DOINormalizer());
        ReflectionTestUtils.setField(finder, "isEnabled", Boolean.TRUE);
        ReflectionTestUtils.setField(finder, "clientId", "APP-9999999999999901");
        ReflectionTestUtils.setField(finder, "metadataEndpoint", ENDPOINT);

        when(cache.get(ENDPOINT + ORCID, "application/json")).thenAnswer(new Answer<InputStream>() {

            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return DataciteFinderTest.class.getResourceAsStream("/examples/works/finder/datacite.json");
            }

        });
    }
    
    
    @Test
    public void testSimple(){
        FindMyStuffResult result = finder.find("0000-0003-1419-2405", new ExternalIDs());
        assertEquals("DataciteFinder",result.getFinderName());
        assertEquals(25,result.getResults().size());
        assertEquals(95,result.getTotal());
    }
    
    @Test
    public void testAlreadyHaveOne(){
        ExternalID id = new ExternalID();
        id.setType("doi");
        id.setValue("10.5438/7rxd-s8a3");
        id.setNormalized(new TransientNonEmptyString("10.5438/7rxd-s8a3"));
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id);
        
        FindMyStuffResult result = finder.find("0000-0003-1419-2405", ids);
        assertEquals("DataciteFinder",result.getFinderName());
        assertEquals(24,result.getResults().size());
    }
    
    @Test
    public void testAlreadyHaveTwo(){
        ExternalID id = new ExternalID();
        id.setType("doi");
        id.setValue("10.5438/7rxd-s8a3");
        id.setNormalized(new TransientNonEmptyString("10.5438/7rxd-s8a3"));
        ExternalID id2 = new ExternalID();
        id2.setType("doi");
        id2.setValue("10.6084/m9.figshare.5821578.v1");
        id2.setNormalized(new TransientNonEmptyString("10.6084/m9.figshare.5821578.v1"));
        
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id);
        ids.getExternalIdentifier().add(id2);
        FindMyStuffResult result = finder.find("0000-0003-1419-2405", ids);
        assertEquals("DataciteFinder",result.getFinderName());
        assertEquals(23,result.getResults().size());
    }
}
