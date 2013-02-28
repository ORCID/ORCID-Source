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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.crypto.OrcidCheckDigitGenerator;
import org.springframework.test.annotation.Rollback;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Will Simpson (will) Date: 15/12/2011
 */

public class OrcidGenerationManagerTest extends BaseTest {

    @Resource
    private OrcidGenerationManager orcidGenerationManager;

    private static final Logger logger = Logger.getLogger(OrcidGenerationManagerTest.class);

    @Test
    @Rollback
    public void testCreateNewOrcid() {
        Set<String> orcids = new HashSet<String>();
        for (int i = 0; i < 20000; i++) {
            String orcid = orcidGenerationManager.createNewOrcid();

            assertNotNull("ORCID is null", orcid);
            assertTrue("ORCID is in wrong format " + orcid, orcid.matches("(\\d{4}-){3}\\d{3}[\\dX]"));
            assertTrue("ORCID has invalid check character " + orcid, OrcidCheckDigitGenerator.validate(orcid));
            assertFalse("ORCID has already been used " + orcid, orcids.contains(orcid));

            String baseDigits = orcid.substring(0, orcid.length() - 1).replace("-", "");
            long numericOrcid = Long.valueOf(baseDigits);
            assertTrue("Numeric value of ORCID is too low " + orcid, numericOrcid >= OrcidGenerationManager.ORCID_BASE_MIN);
            assertTrue("Numeric value of ORCID is too high " + orcid, numericOrcid <= OrcidGenerationManager.ORCID_BASE_MAX);

            orcids.add(orcid);
            logger.info("Got ORCID = " + orcid);
        }
    }

}
