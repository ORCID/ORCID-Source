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
package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.orcid.core.utils.v3.identifiers.normalizers.ISSNNormalizer;

import com.google.common.collect.Lists;

public class ISSNNormalizerTest {

    ISSNNormalizer norm = new ISSNNormalizer();
    
    List<String> tests = Lists.newArrayList(
            "1234-5678",
            "1234-567X",
            "0210-9980 DOI: 10.7203/saitabi.64.7258" ,
            "Print ISSN: 0974–2441  Online ISSN: 2455–3891",
            "09599428 13645501",
            "13886150",
            "0008-5472;1538-7445",
            "ISSN 0121-053X  ISSN en línea 2346-1829",
            "ISSN  0123- 4870   ISSN- E; 0120- 2146",
            "ISSN 0123-4412",
            "0277-786X;978-0-8194-9002-5",
            " (e): 2250-3021, ISSN (p): 2278-8719",
            "EI Accession number: 10179064",
            "ISSN: 2351-8200",
            "ISSN-1870-3984",
            "1434-9949 (Electronic) 0770-3198 (Linking)",
            "1473 - 0111 ",
            "0365-0340 (Print) 1476-3567 (Online)",
            //unrecognised
            "978-85-17-00088-1",
            "1234567",
            "123456789"
            );
    
    List<String> results = Lists.newArrayList(
            "1234-5678",
            "1234-567X",
            "0210-9980" ,
            "0974-2441",
            "0959-9428",
            "1388-6150",
            "0008-5472",
            "0121-053X",
            "0123-4870",
            "0123-4412",
            "0277-786X",
            "2250-3021",
            "1017-9064",
            "2351-8200",
            "1870-3984",
            "1434-9949",
            "1473-0111",
            "0365-0340",
            "",
            "",
            "");
    
    @Test
    public void go(){
        for (int i=0;i<tests.size();i++){
            assertEquals(results.get(i),norm.normalise("issn", tests.get(i)));
        }
    }
}
