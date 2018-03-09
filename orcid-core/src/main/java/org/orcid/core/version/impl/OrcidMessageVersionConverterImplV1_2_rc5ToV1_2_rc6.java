package org.orcid.core.version.impl;

import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.CBA;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.CIT;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.CTX;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.ETHOS;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.HANDLE;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.HIR;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.OTHER_ID;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.PAT;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.SOURCE_WORK_ID;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.URI;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.URN;

import java.util.Arrays;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.ExternalIdSource;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ReferredBy;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkSource;
import org.orcid.utils.OrcidStringUtils;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_2_rc5ToV1_2_rc6 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.2_rc5";
    private static final String TO_VERSION = "1.2_rc6";
    private static final WorkExternalIdentifierType[] NEW_WORK_EXT_ID_TYPES = new WorkExternalIdentifierType[] { AGR, CBA, CIT, CTX, ETHOS, HANDLE, HIR, PAT,
            SOURCE_WORK_ID, URI, URN };
    static {
        // Just to be sure, for binary search
        Arrays.sort(NEW_WORK_EXT_ID_TYPES);
    }

    @Override
    public String getFromVersion() {
        return FROM_VERSION;
    }

    @Override
    public String getToVersion() {
        return TO_VERSION;
    }

    @Override
    public OrcidMessage downgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(FROM_VERSION);
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        downgradeProfile(orcidProfile);
        downgradeSearchResults(orcidMessage);
        return orcidMessage;
    }

    private void downgradeProfile(OrcidProfile orcidProfile) {
        if (orcidProfile != null) {
            if (orcidProfile.getOrcidBio() != null)
                if (orcidProfile.getOrcidBio().getExternalIdentifiers() != null)
                    for (ExternalIdentifier externalIdentifier : orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier()) {
                        Source source = externalIdentifier.getSource();
                        if (source != null) {
                            SourceOrcid sourceOrcid = source.getSourceOrcid();
                            if (sourceOrcid != null) {
                                externalIdentifier.setSource(null);
                                externalIdentifier.setExternalIdSource(new ExternalIdSource(sourceOrcid));
                            }
                        }
                    }
            if (orcidProfile.getOrcidActivities() != null) {
                if (orcidProfile.getOrcidActivities().getOrcidWorks() != null) {
                    for (OrcidWork act : orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork())
                        downGradeActivity(act);
                }
            }
            OrcidInternal orcidInternal = orcidProfile.getOrcidInternal();
            if (orcidInternal != null) {
                Preferences prefs = orcidInternal.getPreferences();
                if (prefs != null) {
                    prefs.setSendEmailFrequencyDays(null);
                    prefs.setSendMemberUpdateRequests(null);
                }
                ReferredBy referredBy = orcidInternal.getReferredBy();
                if (referredBy != null && !OrcidStringUtils.isValidOrcid(referredBy.getPath())) {
                    orcidInternal.setReferredBy(null);
                }
            }
        }
    }

    private void downGradeActivity(OrcidWork orcidWork) {
        Source source = orcidWork.getSource();
        if (source != null) {
            SourceOrcid sourceOrcid = source.getSourceOrcid();
            if (sourceOrcid != null) {
                orcidWork.setSource(null);
                orcidWork.setWorkSource(new WorkSource(sourceOrcid));
            }
        }
        WorkExternalIdentifiers externalIdentifiers = orcidWork.getWorkExternalIdentifiers();
        if (externalIdentifiers != null) {
            for (WorkExternalIdentifier wei : externalIdentifiers.getWorkExternalIdentifier()) {
                WorkExternalIdentifierType type = wei.getWorkExternalIdentifierType();
                if (type != null) {
                    if (Arrays.binarySearch(NEW_WORK_EXT_ID_TYPES, type) >= 0) {
                        wei.setWorkExternalIdentifierType(OTHER_ID);
                    }
                }
            }
        }
    }

    private void downgradeSearchResults(OrcidMessage orcidMessage) {
        OrcidSearchResults searchResults = orcidMessage.getOrcidSearchResults();
        if (searchResults != null) {
            for (OrcidSearchResult searchResult : searchResults.getOrcidSearchResult()) {
                downgradeProfile(searchResult.getOrcidProfile());
            }
        }
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(TO_VERSION);
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        upgradeProfile(orcidProfile);
        upgradeSearchResults(orcidMessage);
        return orcidMessage;
    }

    private void upgradeProfile(OrcidProfile orcidProfile) {
        if (orcidProfile != null) {
            if (orcidProfile.getOrcidBio() != null) {
                if (orcidProfile.getOrcidBio().getExternalIdentifiers() != null)
                    for (ExternalIdentifier externalIdentifier : orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier()) {
                        ExternalIdSource eis = externalIdentifier.getExternalIdSource();
                        if (eis != null) {
                            externalIdentifier.setSource(new Source(eis.getPath()));
                            externalIdentifier.setExternalIdSource(null);
                        }
                    }
            }
            if (orcidProfile.getOrcidActivities() != null) {
                if (orcidProfile.getOrcidActivities().getOrcidWorks() != null) {
                    for (OrcidWork act : orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork())
                        upgradeActivity(act);
                }
            }
        }
    }

    private void upgradeActivity(OrcidWork orcidWork) {
        WorkSource workSource = orcidWork.getWorkSource();
        if (workSource != null) {
            Source source = new Source();
            source.setSourceOrcid(new SourceOrcid(workSource));
        }
    }

    private void upgradeSearchResults(OrcidMessage orcidMessage) {
        OrcidSearchResults searchResults = orcidMessage.getOrcidSearchResults();
        if (searchResults != null) {
            for (OrcidSearchResult searchResult : searchResults.getOrcidSearchResult()) {
                upgradeProfile(searchResult.getOrcidProfile());
            }
        }
    }

}
