package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("developerToolsController")
@RequestMapping(value = { "/developer-tools" })
public class DeveloperToolsController extends BaseWorkspaceController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DeveloperToolsController.class);

    private static int CLIENT_NAME_LENGTH = 255;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "clientManagerV3")
    private ClientManager clientManager;
    
    @Resource(name = "clientManagerReadOnlyV3")
    private ClientManagerReadOnly clientManagerReadOnly;
    
    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;
    
    @RequestMapping
    public ModelAndView manageDeveloperTools() {
        ModelAndView mav = new ModelAndView("developer_tools/developer_tools");
        String userOrcid = getCurrentUserOrcid();
        ProfileEntity entity = profileEntityCacheManager.retrieve(userOrcid);
        if(entity.getEnableDeveloperTools() != null) {
            mav.addObject("developerToolsEnabled", entity.getEnableDeveloperTools());
        }
        if (!entity.getEnableDeveloperTools()) {            
            if (OrcidType.USER.equals(entity.getOrcidType())) {
                mav.addObject("error", getMessage("manage.developer_tools.user.error.enable_developer_tools"));
            } else {
                mav.addObject("error", getMessage("manage.developer_tools.user.error.invalid_user_type"));
            }
        }

        mav.addObject("hideRegistration", (sourceManager.isInDelegationMode() && !sourceManager.isDelegatedByAnAdmin()));
        boolean hasVerifiedEmail = emailManagerReadOnly.haveAnyEmailVerified(userOrcid);
        if(hasVerifiedEmail) {
            mav.addObject("hasVerifiedEmail", true);
        } else {
            mav.addObject("hasVerifiedEmail", false);
            mav.addObject("primaryEmail", emailManagerReadOnly.findPrimaryEmail(userOrcid).getEmail());
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/get-client.json", method = RequestMethod.GET)
    public @ResponseBody Client getClient() {
        String userOrcid = getEffectiveUserOrcid();
        Set<org.orcid.jaxb.model.v3.rc1.client.Client> existingClients = clientManagerReadOnly.getClients(userOrcid);

        if (existingClients.isEmpty()) {
            Client client = new Client();
            client.setClientId(Text.valueOf(""));
            client.setClientSecret(Text.valueOf(""));
            client.setDisplayName(Text.valueOf(""));
            client.setErrors(new ArrayList<String>());
            RedirectUri empty = new RedirectUri();
            empty.setValue(Text.valueOf(""));
            empty.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
            List<RedirectUri> rUris = new ArrayList<RedirectUri>();
            rUris.add(empty);
            client.setRedirectUris(rUris);
            client.setShortDescription(Text.valueOf(""));
            client.setWebsite(Text.valueOf(""));
            return client;
        }

        return Client.fromModelObject(existingClients.stream().findFirst().get());
    }
    
    @RequestMapping(value = "/create-client.json", method = RequestMethod.POST)
    public @ResponseBody Client createClient(@RequestBody Client client) {
        validateClient(client);

        if (client.getErrors().isEmpty()) {
            org.orcid.jaxb.model.v3.rc1.client.Client clientToCreate = client.toModelObject();
            try {
                clientToCreate = clientManager.createPublicClient(clientToCreate);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                String errorDesciption = getMessage("manage.developer_tools.group.cannot_create_client") + " " + e.getMessage();
                client.setErrors(new ArrayList<String>());
                client.getErrors().add(errorDesciption);
                return client;
            }
            client = Client.fromModelObject(clientToCreate);
        }

        return client;
    }

    @RequestMapping(value = "/update-user-credentials.json", method = RequestMethod.POST)
    public @ResponseBody Client updateClient(@RequestBody Client client) {
        validateClient(client);

        if (client.getErrors().isEmpty()) {
            org.orcid.jaxb.model.v3.rc1.client.Client clientToEdit = client.toModelObject();
            try {
                clientToEdit = clientManager.edit(clientToEdit, false);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                String errorDesciption = getMessage("manage.developer_tools.group.cannot_create_client") + " " + e.getMessage();
                client.setErrors(new ArrayList<String>());
                client.getErrors().add(errorDesciption);
                return client;
            }
            client = Client.fromModelObject(clientToEdit);
        } 
        
        return client;
    }

    @RequestMapping(value = "/reset-client-secret", method = RequestMethod.POST)
    public @ResponseBody boolean resetClientSecret(@RequestBody String clientId) {
        //Verify this client belongs to the member
        org.orcid.jaxb.model.v3.rc1.client.Client client = clientManagerReadOnly.get(clientId);
        if(client == null) {
            return false;
        }
        
        if(!client.getGroupProfileId().equals(getCurrentUserOrcid())) {
            return false;
        }
        
        return clientManager.resetClientSecret(clientId);
    }    
    
    /**
     * Validates the Client object
     * 
     * @param ssoCredentials
     * @return true if any error is found in the ssoCredentials object
     * */
    private void validateClient(Client client) {
        client.setErrors(new ArrayList<String>());
        if(client.getDisplayName() == null) {
            client.setDisplayName(new Text());
        } else {
            client.getDisplayName().setErrors(new ArrayList<String>());            
        }
        if (PojoUtil.isEmpty(client.getDisplayName())) {
            client.getDisplayName().setErrors(Arrays.asList(getMessage("manage.developer_tools.name_not_empty")));
        } else if (client.getDisplayName().getValue().length() > CLIENT_NAME_LENGTH) {
            client.getDisplayName().setErrors(Arrays.asList(getMessage("manage.developer_tools.name_too_long")));
        } else if(OrcidStringUtils.hasHtml(client.getDisplayName().getValue())){
            client.getDisplayName().setErrors(Arrays.asList(getMessage("manage.developer_tools.name.html")));
        } 
        copyErrors(client.getDisplayName(), client);

        if(client.getShortDescription() == null) {
            client.setShortDescription(new Text());
        } else {
            client.getShortDescription().setErrors(new ArrayList<String>());            
        }
        if (PojoUtil.isEmpty(client.getShortDescription())) {
            client.getShortDescription().setErrors(Arrays.asList(getMessage("manage.developer_tools.description_not_empty")));
        } else if(OrcidStringUtils.hasHtml(client.getShortDescription().getValue())) {
            client.getShortDescription().setErrors(Arrays.asList(getMessage("manage.developer_tools.description.html")));
        } 
        copyErrors(client.getShortDescription(), client);

        if(client.getWebsite() == null) {
            client.setWebsite(new Text());
        } else {
            client.getWebsite().setErrors(new ArrayList<String>());            
        }
        if (PojoUtil.isEmpty(client.getWebsite())) {
            client.getWebsite().setErrors(Arrays.asList(getMessage("manage.developer_tools.website_not_empty")));
        } else {
            validateUrl(client.getWebsite(), "manage.developer_tools.invalid_website");
        }
        copyErrors(client.getWebsite(), client);
        
        if (client.getRedirectUris() == null){
            client.setRedirectUris(new ArrayList<RedirectUri>());            
        } 
        
        if(client.getRedirectUris().isEmpty()) {
            client.getErrors().add(getMessage("manage.developer_tools.at_least_one"));
        } else {
            for (RedirectUri rUri : client.getRedirectUris()) {
                validateRedirectUri(rUri);
                if (!RedirectUriType.SSO_AUTHENTICATION.value().equals(rUri.getType().getValue()))  {
                    rUri.getErrors().add(getMessage("manage.developer_tools.invalid_redirect_uri"));
                }
                copyErrors(rUri, client);
            }
        }                
    }

    /**
     * Enable developer tools on the current profile
     * 
     * @return true if the developer tools where enabled on the profile
     * */
    @RequestMapping(value = "/enable-developer-tools.json", method = RequestMethod.POST)
    public @ResponseBody
    boolean enableDeveloperTools(HttpServletRequest request) {
        boolean enabled = profileEntityManager.enableDeveloperTools(getCurrentUserOrcid());     
        if (enabled) {
            profileHistoryEventManager.recordEvent(ProfileHistoryEventType.ACCEPTED_PUBLIC_CLIENT_TERMS_CONDITIONS, getCurrentUserOrcid());
        }
        return enabled;
    }
}