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
package org.orcid.password.constants;

/**
 * Class denoting the constants used for Orcid passwords
 * 
 * @author jamesb
 * 
 */
public class OrcidPasswordConstants {

    private static char[] ENTIRE_PASSWORD_CHARS_RANGE;

    //one digit, one character (including non-us ascii), mix the rest...
    public static final String ORCID_PASSWORD_REGEX = "(?=.{8,})(?=.*\\d)(?=.*\\D)(?=.*\\w).*";

    public static final String UNESCAPED_SYMBOL_RANGE = "!@#$%^*()[]~'{}|&_]";

    public static final String CHAR_CLASS_NUMBERS = "0123456789";

    public static final String LOWERCASE_ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final char[] getEntirePasswordCharsRange() {
        if (ENTIRE_PASSWORD_CHARS_RANGE == null) {

            return (UNESCAPED_SYMBOL_RANGE + CHAR_CLASS_NUMBERS + LOWERCASE_ALPHABET + UPPERCASE_ALPHABET).toCharArray();
        }

        return ENTIRE_PASSWORD_CHARS_RANGE;
    }

    public static final String PASSWORD_REGEX_MESSAGE = "Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol";

}
