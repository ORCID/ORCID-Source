package org.orcid.core.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.crypto.OrcidCheckDigitGenerator;
import org.orcid.core.togglz.Features;
import org.togglz.junit.TogglzRule;

/**
 * @author Will Simpson (will) Date: 15/12/2011
 */

public class OrcidGenerationManagerTest extends BaseTest {

    @Resource
    private OrcidGenerationManager orcidGenerationManager;
    
    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    private static final Logger logger = Logger.getLogger(OrcidGenerationManagerTest.class);

    @Test    
    public void testCreateNewOrcidV2() {
        Set<String> orcids = new HashSet<String>();
        for (int i = 0; i < 1000000; i++) {            
            String orcid = orcidGenerationManager.createNewOrcid();

            assertNotNull("ORCID is null", orcid);
            assertTrue("ORCID is in wrong format " + orcid, orcid.matches("(\\d{4}-){3}\\d{3}[\\dX]"));
            assertTrue("ORCID has invalid check character " + orcid, OrcidCheckDigitGenerator.validate(orcid));
            assertFalse("ORCID has already been used " + orcid + " number of elements cached: " + orcids.size(), orcids.contains(orcid));

            String baseDigits = orcid.substring(0, orcid.length() - 1).replace("-", "");
            long numericOrcid = Long.valueOf(baseDigits);
            assertTrue("Numeric value of ORCID is too low " + orcid, numericOrcid >= OrcidGenerationManager.ORCID_BASE_V2_MIN);
            assertTrue("Numeric value of ORCID is too high " + orcid, numericOrcid <= OrcidGenerationManager.ORCID_BASE_V2_MAX);

            orcids.add(orcid);
            logger.info("Got ORCID = " + orcid);
        }
    }
}
