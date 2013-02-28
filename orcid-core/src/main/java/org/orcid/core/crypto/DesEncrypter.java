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

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.orcid.core.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * descryptor class.
 */
public class DesEncrypter {
    /** cipher. */
    private Cipher ecipher;
    /** cipher. */
    private Cipher dcipher;

    /** 8-byte Salt. */
    private byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

    /** Iteration count. */
    private int iterationCount = 19;

    private static final Logger LOGGER = LoggerFactory.getLogger(DesEncrypter.class);

    public DesEncrypter(final String passPhrase, final int count) {
        this.iterationCount = count;
        initDesEncrypter(passPhrase);
    }

    public DesEncrypter(final String passPhrase) {
        initDesEncrypter(passPhrase);
    }

    private void initDesEncrypter(final String passPhrase) {
        try {
            // Create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            // Create the ciphers
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (GeneralSecurityException e) {
            LOGGER.trace("DesEncrypter.creation failed", e);
            throw new ApplicationException("DesEncrypter creation failed", e);
        }
    }

    public String encrypt(final String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");
            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            // Encode bytes to base64 to get a string
            return Base64.encodeBase64String(enc);

        } catch (UnsupportedEncodingException e) {
            LOGGER.trace("DesEncrypter unsupported encoding exception", e);
            throw new ApplicationException("DesEncrypter failed - UnsupportedEncodingException ", e);
        } catch (GeneralSecurityException e) {
            LOGGER.trace("DesEncrypter encryption failed", e);
            throw new ApplicationException("DesEncrypter encryption failed - GeneralSecurityException", e);
        }
    }

    public String decrypt(final String str) {
        try {
            // Decode base64 to get bytes
            byte[] dec = Base64.decodeBase64(str);
            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);
            // Decode using utf-8
            return new String(utf8, "UTF8");

        } catch (GeneralSecurityException e) {
            LOGGER.trace("DesEncrypter.decryptionfailed", e);
            throw new ApplicationException("DesEncrypter decryption failed - GeneralSecurityException", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.trace("DesEncrypter.decryptionfailed", e);
            throw new ApplicationException("DesEncrypter decryption failed - UnsupportedEncodingException", e);
        }
    }

}
