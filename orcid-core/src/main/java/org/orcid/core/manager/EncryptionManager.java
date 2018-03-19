package org.orcid.core.manager;

import java.security.NoSuchAlgorithmException;

public interface EncryptionManager {

    String encryptForInternalUse(String stringToEncrypt);

    String decryptForInternalUse(String stringToDecrypt);

    String legacyEncryptForInternalUse(String stringToEncrypt);

    String legacyDecryptForInternalUse(String stringToDecrypt);

    String hashForInternalUse(String raw);

    String sha256Hash(String s) throws NoSuchAlgorithmException;

    boolean hashMatches(String raw, String hash);

    String encryptForExternalUse(String stringToEncrypt);

    String decryptForExternalUse(String stringToDecrypt);

    String encryptForLegacyExternalUse(String stringToEncrypt);

    String decryptForLegacyExternalUse(String stringToDecrypt);

}
