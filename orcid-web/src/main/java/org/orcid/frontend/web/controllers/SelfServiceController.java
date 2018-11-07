package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactPermission;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.core.salesforce.model.SubMember;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.ContactsForm;
import org.orcid.pojo.ajaxForm.MemberDetailsForm;
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

    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;
    
    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;

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
        return new ModelAndView("redirect:/self-service/" + salesForceManager.retrievePrimaryAccountIdByOrcid(getCurrentUserOrcid()));
    }

    @RequestMapping("/{accountId}")
    public ModelAndView getManageConsortiumPage(@PathVariable(required = false) String accountId) {
        ModelAndView mav = new ModelAndView("self_service");
        return mav;
    }

    @RequestMapping("/{accountId}/all-consortium-contacts")
    public ModelAndView getAllConsortiumContactsPage(@PathVariable(required = true) String accountId) {
        ModelAndView mav = new ModelAndView("all_consortium_contacts");
        return mav;
    }
    
    @RequestMapping(value = "/{accountId}/all-consortium-contacts-download", method = RequestMethod.GET, produces = "text/csv")
    public void getAmbiguousOrgs(HttpServletResponse response, @PathVariable(required = true) String accountId) throws IOException {
        checkAccess(accountId);
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"all_consortium_contacts.csv\"");
        salesForceManager.writeContactsCsv(response.getWriter(), salesForceManager.retrieveSubMemberContactsByConsortiumId(accountId));
    }
    
    @RequestMapping(value = "/validate-member-details-name", method = RequestMethod.POST)
    public @ResponseBody MemberDetailsForm validateMemberDetailsName(@RequestBody MemberDetailsForm consortium) {
        // validate name isn't blank
        consortium.getName().setErrors(new ArrayList<String>());
        if (consortium.getName().getValue() == null || consortium.getName().getValue().trim().isEmpty()) {
            setError(consortium.getName(), "manage_consortium.add_submember_name_required");
        }
        return consortium;
    }

    @RequestMapping(value = "/validate-member-details-website.json", method = RequestMethod.POST)
    public @ResponseBody MemberDetailsForm validateMemberDetailsWebsite(@RequestBody MemberDetailsForm consortium) {
        // validate website url format
        consortium.getWebsite().setErrors(new ArrayList<String>());
        validateUrl(consortium.getWebsite(), "manage_consortium.add_submember_website_valid_format");
        return consortium;
    }

    @RequestMapping(value = "/validate-member-details-email.json", method = RequestMethod.POST)
    public @ResponseBody MemberDetailsForm validateMemberDetailsEmail(@RequestBody MemberDetailsForm consortium) {
        // if email address exists validate format
        consortium.getEmail().setErrors(new ArrayList<String>());
        if (consortium.getEmail().getValue() != null && !consortium.getEmail().getValue().trim().isEmpty()) {
            if (!super.validateEmailAddress(consortium.getEmail().getValue())) {
                setError(consortium.getEmail(), "manage_consortium.email_valid_format");
            }
        }
        return consortium;
    }

    @RequestMapping(value = "/validate-member-details-description.json", method = RequestMethod.POST)
    public @ResponseBody MemberDetailsForm validateMemberDetailsDescription(@RequestBody MemberDetailsForm consortium) {
        // validate description length
        consortium.getDescription().setErrors(new ArrayList<String>());
        super.validateNoLongerThan(600, consortium.getDescription());
        return consortium;
    }

    @RequestMapping(value = "/validate-member-details-community.json", method = RequestMethod.POST)
    public @ResponseBody MemberDetailsForm validateMemberDetailsCommunity(@RequestBody MemberDetailsForm consortium) {
        // validate community isn't blank
        consortium.getCommunity().setErrors(new ArrayList<String>());
        if (consortium.getCommunity().getValue() == null || consortium.getCommunity().getValue().trim().isEmpty()) {
            setError(consortium.getCommunity(), "manage_consortium.community_please_choose");
        }
        return consortium;
    }

    @RequestMapping(value = "/validate-member-details.json", method = RequestMethod.POST)
    public @ResponseBody MemberDetailsForm validateMemberDetails(@RequestBody MemberDetailsForm consortium) {
        validateMemberDetailsFields(consortium);
        return consortium;
    }

    public void validateMemberDetailsFields(MemberDetailsForm consortium) {
        consortium.setErrors(new ArrayList<String>());

        validateMemberDetailsName(consortium);
        validateMemberDetailsWebsite(consortium);
        validateMemberDetailsEmail(consortium);
        validateMemberDetailsDescription(consortium);
        validateMemberDetailsCommunity(consortium);

        copyErrors(consortium.getName(), consortium);
        copyErrors(consortium.getWebsite(), consortium);
        copyErrors(consortium.getEmail(), consortium);
        copyErrors(consortium.getDescription(), consortium);
        copyErrors(consortium.getCommunity(), consortium);

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
        contactsForm.setPermissionsByContactRoleId(ContactPermission.mapByContactRoleId(salesForceManager.calculateContactPermissions(contactsList)));
        contactsForm.setRoleMap(generateSalesForceRoleMap());
        return contactsForm;
    }
    
    @RequestMapping(value = "/get-sub-member-contacts.json", method = RequestMethod.GET)
    public @ResponseBody List<Contact> getSubMemberContacts(@RequestParam("accountId") String accountId) {
        checkAccess(accountId);
        List<Contact> contactsList = salesForceManager.retrieveSubMemberContactsByConsortiumId(accountId);
        salesForceManager.addOrcidsToContacts(contactsList);
        return contactsList;
    }

    @RequestMapping(value = "/add-contact-by-email.json")
    public @ResponseBody Contact addContactByEmail(@RequestBody Contact contact) {
        checkFullAccess(contact.getAccountId());
        EmailEntity emailEntity = emailManager.find(contact.getEmail());
        contact.setOrcid(emailEntity.getProfile().getId());
        RecordNameEntity recordNameEntity = emailEntity.getProfile().getRecordNameEntity();
        if (Visibility.PUBLIC.name().equals(recordNameEntity.getVisibility())) {
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
        salesForceManager.removeContact(contact);
        return contact;
    }

    @RequestMapping(value = "/update-contacts.json", method = RequestMethod.POST)
    public @ResponseBody ContactsForm updateContacts(@RequestBody ContactsForm contactsForm) {
        String accountId = contactsForm.getAccountId();
        checkFullAccess(accountId);
        validateContacts(contactsForm);
        if (contactsForm.getErrors().isEmpty()) {
            salesForceManager.updateContacts(contactsForm.getContactsList());
            return getContacts(accountId);
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
    
    @RequestMapping(value = "/get-org-ids.json", method = RequestMethod.GET)
    public @ResponseBody List<OrgId> getOrgIds(@RequestParam("accountId") String accountId) {
        checkAccess(accountId);
        return salesForceManager.retrieveOrgIdsByAccountId(accountId);
    }

    @RequestMapping(value = "/add-org-id.json", method = RequestMethod.POST)
    public @ResponseBody OrgId addOrgId(@RequestBody OrgId orgId) {
        checkFullAccess(orgId.getAccountId());
        salesForceManager.createOrgId(orgId);
        return orgId;
    }
    
    @RequestMapping(value = "/remove-org-id.json", method = RequestMethod.POST)
    public @ResponseBody void removeOrgId(@RequestBody OrgId orgId) {
        checkFullAccess(orgId.getAccountId());
        salesForceManager.removeOrgId(orgId);
    }

    @RequestMapping(value = "/validate-sub-member-initial-contact-email.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm validateSubMemberInitialContactEmail(@RequestBody SubMemberForm subMember) {
        // validate initial contact email is correct format
        subMember.getInitialContactEmail().setErrors(new ArrayList<String>());
        if (!super.validateEmailAddress(subMember.getInitialContactEmail().getValue())) {
            setError(subMember.getInitialContactEmail(), "manage_consortium.email_valid_format");
        }
        return subMember;
    }

    @RequestMapping(value = "/validate-sub-member-initial-contact-first-name.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm validateSubMemberInitialContactFirstName(@RequestBody SubMemberForm subMember) {
        // validate initial contact first name isn't blank
        subMember.getInitialContactFirstName().setErrors(new ArrayList<String>());
        if (subMember.getInitialContactFirstName().getValue() == null || subMember.getInitialContactFirstName().getValue().trim().isEmpty()) {
            setError(subMember.getInitialContactFirstName(), "manage_consortium.add_submember_error_initial_contact_first_name");
        }

        return subMember;
    }

    @RequestMapping(value = "/validate-sub-member-initial-contact-last-name.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm validateSubMemberInitialContactLastName(@RequestBody SubMemberForm subMember) {
        // validate initial contact last name isn't blank
        subMember.getInitialContactLastName().setErrors(new ArrayList<String>());
        if (subMember.getInitialContactLastName().getValue() == null || subMember.getInitialContactLastName().getValue().trim().isEmpty()) {
            setError(subMember.getInitialContactLastName(), "manage_consortium.add_submember_error_initial_contact_last_name");
        }

        return subMember;
    }

    @RequestMapping(value = "/validate-sub-member-name.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm validateSubMemberName(@RequestBody SubMemberForm subMember) {
        // validate name isn't blank
        subMember.getName().setErrors(new ArrayList<String>());
        if (subMember.getName().getValue() == null || subMember.getName().getValue().trim().isEmpty()) {
            setError(subMember.getName(), "manage_consortium.add_submember_name_required");
        }

        return subMember;

    }

    @RequestMapping(value = "/validate-sub-member-website.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm validateSubMemberWebsite(@RequestBody SubMemberForm subMember) {

        // validate website isn't blank
        subMember.getWebsite().setErrors(new ArrayList<String>());

        if (subMember.getWebsite().getValue() == null || subMember.getWebsite().getValue().trim().isEmpty()) {
            setError(subMember.getWebsite(), "manage_consortium.add_submember_website_required");
        }
        // validate website url
        validateUrl(subMember.getWebsite(), "manage_consortium.add_submember_website_valid_format");
        
        return subMember;
    }

    @RequestMapping(value = "/validate-sub-member.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm validateSubMember(@RequestBody SubMemberForm subMember) {
        validateAddSubMemberFields(subMember);
        return subMember;
    }

    @RequestMapping(value = "/check-existing-sub-member.json", method = RequestMethod.POST)
    public @ResponseBody Member checkExistingMember(@RequestBody SubMemberForm subMember) {
        Optional<Member> existingMember = salesForceManager.checkExistingMember(subMember.toMember());
        Member tempMember = new Member();
        if (existingMember.isPresent()) {
            tempMember.setPublicDisplayName(existingMember.get().getPublicDisplayName());
            tempMember.setWebsiteUrl(existingMember.get().getWebsiteUrl());
            tempMember.setId(existingMember.get().getId());
        } else {
            tempMember.setPublicDisplayName(null);
            tempMember.setWebsiteUrl(null);
            tempMember.setId(null);
        }
        return tempMember;
    }

    @RequestMapping(value = "/add-sub-member.json", method = RequestMethod.POST)
    public @ResponseBody SubMemberForm addSubMember(@RequestBody SubMemberForm subMember) {
        checkFullAccess(subMember.getParentAccountId());
        salesForceManager.createMember(subMember.toMember(), subMember.toContact());
        return subMember;
    }

    public void validateAddSubMemberFields(SubMemberForm subMember) {
        subMember.setErrors(new ArrayList<String>());
        boolean subMemberExists = false;
        subMemberExists = salesForceManager.checkExistingSubMember(subMember.toMember(), subMember.getParentAccountId());

        if (subMemberExists == true) {
            subMember.getErrors().add(getMessage("manage_consortium.add_submember_member_exists"));
        }

        validateSubMemberName(subMember);
        validateSubMemberWebsite(subMember);
        validateSubMemberInitialContactEmail(subMember);
        validateSubMemberInitialContactFirstName(subMember);
        validateSubMemberInitialContactLastName(subMember);

        copyErrors(subMember.getName(), subMember);
        copyErrors(subMember.getWebsite(), subMember);
        copyErrors(subMember.getInitialContactEmail(), subMember);
        copyErrors(subMember.getInitialContactFirstName(), subMember);
        copyErrors(subMember.getInitialContactLastName(), subMember);

    }

    @RequestMapping(value = "/remove-sub-member.json", method = RequestMethod.POST)
    public @ResponseBody void removeSubMember(@RequestBody SubMember subMember) {
        checkFullAccess(subMember.getParentAccountId());
        salesForceManager.flagOpportunityAsRemovalRequested(subMember.getOpportunity());
    }

    @RequestMapping(value = "/cancel-sub-member-removal.json", method = RequestMethod.POST)
    public @ResponseBody void cancelSubMemberRemoval(@RequestBody SubMember subMember) {
        checkFullAccess(subMember.getParentAccountId());
        salesForceManager.flagOpportunityAsRemovalNotRequested(subMember.getOpportunity());
    }

    @RequestMapping(value = "/cancel-sub-member-addition.json", method = RequestMethod.POST)
    public @ResponseBody void cancelSubMemberAddition(@RequestBody SubMember subMember) {
        checkFullAccess(subMember.getParentAccountId());
        salesForceManager.removeOpportunity(subMember.getOpportunity());
    }
    
    @RequestMapping(value = "/disambiguated/search", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> searchDisambiguated(@RequestParam("q") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, String>> datums = new ArrayList<>();
        for (OrgDisambiguated orgDisambiguated : orgDisambiguatedManager.searchOrgsFromSolrForSelfService(query, 0, limit)) {
            datums.add(orgDisambiguated.toMap());
        }
        return datums;
    }

    private void checkFullAccess(String memberId) {
        if (!isAllowedFullAccess(memberId)) {
            throw new OrcidUnauthorizedException("You are not authorized for full access to account ID = " + memberId);
        }
    }

    private boolean isAllowedFullAccess(String memberId) {
        List<String> usersAuthorizedAccountIds = salesForceManager.retrieveAccountIdsByOrcid(sourceManager.retrieveSourceOrcid());
        return usersAuthorizedAccountIds.contains(memberId);
    }

    private void checkAccess(String memberId) {
        List<String> usersAuthorizedAccountIds = salesForceManager.retrieveAccountIdsByOrcid(sourceManager.retrieveSourceOrcid());
        MemberDetails memberDetails = salesForceManager.retrieveDetails(memberId);
        if (!(usersAuthorizedAccountIds.contains(memberId) || usersAuthorizedAccountIds.contains(memberDetails.getMember().getConsortiumLeadId()))) {
            throw new OrcidUnauthorizedException("You are not authorized for account ID = " + memberId);
        }
    }

}
