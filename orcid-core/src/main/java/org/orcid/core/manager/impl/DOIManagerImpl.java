package org.orcid.core.manager.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Resource;

import org.orcid.core.manager.DOIManager;

import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

public class DOIManagerImpl implements DOIManager{
    
    @Resource(name = "doiBibtexCache")
    private SelfPopulatingCache doiBibtexCache;
        
    @Override
    public String fetchDOIBibtex(String doi) {
        try {
            URL doiURL;
            if (doi.startsWith("http"))
                doiURL = new URL(doi);
            else if (doi.startsWith("doi") || doi.startsWith("dx"))
                doiURL = new URL("http://"+doi);
            else
                doiURL = new URL("http://doi.org/"+doi);
            return (String) doiBibtexCache.get(doiURL).getObjectValue();
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
