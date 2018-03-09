package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.orcid.core.utils.v3.identifiers.normalizers.PMIDNormalizer;

import com.google.common.collect.Lists;

public class PMIDNormalizerTest {

    PMIDNormalizer norm = new PMIDNormalizer();
    List<String> tests = Lists.newArrayList(
           " 23684069 ",
           "25392386 ",
           " 22301346",
           "PMID: 10082085",
           "PMID: 21054200 [PubMed - indexed for MEDLINE]",
           "PMID: 9528837 [PubMed - indexed for MEDLINE] PMCID: PMC2150087",
           "PMID: 28005710 ",
           "PMID:   28005710", 
           "      28005710 ",
           "PubMed PMID: 19006866",
           "doi: 10.1007/s10827-015-0564-6. Epub 2015 Apr 24. PMID: 25904470", 
           "http://europepmc.org/abstract/med/26687626",    
           "http://europepmc.org/abstract/MED/26687626", 
           "http://www.ncbi.nlm.nih.gov/pubmed/16475611",
           
           "J Psychopharmacol. 2014 Oct;28(10):935-46. doi: 10.1177/0269881114542856. Epub 2014 Jul 16.",
           "PMC27092172",
           "0000-0003-0245-8683",
           "ISI:000232786000040",
           "10.3991%2Fijet.v3i0.551",
           "org/10.1016/j.psychres.2017.05.027",
           "MR2098779",
           "orci,org/0000-0003-2246-8555" 
            );

    //NOTE, known false match. "12690141  PMCID: PMC1744630" - should be PMC1744630, actual 12690141.

    List<String> results = Lists.newArrayList(
           "23684069",
           "25392386",
           "22301346",
           "10082085",
           "21054200",
           "9528837",
           "28005710",
           "28005710", 
           "28005710",
           "19006866",
           "25904470", 
           "26687626",
           "26687626",
           "16475611",

           "","","","",""
           ,"","",""
            );
    
    @Test
    public void go(){
        for (int i=0;i<tests.size();i++){
            assertEquals(results.get(i),norm.normalise("pmid", tests.get(i)));
        }
    }
}
