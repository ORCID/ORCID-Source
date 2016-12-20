/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
