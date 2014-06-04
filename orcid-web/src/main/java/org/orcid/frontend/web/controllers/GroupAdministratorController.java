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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Copyright 2012-2013 ORCID
 * 
 * @author Angel Montenegro Date: 20/06/2013
 */
@Controller
@RequestMapping(value = "/group/developer-tools")
@PreAuthorize("!@sourceManager.isInDelegationMode() OR @sourceManager.isDelegatedByAnAdmin()")
public class GroupAdministratorController extends BaseWorkspaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAdministratorController.class);

    @Resource
    OrcidClientGroupManager orcidClientGroupManager;
    
    @Resource
    private OrcidSSOManager orcidSSOManager;

    @Resource
    private ThirdPartyLinkManager thirdPartyLinkManager;

    @RequestMapping
    public ModelAndView manageClients() {
        ModelAndView mav = new ModelAndView("member_developer_tools");
        OrcidProfile profile = getEffectiveProfile();

        if (profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)) {
            LOGGER.warn("Trying to access group/developer-tools page with user {} which is not a group", profile.getOrcidIdentifier().getPath());
            return new ModelAndView("redirect:/my-orcid");
        }

        OrcidClientGroup group = orcidClientGroupManager.retrieveOrcidClientGroup(profile.getOrcidIdentifier().getPath());
        mav.addObject("group", group);
        switch (profile.getGroupType()) {
        case BASIC:
            mav.addObject("clientType", "UPDATER");
            break;
        case PREMIUM:
            mav.addObject("clientType", "PREMIUM_UPDATER");
            break;
        case BASIC_INSTITUTION:
            mav.addObject("clientType", "CREATOR");
            break;
        case PREMIUM_INSTITUTION:
            mav.addObject("clientType", "PREMIUM_CREATOR");
            break;
        }

        return mav;
    }

    @RequestMapping(value = "/get-empty-redirect-uri.json", method = RequestMethod.GET)
    public @ResponseBody
    RedirectUri getEmptyRedirectUri(HttpServletRequest request) {
        RedirectUri result = new RedirectUri();
        result.setValue(new Text());
        result.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        return result;
    }
    
    @RequestMapping(value = "/client.json", method = RequestMethod.GET)
    public @ResponseBody
    Client getClient() {
        Client emptyClient = new Client();
        emptyClient.setDisplayName(new Text());
        emptyClient.setWebsite(new Text());
        emptyClient.setShortDescription(new Text());
        emptyClient.setClientId(new Text());
        emptyClient.setClientSecret(new Text());
        emptyClient.setType(new Text());
        ArrayList<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri emptyRedirectUri = new RedirectUri();
        emptyRedirectUri.setValue(new Text());
        emptyRedirectUri.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        redirectUris.add(emptyRedirectUri);
        emptyClient.setRedirectUris(redirectUris);
        return emptyClient;
    }

    private boolean validateUrl(String url) {
        String urlToCheck = null;
        if (PojoUtil.isEmpty(url))
            return false;
        // To validate the URL we need a string with a protocol, so, check if it
        // have it, if it doesn't, add it.
        // Check if the URL begins with the protocol
        if (url.startsWith("http://") || url.startsWith("https://")) {
            urlToCheck = url;
        } else {
            // If it doesn't, add the http protocol by default
            urlToCheck = "http://" + url;
        }

        try {
            new java.net.URL(urlToCheck);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    private Client validateDisplayName(Client client) {
        client.getDisplayName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(client.getDisplayName())) {
            setError(client.getDisplayName(), "manage.developer_tools.group.error.display_name.empty");
        } else if (client.getDisplayName().getValue().length() > 150) {
            setError(client.getDisplayName(), "manage.developer_tools.group.error.display_name.150");
        }

        return client;
    }

    private Client validateWebsite(Client client) {
        client.getWebsite().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(client.getWebsite())) {
            setError(client.getWebsite(), "manage.developer_tools.group.error.website.empty");
        } else if (!validateUrl(client.getWebsite().getValue())) {
            setError(client.getWebsite(), "common.invalid_url");
        }
        return client;
    }

    private Client validateShortDescription(Client client) {
        client.getShortDescription().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(client.getShortDescription()))
            setError(client.getShortDescription(), "manage.developer_tools.group.error.short_description.empty");
        return client;
    }

    private Client validateRedirectUris(Client client) {
        if (client.getRedirectUris() != null && client.getRedirectUris().size() > 0) {
            for (RedirectUri redirectUri : client.getRedirectUris()) {
                redirectUri.setErrors(new ArrayList<String>());
                if (!validateUrl(redirectUri.getValue().getValue())) {
                    setError(redirectUri, "common.invalid_url");
                }

                if (RedirectUriType.DEFAULT.value().equals(redirectUri.getType().getValue())) {
                    // Clean all scopes from default redirect uri type
                    if (redirectUri.getScopes() != null && !redirectUri.getScopes().isEmpty()) {
                        redirectUri.setScopes(new ArrayList<String>());
                    }
                } else {
                    if (redirectUri.getScopes() != null && redirectUri.getScopes().isEmpty()) {
                        //If the redirect type is not default, the scopes must not be emtpy
                        setError(redirectUri, "manage.developer_tools.group.error.empty_scopes");
                    }
                }
            }
        }
        return client;
    }

    @RequestMapping(value = "/add-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    Client createClient(@RequestBody Client client) {
        // Clean the error list
        client.setErrors(new ArrayList<String>());
        // Validate fields
        validateDisplayName(client);
        validateWebsite(client);
        validateShortDescription(client);
        validateRedirectUris(client);

        copyErrors(client.getDisplayName(), client);
        copyErrors(client.getWebsite(), client);
        copyErrors(client.getShortDescription(), client);

        for (RedirectUri redirectUri : client.getRedirectUris()) {
            copyErrors(redirectUri, client);
        }

        if (client.getErrors().size() == 0) {
            OrcidProfile profile = getEffectiveProfile();
            String groupOrcid = profile.getOrcidIdentifier().getPath();

            if (profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)) {
                LOGGER.warn("Trying to create client with non group user {}", profile.getOrcidIdentifier().getPath());
                throw new OrcidClientGroupManagementException("Your account is not allowed to do this operation.");
            }

            OrcidClient result = null;

            try {
                result = orcidClientGroupManager.createAndPersistClientProfile(groupOrcid, client.toOrcidClient());
            } catch (OrcidClientGroupManagementException e) {
                LOGGER.error(e.getMessage());
                result = new OrcidClient();
                result.setErrors(new ErrorDesc(getMessage("manage.developer_tools.group.cannot_create_client")));
            }

            client = Client.valueOf(result);

        }

        return client;
    }

    @RequestMapping(value = "/edit-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    Client editClient(@RequestBody Client client) {
        // Clean the error list
        client.setErrors(new ArrayList<String>());
        // Validate fields
        validateDisplayName(client);
        validateWebsite(client);
        validateShortDescription(client);
        validateRedirectUris(client);

        copyErrors(client.getDisplayName(), client);
        copyErrors(client.getWebsite(), client);
        copyErrors(client.getShortDescription(), client);

        for (RedirectUri redirectUri : client.getRedirectUris()) {
            copyErrors(redirectUri, client);
        }

        if (client.getErrors().size() == 0) {
            OrcidProfile profile = getEffectiveProfile();
            String groupOrcid = profile.getOrcidIdentifier().getPath();

            if (profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)) {
                LOGGER.warn("Trying to edit client with non group user {}", profile.getOrcidIdentifier().getPath());
                throw new OrcidClientGroupManagementException("Your account is not allowed to do this operation.");
            }

            OrcidClient result = null;

            try {
                result = orcidClientGroupManager.updateClientProfile(groupOrcid, client.toOrcidClient());
                clearCache();
            } catch (OrcidClientGroupManagementException e) {
                LOGGER.error(e.getMessage());
                result = new OrcidClient();
                result.setErrors(new ErrorDesc(getMessage("manage.developer_tools.group.unable_to_update")));
            }

            client = Client.valueOf(result);
        }
        return client;
    }

    @RequestMapping(value = "/get-clients.json", method = RequestMethod.GET)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    List<Client> getClients() {
        OrcidProfile profile = getEffectiveProfile();
        String groupOrcid = profile.getOrcidIdentifier().getPath();

        if (profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)) {
            LOGGER.warn("Trying to get clients of non group user {}", profile.getOrcidIdentifier().getPath());
            throw new OrcidClientGroupManagementException("Your account is not allowed to do this operation.");
        }

        OrcidClientGroup group = orcidClientGroupManager.retrieveOrcidClientGroup(groupOrcid);
        List<Client> clients = new ArrayList<Client>();

        for (OrcidClient orcidClient : group.getOrcidClient()) {
            clients.add(Client.valueOf(orcidClient));
        }

        return clients;
    }

    @ModelAttribute("redirectUriTypes")
    public Map<String, String> getRedirectUriTypes() {
        Map<String, String> redirectUriTypes = new LinkedHashMap<String, String>();
        for (RedirectUriType rType : RedirectUriType.values()) {
            if (!RedirectUriType.SSO_AUTHENTICATION.equals(rType))
                redirectUriTypes.put(rType.value(), rType.value());
        }
        return redirectUriTypes;
    }

    /**
     * Since the groups have changed, the cache version must be updated on
     * database and all caches have to be evicted.
     * */
    private void clearCache() {
        // Updates cache database version
        thirdPartyLinkManager.updateDatabaseCacheVersion();
        // Evict current cache
        thirdPartyLinkManager.evictAll();
    }

    @RequestMapping(value = "/get-available-scopes.json", method = RequestMethod.GET)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    List<String> getAvailableRedirectUriScopes() {
        List<String> scopes = new ArrayList<String>();
        // Ignore these scopes
        List<ScopePathType> ignoreScopes = new ArrayList<ScopePathType>(Arrays.asList(ScopePathType.ORCID_PATENTS_CREATE, ScopePathType.ORCID_PATENTS_READ_LIMITED,
                ScopePathType.ORCID_PATENTS_UPDATE, ScopePathType.WEBHOOK));
        for (ScopePathType t : ScopePathType.values()) {
            if (!ignoreScopes.contains(t))
                scopes.add(t.value());
        }
        Collections.sort(scopes);
        return scopes;
    }
    
    /**
     * Reset client secret
     * */
    @RequestMapping(value = "/reset-client-secret.json", method = RequestMethod.POST)
    public @ResponseBody
    boolean resetClientSecret(HttpServletRequest request, @RequestBody String clientId) {
        return orcidSSOManager.resetClientSecret(clientId);
    }
}
