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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EncryptionManagerTest extends BaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(EncryptionManagerTest.class);

    @Autowired
    private EncryptionManager encryptionManager;

    @Test
    public void testLegacyEncyrptForInternalUse() {
        String result = encryptionManager.legacyEncryptForInternalUse("belemn1t{");
        assertEquals("nc1blrUEGws86HFPJKRmGw==", result);
    }

    @Test
    public void testLegacyDecryptForInternalUse() {
        String result = encryptionManager.legacyDecryptForInternalUse("nc1blrUEGws86HFPJKRmGw==");
        assertEquals("belemn1t{", result);
    }

    @Test
    public void testEncryptAndDecryptForInternalUse() {
        String message = "secret";
        String encrypted = encryptionManager.encryptForInternalUse(message);
        assertNotNull(encrypted);
        String decrypted = encryptionManager.decryptForInternalUse(encrypted);
        assertNotNull(decrypted);
        assertEquals(message, decrypted);
        String encrypted2 = encryptionManager.encryptForInternalUse(message);
        // Is salted, so should be different from before
        assertFalse(encrypted.equals(encrypted2));
    }

    @Test
    public void testEncryptAndDecryptForExternalUse() {
        String message = "email=will@semantico.com&fName=Will&lName=Simpson&sponsor=ORCID&identifier=&institution=Semantico";
        String encrypted = encryptionManager.encryptForExternalUse(message);
        assertNotNull(encrypted);
        assertFalse(encrypted.equals(message));
        String decrypted = encryptionManager.decryptForExternalUse(encrypted);
        assertEquals(message, decrypted);
    }

    @Test
    public void testHashForInternalUse() {
        // This is a terrible password. You'd never actually use it, would you?
        // Would you?
        String raw = "password";
        String hash = encryptionManager.hashForInternalUse(raw);
        LOG.info("Hashed password is: {}", hash);
        assertNotNull(hash);
        assertEquals(108, hash.length());
        assertTrue(encryptionManager.hashMatches(raw, hash));
    }

}
