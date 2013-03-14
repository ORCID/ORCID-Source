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
package org.orcid.core.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class DesEncrypterTest {

    private DesEncrypter encrypter;

    @Before
    public void before() {
        encrypter = new DesEncrypter("xxxx", 1111);
    }

    @Test
    public void testEncrypt() {
        String s = encrypter.encrypt("belemn1t{");
        assertNotNull(s);
        assertEquals("nc1blrUEGws86HFPJKRmGw==", s);
    }

    @Test
    public void testDescrypt() {
        String str = "1";
        String encrypted = encrypter.encrypt(str);
        String decrypted = encrypter.decrypt(encrypted);
        assertEquals(str, decrypted);
    }

}
