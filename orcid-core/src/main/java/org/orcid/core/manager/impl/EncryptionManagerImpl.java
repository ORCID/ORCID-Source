package org.orcid.core.manager.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.time.StopWatch;
import org.jasypt.digest.StringDigester;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.crypto.DesEncrypter;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.encoding.PasswordEncoder;

public class EncryptionManagerImpl implements EncryptionManager, PasswordEncoder, InitializingBean {

    private String passPhraseForInternalEncryption;

    private int iterationCountForInternalEncyrption;

    private PBEStringEncryptor internalEncryptor;

    private PBEStringEncryptor externalEncryptor;

    private PBEStringEncryptor legacyExternalEncryptor;

    private DesEncrypter legacyEncrypterForInternalUse;

    private StringDigester passwordEncryptor;

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionManagerImpl.class);

    @Deprecated
    public void setPassPhraseForInternalEncryption(String passPhraseForInternalEncryption) {
        this.passPhraseForInternalEncryption = passPhraseForInternalEncryption;
    }

    @Deprecated
    public void setIterationCountForInternalEncryption(int iterationCountForInternalEncyrption) {
        this.iterationCountForInternalEncyrption = iterationCountForInternalEncyrption;
    }

    @Required
    public void setInternalEncryptor(PBEStringEncryptor internalEncryptor) {
        this.internalEncryptor = internalEncryptor;
    }

    @Required
    public void setLegacyExternalEncryptor(PBEStringEncryptor legacyExternalEncryptor) {
        this.legacyExternalEncryptor = legacyExternalEncryptor;
    }

    @Required
    public void setExternalEncryptor(PBEStringEncryptor externalEncryptor) {
        this.externalEncryptor = externalEncryptor;
    }

    @Required
    public void setPasswordEncryptor(StringDigester passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
    }

    @Override
    public void afterPropertiesSet() {
        legacyEncrypterForInternalUse = new DesEncrypter(passPhraseForInternalEncryption, iterationCountForInternalEncyrption);
    }

    @Override
    public String encryptForInternalUse(String stringToEncrypt) {
        return internalEncryptor.encrypt(stringToEncrypt);
    }

    @Override
    public String decryptForInternalUse(String stringToDecrypt) {
        return internalEncryptor.decrypt(stringToDecrypt);
    }

    @Override
    @Deprecated
    public String legacyEncryptForInternalUse(String stringToEncrypt) {
        return legacyEncrypterForInternalUse.encrypt(stringToEncrypt);
    }

    @Override
    @Deprecated
    public String legacyDecryptForInternalUse(String stringToDecrypt) {
        return legacyEncrypterForInternalUse.decrypt(stringToDecrypt);
    }

    @Override
    public String hashForInternalUse(String raw) {
        return passwordEncryptor.digest(raw);
    }

    @Override
    public boolean hashMatches(String raw, String hash) {
        return passwordEncryptor.matches(raw, hash);
    }

    @Override
    public String encryptForExternalUse(String stringToEncrypt) {
        return externalEncryptor.encrypt(stringToEncrypt);
    }

	@Override
	public String decryptForExternalUse(String stringToDecrypt) {
		try {
			return externalEncryptor.decrypt(stringToDecrypt);
		} catch (EncryptionOperationNotPossibleException e) {
			return decryptForLegacyExternalUse(stringToDecrypt);
		}
	}

    @Override
    @Deprecated
    public String encryptForLegacyExternalUse(String stringToEncrypt) {
        return legacyExternalEncryptor.encrypt(stringToEncrypt);
    }

    @Override
    @Deprecated
    public String decryptForLegacyExternalUse(String stringToDecrypt) {
        return legacyExternalEncryptor.decrypt(stringToDecrypt);
    }

    /**
     * <p>
     * Encodes the specified raw password with an implementation specific
     * algorithm.
     * </p>
     * <P>
     * This will generally be a one-way message digest such as MD5 or SHA, but
     * may also be a plaintext variant which does no encoding at all, but rather
     * returns the same password it was fed. The latter is useful to plug in
     * when the original password must be stored as-is.
     * </p>
     * <p>
     * The specified salt will potentially be used by the implementation to
     * "salt" the initial value before encoding. A salt is usually a
     * user-specific value which is added to the password before the digest is
     * computed. This means that computation of digests for common dictionary
     * words will be different than those in the backend store, because the
     * dictionary word digests will not reflect the addition of the salt. If a
     * per-user salt is used (rather than a system-wide salt), it also means
     * users with the same password will have different digest encoded passwords
     * in the backend store.
     * </p>
     * <P>
     * If a salt value is provided, the same salt value must be use when calling
     * the {@link #isPasswordValid(String, String, Object)} method. Note that a
     * specific implementation may choose to ignore the salt value (via
     * <code>null</code>), or provide its own.
     * </p>
     * 
     * @param rawPass
     *            the password to encode
     * @param salt
     *            optionally used by the implementation to "salt" the raw
     *            password before encoding. A <code>null</code> value is legal.
     * @return encoded password
     */
    @Override
    public String encodePassword(String rawPass, Object salt) {
        return hashForInternalUse(rawPass);
    }

    /**
     * <p>
     * Validates a specified "raw" password against an encoded password.
     * </p>
     * <P>
     * The encoded password should have previously been generated by
     * {@link #encodePassword(String, Object)}. This method will encode the
     * <code>rawPass</code> (using the optional <code>salt</code>), and then
     * compared it with the presented <code>encPass</code>.
     * </p>
     * <p>
     * For a discussion of salts, please refer to
     * {@link #encodePassword(String, Object)}.
     * </p>
     * 
     * @param encPass
     *            a pre-encoded password
     * @param rawPass
     *            a raw password to encode and compare against the pre-encoded
     *            password
     * @param salt
     *            optionally used by the implementation to "salt" the raw
     *            password before encoding. A <code>null</code> value is legal.
     * @return true if the password is valid , false otherwise
     */
    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        LOGGER.debug("About to start password check");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean result = hashMatches(rawPass, encPass);
        stopWatch.stop();
        LOGGER.debug("Password check took {} ms", stopWatch.getTime());
        return result;
    }

    @Override
    public String sha256Hash(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(s.getBytes());
        return Hex.encodeHexString(md.digest());
    }
    
    @Override
    public String getEmailHash(String email) {
        if (PojoUtil.isEmpty(email)) {
            throw new RuntimeException("Unable to hash null email address");
        }
        
        try {
            return sha256Hash(email.trim().toLowerCase());
        } catch(NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
    } 
}
