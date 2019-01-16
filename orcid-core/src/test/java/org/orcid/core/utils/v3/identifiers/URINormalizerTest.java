package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.orcid.core.utils.v3.identifiers.normalizers.URINormalizer;

import com.google.common.collect.Lists;

public class URINormalizerTest {

    URINormalizer norm = new URINormalizer();
    List<String> tests = Lists.newArrayList(
            "https://bbc.co.uk",
            "http://bbc.co.uk",
            "https://www.bbc.co.uk",
            "http://www.bbc.co.uk",
            "bbc.co.uk",
            "www.bbc.co.uk");
    
    List<String> results = Lists.newArrayList(
            "https://bbc.co.uk",
            "http://bbc.co.uk",
            "https://www.bbc.co.uk",
            "http://www.bbc.co.uk",
            "http://bbc.co.uk",
            "http://www.bbc.co.uk");
    
    @Test
    public void go(){
        for (int i=0;i<tests.size();i++){
            assertEquals(results.get(i),norm.normalise("uri", tests.get(i)));
        }
    }
}
