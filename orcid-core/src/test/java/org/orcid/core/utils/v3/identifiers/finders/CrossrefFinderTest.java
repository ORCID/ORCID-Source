package org.orcid.core.utils.v3.identifiers.finders;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.utils.ArXivResolverTest;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.finders.DataciteFinder;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.jaxb.model.v3.rc1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffResult;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class CrossrefFinderTest {

    @Mock
    PIDResolverCache cache;
    
    @Resource
    CrossrefFinder finder;
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(finder, "cache", cache);

        when(cache.isHttp200(anyString())).thenReturn(true);

        when(cache.get("https://api.crossref.org/works?filter=orcid:0000-0003-1419-2405", "application/json")).thenAnswer(new Answer<InputStream>() {

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
