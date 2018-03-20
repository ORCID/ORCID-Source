package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.orcid.core.utils.v3.identifiers.normalizers.BibcodeNormalizer;

import com.google.common.collect.Lists;

public class BibcodeNormalizerTest {

    BibcodeNormalizer norm = new BibcodeNormalizer();
    List<String> tests = Lists.newArrayList(
            "123456789.A23456789",
            "123456789.A23456789 ",
            " 123456789.A23456789",
            " 123456789.A23456789 ",
            "Bibcode: 123456789.A23456789 ",
            "BIBCODE:  123456789.A23456789 ",
            "bibcode 123456789.A23456789 ",
            //invalid
            " 123456789.A2345678 ",
            " 123456789. A23456789 ",
            "ABCD56789.A23456789"
            );
    
    List<String> results = Lists.newArrayList(
            "123456789.A23456789",
            "123456789.A23456789",
            "123456789.A23456789",
            "123456789.A23456789",
            "123456789.A23456789",
            "123456789.A23456789",
            "123456789.A23456789",
            "",
            "",
            ""
            );
    
    @Test
    public void go(){
        for (int i=0;i<tests.size();i++){
            assertEquals(results.get(i),norm.normalise("bibcode", tests.get(i)));
        }
    }
}
