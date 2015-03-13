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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Angel Montenegro
 */

@Controller
@RequestMapping(value = { "/manage-members" })
public class ManageMembersController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageMembersController.class);

    private static String SALESFORCE_ID_PATTERN = "[a-zA-Z0-9]{15}";

    @Resource
    EmailManager emailManager;

    @Resource
    ProfileEntityManager profileEntityManager;

    @Resource
    OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    ClientDetailsManager clientDetailsManager;

    @Resource
    private GroupAdministratorController groupAdministratorController;

    public OrcidClientGroupManager getOrcidClientGroupManager() {
        return orcidClientGroupManager;
    }

    public void setOrcidClientGroupManager(OrcidClientGroupManager orcidClientGroupManager) {
        this.orcidClientGroupManager = orcidClientGroupManager;
    }

    @RequestMapping
    public ModelAndView getManageMembersPage() {
        ModelAndView mav = new ModelAndView("/admin/manage_members");
        return mav;
    }

    /**
     * Get an empty group
     * 
     * @return an empty group
     * */
    @RequestMapping(value = "/member.json", method = RequestMethod.GET)
    public @ResponseBody
    Group getEmptyGroup() {
        Text empty = Text.valueOf("");
        Group group = new Group();
        group.setEmail(empty);
        group.setGroupName(empty);
        group.setGroupOrcid(empty);
        group.setSalesforceId(empty);
        // Set the default type as basic
        group.setType(Text.valueOf(GroupType.BASIC.value()));
        return group;
    }

    @RequestMapping(value = "/find-member.json", method = RequestMethod.GET)
    public @ResponseBody
    Group findMember(@RequestParam("orcidOrEmail") String orcidOrEmail) {
        Group group = new Group();

        String orcid = orcidOrEmail;
        if (!matchesOrcidPattern(orcidOrEmail)) {
            Map<String, String> ids = emailManager.findIdByEmail(orcidOrEmail);
            if (ids != null && ids.containsKey(orcidOrEmail)) {
                orcid = ids.get(orcidOrEmail);
            } else {
                group.getErrors().add(getMessage("manage_member.email_not_found"));
                orcid = null;
            }
        }

        if (orcid != null) {
            if (profileEntityManager.orcidExists(orcid)) {
                GroupType groupType = profileEntityManager.getGroupType(orcid);
                if (groupType != null) {
                    Date lastModified = profileEntityManager.getLastModified(orcid);
                    ProfileEntity memberProfile = profileEntityManager.findByOrcid(orcid, lastModified.getTime());
                    group = Group.fromProfileEntity(memberProfile);
                } else {
                    group.getErrors().add(getMessage("manage_members.orcid_is_not_a_member"));
                }
            } else {
                group.getErrors().add(getMessage("manage_members.orcid_doesnt_exists"));
            }
        }

        return group;
    }

    /**
     * Create a member
     * 
     * @param the
     *            member to be created
     * @return the member with the orcid information or with the error
     *         information
     * */
    @RequestMapping(value = "/create-member.json", method = RequestMethod.POST)
    public @ResponseBody
    Group createMember(@RequestBody Group group) {
        group.setErrors(new ArrayList<String>());

        validateGroupEmail(group);
        validateGroupName(group);
        validateGroupType(group);
        validateSalesforceId(group);

        copyErrors(group.getEmail(), group);
        copyErrors(group.getGroupName(), group);
        copyErrors(group.getType(), group);
        copyErrors(group.getSalesforceId(), group);

        if (group.getErrors().isEmpty()) {
            OrcidClientGroup orcidClientGroup = group.toOrcidClientGroup();
            orcidClientGroup = orcidClientGroupManager.createGroup(orcidClientGroup);
            group.setGroupOrcid(Text.valueOf(orcidClientGroup.getGroupOrcid()));
        }

        return group;
    }

    @RequestMapping(value = "/update-member.json", method = RequestMethod.POST)
    public @ResponseBody
    Group updateMember(@RequestBody Group group) {
        group.setErrors(new ArrayList<String>());

        validateGroupEmail(group);
        validateGroupName(group);
        validateGroupType(group);
        validateSalesforceId(group);

        copyErrors(group.getEmail(), group);
        copyErrors(group.getGroupName(), group);
        copyErrors(group.getType(), group);
        copyErrors(group.getSalesforceId(), group);

        if (group.getErrors().isEmpty()) {
            OrcidClientGroup orcidClientGroup = group.toOrcidClientGroup();
            orcidClientGroupManager.updateGroup(orcidClientGroup);
            groupAdministratorController.clearCache();
        }

        return group;
    }

    @RequestMapping(value = "/find-client.json", method = RequestMethod.GET)
    public @ResponseBody
    Client findClient(@RequestParam("orcid") String orcid) {
        Client result = new Client();
        ClientDetailsEntity clientDetailsEntity = clientDetailsManager.findByClientId(orcid);
        if (clientDetailsEntity != null) {
            ClientType clientType = profileEntityManager.getClientType(orcid);
            if (clientType != null) {
                result = Client.valueOf(clientDetailsEntity);
                // If the client types is undefined, get it from DB
                if (PojoUtil.isEmpty(result.getType()))
                    result.setType(Text.valueOf(clientType.value()));
            } else {
                result.getErrors().add(getMessage("admin.edit_client.orcid_is_not_a_client"));
            }

        } else {
            result.getErrors().add(getMessage("admin.edit_client.invalid_orcid"));
        }
        return result;
    }

    @RequestMapping(value = "/update-client.json", method = RequestMethod.POST)
    public @ResponseBody
    Client updateClient(@RequestBody Client client) {
        // Clean the error list
        client.setErrors(new ArrayList<String>());
        // Validate fields
        groupAdministratorController.validateDisplayName(client);
        groupAdministratorController.validateWebsite(client);
        groupAdministratorController.validateShortDescription(client);
        groupAdministratorController.validateRedirectUris(client, true);

        copyErrors(client.getDisplayName(), client);
        copyErrors(client.getWebsite(), client);
        copyErrors(client.getShortDescription(), client);

        for (RedirectUri redirectUri : client.getRedirectUris()) {
            copyErrors(redirectUri, client);
        }

        if (client.getErrors().isEmpty()) {

            OrcidClient result = null;
            try {
                result = orcidClientGroupManager.updateClient(client.toOrcidClient());
                groupAdministratorController.clearCache();
            } catch (OrcidClientGroupManagementException e) {
                LOGGER.error(e.getMessage());
                result = new OrcidClient();
                result.setErrors(new ErrorDesc(getMessage("manage.developer_tools.group.unable_to_update")));
            }

            client = Client.valueOf(result);
        }

        return client;
    }

    @RequestMapping(value = "/empty-redirect-uri.json", method = RequestMethod.GET)
    public @ResponseBody
    RedirectUri getEmptyRedirectUri() {
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        rUri.setValue(Text.valueOf(""));
        return rUri;
    }

    /**
     * MODEL ATTRIBUTES
     * */
    @ModelAttribute("redirectUriTypes")
    public Map<String, String> getRedirectUriTypes() {
        Map<String, String> redirectUriTypes = new LinkedHashMap<String, String>();
        for (RedirectUriType rType : RedirectUriType.values()) {
            if (!RedirectUriType.SSO_AUTHENTICATION.equals(rType))
                redirectUriTypes.put(rType.value(), rType.value());
        }
        return redirectUriTypes;
    }

    @ModelAttribute("groupTypes")
    public Map<String, String> retrieveGroupTypes() {
        GroupType[] groupTypes = GroupType.values();
        Map<String, String> groupTypesMap = new TreeMap<String, String>();

        for (GroupType groupType : groupTypes) {
            String key = groupType.value();
            String value = key.replace('-', ' ');
            groupTypesMap.put(key, value);
        }

        return groupTypesMap;
    }

    /**
     * VALIDATORS
     * */
    private void validateGroupEmail(Group group) {
        group.getEmail().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(group.getEmail())) {
            setError(group.getEmail(), "NotBlank.group.email");
        } else if (!validateEmailAddress(group.getEmail().getValue())) {
            setError(group.getEmail(), "group.email.invalid_email");
        } else if (PojoUtil.isEmpty(group.getGroupOrcid())) {
            if (emailManager.emailExists(group.getEmail().getValue()))
                setError(group.getEmail(), "group.email.already_used");
        } else if (!PojoUtil.isEmpty(group.getGroupOrcid())) {
            String newEmail = group.getEmail().getValue();
            String userOrcid = group.getGroupOrcid().getValue();
            if (emailManager.emailExists(newEmail)) {
                Map<String, String> ids = emailManager.findIdByEmail(newEmail);
                String orcidThatOwnsTheEmail = ids.get(newEmail);
                // If the email is not the same, it means the member cannot use
                // that email address
                if (!userOrcid.equals(orcidThatOwnsTheEmail)) {
                    setError(group.getEmail(), "group.email.already_used");
                }
            }
        }
    }

    private void validateGroupName(Group group) {
        group.getGroupName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(group.getGroupName())) {
            setError(group.getGroupName(), "NotBlank.group.name");
        } else if (group.getGroupName().getValue().length() > 150) {
            setError(group.getGroupName(), "group.name.too_long");
        }
    }

    private void validateGroupType(Group group) {
        group.getType().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(group.getType())) {
            setError(group.getType(), "NotBlank.group.type");
        } else {
            try {
                GroupType.fromValue(group.getType().getValue());
            } catch (IllegalArgumentException e) {
                setError(group.getType(), "group.type.invalid");
            }
        }
    }

    private void validateSalesforceId(Group group) {
        group.getSalesforceId().setErrors(new ArrayList<String>());
        if (group != null && !PojoUtil.isEmpty(group.getSalesforceId())) {
            if (group.getSalesforceId().getValue().length() != 15) {
                setError(group.getSalesforceId(), "group.salesforce_id.invalid_length");
            } else if (!group.getSalesforceId().getValue().matches(SALESFORCE_ID_PATTERN)) {
                setError(group.getSalesforceId(), "group.salesforce_id.invalid");
            }
        }
    }

    private boolean matchesOrcidPattern(String orcid) {
        return OrcidStringUtils.isValidOrcid(orcid);
    }
}
