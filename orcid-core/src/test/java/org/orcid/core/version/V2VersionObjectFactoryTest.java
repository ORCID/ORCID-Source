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
package org.orcid.core.version;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.record.summary_rc1.FundingSummary;
import org.orcid.jaxb.model.record_rc1.Work;

/**
 * 
 * @author Will Simpson
 *
 */
public class V2VersionObjectFactoryTest extends BaseTest {

    @Resource
    V2VersionObjectFactory v2VersionObjectFactory;

    @Test
    public void testWorkMapping() {
        Work workRc1 = new Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record_rc2.Work);
    }

    @Test
    public void testFundingSummaryMapping() {
        FundingSummary fundingSummaryRc1 = new FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record.summary_rc2.FundingSummary);
    }

}
