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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TemplateManagerTest extends BaseTest {

    @Autowired
    private TemplateManager templateManager;

    @Test
    public void testProcessTemplate() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Declan");
        params.put("word", "cucumber");
        String result = templateManager.processTemplate("test.ftl", params);
        assertNotNull(result);
        assertEquals("Hello, Declan. Did you say cucumber?", result);
    }

}
