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
package org.orcid.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * 2011-2012 ORCID
 * </p>
 * <p>
 * User: Declan Newman (declan) Date: 10/02/2012
 * </p>
 */
public class OrcidStringUtilsTest {

    @Test
    public void testIsValidOrcid() {
        assertTrue(OrcidStringUtils.isValidOrcid("4444-4444-4444-4446"));
        // assertFalse(OrcidStringUtils.isValidOrcid("4444-4444-4444-444X"));

    }
}
