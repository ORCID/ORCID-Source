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
package org.orcid.jaxb.model.message;

import static org.junit.Assert.*;

import org.junit.Test;

public class VisibilityTest {

    @Test
    public void testIsMoreRestrictiveThan() {
        assertFalse(Visibility.SYSTEM.isMoreRestrictiveThan(Visibility.SYSTEM));
        assertTrue(Visibility.SYSTEM.isMoreRestrictiveThan(Visibility.PRIVATE));
        assertTrue(Visibility.SYSTEM.isMoreRestrictiveThan(Visibility.LIMITED));
        assertTrue(Visibility.SYSTEM.isMoreRestrictiveThan(Visibility.REGISTERED_ONLY));
        assertTrue(Visibility.SYSTEM.isMoreRestrictiveThan(Visibility.PUBLIC));
        assertTrue(Visibility.SYSTEM.isMoreRestrictiveThan(null));

        assertFalse(Visibility.PRIVATE.isMoreRestrictiveThan(Visibility.SYSTEM));
        assertFalse(Visibility.PRIVATE.isMoreRestrictiveThan(Visibility.PRIVATE));
        assertTrue(Visibility.PRIVATE.isMoreRestrictiveThan(Visibility.LIMITED));
        assertTrue(Visibility.PRIVATE.isMoreRestrictiveThan(Visibility.REGISTERED_ONLY));
        assertTrue(Visibility.PRIVATE.isMoreRestrictiveThan(Visibility.PUBLIC));
        assertTrue(Visibility.PRIVATE.isMoreRestrictiveThan(null));

        assertFalse(Visibility.LIMITED.isMoreRestrictiveThan(Visibility.LIMITED));
        assertFalse(Visibility.LIMITED.isMoreRestrictiveThan(Visibility.PRIVATE));
        assertFalse(Visibility.LIMITED.isMoreRestrictiveThan(Visibility.LIMITED));
        assertTrue(Visibility.LIMITED.isMoreRestrictiveThan(Visibility.REGISTERED_ONLY));
        assertTrue(Visibility.LIMITED.isMoreRestrictiveThan(Visibility.PUBLIC));
        assertTrue(Visibility.LIMITED.isMoreRestrictiveThan(null));

        assertFalse(Visibility.REGISTERED_ONLY.isMoreRestrictiveThan(Visibility.REGISTERED_ONLY));
        assertFalse(Visibility.REGISTERED_ONLY.isMoreRestrictiveThan(Visibility.PRIVATE));
        assertFalse(Visibility.REGISTERED_ONLY.isMoreRestrictiveThan(Visibility.LIMITED));
        assertFalse(Visibility.REGISTERED_ONLY.isMoreRestrictiveThan(Visibility.REGISTERED_ONLY));
        assertTrue(Visibility.REGISTERED_ONLY.isMoreRestrictiveThan(Visibility.PUBLIC));
        assertTrue(Visibility.REGISTERED_ONLY.isMoreRestrictiveThan(null));

        assertFalse(Visibility.PUBLIC.isMoreRestrictiveThan(Visibility.PUBLIC));
        assertFalse(Visibility.PUBLIC.isMoreRestrictiveThan(Visibility.PRIVATE));
        assertFalse(Visibility.PUBLIC.isMoreRestrictiveThan(Visibility.LIMITED));
        assertFalse(Visibility.PUBLIC.isMoreRestrictiveThan(Visibility.REGISTERED_ONLY));
        assertFalse(Visibility.PUBLIC.isMoreRestrictiveThan(Visibility.PUBLIC));
        assertTrue(Visibility.PUBLIC.isMoreRestrictiveThan(null));
    }

}
