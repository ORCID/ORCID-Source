package org.orcid.core.orgs.grouping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.OrgDisambiguatedExternalIdentifiers;
import org.orcid.pojo.grouping.OrgGroup;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.core.utils.FunderIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class OrgGrouping implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String KEY_SEPARATOR = "::";
    private OrgGroup orgGroup = new OrgGroup();
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgGrouping.class);
    private static final String FUNDREF_BASE_URL = "http://dx.doi.org/10.13039/";

    private OrgDisambiguatedManager orgDisambiguatedManager;

    // default constructor private
    private OrgGrouping() {
    };

    public OrgGrouping(OrgDisambiguated sourceOrg, OrgDisambiguatedManager orgDisambiguatedManager) {
        this.orgDisambiguatedManager = orgDisambiguatedManager;

        setExtentedOrgGroup(sourceOrg);
        LOGGER.info("Group for: " + sourceOrg.getSourceId() + ":" + sourceOrg.getSourceType() + " hasROR? " + (orgGroup.getRorOrg() != null));
        orgGroup.getOrgs().values().stream().forEach(o -> {
            LOGGER.info("PART OF GROUP: " + o.getSourceId() + ":" + o.getSourceType());
        });

    }

    public OrgGrouping(OrgDisambiguatedEntity sourceOrg, OrgDisambiguatedManager orgDisambiguatedManager) {
        this.orgDisambiguatedManager = orgDisambiguatedManager;
        setExtentedOrgGroup(convertEntity(sourceOrg));
        LOGGER.info("Group for: " + sourceOrg.getSourceId() + ":" + sourceOrg.getSourceType() + " hasROR? " + (orgGroup.getRorOrg() != null));
        orgGroup.getOrgs().values().stream().forEach(o -> {
            LOGGER.info("PART OF GROUP: " + o.getSourceId() + ":" + o.getSourceType());
        });
    }

    private void getGroupForOrg(OrgDisambiguated orgToGroup) {
        OrgDisambiguated org;

        if (orgToGroup != null && orgToGroup.getOrgDisambiguatedExternalIdentifiers() != null) {
            String orgKey = buildOrgDisambiguatedKey(orgToGroup);
            if (!orgGroup.getOrgs().containsKey(orgKey)) {
                orgGroup.getOrgs().put(orgKey, orgToGroup);
            }

            if (orgToGroup.getSourceType().equals(OrgDisambiguatedSourceType.ROR.name())) {
                orgGroup.setRorOrg(orgToGroup);
            }
            if (orgToGroup.getSourceType().equals(OrgDisambiguatedSourceType.FUNDREF.name())) {
                orgGroup.setFunding(true);
            }

            for (OrgDisambiguatedExternalIdentifiers externalIdentifiers : orgToGroup.getOrgDisambiguatedExternalIdentifiers()) {
                for (String externalIdentifier : externalIdentifiers.getAll()) {
                    if (ObjectUtils.containsConstant(OrgDisambiguatedSourceType.values(), externalIdentifiers.getIdentifierType(), true)) {
                        // check for external id type because FUNDREF is stored
                        // as simple id as external identifier, and as
                        // disambiguated org as url
                        if (externalIdentifiers.getIdentifierType().equals(OrgDisambiguatedSourceType.FUNDREF.name())
                                && !OrcidStringUtils.isValidURL(externalIdentifier)) {
                            externalIdentifier = FUNDREF_BASE_URL + externalIdentifier;
                            orgGroup.setFunding(true);
                        }
                        org = orgDisambiguatedManager.findInDB(externalIdentifier, externalIdentifiers.getIdentifierType());

                        if (org != null && org.getSourceType() != null) {
                            if (org.getSourceType().equals(OrgDisambiguatedSourceType.ROR.name())) {
                                orgGroup.setRorOrg(org);
                            }
                            orgKey = buildOrgDisambiguatedKey(org);
                            if (!orgGroup.getOrgs().containsKey(orgKey)) {
                                orgGroup.getOrgs().put(orgKey, org);
                            }
                        } 

                    }
                    else {
                        // check other possible orgs that have the same ISNI identifier
                        if(StringUtils.equals(externalIdentifiers.getIdentifierType(), FunderIdentifierType.ISNI.value())){
                            List<OrgDisambiguated> orgsFromExternalIdentifier = orgDisambiguatedManager
                                    .findOrgDisambiguatedIdsForSameExternalIdentifier(externalIdentifier, externalIdentifiers.getIdentifierType());
                            if (orgsFromExternalIdentifier != null) {
                                orgsFromExternalIdentifier.stream().forEach((o -> {
                                    String key = buildOrgDisambiguatedKey(o);
                                    if (!orgGroup.getOrgs().containsKey(key)) {
                                        orgGroup.getOrgs().put(key, o);
                                        if (o.getSourceType().equals(OrgDisambiguatedSourceType.ROR.name())) {
                                            orgGroup.setRorOrg(o);
                                        }
                                    }
                                }));
                            }
                        }
                    }
                }
            }
        }
        return;
    }

    private void setExtentedOrgGroup(OrgDisambiguated sourceOrg) {
        // set source Organization
        orgGroup.setSourceOrg(sourceOrg);
        String orgKey = buildOrgDisambiguatedKey(sourceOrg);
        if (!orgGroup.getOrgs().containsKey(orgKey)) {
            orgGroup.getOrgs().put(orgKey, sourceOrg);
        }
        // get initial group setup
        getGroupForOrg(sourceOrg);
        // second iteration to get any connected orgs through external
        // identifiers of the source organization
        Collection<OrgDisambiguated> firstIterationOrgs = orgGroup.getOrgs().values();
        for (OrgDisambiguated org : firstIterationOrgs) {
            if (!orgGroup.getOrgs().containsKey(buildOrgDisambiguatedKey(org))) {
                getGroupForOrg(org);
            }
        }
        LOGGER.info("Group created for source type: [" + sourceOrg.sourceType + "] and id: [" + sourceOrg.sourceId + "] total orgs in the group: "
                + orgGroup.getOrgs().size() + " . It has ROR? " + (orgGroup.getRorOrg() != null));
        return;
    }

    public OrgGroup getOrganizationGroup() {
        return orgGroup;
    }

    private OrgDisambiguated convertEntity(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        OrgDisambiguated org = new OrgDisambiguated();
        org.setValue(orgDisambiguatedEntity.getName());
        org.setCity(orgDisambiguatedEntity.getCity());
        org.setRegion(orgDisambiguatedEntity.getRegion());
        org.setCountry(orgDisambiguatedEntity.getCountry() != null ? orgDisambiguatedEntity.getCountry() : null);
        org.setOrgType(orgDisambiguatedEntity.getOrgType());
        org.setSourceId(orgDisambiguatedEntity.getSourceId());
        org.setSourceType(orgDisambiguatedEntity.getSourceType());
        org.setUrl(orgDisambiguatedEntity.getUrl());
        Map<String, OrgDisambiguatedExternalIdentifiers> externalIdsMap = new HashMap<String, OrgDisambiguatedExternalIdentifiers>();
        if (orgDisambiguatedEntity.getExternalIdentifiers() != null && !orgDisambiguatedEntity.getExternalIdentifiers().isEmpty()) {
            for (OrgDisambiguatedExternalIdentifierEntity extIdEntity : orgDisambiguatedEntity.getExternalIdentifiers()) {
                String type = extIdEntity.getIdentifierType();
                String identifier = extIdEntity.getIdentifier();
                Boolean preferred = extIdEntity.getPreferred();
                if(preferred == null) {
                    preferred =  Boolean.FALSE;
                }
 
                OrgDisambiguatedExternalIdentifiers extId = null;

                if (externalIdsMap.containsKey(type)) {
                    extId = externalIdsMap.get(type);
                } else {
                    extId = new OrgDisambiguatedExternalIdentifiers();
                    extId.setIdentifierType(type);
                    externalIdsMap.put(type, extId);
                }

                if (preferred) {
                    extId.setPreferred(identifier);
                }

                extId.getAll().add(identifier);
            }

            if (!externalIdsMap.isEmpty()) {
                List<OrgDisambiguatedExternalIdentifiers> extIds = new ArrayList<OrgDisambiguatedExternalIdentifiers>();
                externalIdsMap.keySet().stream().sorted().collect(Collectors.toList()).forEach(k -> {
                    extIds.add(externalIdsMap.get(k));
                });
                org.setOrgDisambiguatedExternalIdentifiers(extIds);
            }

        }
        return org;
    }

    private String buildOrgDisambiguatedKey(OrgDisambiguated org) {
        return org.getSourceId() + KEY_SEPARATOR + org.sourceType.trim().toUpperCase();
    }

    public void markGroupForIndexing(OrgDisambiguatedDao orgDisambiguatedDao) {
        int popularityOfGroup = 0;
        OrgDisambiguatedEntity rorOrgEntity = null;
        // if the group has a ROR mark the other not ROR organization as part of
        // group
        if (orgGroup.getRorOrg() != null) {
            OrgDisambiguatedEntity orgEntity;
            for (OrgDisambiguated org : orgGroup.getOrgs().values()) {
                orgEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(org.getSourceId(), org.getSourceType());
                if (orgEntity != null) {
                    if (!OrganizationStatus.DEPRECATED.name().equals(orgEntity.getStatus()) || !OrganizationStatus.OBSOLETE.name().equals(orgEntity.getStatus())
                            || !orgEntity.getSourceType().equalsIgnoreCase(OrgDisambiguatedSourceType.LEI.name())) {
                        orgEntity.setIndexingStatus(IndexingStatus.PENDING);
                        if (!orgEntity.getSourceType().equalsIgnoreCase(OrgDisambiguatedSourceType.ROR.name())) {
                            orgEntity.setStatus(OrganizationStatus.PART_OF_GROUP.name());
                            if (orgEntity.getPopularity().intValue() > popularityOfGroup) {
                                popularityOfGroup = orgEntity.getPopularity();
                            }
                            orgDisambiguatedManager.updateOrgDisambiguated(orgEntity);
                        } else {
                            rorOrgEntity = orgEntity;
                        }

                    }
                }
            }

            // update ROR
            if (rorOrgEntity != null) {
                // check Popularity
                if (popularityOfGroup > rorOrgEntity.getPopularity().intValue()) {
                    rorOrgEntity.setPopularity(popularityOfGroup);
                }
                orgDisambiguatedManager.updateOrgDisambiguated(rorOrgEntity);
            }
        }
    }
    
    public void ungroupObsoleteRorForIndexing(OrgDisambiguatedDao orgDisambiguatedDao) {
        int popularityOfGroup = 0;
        OrgDisambiguatedEntity rorOrgEntity = null;
        // if the group has a ROR mark the other not ROR organization as part of
        // group
        if (orgGroup.getRorOrg() != null) {
            OrgDisambiguatedEntity orgEntity;
            for (OrgDisambiguated org : orgGroup.getOrgs().values()) {
                orgEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(org.getSourceId(), org.getSourceType());
                if (orgEntity != null) {
                    if (OrganizationStatus.DEPRECATED.name().equals(orgEntity.getStatus()) || OrganizationStatus.OBSOLETE.name().equals(orgEntity.getStatus())
                          ) {
                        orgEntity.setIndexingStatus(IndexingStatus.PENDING);
                        if (!orgEntity.getSourceType().equalsIgnoreCase(OrgDisambiguatedSourceType.ROR.name())) {
                            orgEntity.setStatus(null);
                            if (orgEntity.getPopularity().intValue() > popularityOfGroup) {
                                popularityOfGroup = orgEntity.getPopularity();
                            }
                            orgDisambiguatedManager.updateOrgDisambiguated(orgEntity);
                        } else {
                            rorOrgEntity = orgEntity;
                        }

                    }
                }
            }

            // update ROR
            if (rorOrgEntity != null) {
                // check Popularity
                if (popularityOfGroup > rorOrgEntity.getPopularity().intValue()) {
                    rorOrgEntity.setPopularity(popularityOfGroup);
                }
                orgDisambiguatedManager.updateOrgDisambiguated(rorOrgEntity);
            }
        }
    }

}
