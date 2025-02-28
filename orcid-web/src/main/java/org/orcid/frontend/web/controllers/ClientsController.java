package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ClientDetailsManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
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
 * @author Angel Montenegro Date: 20/06/2013
 */
@Controller
@RequestMapping(value = "/group/developer-tools")
@PreAuthorize("!@sourceManagerV3.isInDelegationMode() OR @sourceManagerV3.isDelegatedByAnAdmin()")
public class ClientsController extends BaseWorkspaceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientsController.class);

    @Resource(name = "clientDetailsManagerV3")
    private ClientDetailsManager clientDetailsManager;

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "clientManagerV3")
    private ClientManager clientManager;

    @Resource(name = "clientManagerReadOnlyV3")
    private ClientManagerReadOnly clientManagerReadOnly;    

    @RequestMapping
    public ModelAndView manageClients() {
        ModelAndView mav = new ModelAndView("member_developer_tools");
        return mav;
    }

    @RequestMapping(value = "/client.json", method = RequestMethod.GET)
    public @ResponseBody Client getEmptyClient() {
        Client emptyClient = new Client();
        emptyClient.setDisplayName(new Text());
        emptyClient.setWebsite(new Text());
        emptyClient.setShortDescription(new Text());
        emptyClient.setClientId(new Text());
        emptyClient.setClientSecret(new Text());
        emptyClient.setType(new Text());
        emptyClient.setAllowAutoDeprecate(Checkbox.valueOf(false));
        ArrayList<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri emptyRedirectUri = new RedirectUri();
        emptyRedirectUri.setValue(new Text());
        emptyRedirectUri.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        emptyRedirectUri.setActType(Text.valueOf(""));
        emptyRedirectUri.setGeoArea(Text.valueOf(""));
        redirectUris.add(emptyRedirectUri);
        emptyClient.setRedirectUris(redirectUris);
        return emptyClient;
    }

    public Client validateDisplayName(Client client) {
        client.getDisplayName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(client.getDisplayName())) {
            setError(client.getDisplayName(), "manage.developer_tools.group.error.display_name.empty");
        } else if (client.getDisplayName().getValue().length() > 150) {
            setError(client.getDisplayName(), "manage.developer_tools.group.error.display_name.150");
        } else {
            if (OrcidStringUtils.hasHtml(client.getDisplayName().getValue()))
                setError(client.getDisplayName(), "manage.developer_tools.group.error.display_name.html");
        }

        return client;
    }

    public Client validateWebsite(Client client) {
        client.getWebsite().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(client.getWebsite())) {
            setError(client.getWebsite(), "manage.developer_tools.group.error.website.empty");
        } else {
            validateUrl(client.getWebsite(), "common.invalid_url");
        }

        return client;
    }

    public Client validateShortDescription(Client client) {
        client.getShortDescription().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(client.getShortDescription()))
            setError(client.getShortDescription(), "manage.developer_tools.group.error.short_description.empty");
        else {
            if (OrcidStringUtils.hasHtml(client.getShortDescription().getValue()))
                setError(client.getShortDescription(), "manage.developer_tools.group.error.short_description.html");
        }

        return client;
    }

    public Client validateRedirectUris(Client client) {
        return validateRedirectUris(client, false);
    }

    public Client validateRedirectUris(Client client, boolean checkProtocol) {
        if (client.getRedirectUris() != null && client.getRedirectUris().size() > 0) {
            for (RedirectUri redirectUri : client.getRedirectUris()) {
                validateRedirectUri(redirectUri);
                if(!ClientType.PUBLIC_CLIENT.value().equals(client.getType().getValue())) {
                    if (RedirectUriType.DEFAULT.value().equals(redirectUri.getType().getValue())) {
                        // Clean all scopes from default redirect uri type
                        if (redirectUri.getScopes() != null && !redirectUri.getScopes().isEmpty()) {
                            redirectUri.setScopes(new ArrayList<String>());
                        }
                    } else {
                        if (redirectUri.getScopes() != null && redirectUri.getScopes().isEmpty()) {
                            // If the redirect type is not default, the scopes must
                            // not be emtpy
                            setError(redirectUri, "manage.developer_tools.group.error.empty_scopes");
                        }
                    }
                }
            }
        }
        return client;
    }

    @RequestMapping(value = "/get-clients.json", method = RequestMethod.GET)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody List<Client> getClients() {
        String memberId = getEffectiveUserOrcid();
        Set<org.orcid.jaxb.model.v3.release.client.Client> existingClients = clientManagerReadOnly.getClients(memberId);
        List<Client> clients = new ArrayList<Client>();
        for (org.orcid.jaxb.model.v3.release.client.Client existingClient : existingClients) {
            clients.add(Client.fromModelObject(existingClient));
        }
        Collections.sort(clients);
        return clients;
    }

    @RequestMapping(value = "/add-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody Client createClient(@RequestBody Client client) {
        validateIncomingElement(client);
        if (client.getErrors().size() == 0) {
            org.orcid.jaxb.model.v3.release.client.Client newClient = client.toModelObject();
            try {
                newClient = clientManager.create(newClient);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                String errorDesciption = getMessage("manage.developer_tools.group.cannot_create_client") + " " + e.getMessage();
                client.setErrors(new ArrayList<String>());
                client.getErrors().add(errorDesciption);
                return client;
            }
            client = Client.fromModelObject(newClient);
        }
        return client;
    }

    @RequestMapping(value = "/edit-client.json", method = RequestMethod.POST)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody Client editClient(@RequestBody Client client) {
        validateIncomingElement(client);

        if (client.getErrors().size() == 0) {
            org.orcid.jaxb.model.v3.release.client.Client clientToEdit = client.toModelObject();
            try {
                // Updating from the clients edit page should not overwrite
                // configuration values on the DB
                clientToEdit = clientManager.edit(clientToEdit, false);                
            } catch (OrcidClientGroupManagementException e) {
                LOGGER.error(e.getMessage());
                String errorDesciption = getMessage("manage.developer_tools.group.unable_to_update") + " " + e.getMessage();
                client.setErrors(new ArrayList<String>());
                client.getErrors().add(errorDesciption);
                return client;
            }

            client = Client.fromModelObject(clientToEdit);
        }
        return client;
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

    @RequestMapping(value = "/get-available-scopes.json", method = RequestMethod.GET)
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody List<String> getAvailableRedirectUriScopes() {
        List<String> scopes = new ArrayList<String>(Arrays.asList(ScopePathType.ACTIVITIES_UPDATE.value(), ScopePathType.READ_LIMITED.value(),
                ScopePathType.PERSON_UPDATE.value(), ScopePathType.AUTHENTICATE.value()));
        Collections.sort(scopes);
        return scopes;
    }

    /**
     * Reset client secret
     */
    @RequestMapping(value = "/reset-client-secret.json", method = RequestMethod.POST)
    public @ResponseBody boolean resetClientSecret(@RequestBody String clientId) {
        // Verify this client belongs to the member
        org.orcid.jaxb.model.v3.release.client.Client client = clientManagerReadOnly.get(clientId);
        if (client == null) {
            return false;
        }

        if (!client.getGroupProfileId().equals(getCurrentUserOrcid())) {
            return false;
        }

        return clientManager.resetClientSecret(clientId);
    }
    
    

    private void validateIncomingElement(Client client) {
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
    }
}
