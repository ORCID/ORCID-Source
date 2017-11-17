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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.core.utils.v3.OrcidIdentifierUtils;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.DelegateForm;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for delegate permissions that have been granted TO the current
 * user
 * 
 * @author Will Simpson
 */
@Controller("manageDelegatorsController")
@RequestMapping(value = { "/delegators" })
public class ManageDelegatorsController extends BaseWorkspaceController {

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private SourceNameCacheManager sourceNameCacheManager;

    @Resource
    private GivenPermissionToManagerReadOnly givenPermissionToManagerReadOnly;

    @Resource
    private OrcidIdentifierUtils orcidIdentifierUtils;

    @RequestMapping
    public ModelAndView manageDelegators() {
        return new ModelAndView("manage_delegators");
    }

    @RequestMapping(value = "/delegators-and-me.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getDelegatorsPlusMeJson() {
        Map<String, Object> map = new HashMap<>();
        String realUserOrcid = getRealUserOrcid();
        List<DelegateForm> delegates = givenPermissionToManagerReadOnly.findByReceiver(realUserOrcid);
        map.put("delegators", delegates);

        if (sourceManager.isInDelegationMode()) {
            // Add me, so I can switch back to me
            DelegateForm form = new DelegateForm();
            form.setGiverOrcid(orcidIdentifierUtils.buildOrcidIdentifier(realUserOrcid));
            form.setGiverName(Text.valueOf(sourceNameCacheManager.retrieve(realUserOrcid)));
            map.put("me", form);
        }
        return map;
    }

    /**
     * Search delegators to suggest to user
     */
    @RequestMapping(value = "/search-for-data/{query}", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, Object>> searchDelegatorsForData(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, Object>> datums = new ArrayList<>();
        Locale locale = getLocale();
        query = query.toLowerCase(locale);

        List<DelegateForm> delegates = givenPermissionToManagerReadOnly.findByReceiver(getRealUserOrcid());
        for (DelegateForm delegate : delegates) {
            String name = delegate.getGiverName().getValue().toLowerCase(locale);
            String orcid = delegate.getGiverOrcid().getPath();
            if (name.contains(query) || orcid.contains(query)) {
                Map<String, Object> datum = new HashMap<>();
                datum.put("value", name);
                datum.put("orcid", orcid);
                datums.add(datum);
            }
        }

        return datums;
    }

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/search/{query}", method = RequestMethod.GET)
    public @ResponseBody List<DelegateForm> searchDelegators(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        Locale locale = getLocale();
        query = query.toLowerCase(locale);
        String realUserOrcid = getRealUserOrcid();
        String currentOrcid = getEffectiveUserOrcid();
        List<DelegateForm> delegates = givenPermissionToManagerReadOnly.findByReceiver(realUserOrcid);
        List<DelegateForm> filtered = new ArrayList<DelegateForm>();
        for (DelegateForm delegate : delegates) {
            String name = delegate.getGiverName().getValue().toLowerCase(locale);
            String orcidUri = delegate.getGiverOrcid().getUri();
            String orcidPath = delegate.getGiverOrcid().getPath();
            if ((name.contains(query) || orcidUri.contains(query)) && !(currentOrcid.equals(orcidPath))) {
                filtered.add(delegate);
            }
        }
        return filtered;
    }

    public OrcidProfile getRealProfile() {
        String realOrcid = getRealUserOrcid();
        return realOrcid == null ? null : orcidProfileManager.retrieveOrcidProfile(realOrcid, LoadOptions.BIO_AND_INTERNAL_ONLY);
    }

}
