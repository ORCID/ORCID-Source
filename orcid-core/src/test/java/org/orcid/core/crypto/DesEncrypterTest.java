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
