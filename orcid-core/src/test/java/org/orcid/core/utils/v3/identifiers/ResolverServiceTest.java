package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.utils.v3.identifiers.resolvers.ResolutionResult;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

//@RunWith(OrcidJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ResolverServiceTest {
    
    //@Resource
    ResolverService resolver;
    
    //Commented out.  Only use locally.
    //@Test
    public void workingTests(){
        ResolutionResult r = null;
        
        //missing values always false, for any type
        r = resolver.resolve("doi", "");
        assertFalse(r.isResolved());
        assertFalse(r.getAttemptedResolution());
        r = resolver.resolve("uri", null);
        assertFalse(r.isResolved());
        assertFalse(r.getAttemptedResolution());
        r = resolver.resolve("agr", null);
        assertFalse(r.isResolved());
        assertFalse(r.getAttemptedResolution());
        
        //if the value does not have a resolution prefix, and is not a handle or uri, ignore it.
        r = resolver.resolve("agr", "https://orcid.org"); 
        assertFalse(r.isResolved());
        assertFalse(r.getAttemptedResolution());
        r = resolver.resolve("agr", null); 
        assertFalse(r.isResolved());
        assertFalse(r.getAttemptedResolution());

        //if the value is a url, use the url
        r = resolver.resolve("uri", "https://orcid.org");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://orcid.org",r.getResolvedUrl());

        r = resolver.resolve("handle", "https://orcid.org");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://orcid.org",r.getResolvedUrl());

        r = resolver.resolve("uri", "https://orcid.org/content/DOES_NOT_EXIST_404");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());

        r = resolver.resolve("uri", "MISSING");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        
        //if we have a URN, it will fail to resolve :(
        r = resolver.resolve("uri", "urn:whatever");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());        
        
        //if the value has a resolution prefix, normalise if possible, then attempt to resolve
        r = resolver.resolve("doi", "10.6084/m9.figshare.5479792.v1");
        assertTrue(r.isResolved());
        assertEquals("https://doi.org/10.6084/m9.figshare.5479792.v1",r.getResolvedUrl());

        //if the value is a URL, try that BEFORE normalization step (this may change)
        r = resolver.resolve("doi", "https://dx.doi.org/10.6084/m9.figshare.5479792.v1");
        assertTrue(r.isResolved());
        assertEquals("https://dx.doi.org/10.6084/m9.figshare.5479792.v1",r.getResolvedUrl());

        //if the value is a URL but fails, try the normalizaed version
        r = resolver.resolve("doi", "https://dkfsjaldksjfdaksjg.org/10.6084/m9.figshare.5479792.v1");
        assertTrue(r.isResolved());
        assertEquals("https://doi.org/10.6084/m9.figshare.5479792.v1",r.getResolvedUrl());

        //valid DOI, but does not exist
        r = resolver.resolve("doi", "10.1234/DOES_NOT_EXIST_404");
        assertFalse(r.isResolved());
        assertNull(r.getResolvedUrl());
        assertTrue(r.getAttemptedResolution());

        //invalid DOI
        r = resolver.resolve("doi", "XXXX");
        assertFalse(r.isResolved());
        assertNull(r.getResolvedUrl());
        assertTrue(r.getAttemptedResolution());

        //ISBN
        //valid ISBN 10
        r = resolver.resolve("isbn", "009177909X");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://www.worldcat.org/isbn/009177909X",r.getResolvedUrl());
        //valid ISBN 13
        r = resolver.resolve("isbn", "9780091779092");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://www.worldcat.org/isbn/9780091779092",r.getResolvedUrl());
        //valid ISBN 13 un-normalized
        r = resolver.resolve("isbn", "ISBN:978-0091779092");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://www.worldcat.org/isbn/9780091779092",r.getResolvedUrl());
        //valid ISBN that doesn't exist
        r = resolver.resolve("isbn", "9780091779093");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        //invalid ISBN
        r = resolver.resolve("isbn", "9780091779093XXXXX");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        
        //BIBCODE
        r = resolver.resolve("bibcode", "1974AJ.....79..819H");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("http://adsabs.harvard.edu/abs/1974AJ.....79..819H",r.getResolvedUrl());

        r = resolver.resolve("bibcode", "bibcode:1974AJ.....79..819H");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("http://adsabs.harvard.edu/abs/1974AJ.....79..819H",r.getResolvedUrl());

        r = resolver.resolve("bibcode", "junk");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        
        //PMC
        r = resolver.resolve("pmc", "PMC3820882");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://europepmc.org/articles/PMC3820882",r.getResolvedUrl());
        
        r = resolver.resolve("pmc", "3820882");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://europepmc.org/articles/PMC3820882",r.getResolvedUrl());

        r = resolver.resolve("pmc", "junk");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());

        //RRID
        r = resolver.resolve("rrid", "RRID:AB_2203913");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://identifiers.org/rrid/RRID:AB_2203913",r.getResolvedUrl());

        r = resolver.resolve("rrid", "AB_2203913");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://identifiers.org/rrid/RRID:AB_2203913",r.getResolvedUrl());

        //using identifiers.org URL as value
        r = resolver.resolve("rrid", "https://scicrunch.org/resolver/RRID:AB_2203913");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://scicrunch.org/resolver/RRID:AB_2203913",r.getResolvedUrl());

        //PMID:
        r = resolver.resolve("pmid","22791631");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://www.ncbi.nlm.nih.gov/pubmed/22791631",r.getResolvedUrl());

        r = resolver.resolve("pmid","PMID:22791631");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://www.ncbi.nlm.nih.gov/pubmed/22791631",r.getResolvedUrl());

        r = resolver.resolve("pmid", "junk");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        
        
        //ARXIV
        r = resolver.resolve("arxiv", "1802.05783");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://arxiv.org/abs/1802.05783",r.getResolvedUrl());

        r = resolver.resolve("arxiv", "https://arxiv.org/abs/1802.05783");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://arxiv.org/abs/1802.05783",r.getResolvedUrl());

        r = resolver.resolve("arxiv", "arXiv:1802.05783");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://arxiv.org/abs/1802.05783",r.getResolvedUrl());
        
        r = resolver.resolve("arxiv", "arxiv:1802.05783");
        assertTrue(r.isResolved());
        assertTrue(r.getAttemptedResolution());
        assertEquals("https://arxiv.org/abs/1802.05783",r.getResolvedUrl());

        r = resolver.resolve("arxiv", "junk");
        assertFalse(r.isResolved());
        assertTrue(r.getAttemptedResolution());
    }

}