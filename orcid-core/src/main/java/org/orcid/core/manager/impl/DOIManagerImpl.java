package org.orcid.core.manager.impl;

import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;

import jakarta.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.DOIManager;

@Transactional(value = "transactionManager")
public class DOIManagerImpl implements DOIManager{
    
    @Resource(name = "doiBibtexCache")
    private Cache<URL, String> doiBibtexCache;
        
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
            return doiBibtexCache.get(doiURL);
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
