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
import org.orcid.pojo.ajaxForm.MemberDetailsForm;
import org.orcid.pojo.ajaxForm.ContactsForm;
import org.orcid.pojo.ajaxForm.SubMemberForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Will Simpson
 *
 */
@Controller
@RequestMapping(value = { "/self-service", "/manage-consortium", })
public class SelfServiceController extends BaseController {

    private static final String NOT_PUBLIC = "not public";

    @Resource
    private SalesForceManager salesForceManager;

    @Resource
    private EmailManager emailManager;

    @ModelAttribute("contactRoleTypes")
    public Map<String, String> retrieveContactRoleTypesAsMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (ContactRoleType type : ContactRoleType.values()) {
            map.put(type.name(), getMessage(buildInternationalizationKey(ContactRoleType.class, type.name())));
        }

        Map<String, String> sorted = new LinkedHashMap<>();
        // @formatter:off
        map.entrySet().stream()
        .sorted(Map.Entry.<String, String> comparingByValue())
        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        // @formatter:on
        return sorted;
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
        return new ModelAndView("redirect:/self-service/" + salesForceManager.retrieveAccountIdByOrcid(getCurrentUserOrcid()));
    }

    @RequestMapping("/{accountId}")
    public ModelAndView getManageConsortiumPage(@PathVariable(required = false) String accountId) {
        ModelAndView mav = new ModelAndView("self_service");
        return mav;
    }

    @RequestMapping(value = "/get-member-details.json", method = RequestMethod.GET)
    public @ResponseBody MemberDetailsForm getConsortium(@RequestParam("accountId") String accountId) {
        checkAccess(accountId);
        MemberDetails memberDetails = salesForceManager.retrieveDetails(accountId);
        MemberDetailsForm consortiumForm = MemberDetailsForm.fromMemberDetails(memberDetails);
        consortiumForm.setAllowedFullAccess(isAllowedFullAccess(accountId));
        return consortiumForm;
    }

    @RequestMapping(value = "/update-member-details.json", method = RequestMethod.POST)
    public @ResponseBody MemberDetailsForm updateMemberDetails(@RequestBody MemberDetailsForm consortium) {
        MemberDetails memberDetails = consortium.toMemberDetails();
        Member member = memberDetails.getMember();
        checkAccess(member.getId());
        salesForceManager.updateMember(member);
        return consortium;
    }

    @RequestMapping(value = "/get-contacts.json", method = RequestMethod.GET)
    public @ResponseBody ContactsForm getContacts(@RequestParam("accountId") String accountId) {
        checkAccess(accountId);
        ContactsForm contactsForm = new ContactsForm();
        List<Contact> contactsList = salesForceManager.retrieveContactsByAccountId(accountId);
        salesForceManager.addOrcidsToContacts(contactsList);
        contactsForm.setContactsList(contactsList);
        contactsForm.setRoleMap(generateSalesForceRoleMap());
        return contactsForm;
    }

    @RequestMapping(value = "/add-contact-by-email.json")
    public @ResponseBody Contact addContactByEmail(@RequestBody Contact contact) {
        checkFullAccess(contact.getAccountId());
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
        checkFullAccess(contact.getAccountId());
        salesForceManager.createContact(contact);
        return contact;
    }

    @RequestMapping(value = "/remove-contact.json", method = RequestMethod.POST)
    public @ResponseBody Contact removeContact(@RequestBody Contact contact) {
        checkFullAccess(contact.getAccountId());
        salesForceManager.removeContactRole(contact);
        return contact;
    }

    @RequestMapping(value = "/update-contact.json", method = RequestMethod.POST)
    public @ResponseBody Contact updateContact(@RequestBody Contact contact) {
        checkFullAccess(contact.getAccountId());
        salesForceManager.updateContact(contact);
        return contact;
    }

    @RequestMapping(value = "/update-contacts.json", method = RequestMethod.POST)
    public @ResponseBody ContactsForm updateContacts(@RequestBody ContactsForm contactsForm) {
        checkFullAccess(contactsForm.getAccountId());
        validateContacts(contactsForm);
        if (contactsForm.getErrors().isEmpty()) {
            salesForceManager.updateContacts(contactsForm.getContactsList());
            return getContacts(salesForceManager.retrieveAccountIdByOrcid(getCurrentUserOrcid()));
        } else {
            return contactsForm;
        }
    }

    @RequestMapping(value = "/validate-contacts.json", method = RequestMethod.POST)
    public @ResponseBody ContactsForm validateContacts(@RequestBody ContactsForm contactsForm) {
        List<String> errors = contactsForm.getErrors();
        errors.clear();
        int agreementSignatoryContactCount = 0;
        int mainContactCount = 0;
        int votingContactCount = 0;
        for (Contact contact : contactsForm.getContactsList()) {
            if (ContactRoleType.AGREEMENT_SIGNATORY.equals(contact.getRole().getRoleType())) {
                agreementSignatoryContactCount++;
            }
            if (ContactRoleType.MAIN_CONTACT.equals(contact.getRole().getRoleType())) {
                mainContactCount++;
            }
            if (contact.getRole().isVotingContact()) {
                votingContactCount++;
            }
        }
        if (agreementSignatoryContactCount == 0) {
            errors.add(getMessage("manage_consortium.contacts_must_have_agreement_signatory_contact"));
        }
        if (agreementSignatoryContactCount > 1) {
            errors.add(getMessage("manage_consortium.contacts_must_not_have_more_than_one_agreement_signatory_contact"));
        }
        if (mainContactCount == 0) {
            errors.add(getMessage("manage_consortium.contacts_must_have_main_contact"));
        }
        if (mainContactCount > 1) {
            errors.add(getMessage("manage_consortium.contacts_must_not_have_more_than_one_main_contact"));
        }
        if (votingContactCount == 0) {
            errors.add(getMessage("manage_consortium.contacts_must_have_voting_contact"));
        }
        if (votingContactCount > 1) {
            errors.add(getMessage("manage_consortium.contacts_must_not_have_more_than_one_voting_contact"));
        }
        return contactsForm;
    }

    @RequestMapping(value = "/add-sub-member.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm addSubMember(@RequestBody SubMemberForm subMember) {
        checkFullAccess(subMember.getParentAccountId());
        salesForceManager.createMember(subMember.toMember());
        return subMember;
    }

    @RequestMapping(value = "/remove-sub-member.json", method = RequestMethod.POST)
    public @ResponseBody void removeSubMember(@RequestBody SubMember subMember) {
        checkFullAccess(subMember.getParentAccountId());
        salesForceManager.flagOpportunityAsClosed(subMember.getOpportunity().getId());
    }

    private void checkFullAccess(String memberId) {
        if (!isAllowedFullAccess(memberId)) {
            throw new OrcidUnauthorizedException("You are not authorized for full access to account ID = " + memberId);
        }
    }

    private boolean isAllowedFullAccess(String memberId) {
        String usersAuthorizedAccountId = salesForceManager.retrieveAccountIdByOrcid(sourceManager.retrieveSourceOrcid());
        return usersAuthorizedAccountId.equals(memberId);
    }

    private void checkAccess(String memberId) {
        String usersAuthorizedAccountId = salesForceManager.retrieveAccountIdByOrcid(sourceManager.retrieveSourceOrcid());
        MemberDetails memberDetails = salesForceManager.retrieveDetails(memberId);
        if (!(usersAuthorizedAccountId.equals(memberId) || usersAuthorizedAccountId.equals(memberDetails.getMember().getConsortiumLeadId()))) {
            throw new OrcidUnauthorizedException("You are not authorized for account ID = " + memberId);
        }
    }

}
