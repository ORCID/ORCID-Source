package org.orcid.listener.solr;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceItem;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceProposal;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceTitle;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.utils.NullUtils;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.orcid.utils.solr.entities.SolrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrcidRecordToSolrDocument {

    Logger LOG = LoggerFactory.getLogger(OrcidRecordToSolrDocument.class);

    private final boolean indexProfile;
    private final JAXBContext jaxbContext_3_0_api;

    public OrcidRecordToSolrDocument(boolean indexProfile) {
        this.indexProfile = indexProfile;
        try {
            this.jaxbContext_3_0_api = JAXBContext.newInstance(Record.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public OrcidSolrDocument convert(Record record, List<ResearchResource> researchResources) {
        OrcidSolrDocument profileIndexDocument = new OrcidSolrDocument();
        profileIndexDocument.setOrcid(record.getOrcidIdentifier().getPath());

        if (record.getHistory() != null) {
            if (record.getHistory().getLastModifiedDate() != null) {
                profileIndexDocument.setProfileLastModifiedDate(record.getHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime());
            }
            if (record.getHistory().getSubmissionDate() != null) {
                profileIndexDocument.setProfileSubmissionDate(record.getHistory().getSubmissionDate().getValue().toGregorianCalendar().getTime());
            }
        }

        if (record.getDeprecated() != null) {
            profileIndexDocument.setPrimaryRecord(
                    record.getDeprecated().getPrimaryRecord() != null ? record.getDeprecated().getPrimaryRecord().getOrcidIdentifier().getPath() : null);
        }

        if (record.getPerson() != null) {
            if (record.getPerson().getName() != null) {
                profileIndexDocument
                        .setFamilyName(record.getPerson().getName().getFamilyName() != null ? record.getPerson().getName().getFamilyName().getContent() : null);
                profileIndexDocument
                        .setGivenNames(record.getPerson().getName().getGivenNames() != null ? record.getPerson().getName().getGivenNames().getContent() : null);
                profileIndexDocument
                        .setCreditName(record.getPerson().getName().getCreditName() != null ? record.getPerson().getName().getCreditName().getContent() : null);

            }
            if(record.getPerson().getBiography() != null && !StringUtils.isBlank(record.getPerson().getBiography().getContent())) {
                profileIndexDocument.setBiography(record.getPerson().getBiography().getContent());
            }
            if (record.getPerson().getOtherNames() != null) {
                if (record.getPerson().getOtherNames().getOtherNames() != null && !record.getPerson().getOtherNames().getOtherNames().isEmpty()) {
                    List<String> names = new ArrayList<String>();
                    for (OtherName on : record.getPerson().getOtherNames().getOtherNames()) {
                        names.add(on.getContent());
                    }
                    profileIndexDocument.setOtherNames(names);
                }
            }

            if (record.getPerson().getEmails() != null && record.getPerson().getEmails().getEmails() != null) {
                for (Email e : record.getPerson().getEmails().getEmails()) {
                    profileIndexDocument.addEmailAddress(e.getEmail());
                }
            }

            if (record.getPerson().getExternalIdentifiers() != null && record.getPerson().getExternalIdentifiers().getExternalIdentifiers() != null) {
                List<String> extIdOrcids = new ArrayList<String>();
                List<String> extIdRefs = new ArrayList<String>();
                List<String> extIdTypeAndValue = new ArrayList<String>();
                List<String> extIdSourceAndRefs = new ArrayList<String>();
                for (PersonExternalIdentifier externalIdentifier : record.getPerson().getExternalIdentifiers().getExternalIdentifiers()) {
                    String sourcePath = null;
                    if (externalIdentifier.getSource() != null && externalIdentifier.getSource().retrieveSourcePath() != null) {
                        sourcePath = externalIdentifier.getSource().retrieveSourcePath();
                        extIdOrcids.add(sourcePath);
                    }
                    if (externalIdentifier.getValue() != null) {
                        extIdRefs.add(externalIdentifier.getValue());
                        extIdTypeAndValue.add(externalIdentifier.getType() + '=' + externalIdentifier.getValue());
                    }
                    if (NullUtils.noneNull(sourcePath, externalIdentifier.getValue())) {
                        extIdSourceAndRefs.add(sourcePath + "=" + externalIdentifier.getValue());
                    }
                }
                if (!extIdOrcids.isEmpty()) {
                    profileIndexDocument.setExternalIdSources(extIdOrcids);
                }
                if (!extIdRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdReferences(extIdRefs);
                }

                if (!extIdTypeAndValue.isEmpty()) {
                    profileIndexDocument.setExternalIdTypeAndValue(extIdTypeAndValue);
                }

                if (!extIdSourceAndRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdSourceReferences(extIdSourceAndRefs);
                }
            }

            if (record.getPerson() != null && record.getPerson().getKeywords() != null && record.getPerson().getKeywords().getKeywords() != null) {
                List<String> keywordValues = new ArrayList<String>();
                for (Keyword keyword : record.getPerson().getKeywords().getKeywords()) {
                    keywordValues.add(keyword.getContent());
                }
                profileIndexDocument.setKeywords(keywordValues);
            }

            // Activities ext ids
            Map<String, List<String>> allExternalIdentifiers = new HashMap<String, List<String>>();
            Map<String, List<String>> partOf = new HashMap<String, List<String>>();
            Map<String, List<String>> self = new HashMap<String, List<String>>();
            Map<String, List<String>> versionOf = new HashMap<String, List<String>>();

            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getWorks() != null
                    && record.getActivitiesSummary().getWorks().getWorkGroup() != null) {
                Set<String> workTitles = new HashSet<String>();
                for (WorkGroup wg : record.getActivitiesSummary().getWorks().getWorkGroup()) {
                    if (wg.getWorkSummary() != null) {
                        for (WorkSummary w : wg.getWorkSummary()) {
                            if (w.getExternalIdentifiers() != null && w.getExternalIdentifiers().getExternalIdentifier() != null) {
                                for (ExternalID id : w.getExternalIdentifiers().getExternalIdentifier()) {
                                    // old way
                                    if (!allExternalIdentifiers.containsKey(id.getType())) {
                                        allExternalIdentifiers.put(id.getType(), new ArrayList<String>());
                                    }
                                    if (!allExternalIdentifiers.get(id.getType()).contains(id.getValue())) {
                                        allExternalIdentifiers.get(id.getType()).add(id.getValue());
                                    }
                                    // new way
                                    if (Relationship.SELF.equals(id.getRelationship())) {
                                        if (!self.containsKey(id.getType() + SolrConstants.DYNAMIC_SELF)) {
                                            self.put(id.getType() + SolrConstants.DYNAMIC_SELF, new ArrayList<String>());
                                        }
                                        if (!self.get(id.getType() + SolrConstants.DYNAMIC_SELF).contains(id.getValue())) {
                                            self.get(id.getType() + SolrConstants.DYNAMIC_SELF).add(id.getValue());
                                        }
                                    }
                                    if (Relationship.PART_OF.equals(id.getRelationship())) {
                                        if (!partOf.containsKey(id.getType() + SolrConstants.DYNAMIC_PART_OF)) {
                                            partOf.put(id.getType() + SolrConstants.DYNAMIC_PART_OF, new ArrayList<String>());
                                        }
                                        if (!partOf.get(id.getType() + SolrConstants.DYNAMIC_PART_OF).contains(id.getValue())) {
                                            partOf.get(id.getType() + SolrConstants.DYNAMIC_PART_OF).add(id.getValue());
                                        }
                                    }
                                }
                            }
                            if (w.getTitle() != null) {
                                if (w.getTitle().getTitle() != null && StringUtils.isNotEmpty(w.getTitle().getTitle().getContent())) {
                                    workTitles.add(w.getTitle().getTitle().getContent());
                                }
                                if (w.getTitle().getSubtitle() != null && StringUtils.isNotEmpty(w.getTitle().getSubtitle().getContent())) {
                                    workTitles.add(w.getTitle().getSubtitle().getContent());
                                }
                                if (w.getTitle().getTranslatedTitle() != null && StringUtils.isNotEmpty(w.getTitle().getTranslatedTitle().getContent())) {
                                    workTitles.add(w.getTitle().getTranslatedTitle().getContent());
                                }
                            }
                        }
                    }
                }
                profileIndexDocument.setWorkTitles(new ArrayList<String>(workTitles));
            }

            Map<String, Set<String>> organisationIds = new HashMap<String, Set<String>>();
            organisationIds.put(SolrConstants.FUNDREF_ORGANISATION_ID, new HashSet<String>());
            organisationIds.put(SolrConstants.RINGGOLD_ORGANISATION_ID, new HashSet<String>());
            organisationIds.put(SolrConstants.GRID_ORGANISATION_ID, new HashSet<String>());
            Map<String, Set<String>> organisationNames = new HashMap<String, Set<String>>();
            organisationNames.put(SolrConstants.AFFILIATION_ORGANISATION_NAME, new HashSet<String>());
            organisationNames.put(SolrConstants.FUNDING_ORGANISATION_NAME, new HashSet<String>());
            organisationNames.put(SolrConstants.PEER_REVIEW_ORGANISATION_NAME, new HashSet<String>());
            organisationNames.put(SolrConstants.PEER_REVIEW_ORGANISATION_NAME, new HashSet<String>());
            organisationNames.put(SolrConstants.RESEARCH_RESOURCE_ITEM_HOSTS_NAME, new HashSet<String>());
            organisationNames.put(SolrConstants.RESEARCH_RESOURCE_PROPOSAL_HOSTS_NAME, new HashSet<String>());

            // Research resources
            if (researchResources != null && !researchResources.isEmpty()) {
                for (ResearchResource r : researchResources) {
                    if (r.getProposal() != null) {
                        ResearchResourceProposal proposal = r.getProposal();
                        if (proposal.getTitle() != null) {
                            List<String> proposalTitles = new ArrayList<String>();
                            ResearchResourceTitle t = proposal.getTitle();
                            if (t.getTitle() != null && StringUtils.isNotEmpty(t.getTitle().getContent())) {
                                proposalTitles.add(t.getTitle().getContent());
                            }
                            if (t.getTranslatedTitle() != null && StringUtils.isNotEmpty(t.getTranslatedTitle().getContent())) {
                                proposalTitles.add(t.getTranslatedTitle().getContent());
                            }
                            profileIndexDocument.setResearchResourceProposalTitles(proposalTitles);
                        }
                        if (proposal.getHosts() != null) {
                            for (Organization organization : proposal.getHosts().getOrganization()) {
                                organisationNames.get(SolrConstants.RESEARCH_RESOURCE_PROPOSAL_HOSTS_NAME).add(organization.getName());
                                if (organization.getDisambiguatedOrganization() != null) {
                                    String sourceType = organization.getDisambiguatedOrganization().getDisambiguationSource();
                                    if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                        organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                                .add(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                    } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                        organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                                .add(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                    } else if (SolrConstants.FUNDREF_ORG_TYPE.equals(sourceType)) {
                                        organisationIds.get(SolrConstants.FUNDREF_ORG_TYPE)
                                                .add(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                    }
                                }
                            }
                        }
                        if (proposal.getExternalIdentifiers() != null && proposal.getExternalIdentifiers().getExternalIdentifier() != null) {
                            for (ExternalID id : proposal.getExternalIdentifiers().getExternalIdentifier()) {
                                // old way
                                if (!allExternalIdentifiers.containsKey(id.getType())) {
                                    allExternalIdentifiers.put(id.getType(), new ArrayList<String>());
                                }
                                if (!allExternalIdentifiers.get(id.getType()).contains(id.getValue())) {
                                    allExternalIdentifiers.get(id.getType()).add(id.getValue());
                                }
                                // new way
                                if (org.orcid.jaxb.model.common.Relationship.SELF.equals(id.getRelationship())) {
                                    if (!self.containsKey(id.getType() + SolrConstants.DYNAMIC_SELF)) {
                                        self.put(id.getType() + SolrConstants.DYNAMIC_SELF, new ArrayList<String>());
                                    }
                                    if (!self.get(id.getType() + SolrConstants.DYNAMIC_SELF).contains(id.getValue())) {
                                        self.get(id.getType() + SolrConstants.DYNAMIC_SELF).add(id.getValue());
                                    }
                                } else if (org.orcid.jaxb.model.common.Relationship.PART_OF.equals(id.getRelationship())) {
                                    if (!partOf.containsKey(id.getType() + SolrConstants.DYNAMIC_PART_OF)) {
                                        partOf.put(id.getType() + SolrConstants.DYNAMIC_PART_OF, new ArrayList<String>());
                                    }
                                    if (!partOf.get(id.getType() + SolrConstants.DYNAMIC_PART_OF).contains(id.getValue())) {
                                        partOf.get(id.getType() + SolrConstants.DYNAMIC_PART_OF).add(id.getValue());
                                    }
                                } else if (org.orcid.jaxb.model.common.Relationship.VERSION_OF.equals(id.getRelationship())) {
                                    if (!versionOf.containsKey(id.getType() + SolrConstants.DYNAMIC_VERSION_OF)) {
                                        versionOf.put(id.getType() + SolrConstants.DYNAMIC_VERSION_OF, new ArrayList<String>());
                                    }
                                    if (!versionOf.get(id.getType() + SolrConstants.DYNAMIC_VERSION_OF).contains(id.getValue())) {
                                        versionOf.get(id.getType() + SolrConstants.DYNAMIC_VERSION_OF).add(id.getValue());
                                    }
                                }
                            }
                        }
                    }

                    if (r.getResourceItems() != null) {
                        List<String> itemNames = new ArrayList<String>();
                        profileIndexDocument.setResearchResourceItemNames(itemNames);
                        for (ResearchResourceItem item : r.getResourceItems()) {
                            itemNames.add(item.getName());
                            if (item.getHosts() != null) {
                                for (Organization organization : item.getHosts().getOrganization()) {
                                    organisationNames.get(SolrConstants.RESEARCH_RESOURCE_ITEM_HOSTS_NAME).add(organization.getName());
                                    if (organization.getDisambiguatedOrganization() != null) {
                                        String sourceType = organization.getDisambiguatedOrganization().getDisambiguationSource();
                                        if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                            organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                                    .add(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                        } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                            organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                                    .add(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                        } else if (SolrConstants.FUNDREF_ORG_TYPE.equals(sourceType)) {
                                            organisationIds.get(SolrConstants.FUNDREF_ORG_TYPE)
                                                    .add(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                        }
                                    }
                                }
                            }
                            for (ExternalID id : item.getExternalIdentifiers().getExternalIdentifier()) {
                                // old way
                                if (!allExternalIdentifiers.containsKey(id.getType())) {
                                    allExternalIdentifiers.put(id.getType(), new ArrayList<String>());
                                }
                                if (!allExternalIdentifiers.get(id.getType()).contains(id.getValue())) {
                                    allExternalIdentifiers.get(id.getType()).add(id.getValue());
                                }
                                // new way
                                if (org.orcid.jaxb.model.common.Relationship.SELF.equals(id.getRelationship())) {
                                    if (!self.containsKey(id.getType() + SolrConstants.DYNAMIC_SELF)) {
                                        self.put(id.getType() + SolrConstants.DYNAMIC_SELF, new ArrayList<String>());
                                    }
                                    if (!self.get(id.getType() + SolrConstants.DYNAMIC_SELF).contains(id.getValue())) {
                                        self.get(id.getType() + SolrConstants.DYNAMIC_SELF).add(id.getValue());
                                    }
                                } else if (org.orcid.jaxb.model.common.Relationship.PART_OF.equals(id.getRelationship())) {
                                    if (!partOf.containsKey(id.getType() + SolrConstants.DYNAMIC_PART_OF)) {
                                        partOf.put(id.getType() + SolrConstants.DYNAMIC_PART_OF, new ArrayList<String>());
                                    }
                                    if (!partOf.get(id.getType() + SolrConstants.DYNAMIC_PART_OF).contains(id.getValue())) {
                                        partOf.get(id.getType() + SolrConstants.DYNAMIC_PART_OF).add(id.getValue());
                                    }
                                } else if (org.orcid.jaxb.model.common.Relationship.VERSION_OF.equals(id.getRelationship())) {
                                    if (!versionOf.containsKey(id.getType() + SolrConstants.DYNAMIC_VERSION_OF)) {
                                        versionOf.put(id.getType() + SolrConstants.DYNAMIC_VERSION_OF, new ArrayList<String>());
                                    }
                                    if (!versionOf.get(id.getType() + SolrConstants.DYNAMIC_VERSION_OF).contains(id.getValue())) {
                                        versionOf.get(id.getType() + SolrConstants.DYNAMIC_VERSION_OF).add(id.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Peer reviews
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getPeerReviews() != null
                    && !record.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().isEmpty()) {
                Set<String> peerReviewTypes = new HashSet<String>();
                Set<String> peerReviewRoles = new HashSet<String>();
                Set<String> peerReviewGroups = new HashSet<String>();
                for (PeerReviewGroup g : record.getActivitiesSummary().getPeerReviews().getPeerReviewGroup()) {
                    for (PeerReviewDuplicateGroup dg : g.getPeerReviewGroup()) {
                        for (PeerReviewSummary s : dg.getPeerReviewSummary()) {
                            if (s.getType() != null) {
                                peerReviewTypes.add(s.getType().value());
                            }
                            if (s.getRole() != null) {
                                peerReviewRoles.add(s.getRole().value());
                            }
                            if (s.getGroupId() != null) {
                                peerReviewGroups.add(s.getGroupId());
                            }
                            // Index external identifiers
                            ExternalIDs extIds = s.getExternalIdentifiers();
                            if (extIds != null && !extIds.getExternalIdentifier().isEmpty()) {
                                for (ExternalID id : extIds.getExternalIdentifier()) {
                                    if (org.orcid.jaxb.model.common.Relationship.SELF.equals(id.getRelationship())) {
                                        if (!self.containsKey(id.getType() + SolrConstants.DYNAMIC_SELF)) {
                                            self.put(id.getType() + SolrConstants.DYNAMIC_SELF, new ArrayList<String>());
                                        }
                                        if (!self.get(id.getType() + SolrConstants.DYNAMIC_SELF).contains(id.getValue())) {
                                            self.get(id.getType() + SolrConstants.DYNAMIC_SELF).add(id.getValue());
                                        }
                                    } else if (org.orcid.jaxb.model.common.Relationship.PART_OF.equals(id.getRelationship())) {
                                        if (!partOf.containsKey(id.getType() + SolrConstants.DYNAMIC_PART_OF)) {
                                            partOf.put(id.getType() + SolrConstants.DYNAMIC_PART_OF, new ArrayList<String>());
                                        }
                                        if (!partOf.get(id.getType() + SolrConstants.DYNAMIC_PART_OF).contains(id.getValue())) {
                                            partOf.get(id.getType() + SolrConstants.DYNAMIC_PART_OF).add(id.getValue());
                                        }
                                    } else if (org.orcid.jaxb.model.common.Relationship.VERSION_OF.equals(id.getRelationship())) {
                                        if (!versionOf.containsKey(id.getType() + SolrConstants.DYNAMIC_VERSION_OF)) {
                                            versionOf.put(id.getType() + SolrConstants.DYNAMIC_VERSION_OF, new ArrayList<String>());
                                        }
                                        if (!versionOf.get(id.getType() + SolrConstants.DYNAMIC_VERSION_OF).contains(id.getValue())) {
                                            versionOf.get(id.getType() + SolrConstants.DYNAMIC_VERSION_OF).add(id.getValue());
                                        }
                                    }
                                }
                            }

                            // Index organization names
                            if (s.getOrganization() != null) {
                                organisationNames.get(SolrConstants.PEER_REVIEW_ORGANISATION_NAME).add(s.getOrganization().getName());
                                if (s.getOrganization().getDisambiguatedOrganization() != null) {
                                    String sourceType = s.getOrganization().getDisambiguatedOrganization().getDisambiguationSource();
                                    if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                        organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                                .add(s.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                    } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                        organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                                .add(s.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                    } else if (SolrConstants.FUNDREF_ORG_TYPE.equals(sourceType)) {
                                        organisationIds.get(SolrConstants.FUNDREF_ORGANISATION_ID)
                                                .add(s.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                    }
                                }
                            }
                        }
                    }
                }
                profileIndexDocument.setPeerReviewGroupId(peerReviewGroups);
                profileIndexDocument.setPeerReviewRole(peerReviewRoles);
                profileIndexDocument.setPeerReviewType(peerReviewTypes);
            }

            // Fundings
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getFundings() != null
                    && !record.getActivitiesSummary().getFundings().getFundingGroup().isEmpty()) {
                Set<String> fundingTitle = new HashSet<String>();
                Set<String> fundingGrantNumbers = new HashSet<String>();
                for (FundingGroup g : record.getActivitiesSummary().getFundings().getFundingGroup()) {
                    for (FundingSummary f : g.getFundingSummary()) {
                        if (f.getTitle() != null) {
                            if (f.getTitle().getTitle() != null && StringUtils.isNotEmpty(f.getTitle().getTitle().getContent())) {
                                fundingTitle.add(f.getTitle().getTitle().getContent());
                            }
                            if (f.getTitle().getTranslatedTitle() != null && StringUtils.isNotEmpty(f.getTitle().getTranslatedTitle().getContent())) {
                                fundingTitle.add(f.getTitle().getTranslatedTitle().getContent());
                            }
                        }
                        if (f.getExternalIdentifiers() != null && f.getExternalIdentifiers().getExternalIdentifier() != null) {
                            for (ExternalID id : f.getExternalIdentifiers().getExternalIdentifier()) {
                                if (id.getType().equals("grant_number")) {
                                    fundingGrantNumbers.add(id.getValue());
                                }
                            }
                        }
                        if (f.getOrganization() != null) {
                            organisationNames.get(SolrConstants.FUNDING_ORGANISATION_NAME).add(f.getOrganization().getName());
                            if (f.getOrganization().getDisambiguatedOrganization() != null)
                                organisationIds.get(SolrConstants.FUNDREF_ORGANISATION_ID)
                                        .add(f.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                        }
                    }
                }

                profileIndexDocument.setFundingTitles(new ArrayList<String>(fundingTitle));
                profileIndexDocument.setGrantNumbers(new ArrayList<String>(fundingGrantNumbers));
            }

            List<String> pastInstitutionAffiliationNames = new ArrayList<String>();
            List<String> currentInstitutionAffiliationNames = new ArrayList<String>();

            // Educations
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getEducations() != null
                    && !record.getActivitiesSummary().getEducations().getEducationGroups().isEmpty()) {
                for (AffiliationGroup<EducationSummary> g : record.getActivitiesSummary().getEducations().getEducationGroups()) {
                    for (EducationSummary e : g.getActivities()) {
                        setAffiliationNames(e, pastInstitutionAffiliationNames, currentInstitutionAffiliationNames);
                        if (e.getOrganization() != null) {
                            organisationNames.get(SolrConstants.AFFILIATION_ORGANISATION_NAME).add(e.getOrganization().getName());
                            if (e.getOrganization().getDisambiguatedOrganization() != null) {
                                String sourceType = e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource();
                                if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                }
                            }
                        }
                    }
                }
            }

            // Employments
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getEmployments() != null
                    && !record.getActivitiesSummary().getEmployments().getEmploymentGroups().isEmpty()) {
                for (AffiliationGroup<EmploymentSummary> g : record.getActivitiesSummary().getEmployments().getEmploymentGroups()) {
                    for (EmploymentSummary e : g.getActivities()) {
                        setAffiliationNames(e, pastInstitutionAffiliationNames, currentInstitutionAffiliationNames);
                        if (e.getOrganization() != null) {
                            organisationNames.get(SolrConstants.AFFILIATION_ORGANISATION_NAME).add(e.getOrganization().getName());
                            if (e.getOrganization().getDisambiguatedOrganization() != null) {
                                String sourceType = e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource();
                                if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                }
                            }
                        }
                    }
                }
            }

            // Distinctions
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getDistinctions() != null
                    && !record.getActivitiesSummary().getDistinctions().getDistinctionGroups().isEmpty()) {
                for (AffiliationGroup<DistinctionSummary> g : record.getActivitiesSummary().getDistinctions().getDistinctionGroups()) {
                    for (DistinctionSummary e : g.getActivities()) {
                        setAffiliationNames(e, pastInstitutionAffiliationNames, currentInstitutionAffiliationNames);
                        if (e.getOrganization() != null) {
                            organisationNames.get(SolrConstants.AFFILIATION_ORGANISATION_NAME).add(e.getOrganization().getName());
                            if (e.getOrganization().getDisambiguatedOrganization() != null) {
                                String sourceType = e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource();
                                if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                }
                            }
                        }
                    }
                }
            }

            // Invited positions
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getInvitedPositions() != null
                    && !record.getActivitiesSummary().getInvitedPositions().getInvitedPositionGroups().isEmpty()) {
                for (AffiliationGroup<InvitedPositionSummary> g : record.getActivitiesSummary().getInvitedPositions().getInvitedPositionGroups()) {
                    for (InvitedPositionSummary e : g.getActivities()) {
                        setAffiliationNames(e, pastInstitutionAffiliationNames, currentInstitutionAffiliationNames);
                        if (e.getOrganization() != null) {
                            organisationNames.get(SolrConstants.AFFILIATION_ORGANISATION_NAME).add(e.getOrganization().getName());
                            if (e.getOrganization().getDisambiguatedOrganization() != null) {
                                String sourceType = e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource();
                                if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                }
                            }
                        }
                    }
                }
            }

            // Memberships
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getMemberships() != null
                    && !record.getActivitiesSummary().getMemberships().getMembershipGroups().isEmpty()) {
                for (AffiliationGroup<MembershipSummary> g : record.getActivitiesSummary().getMemberships().getMembershipGroups()) {
                    for (MembershipSummary e : g.getActivities()) {
                        setAffiliationNames(e, pastInstitutionAffiliationNames, currentInstitutionAffiliationNames);
                        if (e.getOrganization() != null) {
                            organisationNames.get(SolrConstants.AFFILIATION_ORGANISATION_NAME).add(e.getOrganization().getName());
                            if (e.getOrganization().getDisambiguatedOrganization() != null) {
                                String sourceType = e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource();
                                if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                }
                            }
                        }
                    }
                }
            }

            // Qualifications
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getQualifications() != null
                    && !record.getActivitiesSummary().getQualifications().getQualificationGroups().isEmpty()) {
                for (AffiliationGroup<QualificationSummary> g : record.getActivitiesSummary().getQualifications().getQualificationGroups()) {
                    for (QualificationSummary e : g.getActivities()) {
                        setAffiliationNames(e, pastInstitutionAffiliationNames, currentInstitutionAffiliationNames);
                        if (e.getOrganization() != null) {
                            organisationNames.get(SolrConstants.AFFILIATION_ORGANISATION_NAME).add(e.getOrganization().getName());
                            if (e.getOrganization().getDisambiguatedOrganization() != null) {
                                String sourceType = e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource();
                                if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                }
                            }
                        }
                    }
                }
            }

            // Services
            if (record.getActivitiesSummary() != null && record.getActivitiesSummary().getServices() != null
                    && !record.getActivitiesSummary().getServices().getServiceGroups().isEmpty()) {
                for (AffiliationGroup<ServiceSummary> g : record.getActivitiesSummary().getServices().getServiceGroups()) {
                    for (ServiceSummary e : g.getActivities()) {
                        setAffiliationNames(e, pastInstitutionAffiliationNames, currentInstitutionAffiliationNames);
                        if (e.getOrganization() != null) {
                            organisationNames.get(SolrConstants.AFFILIATION_ORGANISATION_NAME).add(e.getOrganization().getName());
                            if (e.getOrganization().getDisambiguatedOrganization() != null) {
                                String sourceType = e.getOrganization().getDisambiguatedOrganization().getDisambiguationSource();
                                if (SolrConstants.RINGGOLD_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.RINGGOLD_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                } else if (SolrConstants.GRID_ORG_TYPE.equals(sourceType)) {
                                    organisationIds.get(SolrConstants.GRID_ORGANISATION_ID)
                                            .add(e.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
                                }
                            }
                        }
                    }
                }
            }

            // Now add all self, part of and version of identifiers
            profileIndexDocument.setSelfIds(self);
            profileIndexDocument.setPartOfIds(partOf);
            profileIndexDocument.setVersionOfIds(versionOf);
            // Now add all activities ext ids to the doc, the old way
            addExternalIdentifiersToIndexDocument(profileIndexDocument, allExternalIdentifiers);

            // Now add all affiliation names 
            profileIndexDocument.setCurrentInstitutionAffiliationNames(currentInstitutionAffiliationNames);
            profileIndexDocument.setPastInstitutionAffiliationNames(pastInstitutionAffiliationNames);
            
            profileIndexDocument.setOrganisationIds(organisationIds);
            profileIndexDocument.setOrganisationNames(organisationNames);
        }

        if (indexProfile) {
            try {
                StringWriter sw = new StringWriter();
                jaxbContext_3_0_api.createMarshaller().marshal(record, sw);
                profileIndexDocument.setPublicProfileMessage(sw.getBuffer()
                        .toString()/* .replaceAll("<[^>]+>", " ") */);
            } catch (JAXBException e) {
                LOG.error("problem marshalling xml", e);
            }
        }

        return profileIndexDocument;
    }

    /**
     * Fill all the different external identifiers in the profile index
     * document.
     * 
     * @param profileIndexDocument
     *            The document that will be indexed by solr
     * @param externalIdentifiers
     *            The list of external identifiers
     */
    private void addExternalIdentifiersToIndexDocument(OrcidSolrDocument profileIndexDocument, Map<String, List<String>> externalIdentifiers) {

        Iterator<Entry<String, List<String>>> it = externalIdentifiers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) it.next();
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty()) {

                switch (WorkExternalIdentifierType.fromValue(entry.getKey())) {
                case AGR:
                    profileIndexDocument.setAgr(entry.getValue());
                    break;
                case ARXIV:
                    profileIndexDocument.setArxiv(entry.getValue());
                    break;
                case ASIN:
                    profileIndexDocument.setAsin(entry.getValue());
                    break;
                case ASIN_TLD:
                    profileIndexDocument.setAsintld(entry.getValue());
                    break;
                case BIBCODE:
                    profileIndexDocument.setBibcode(entry.getValue());
                    break;
                case CBA:
                    profileIndexDocument.setCba(entry.getValue());
                    break;
                case CIT:
                    profileIndexDocument.setCit(entry.getValue());
                    break;
                case CTX:
                    profileIndexDocument.setCtx(entry.getValue());
                    break;
                case DOI:
                    profileIndexDocument.setDigitalObjectIds(entry.getValue());
                    break;
                case EID:
                    profileIndexDocument.setEid(entry.getValue());
                    break;
                case ETHOS:
                    profileIndexDocument.setEthos(entry.getValue());
                    break;
                case HANDLE:
                    profileIndexDocument.setHandle(entry.getValue());
                    break;
                case HIR:
                    profileIndexDocument.setHir(entry.getValue());
                    break;
                case ISBN:
                    profileIndexDocument.setIsbn(entry.getValue());
                    break;
                case ISSN:
                    profileIndexDocument.setIssn(entry.getValue());
                    break;
                case JFM:
                    profileIndexDocument.setJfm(entry.getValue());
                    break;
                case JSTOR:
                    profileIndexDocument.setJstor(entry.getValue());
                    break;
                case LCCN:
                    profileIndexDocument.setLccn(entry.getValue());
                    break;
                case MR:
                    profileIndexDocument.setMr(entry.getValue());
                    break;
                case OCLC:
                    profileIndexDocument.setOclc(entry.getValue());
                    break;
                case OL:
                    profileIndexDocument.setOl(entry.getValue());
                    break;
                case OSTI:
                    profileIndexDocument.setOsti(entry.getValue());
                    break;
                case OTHER_ID:
                    profileIndexDocument.setOtherIdentifierType(entry.getValue());
                    break;
                case PAT:
                    profileIndexDocument.setPat(entry.getValue());
                    break;
                case PMC:
                    profileIndexDocument.setPmc(entry.getValue());
                    break;
                case PMID:
                    profileIndexDocument.setPmid(entry.getValue());
                    break;
                case RFC:
                    profileIndexDocument.setRfc(entry.getValue());
                    break;
                case SOURCE_WORK_ID:
                    profileIndexDocument.setSourceWorkId(entry.getValue());
                    break;
                case SSRN:
                    profileIndexDocument.setSsrn(entry.getValue());
                    break;
                case URI:
                    profileIndexDocument.setUri(entry.getValue());
                    break;
                case URN:
                    profileIndexDocument.setUrn(entry.getValue());
                    break;
                case WOSUID:
                    profileIndexDocument.setWosuid(entry.getValue());
                case ZBL:
                    profileIndexDocument.setZbl(entry.getValue());
                    break;
                }
            }
        }
    }

    private void setAffiliationNames(AffiliationSummary e, List<String> pastInstitutionAffiliationNames, List<String> currentInstitutionAffiliationNames) {
        if (e.getEndDate() == null) {
            currentInstitutionAffiliationNames.add(e.getOrganization().getName());
        } else {
            FuzzyDate endDate = e.getEndDate();
            LocalDate localDate = LocalDate.now();
            Integer currentYear = localDate.getYear();
            Integer currentMonth = localDate.getMonthValue();
            Integer currentDay = localDate.getDayOfMonth();
            if(endDate.getYear() == null || endDate.getYear().getValue() ==  null) {
                currentInstitutionAffiliationNames.add(e.getOrganization().getName());
            } else {
                try {
                    Integer aYear = Integer.valueOf(endDate.getYear().getValue());
                    if(aYear < currentYear) {
                        pastInstitutionAffiliationNames.add(e.getOrganization().getName());
                    } else if(aYear.equals(currentYear)) {
                        Integer aMonth = (endDate.getMonth() == null || endDate.getMonth().getValue() == null) ? null : Integer.valueOf(endDate.getMonth().getValue());
                        if(aMonth == null || aMonth > currentMonth) {
                            currentInstitutionAffiliationNames.add(e.getOrganization().getName());
                        } else if(aMonth < currentMonth) {
                            pastInstitutionAffiliationNames.add(e.getOrganization().getName());
                        } else {
                            Integer aDay = (endDate.getDay() == null || endDate.getDay().getValue() == null) ? null : Integer.valueOf(endDate.getDay().getValue());
                            if(aDay == null || aDay > currentDay) {
                                currentInstitutionAffiliationNames.add(e.getOrganization().getName());
                            } else {
                                pastInstitutionAffiliationNames.add(e.getOrganization().getName());
                            }                            
                        }                        
                    } else {
                        currentInstitutionAffiliationNames.add(e.getOrganization().getName());
                    }
                } catch(Exception ex) {
                    // Can't parse end year, log the exception, but keep parsing the record
                    LOG.error("Invalid end date " + endDate.toString() + " for affiliation with put code " + e.getPutCode());
                    currentInstitutionAffiliationNames.add(e.getOrganization().getName());
                }
            } 
        }
    }
}
