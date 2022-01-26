package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.orcid.core.utils.v3.identifiers.normalizers.PMCNormalizer;

import com.google.common.collect.Lists;

public class PMCNormalizerTest {

    PMCNormalizer norm = new PMCNormalizer();
    List<String> tests = Lists.newArrayList(
            "22030407",
            "    PMC4731170",
            "pmc4901884",
            "Pmc4582883",
            "PMC 1234567",
            "123456",
            "12345",
            " 12345678",
            "PMID: 25123551 [PubMed - in process] PMCID: PMC4146442 ",
            "PMID: 27930716 PMCID: PMC5145179 DOI: 10.1371/journal.pone.0167675",
            "PMCID: PMC4937647",
            "12690141  PMCID: PMC1744630", //really annoying!
            
            "PMID: 25533354",
            "ISSN: 0363-0269",
            "EMS58897",
            "PMID: 9360540 DOI: 10.1210/jcem.82.11.4385",
            "dx.doi.org/10.1021/jf2029972",
            "Helicobacter ISSN 1523-5378",
            "574: Modeling of Pipeline Corrosion Deterioration Mechanism with a Lévy Process Based on ILI (In‐Line) Inspections",
            "0000-0003-0245-8683",
            "UKMS36054",
            "Biol Res Nursing",
            "pub med",
            "www.sciencedirect.com/science/article/pii/S1741940905000932",
            "NIHMS599470",
            "?"
            );

    //NOTE, known false match. "12690141  PMCID: PMC1744630" - should be PMC1744630, actual 12690141.

    List<String> results = Lists.newArrayList(
            "22030407",
            "4731170",
            "4901884",
            "4582883",
            "1234567",
            "123456",
            "12345",
            "12345678",
            "4146442",
            "5145179",
            "4937647",
            "1744630",
            "","","","","",
            "","","","","",
            "","","",""
            );
    
    @Test
    public void go(){
        for (int i=0;i<tests.size();i++){
            assertEquals(results.get(i),norm.normalise("pmc", tests.get(i)));
        }
    }
}
