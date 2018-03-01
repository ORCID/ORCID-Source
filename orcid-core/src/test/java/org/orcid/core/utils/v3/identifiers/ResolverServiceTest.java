package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ResolverServiceTest {
    
    @Resource
    ResolverService resolver;
    
    @Test
    public void testWithMocks(){
        //create fake resolver cache, use that.
    }
    
    //Commented out.  Only use locally.
    //@Test
    public void workingTests(){
        //GENERIC
        //missing values always false;
        assertFalse(resolver.canResolve("uri", "","http://doi.org/10.6084/m9.figshare.5479792.v1"));
        assertFalse(resolver.canResolve("uri", null,"http://doi.org/10.6084/m9.figshare.5479792.v1"));
        
        //if the value is in the url, try the url
        assertTrue(resolver.canResolve("uri", "orcid.org","https://orcid.org"));
        assertFalse(resolver.canResolve("uri", "MISSING","https://orcid.org"));
        
        //if the value is a url, try that
        assertTrue(resolver.canResolve("uri", "https://orcid.org",null));

        //DOI
        assertTrue(resolver.canResolve("doi", "10.6084/m9.figshare.5479792.v1",null));
        //this should actually be false, as that's not a valid DOI!
        assertTrue(resolver.canResolve("doi", "m9.figshare.5479792.v1","https://doi.org/10.6084/m9.figshare.5479792.v1")); 
        assertFalse(resolver.canResolve("doi", "10.1234/notarealdoi",null));
        assertFalse(resolver.canResolve("doi", "junk",null));
        //junk in the URL of valid ID
        assertTrue(resolver.canResolve("doi", "10.6084/m9.figshare.5479792.v1","junk"));
        //A bit of a trick, but this checks it can handle http->https redirection
        assertTrue(resolver.canResolve("doi", "m9.figshare.5479792.v1","http://doi.org/10.6084/m9.figshare.5479792.v1")); 
        
        //ISBN
        //valid ISBN 10
        assertTrue(resolver.canResolve("isbn", "009177909X",null));
        //valid ISBN 13
        assertTrue(resolver.canResolve("isbn", "9780091779092",null));
        //valid ISBN 13 un-normalized
        assertTrue(resolver.canResolve("isbn", "ISBN:978-0091779092",null));
        //invalid ISBN
        assertFalse(resolver.canResolve("isbn", "9780091779093",null)); //invalid ISBN

        //BIBCODE
        assertTrue(resolver.canResolve("bibcode", "1974AJ.....79..819H",null));
        assertTrue(resolver.canResolve("bibcode", "bibcode:1974AJ.....79..819H",null));
        assertFalse(resolver.canResolve("bibcode", "junk",null));
        //PMC
        assertTrue(resolver.canResolve("pmc", "PMC3820882",null));
        assertTrue(resolver.canResolve("pmc", "3820882",null));
        assertTrue(resolver.canResolve("pmc", "3820882","https://europepmc.org/articles/3820882"));
        assertFalse(resolver.canResolve("pmc", "junk",null));
        //RRID - note they must start with RRID: (created a curie normalizer to ensure prefix exists)
        assertTrue(resolver.canResolve("rrid", "RRID:AB_2203913","https://scicrunch.org/resolver/RRID:AB_2203913"));
        assertTrue(resolver.canResolve("rrid", "RRID:AB_2203913","https://identifiers.org/rrid/RRID:AB_2203913"));
        assertFalse(resolver.canResolve("rrid", "junk",null));
        assertTrue(resolver.canResolve("rrid", "RRID:AB_2203913",null));
        //ARXIV
        assertTrue(resolver.canResolve("arxiv", "1802.05783",null));
        assertFalse(resolver.canResolve("arxiv", "junk","https://arxiv.org/abs/1802.05783"));
        assertTrue(resolver.canResolve("arxiv", "1802","https://arxiv.org/abs/1802.05783"));
        assertFalse(resolver.canResolve("arxiv", "junk",null));        
        //PMID
        assertTrue(resolver.canResolve("pmid", "22791631",null));
        assertTrue(resolver.canResolve("pmid", "PMID:22791631",null));
        assertTrue(resolver.canResolve("pmid", "22791631","https://www.ncbi.nlm.nih.gov/pubmed/22791631"));
        assertFalse(resolver.canResolve("pmid", "junk",null));
    }
    
    @Test
    public void testExists(){
        //this should work, but fails because of http->https not following redirects
        //

    }

}
