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
package org.orcid.frontend.web.controllers;


import javax.annotation.Resource;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.pojo.ajaxForm.Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author rcpeters
 */
@Controller("cacheClearController")
@RequestMapping(value = {"/cacheClear"})
public class CacheClearController extends BaseWorkspaceController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheClearController.class);

    @Resource
    private ThirdPartyLinkManager thirdPartyLinkManager;


    @RequestMapping(value = "/thirdPartyLinkManager.json", method = RequestMethod.GET)
    public @ResponseBody
    Errors clearThirdPartyLinkManager() {
        thirdPartyLinkManager.evictAll();
        return new Errors();
    }

}