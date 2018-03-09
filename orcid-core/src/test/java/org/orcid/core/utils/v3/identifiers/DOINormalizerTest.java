package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;

import com.google.common.collect.Lists;

public class DOINormalizerTest {

    DOINormalizer norm = new DOINormalizer();
    List<String> tests = Lists.newArrayList(
            "10.123/123",
            "https://dx.doi.org/10.123/123",
            "http://doi.org/10.123/123",
            "S0022-2828(12)00261-1 [pii]\n10.1016/j.yjmcc.2012.07.013" ,
            " 10.1051/0004-6361/201424908",
            "052029\n10.1088/1742-6596/635/5/052029",
            " doi:10.1016/B978-0-12-384947-2.00376-7",
            "S0901-5027(13)00256-7 [pii] 10.1016/j.ijom.2013.05.017 [doi]\n10.1016/j.ijom.2013.05.017. Epub 2013 Jun 28." ,
            "dx.doi.org/10.24273/JGEET.2016.11.9",
            "Br J Sports Med 2013;47:e3 doi:10.1136/bjsports-2013-092558.24",
            "ISSN 2040-4689 10.1386/crre.8.1.141_7",
            "doi:10.1016/j.foodqual.2016.02.012",
            "DOI: 10.1039/c5ra23133g",
            "DOI 10.1007/978-981-10-5520-1_60",
            "DOI:http://dx.doi.org/10.1103/PhysRevApplied.4.014012",
            " DOI 10.4236/ojgen.2013.33021 ",
            "Doi:10.3153/jfscom.201420",
            "http://10.1016/j.lrp.2014.03.001",
            "http:/dx.doi.org/10.5965/1414573101242015147",
            "http://www.dx.doi.org/10.5935/1415-2762.20160006",
            "http://scitation.aip.org/content/aip/journal/jcp/115/11/10.1063/1.1395625",
            "https://doi.org/10.1049/el:20000645",
            "https://doi.org/10.1016/S0920-5632(02)80001-9",
            "10.1016/j.ijom.2013.05.017.",
            //these do not match
            "ijiv11i2a8",
            "doi:10.1038",
            "papers3://publication/doi/10.1038",
            "Unisinos - 10.4013 /cld.2016.141.04"
            );
    
    List<String> results = Lists.newArrayList(
            "10.123/123",
            "10.123/123",
            "10.123/123",
            "10.1016/j.yjmcc.2012.07.013" ,
            "10.1051/0004-6361/201424908",
            "10.1088/1742-6596/635/5/052029",
            "10.1016/B978-0-12-384947-2.00376-7",
            "10.1016/j.ijom.2013.05.017" ,
            "10.24273/JGEET.2016.11.9",
            "10.1136/bjsports-2013-092558.24",
            "10.1386/crre.8.1.141_7",
            "10.1016/j.foodqual.2016.02.012",
            "10.1039/c5ra23133g",
            "10.1007/978-981-10-5520-1_60",
            "10.1103/PhysRevApplied.4.014012",
            "10.4236/ojgen.2013.33021",
            "10.3153/jfscom.201420",
            "10.1016/j.lrp.2014.03.001",
            "10.5965/1414573101242015147",
            "10.5935/1415-2762.20160006",
            "10.1063/1.1395625",
            "10.1049/el:20000645",
            "10.1016/S0920-5632(02)80001-9",
            "10.1016/j.ijom.2013.05.017",
            //these do not match
            "","","",""/*
            "ijiv11i2a8",
            "doi:10.1038",
            "papers3://publication/doi/10.1038",
            "Unisinos - 10.4013 /cld.2016.141.04"*/
            );
    
    @Test
    public void go(){
        for (int i=0;i<tests.size();i++){
            assertEquals(results.get(i),norm.normalise("doi", tests.get(i)));
        }
    }
}
