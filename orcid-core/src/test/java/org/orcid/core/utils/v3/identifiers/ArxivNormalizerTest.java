package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.orcid.core.utils.v3.identifiers.normalizers.ArxivNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.PMCNormalizer;

import com.google.common.collect.Lists;

public class ArxivNormalizerTest {

    ArxivNormalizer norm = new ArxivNormalizer();
    List<String> tests = Lists.newArrayList(
            "0704.0001",
            "0704.0001v1",
            "0704.0001v2",
            "arxiv:0704.0001",
            "arXiv:0704.0001v1",
            "hep-th/9901001",
            "hep-th/9901001v1",
            "math.CA/0611800v2",
            "ARXIV:hep-th/9901001",
            "arxiv:math.CA/0611800v2",
            "arXiv:physics/9901001v1 [physics.optics] 1 Jan 1999",
            "arXiv:0706.0002v3 [astro-ph] 15 Mar 2008",
            "blah blah blah 0706.0002v3 [astro-ph] 15 Mar 2008",
            "219812y arxiv:math.CA/0611800v2"
            );

    List<String> results = Lists.newArrayList(
            "arXiv:0704.0001",
            "arXiv:0704.0001v1",
            "arXiv:0704.0001v2",
            "arXiv:0704.0001",
            "arXiv:0704.0001v1",
            "arXiv:hep-th/9901001",
            "arXiv:hep-th/9901001v1",
            "arXiv:math.CA/0611800v2",
            "arXiv:hep-th/9901001",
            "arXiv:math.CA/0611800v2",
            "arXiv:physics/9901001v1",
            "arXiv:0706.0002v3",
            "arXiv:0706.0002v3",
            "arXiv:math.CA/0611800v2"
            );
    
    @Test
    public void go(){
        for (int i=0;i<tests.size();i++){
            assertEquals(results.get(i),norm.normalise("arxiv", tests.get(i)));
        }
    }
}
