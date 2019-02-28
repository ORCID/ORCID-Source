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
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.record_v2.Citation;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.test.TargetProxyHelper;

public class BibtexManagerTest extends BaseTest{
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/WorksEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String ORCID = "0000-0000-0000-0003";
    
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
        Assert.assertTrue(bib.startsWith("@article{Credit_Name"));
        Assert.assertTrue(bib.contains(",\ntitle={SELF PRIVATE},\nauthor={Credit Name},\ndoi={5},\nurl={http://doi.org/5},\nyear={2016}\n}"));
        Assert.assertTrue(bib.contains(",\ntitle={SELF LIMITED},\nauthor={Credit Name},\ndoi={4},\nurl={http://doi.org/4},\nyear={2016}\n}"));
        Assert.assertTrue(bib.endsWith("year={2016}\n}"));
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
    public void testGenerateBibtexForSingleWorkEsaped(){
        Work w = new Work();
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Escapes θ à À È © ë Ö ì"));
        w.setWorkTitle(title);
        w.setWorkType(WorkType.JOURNAL_ARTICLE);
        w.setPutCode(100l);
        String bib = bibtexManager.generateBibtex(ORCID, w);
        Assert.assertEquals("@article{Credit_Name100,\ntitle={Escapes \\texttheta {\\`a} \\`{A} \\`{E} \\textcopyright {\\\"e} {\\\"O} {\\`i}},\nauthor={Credit Name}\n}",bib);
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
