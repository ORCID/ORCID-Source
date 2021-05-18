package org.orcid.frontend.web.controllers;

import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.groupIds.issn.IssnPortalUrlBuilder;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.read_only.*;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.frontend.web.pagination.ResearchResourcePaginator;
import org.orcid.frontend.web.pagination.WorksPaginator;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.v3.rc1.common.OrcidType;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.*;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PublicRecord;
import org.orcid.pojo.ajaxForm.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PublicRecordController extends BaseWorkspaceController {

    @Resource(name = "membersManagerV3")
    MembersManager membersManager;

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Resource(name = "peerReviewManagerReadOnlyV3")
    private PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Resource(name = "profileFundingManagerReadOnlyV3")
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Resource
    private WorksPaginator worksPaginator;

    @Resource(name = "activityManagerV3")
    private ActivityManager activityManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "groupIdRecordManagerReadOnlyV3")
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

    @Resource(name = "personalDetailsManagerReadOnlyV3")
    private PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Resource(name = "sourceUtilsV3")
    private SourceUtils sourceUtils;

    @Resource(name = "affiliationsManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Resource
    private ResearchResourcePaginator researchResourcePaginator;

    @Resource(name = "researchResourceManagerReadOnlyV3")
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnly;

    @Resource
    private IssnPortalUrlBuilder issnPortalUrlBuilder;

    @Resource(name = "emailManagerReadOnlyV3")
    protected EmailManagerReadOnly emailManagerReadOnly;

    @Resource(name = "addressManagerReadOnlyV3")
    private AddressManagerReadOnly addressManagerReadOnly;

    @Resource(name = "profileKeywordManagerReadOnlyV3")
    private ProfileKeywordManagerReadOnly keywordManagerReadOnly;

    @Resource(name = "researcherUrlManagerReadOnlyV3")
    private ResearcherUrlManagerReadOnly researcherUrlManagerReadOnly;

    @Resource(name = "externalIdentifierManagerReadOnlyV3")
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;

    public static int ORCID_HASH_LENGTH = 8;
    private static final String PAGE_SIZE_DEFAULT = "50";

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/public-record.json", method = RequestMethod.GET)
    public @ResponseBody
    PublicRecord getPublicRecord(@PathVariable("orcid") String orcid) {
        PublicRecord publicRecord = new PublicRecord();
        Boolean isDeprecated = false;

        try {
            // Check if the profile is deprecated or locked
            orcidSecurityManager.checkProfile(orcid);
        } catch (LockedException | DeactivatedException e) {
            publicRecord.setDisplayName(localeManager.resolveMessage("public_profile.deactivated.given_names") + " "
                    + localeManager.resolveMessage("public_profile.deactivated.family_name"));
            return publicRecord;
        } catch (OrcidNotClaimedException e) {
            publicRecord.setDisplayName(localeManager.resolveMessage("orcid.reserved_for_claim"));
            return publicRecord;
        } catch (OrcidDeprecatedException e) {
            isDeprecated = true;

        }

        publicRecord = getRecord(orcid);
        if (isDeprecated) {
            // If deprecated be sure to remove all fields
            publicRecord.setBiography(null);
            publicRecord.setOtherNames(null);
            publicRecord.setCountries(null);
            publicRecord.setKeyword(null);
            publicRecord.setEmails(null);
            publicRecord.setExternalIdentifier(null);
        }

        // If the id belongs to a group the name field is removed
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (OrcidType.GROUP.name().equals(profile.getOrcidType())) {
            publicRecord.setDisplayName(null);
        }

        return publicRecord;
    }

    public @ResponseBody
    PublicRecord getRecord(String orcid) {
        PublicRecord publicRecord = new PublicRecord();

        PersonalDetails publicPersonalDetails = personalDetailsManagerReadOnly.getPublicPersonalDetails(orcid);
        // Fill personal details
        if (publicPersonalDetails != null) {
            // Get display name
            String displayName = "";

            if (publicPersonalDetails.getName() != null) {
                Name name = publicPersonalDetails.getName();
                if (name.getVisibility().equals(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC)) {
                    if (name.getCreditName() != null && !PojoUtil.isEmpty(name.getCreditName().getContent())) {
                        displayName = name.getCreditName().getContent();
                    } else {
                        if (name.getGivenNames() != null && !PojoUtil.isEmpty(name.getGivenNames().getContent())) {
                            displayName = name.getGivenNames().getContent() + " ";
                        }
                        if (name.getFamilyName() != null && !PojoUtil.isEmpty(name.getFamilyName().getContent())) {
                            displayName += name.getFamilyName().getContent();
                        }
                    }
                    publicRecord.setNames(NamesForm.valueOf(name));
                }
            }

            if (!PojoUtil.isEmpty(displayName)) {
                // <Published Name> (<ORCID iD>) - ORCID | Connecting Research
                // and Researchers
                publicRecord.setTitle(displayName + " (" + orcid + ") - " + getMessage("layout.public-layout.title"));
                publicRecord.setDisplayName(displayName);
            }

            // Get biography
            if (publicPersonalDetails.getBiography() != null) {
                Biography bio = publicPersonalDetails.getBiography();
                if (org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC.equals(bio.getVisibility()) && !PojoUtil.isEmpty(bio.getContent())) {
                    publicRecord.setBiography(BiographyForm.valueOf(bio));
                }
            }

            // Fill other names
            OtherNames publicOtherNames = publicPersonalDetails.getOtherNames();
            if (publicOtherNames != null && publicOtherNames.getOtherNames() != null) {
                Iterator<OtherName> it = publicOtherNames.getOtherNames().iterator();
                while (it.hasNext()) {
                    OtherName otherName = it.next();
                    if (!org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC.equals(otherName.getVisibility())) {
                        it.remove();
                    }
                }
            }
            publicRecord.setOtherNames(OtherNamesForm.valueOf(publicOtherNames));
        }

        // Fill country
        Addresses publicAddresses;
        publicAddresses = addressManagerReadOnly.getPublicAddresses(orcid);
        if (publicAddresses != null && publicAddresses.getAddress() != null) {
            AddressesForm form = AddressesForm.valueOf(publicAddresses);
            // Set country name
            if (form != null && form.getAddresses() != null) {
                Map<String, String> countries = retrieveIsoCountries();
                for (AddressForm addressForm : form.getAddresses()) {
                    addressForm.setCountryName(countries.get(addressForm.getIso2Country().getValue().name()));
                }
            }
            publicRecord.setCountries(form);
        }

        // Fill keywords
        Keywords publicKeywords;
        publicKeywords = keywordManagerReadOnly.getPublicKeywords(orcid);
        publicRecord.setKeyword(KeywordsForm.valueOf(publicKeywords));

        // Fill researcher urls
        ResearcherUrls publicResearcherUrls;
        publicResearcherUrls = researcherUrlManagerReadOnly.getPublicResearcherUrls(orcid);

        publicRecord.setWebsite(WebsitesForm.valueOf(publicResearcherUrls));

        // Fill emails
        Emails publicEmails;
        publicEmails = emailManagerReadOnly.getPublicEmails(orcid);

        Emails filteredEmails = new Emails();
        if (Features.HIDE_UNVERIFIED_EMAILS.isActive()) {
            filteredEmails.setEmails(new ArrayList<Email>(publicEmails.getEmails().stream().filter(e -> e.isVerified()).collect(Collectors.toList())));
        } else {
            filteredEmails.setEmails(new ArrayList<Email>(publicEmails.getEmails()));
        }

        publicRecord.setEmails(org.orcid.pojo.ajaxForm.Emails.valueOf(filteredEmails));

        // Fill external identifiers
        PersonExternalIdentifiers publicPersonExternalIdentifiers;
        publicPersonExternalIdentifiers = externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(orcid);

        publicRecord.setExternalIdentifier(ExternalIdentifiersForm.valueOf(publicPersonExternalIdentifiers));

        Long lastModifiedTime = getLastModifiedTime(orcid);

        publicRecord.setLastModifiedTime(new java.util.Date(lastModifiedTime));

        return publicRecord;
    }

    private Long getLastModifiedTime(String orcid) {
        return profileEntityManager.getLastModified(orcid);
    }
}
