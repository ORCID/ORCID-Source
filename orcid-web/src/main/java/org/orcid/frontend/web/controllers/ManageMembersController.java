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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.MembersManager;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
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
    private static String SALESFORCE_ID_PATTERN = "[a-zA-Z0-9]{15}";

    @Resource
    MembersManager membersManager;
    
    @Resource
    ClientDetailsManager clientDetailsManager;

    @Resource
    private GroupAdministratorController groupAdministratorController;          

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
    Member getEmptyGroup() {
        Text empty = Text.valueOf("");
        Member group = new Member();
        group.setEmail(empty);
        group.setGroupName(empty);
        group.setGroupOrcid(empty);
        group.setSalesforceId(empty);
        // Set the default type as basic
        group.setType(Text.valueOf(MemberType.BASIC.value()));
        return group;
    }

    @RequestMapping(value = "/find.json", method = RequestMethod.GET)
    public @ResponseBody ResultContainer find(@RequestParam("id") String id) {
        ResultContainer result = new ResultContainer();
        
        if(clientDetailsManager.exists(id)) {
            result.setClient(true);
            result.setClientObject(findClient(id));
        } else {
            result.setClient(false);
            result.setMemberObject(findMember(id));
        }     
        
        return result;
    } 
    
    @RequestMapping(value = "/find-member.json", method = RequestMethod.GET)
    public @ResponseBody
    Member findMember(@RequestParam("orcidOrEmail") String orcidOrEmail) {
        Member group = new Member();

        if(PojoUtil.isEmpty(orcidOrEmail)) {
            group.getErrors().add("manage_member.not_blank");
        } else {
            group = membersManager.getMember(orcidOrEmail);
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
    Member createMember(@RequestBody Member member) {
        member.setErrors(new ArrayList<String>());

        validateGroupEmail(member);
        validateGroupName(member);
        validateGroupType(member);
        validateSalesforceId(member);

        copyErrors(member.getEmail(), member);
        copyErrors(member.getGroupName(), member);
        copyErrors(member.getType(), member);
        copyErrors(member.getSalesforceId(), member);

        if (member.getErrors().isEmpty()) {
            member = membersManager.createMember(member);
        }

        return member;
    }

    @RequestMapping(value = "/update-member.json", method = RequestMethod.POST)
    public @ResponseBody
    Member updateMember(@RequestBody Member member) {
        member.setErrors(new ArrayList<String>());

        validateGroupEmail(member);
        validateGroupName(member);
        validateGroupType(member);
        validateSalesforceId(member);

        copyErrors(member.getEmail(), member);
        copyErrors(member.getGroupName(), member);
        copyErrors(member.getType(), member);
        copyErrors(member.getSalesforceId(), member);

        if (member.getErrors().isEmpty()) {
            member = membersManager.updateMemeber(member);
        }

        return member;
    }

    @RequestMapping(value = "/find-client.json", method = RequestMethod.GET)
    public @ResponseBody
    Client findClient(@RequestParam("orcid") String orcid) {
        Client result = new Client();
        
        if(PojoUtil.isEmpty(orcid)) {
            result.getErrors().add(getMessage("manage_member.not_blank"));
        } else {
            result = membersManager.getClient(orcid);
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
        if(client.getAuthenticationProviderId() != null) {
            validateIdP(client);
            copyErrors(client.getAuthenticationProviderId(), client);
        }
        
        for (RedirectUri redirectUri : client.getRedirectUris()) {
            copyErrors(redirectUri, client);
        }

        if (client.getErrors().isEmpty()) {
            client = membersManager.updateClient(client);
        }

        return client;
    }

    @RequestMapping(value = "/empty-redirect-uri.json", method = RequestMethod.GET)
    public @ResponseBody
    RedirectUri getEmptyRedirectUri() {
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        rUri.setValue(Text.valueOf(""));
        rUri.setActType(Text.valueOf(""));
        rUri.setGeoArea(Text.valueOf(""));
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
        MemberType[] groupTypes = MemberType.values();
        Map<String, String> groupTypesMap = new TreeMap<String, String>();

        for (MemberType groupType : groupTypes) {
            String key = groupType.value();
            String value = key.replace('-', ' ');
            groupTypesMap.put(key, value);
        }

        return groupTypesMap;
    }

    /**
     * VALIDATORS
     * */
    private void validateGroupEmail(Member group) {
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
                Map<String, String> ids = emailManager.findOricdIdsByCommaSeparatedEmails(newEmail);
                String orcidThatOwnsTheEmail = ids.get(newEmail);
                // If the email is not the same, it means the member cannot use
                // that email address
                if (!userOrcid.equals(orcidThatOwnsTheEmail)) {
                    setError(group.getEmail(), "group.email.already_used");
                }
            }
        }
    }

    private void validateGroupName(Member group) {
        group.getGroupName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(group.getGroupName())) {
            setError(group.getGroupName(), "NotBlank.group.name");
        } else if (group.getGroupName().getValue().length() > 150) {
            setError(group.getGroupName(), "group.name.too_long");
        }
    }

    private void validateGroupType(Member group) {
        group.getType().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(group.getType())) {
            setError(group.getType(), "NotBlank.group.type");
        } else {
            try {
                MemberType.fromValue(group.getType().getValue());
            } catch (IllegalArgumentException e) {
                setError(group.getType(), "group.type.invalid");
            }
        }
    }

    private void validateSalesforceId(Member group) {
        group.getSalesforceId().setErrors(new ArrayList<String>());
        if (group != null && !PojoUtil.isEmpty(group.getSalesforceId())) {
            if (group.getSalesforceId().getValue().length() != 15) {
                setError(group.getSalesforceId(), "group.salesforce_id.invalid_length");
            } else if (!group.getSalesforceId().getValue().matches(SALESFORCE_ID_PATTERN)) {
                setError(group.getSalesforceId(), "group.salesforce_id.invalid");
            }
        }
    }    
    
    private void validateIdP(Client client) {
        if(client != null) {
            if(!PojoUtil.isEmpty(client.getAuthenticationProviderId())) {
                client.getAuthenticationProviderId().setErrors(new ArrayList<String>());
                boolean redirectUriFound = false;
                if(client.getRedirectUris() != null) {
                    for(RedirectUri rUri : client.getRedirectUris()) {
                        if(RedirectUriType.INSTITUTIONAL_SIGN_IN.value().equals(rUri.getType().getValue())) {
                            redirectUriFound = true;
                        }
                    }
                }
                if(!redirectUriFound) {                    
                    setError(client.getAuthenticationProviderId(), "manage.developer_tools.client.idp.error.no_redirect_uri_found");
                }
            }
        }
    }
}

class ResultContainer implements Serializable {
    private static final long serialVersionUID = -3832431757948716851L;
    
    boolean isClient = false;
    Client clientObject;
    Member memberObject;

    public boolean isClient() {
        return isClient;
    }
    public void setClient(boolean isClient) {
        this.isClient = isClient;
    }
    public Client getClientObject() {
        return clientObject;
    }
    public void setClientObject(Client clientObject) {
        this.clientObject = clientObject;
    }
    public Member getMemberObject() {
        return memberObject;
    }
    public void setMemberObject(Member memberObject) {
        this.memberObject = memberObject;
    }
}