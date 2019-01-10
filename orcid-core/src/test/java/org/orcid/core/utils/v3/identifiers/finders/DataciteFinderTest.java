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
import org.orcid.jaxb.model.v3.rc2.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffResult;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class DataciteFinderTest {

    @Mock
    DOINormalizer norm;

    @Mock
    PIDResolverCache cache;
    
    @Resource
    DataciteFinder finder;
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(finder, "norm", norm);
        TargetProxyHelper.injectIntoProxy(finder, "cache", cache);

        when(norm.normalise(eq("doi"), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1).toString();
            }
        });

        when(cache.isHttp200(anyString())).thenReturn(true);

        when(cache.get("https://api.datacite.org/works?query=0000-0003-1419-2405", "application/json")).thenAnswer(new Answer<InputStream>() {

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
