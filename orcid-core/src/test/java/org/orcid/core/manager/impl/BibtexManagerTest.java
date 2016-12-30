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

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.BibtexManager;
import org.orcid.core.manager.DOIManager;
import org.orcid.jaxb.model.record_v2.Citation;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.test.TargetProxyHelper;

public class BibtexManagerTest extends BaseTest{
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/WorksEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String ORCID = "0000-0000-0000-0003";
    
    private static String bibtex = " @article{Credit_Name15, title={SELF PRIVATE}, url={http://doi.org/5}, DOI={5}, author={Credit Name}, year={2016}, month={Jan}}\n,\n @article{Credit_Name14, title={SELF LIMITED}, url={http://doi.org/4}, DOI={4}, author={Credit Name}, year={2016}, month={Jan}}\n,\n @article{Credit_Name13, title={PRIVATE}, url={http://doi.org/3}, DOI={3}, author={Credit Name}, year={2016}, month={Jan}}\n,\n @article{Credit_Name12, title={LIMITED}, url={http://doi.org/2}, DOI={2}, author={Credit Name}, year={2016}, month={Jan}}\n,\n @article{Credit_Name11, title={PUBLIC}, url={http://doi.org/1}, DOI={1}, author={Credit Name}, year={2016}, month={Jan}}\n";
    
    @Resource
    private BibtexManager bibtexManager;
    
    @Mock
    private DOIManager doiManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
        
    }
    
    @Before
    public void before(){
        TargetProxyHelper.injectIntoProxy(bibtexManager, "doiManager", doiManager);
    }
    
    @Test
    public void testGenerateBibtex(){
        String bib = bibtexManager.generateBibtexReferenceList(ORCID);
        Assert.assertEquals(bibtex,bib);
    }
    
    @Test
    public void testGenerateBibtexForSingleWorkFromCitationField(){
        Work w = new Work();
        Citation c = new Citation();
        c.setWorkCitationType(CitationType.BIBTEX);
        c.setCitation("HELLO");
        w.setWorkCitation(c);
        String bib = bibtexManager.generateBibtex(ORCID, w);
        Assert.assertEquals("HELLO",bib);
    }
    
    @Test
    public void testDOIManagerIsInvoked(){
        when(doiManager.fetchDOIBibtex("111")).thenReturn("OK");
        Work w = new Work();
        w.setWorkExternalIdentifiers(new ExternalIDs());
        ExternalID id = new ExternalID();
        id.setType("doi");
        id.setValue("111");
        w.getExternalIdentifiers().getExternalIdentifier().add(id);
        String bib = bibtexManager.generateBibtex(ORCID, w);
        Assert.assertEquals("OK",bib);
    }
}
