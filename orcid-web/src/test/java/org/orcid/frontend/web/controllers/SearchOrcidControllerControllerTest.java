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
package org.orcid.frontend.web.controllers;

import static org.mockito.Mockito.mock;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidSearchManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml" })
public class SearchOrcidControllerControllerTest {

    @Resource(name = "searchOrcidController")
    SearchOrcidController searchOrcidController;

    private OrcidSearchManager orcidSearchManager;

    @Before
    public void setupDependencies() throws Exception {
        SearchOrcidController.FRONTEND_WEB_SEARCH_REQUESTS.clear();
        SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_NONE_FOUND.clear();
        SearchOrcidController.FRONTEND_WEB_SEARCH_RESULTS_FOUND.clear();
        orcidSearchManager = mock(OrcidSearchManager.class);
        searchOrcidController.setOrcidSearchManager(orcidSearchManager);
    }

}
