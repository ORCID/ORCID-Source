/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.localization;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipException;

import org.junit.Test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.orcid.core.utils.Classpath;
import org.orcid.core.utils.Classpath.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
public class PropertyFiles {
    ArrayList<Properties> pList = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(PropertyFiles.class);

    @Before
    public void loadProFiles() throws ZipException, IOException {
        // ResourceBundle resources = ResourceBundle.
        FileFilter fF = new FileFilter() {
            public boolean accept(String fileName) {
                return fileName.matches(".*messages[a-zA-Z\\_\\.]*\\.properties$");
            }
        };

        String[] propfiles = Classpath.getClasspathFileNames(fF, true);

        for (String propertiesFile : propfiles) {
            if (!propertiesFile.contains("orcid"))
                continue;
            byte[] encoded = Files.readAllBytes(Paths.get(propertiesFile));

            Charset utf = Charset.forName("UTF-8");
            CharsetDecoder dec = utf.newDecoder();
            dec.onUnmappableCharacter(CodingErrorAction.REPORT);

            String propStr = dec.decode(ByteBuffer.wrap(encoded)).toString();

            Properties prop = new Properties();

            prop.load(new StringReader(propStr));
            pList.add(prop);
        }

    }

    @Test
    public void PropNamesmatch() throws Exception {
        Properties pA = pList.get(0);
        for (int i = 1; i < pList.size(); i++) {
            Set<Object> aSet = pA.keySet();
            Properties pB = pList.get(i);
            for (Object key : pB.keySet()) {
                if (!aSet.contains(key)) {
                    LOG.equals("missing key" + key);
                    throw new Exception("missing key" + key);
                }
                aSet.remove(key);
            }
            if (aSet.size() != 0) {
                LOG.equals("missing key/keys" + aSet.toString());
                throw new Exception("missing key/keys" + aSet.toString());
            }
            assertTrue(aSet.size() == 0);
        }
    }

}
