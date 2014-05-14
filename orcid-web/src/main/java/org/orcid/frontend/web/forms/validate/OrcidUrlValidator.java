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
package org.orcid.frontend.web.forms.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidUrlValidator extends UrlValidator {

    private static final long serialVersionUID = 1L;

    private static final String AUTHORITY_CHARS_REGEX = "\\p{Alnum}\\-\\.";

    private static final String AUTHORITY_REGEX = "^([" + AUTHORITY_CHARS_REGEX + "]*)(:\\d*)?(.*)?";
    private static final Pattern AUTHORITY_PATTERN = Pattern.compile(AUTHORITY_REGEX);

    private static final int PARSE_AUTHORITY_HOST_IP = 1;

    private static final int PARSE_AUTHORITY_PORT = 2;

    private static final int PARSE_AUTHORITY_EXTRA = 3;

    private static final String PORT_REGEX = "^:(\\d{1,5})$";
    private static final Pattern PORT_PATTERN = Pattern.compile(PORT_REGEX);

    public OrcidUrlValidator(String[] urlValschemes) {
        super(urlValschemes);
    }

    protected boolean isValidAuthority(String authority) {
        if (authority == null) {
            return false;
        }

        Matcher authorityMatcher = AUTHORITY_PATTERN.matcher(authority);
        if (!authorityMatcher.matches()) {
            return false;
        }

        String hostLocation = authorityMatcher.group(PARSE_AUTHORITY_HOST_IP);
        // check if authority is hostname or IP address:
        // try a hostname first since that's much more likely
        OrcidDomainValidator domainValidator = new OrcidDomainValidator();
        if (!domainValidator.isValid(hostLocation)) {
            // try an IP address
            InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();
            if (!inetAddressValidator.isValid(hostLocation)) {
                // isn't either one, so the URL is invalid
                return false;
            }
        }

        String port = authorityMatcher.group(PARSE_AUTHORITY_PORT);
        if (port != null) {
            if (!PORT_PATTERN.matcher(port).matches()) {
                return false;
            }
        }

        String extra = authorityMatcher.group(PARSE_AUTHORITY_EXTRA);
        if (extra != null && extra.trim().length() > 0) {
            return false;
        }

        return true;
    }

}
