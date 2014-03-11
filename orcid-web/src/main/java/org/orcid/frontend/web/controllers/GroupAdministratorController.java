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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Controller("GroupAdministratorController")
@RequestMapping(value = "/group/developer-tools")
public class GroupAdministratorController extends BaseWorkspaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAdministratorController.class);

    @Resource
    OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    private ThirdPartyImportManager thirdPartyImportManager;
    
    @RequestMapping
    public ModelAndView manageClients() {
        ModelAndView mav = new ModelAndView("group_developer_tools");
        OrcidProfile profile = getEffectiveProfile();

        if (profile.getType() == null || !profile.getType().equals(OrcidType.GROUP)) {
            LOGGER.warn("Trying to access manage-clients page with user {} which is not a group", profile.getOrcidIdentifier().getPath());
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

    @RequestMapping(value = "/client.json", method = RequestMethod.GET)
    public @ResponseBody
    Client getClient(HttpServletRequest request) {
        Client emptyClient = new Client();
        emptyClient.setDisplayName(Text.valueOf(""));
        emptyClient.setWebsite(Text.valueOf(""));
        emptyClient.setShortDescription(Text.valueOf(""));
        emptyClient.setClientId(Text.valueOf(""));
        emptyClient.setClientSecret(Text.valueOf(""));
        emptyClient.setType(Text.valueOf(""));
        ArrayList<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        emptyClient.setRedirectUris(redirectUris);
        return emptyClient;
    }
    private boolean validateUrl(String url) {
        String urlToCheck = null;
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
            setError(client.getDisplayName(), "manage_clients.error.display_name.empty");
        } else if (client.getDisplayName().getValue().length() > 150) {
            setError(client.getDisplayName(), "manage_clients.error.display_name.150");
        }

        return client;
    }

    private Client validateWebsite(Client client) {
        client.getWebsite().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(client.getWebsite())) {
            setError(client.getWebsite(), "manage_clients.error.website.empty");
        } else if (!validateUrl(client.getWebsite().getValue())) {
            setError(client.getWebsite(), "manage_clients.error.invalid_url");
        }
        return client;
    }

    private Client validateShortDescription(Client client) {
        client.getShortDescription().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(client.getShortDescription()))
            setError(client.getShortDescription(), "manage_clients.error.short_description.empty");
        return client;
    }

    private Client validateRedirectUris(Client client) {
        if (client.getRedirectUris() != null && client.getRedirectUris().size() > 0) {
            for (RedirectUri redirectUri : client.getRedirectUris()) {
                redirectUri.setErrors(new ArrayList<String>());
                if (!validateUrl(redirectUri.getValue().getValue())) {
                    setError(redirectUri, "manage_clients.error.invalid_url");
                }
            }
        }
        return client;
    }

    @RequestMapping(value = "/add-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    Client createClient(HttpServletRequest request, @RequestBody Client client) {
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
                result.setErrors(new ErrorDesc(getMessage("manage_clients.cannot_create_client")));
            }

            client = Client.valueOf(result);

        }

        return client;
    }

    @RequestMapping(value = "/edit-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    Client editClient(HttpServletRequest request, @RequestBody Client client) {
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
                result.setErrors(new ErrorDesc(getMessage("manage_clients.unable_to_update")));
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
    public Map<String, String> getRedirectUriTypes(){
        Map<String, String> redirectUriTypes = new LinkedHashMap<String, String>();
        for(RedirectUriType rType : RedirectUriType.values()) {
            redirectUriTypes.put(rType.value(), rType.value());
        }
        return redirectUriTypes;
    }
    
    private void clearCache() {
        thirdPartyImportManager.evictAll();
    }
}