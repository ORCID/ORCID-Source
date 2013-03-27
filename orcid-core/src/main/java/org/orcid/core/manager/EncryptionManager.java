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

public interface EncryptionManager {

    String encryptForInternalUse(String stringToEncrypt);

    String decryptForInternalUse(String stringToDecrypt);

    String legacyEncryptForInternalUse(String stringToEncrypt);

    String legacyDecryptForInternalUse(String stringToDecrypt);

    String hashForInternalUse(String raw);

    boolean hashMatches(String raw, String hash);

    String encryptForExternalUse(String stringToEncrypt);

    String decryptForExternalUse(String stringToDecrypt);

}
