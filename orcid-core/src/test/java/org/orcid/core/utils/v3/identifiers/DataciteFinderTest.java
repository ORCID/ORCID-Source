package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.utils.v3.identifiers.finders.DataciteFinder;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffResult;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class DataciteFinderTest {

    @Resource 
    DataciteFinder finder;
    
    @Test
    public void testSimple(){
        FindMyStuffResult result = finder.find("0000-0003-1419-2405", new ExternalIDs());
        assertEquals("datacite",result.getServiceName());
        assertEquals(11,result.getResults().size());
        
    }
}
