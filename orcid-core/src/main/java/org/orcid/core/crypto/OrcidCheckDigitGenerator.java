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

public class OrcidCheckDigitGenerator {

    public static void main(String[] args) {
        String input = args[0];
        String checkDigit = generateCheckDigit(input);
        System.out.println("Input was: " + input);
        System.out.println("Check digit is: " + checkDigit);
        System.out.println("Full ORCID is: " + input + checkDigit);
    }

    /**
     * Generates check digit as per ISO 7064 11,2.
     * 
     */
    public static String generateCheckDigit(String baseDigits) {
        int total = 0;
        for (int i = 0; i < baseDigits.length(); i++) {
            int digit = Character.getNumericValue(baseDigits.charAt(i));
            total = (total + digit) * 2;
        }
        int remainder = total % 11;
        int result = (12 - remainder) % 11;
        return result == 10 ? "X" : String.valueOf(result);
    }

    public static boolean validate(String orcid) {
        String tidyOrcid = orcid.replace("-", "");
        String baseDigits = tidyOrcid.substring(0, tidyOrcid.length() - 1);
        String existingCheckDigit = tidyOrcid.substring(tidyOrcid.length() - 1);
        String correctCheckDigit = generateCheckDigit(baseDigits);
        return existingCheckDigit.equals(correctCheckDigit);
    }

}
