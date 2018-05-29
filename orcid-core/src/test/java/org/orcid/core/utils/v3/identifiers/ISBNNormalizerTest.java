package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.orcid.core.utils.v3.identifiers.normalizers.ISBNNormalizer;

import com.google.common.collect.Lists;

public class ISBNNormalizerTest {

    ISBNNormalizer norm = new ISBNNormalizer();
    List<String> tests = Lists.newArrayList(
            "0168-132X/n0-7923-4604-1",
            "0065-3071;978-3-8055-8113-4",
            "\" ISBN: 92-828-5179-6 ISSN: 1018-5593\"",
            "0-444-51034-6 (cloth);0531-5131",
            "(10) 1-4438-3289-8",
            "(13): 978-84-939843-2-8",
            "(978) 963 301 489 1",
            "ISBN-10: 0470860782",
            "ISBN: 972-829-898-6",
            "\" ISBN: 978-972-789-400-0. \"",
            "{ISBN} 1-86094-191-5",
            "ISBN: 978385476-449-6",
            "Print 978-3-642-22940-4; Online 978-3-642-22941-1",
            "978-1-4939-2530-8 978-1-4939-2531-5",
            "\" 9781136218125 1136218122 9780203096437 0203096436\"",
            "\"ISBN-13: 978-3-8443-1057-3, ISBN-10: 3844310576, EAN: 9783844310573\"",
            "{981-238-860-5}",
            "^\\^^9789185245313",
            "Vol. 4 ISBN 951-817-386-9.\"",
            "\"9780415801744 (hardback acid-free paper)",
            "\"ISBN 978-3-85132-675-8, 2012\"",
            "\"Palgrave 2012, paperback, 978-0-230-27567-6 \"",
            "國際標準書號:1572731109",
            "國際標準書號:157273110X",
            "國際標準書號:157273110x",
            "ISBN: 123-456-7-89x junk",
            //unrecognised
            "\"0065-2113",
            "12345678901",
            "123456789");
    
    List<String> results = Lists.newArrayList(
            "0792346041",
            "9783805581134",
            "9282851796",
            "0444510346",
            "1443832898",
            "9788493984328",
            "9633014891",
            "0470860782",
            "9728298986",
            "9789727894000",
            "1860941915",
            "9783854764496",
            "9783642229404",
            "9781493925308",
            "9781136218125",
            "9783844310573",
            "9812388605",
            "9789185245313",
            "9518173869",
            "9780415801744",
            "9783851326758",
            "9780230275676",
            "1572731109",
            "157273110X",
            "157273110X",
            "123456789X",
            "",
            "",
            "");
    
    @Test
    public void go(){
        for (int i=0;i<tests.size();i++){
            assertEquals(results.get(i),norm.normalise("isbn", tests.get(i)));
        }
    }
}
