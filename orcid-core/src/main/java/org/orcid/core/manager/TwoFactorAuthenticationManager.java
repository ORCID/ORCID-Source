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
package org.orcid.core.manager;

import java.util.List;

public interface TwoFactorAuthenticationManager {

    String getQRCode(String orcid);

    void disable2FA(String orcid);

    boolean userUsing2FA(String orcid);

    void enable2FA(String orcid);
    
    boolean verificationCodeIsValid(String code, String orcid);

    List<String> getBackupCodes(String orcid);

    String getSecret(String orcid);

}
