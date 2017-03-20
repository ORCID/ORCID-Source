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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.SubMember;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.ConsortiumForm;
import org.orcid.pojo.ajaxForm.SubMemberForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Will Simpson
 *
 */
@Controller
@RequestMapping(value = { "/manage-consortium" })
public class ManageConsortiumController extends BaseController {

    private static final String NOT_PUBLIC = "not public";

    @Resource
    private SalesForceManager salesForceManager;

    @Resource
    private EmailManager emailManager;

    @ModelAttribute("contactRoleTypes")
    public Map<String, String> retrieveContactRoleTypesAsMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (ContactRoleType type : ContactRoleType.values()) {
            map.put(type.name(), type.value());
        }
        return map;
    }

    @ModelAttribute("communityTypes")
    public Map<String, String> retrieveCommunityTypesAsMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (CommunityType type : CommunityType.values()) {
            map.put(type.name(), type.value());
        }
        return map;
    }

    @RequestMapping
    public ModelAndView getManageConsortiumPage() {
        ModelAndView mav = new ModelAndView("manage_consortium");
        return mav;
    }

    @RequestMapping(value = "/get-consortium.json", method = RequestMethod.GET)
    public @ResponseBody ConsortiumForm getConsortium() {
        String accountId = salesForceManager.retriveAccountIdByOrcid(getCurrentUserOrcid());
        MemberDetails memberDetails = salesForceManager.retrieveDetails(accountId);
        ConsortiumForm consortiumForm = ConsortiumForm.fromMemberDetails(memberDetails);
        List<Contact> contactsList = salesForceManager.retrieveContactsByAccountId(accountId);
        salesForceManager.addOrcidsToContacts(contactsList);
        consortiumForm.setContactsList(contactsList);
        consortiumForm.setRoleMap(generateSalesForceRoleMap());
        return consortiumForm;
    }

    @RequestMapping(value = "/update-consortium.json", method = RequestMethod.POST)
    public @ResponseBody ConsortiumForm updateConsortium(@RequestBody ConsortiumForm consortium) {
        MemberDetails memberDetails = consortium.toMemberDetails();
        String usersAuthorizedAccountId = salesForceManager.retriveAccountIdByOrcid(getCurrentUserOrcid());
        Member member = memberDetails.getMember();
        if (!usersAuthorizedAccountId.equals(member.getId())) {
            throw new OrcidUnauthorizedException("You are not authorized for account ID = " + member.getId());
        }
        salesForceManager.updateMember(member);
        return consortium;
    }

    @RequestMapping(value = "/add-contact-by-email.json")
    public @ResponseBody Contact addContactByEmail(@RequestBody Contact contact) {
        EmailEntity emailEntity = emailManager.findCaseInsensitive(contact.getEmail());
        contact.setOrcid(emailEntity.getProfile().getId());
        RecordNameEntity recordNameEntity = emailEntity.getProfile().getRecordNameEntity();
        if (Visibility.PUBLIC.equals(recordNameEntity.getVisibility())) {
            contact.setFirstName(recordNameEntity.getGivenNames());
            contact.setLastName(recordNameEntity.getFamilyName());
        } else {
            contact.setFirstName(NOT_PUBLIC);
            contact.setLastName(NOT_PUBLIC);
        }
        salesForceManager.createContact(contact);
        return contact;
    }

    @RequestMapping(value = "/add-contact.json", method = RequestMethod.POST)
    public @ResponseBody Contact addContact(@RequestBody Contact contact) {
        salesForceManager.createContact(contact);
        return contact;
    }

    @RequestMapping(value = "/remove-contact.json", method = RequestMethod.POST)
    public @ResponseBody Contact removeContact(@RequestBody Contact contact) {
        salesForceManager.removeContactRole(contact);
        return contact;
    }

    @RequestMapping(value = "/update-contact.json", method = RequestMethod.POST)
    public @ResponseBody Contact updateContact(@RequestBody Contact contact) {
        salesForceManager.updateContact(contact);
        return contact;
    }

    @RequestMapping(value = "/add-sub-member.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm addSubMember(@RequestBody SubMemberForm subMember) {
        salesForceManager.createMember(subMember.toMember());
        return subMember;
    }

    @RequestMapping(value = "/remove-sub-member.json", method = RequestMethod.POST)
    public @ResponseBody void removeSubMember(@RequestBody SubMember subMember) {
        salesForceManager.flagOpportunityAsClosed(subMember.getOpportunity().getId());
    }

}
