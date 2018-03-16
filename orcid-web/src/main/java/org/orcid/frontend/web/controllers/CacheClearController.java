package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.pojo.ajaxForm.Errors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author rcpeters
 */
@Controller("cacheClearController")
@RequestMapping(value = { "/cacheClear" })
public class CacheClearController extends BaseWorkspaceController {

    @Resource
    private ThirdPartyLinkManager thirdPartyLinkManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private SalesForceManager salesForceManager;

    @RequestMapping(value = "/thirdPartyLinkManager.json", method = RequestMethod.GET)
    public @ResponseBody Errors clearThirdPartyLinkManager() {
        thirdPartyLinkManager.evictAll();
        return new Errors();
    }

    @RequestMapping(value = "/profileEntityCache.json", method = RequestMethod.GET)
    public @ResponseBody Errors clearProfileEntityCacheManager() {
        profileEntityCacheManager.removeAll();
        return new Errors();
    }

    @RequestMapping(value = "/salesForceCache.json", method = RequestMethod.GET)
    public @ResponseBody Errors clearSalesForceCacheManager() {
        salesForceManager.evictAll();
        return new Errors();
    }

}