package org.orcid.core.utils;

import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.manager.ActivityManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.common_v2.ContributorAttributes;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingContributor;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ContributorsRolesAndSequencesV2;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ContributorUtils {
    
    private final Integer BATCH_SIZE;           

    private ActivityManager cacheManager;

    private ProfileEntityManager profileEntityManager;

    private RecordNameDao recordNameDao; 
    
    private Cache<String, String> contributorsNameCache;
    
    protected ProfileLastModifiedAspect profileLastModifiedAspect;
    
    public ContributorUtils(@Value("${org.orcid.contributor.names.batch_size:2500}") Integer batchSize) {
        if(batchSize == null) {
            BATCH_SIZE = 2500;
        } else {
            BATCH_SIZE = batchSize;
        }
    }
    
    public void filterContributorPrivateData(Funding funding) {
        if (funding.getContributors() != null && funding.getContributors().getContributor() != null) {
            for (FundingContributor contributor : funding.getContributors().getContributor()) {
                contributor.setContributorEmail(null);
                if (!PojoUtil.isEmpty(contributor.getContributorOrcid())) {
                    String contributorOrcid = contributor.getContributorOrcid().getPath();
                    if (profileEntityManager.orcidExists(contributorOrcid)) {
                        // contributor is an ORCID user - visibility of user's
                        // name in record must be taken into account                        
                        String publicContributorCreditName = cacheManager.getPublicCreditName(contributorOrcid);
                        CreditName creditName = new CreditName(publicContributorCreditName != null ? publicContributorCreditName : "");
                        contributor.setCreditName(creditName);
                    }
                }
            }
        }
    }

    public void filterContributorPrivateData(Work work) {
        if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null) {
            List<Contributor> contributorList = work.getWorkContributors().getContributor();
            List<Contributor> contributorsToPopulateName = new ArrayList<Contributor>();
            Set<String> idsToPopulateName = new HashSet<String>();
            // Populate the credit name of cached contributors and populate the list of names to retrive from the DB
            for (Contributor contributor : contributorList) {
                contributor.setContributorEmail(null);
                if (!PojoUtil.isEmpty(contributor.getContributorOrcid())) {
                    String orcid = contributor.getContributorOrcid().getPath();
                    String cachedName = getCachedContributorName(orcid);
                    if(cachedName == null) {
                        idsToPopulateName.add(orcid);
                        contributorsToPopulateName.add(contributor);
                    } else {
                        CreditName creditName = new CreditName(cachedName);
                        contributor.setCreditName(creditName);
                    }                    
                }
            }
            
            // Fetch the contributor names
            Map<String, String> contributorNames = getContributorNamesFromDB(idsToPopulateName);
            
            // Populate missing names
            for(Contributor contributor : contributorsToPopulateName) {
                String orcid = contributor.getContributorOrcid().getPath();
                // If the key doesn't exists in the name, it means the name is private or the orcid id doesn't exists
                if(contributorNames.containsKey(orcid)) {
                    String name = contributorNames.get(orcid);                    
                    CreditName creditName = new CreditName(name);
                    contributor.setCreditName(creditName);                    
                } 
            }
        }
    }

    public List<ContributorsRolesAndSequencesV2> getContributorsGroupedByOrcid(List<Contributor> contributors, Integer maxContributorsForUI) {
        List<ContributorsRolesAndSequencesV2> contributorsRolesAndSequencesList = new ArrayList<>();
        for(Contributor contributor : contributors) {
            // Process the list of contributors till reaching the max, then break
            // We add an extra contributor to display a message in the UI in case than there is more than maxContributorsForUI
            if(contributorsRolesAndSequencesList.size() == maxContributorsForUI + 1) {
                break;
            }
            groupContributorsByOrcid(contributor, contributorsRolesAndSequencesList);
        }
        return contributorsRolesAndSequencesList;
    }

    private void groupContributorsByOrcid(Contributor contributor, List<ContributorsRolesAndSequencesV2> contributorsRolesAndSequencesList) {
        if (contributor.getContributorOrcid() != null) {
            String orcid = contributor.getContributorOrcid().getPath();
            if (!StringUtils.isBlank(orcid)) {
                if (contributorsRolesAndSequencesList.size() > 0) {
                    List<ContributorsRolesAndSequencesV2> c = contributorsRolesAndSequencesList
                            .stream()
                            .filter(contr -> contr.getContributorOrcid() != null && contr.getContributorOrcid().getPath() != null && orcid.equals(contr.getContributorOrcid().getPath()))
                            .collect(Collectors.toList());
                    if (c.size() > 0) {
                        ContributorsRolesAndSequencesV2 contributorsRolesAndSequences = c.get(0);
                        ContributorAttributes ca = new ContributorAttributes();
                        if(contributor.getContributorAttributes() != null) {
                            if (contributor.getContributorAttributes().getContributorRole() != null) {
                                ca.setContributorRole(contributor.getContributorAttributes().getContributorRole());
                            }
                            if(contributor.getContributorAttributes().getContributorSequence() != null) {
                                ca.setContributorSequence(contributor.getContributorAttributes().getContributorSequence());
                            }
                        }
                        List<ContributorAttributes> rolesAndSequencesList = contributorsRolesAndSequences.getRolesAndSequences();
                        rolesAndSequencesList.add(ca);
                        contributorsRolesAndSequences.setRolesAndSequences(rolesAndSequencesList);
                    } else {
                        addContributorWithNameOrOrcid(contributorsRolesAndSequencesList, contributor);
                    }
                } else {
                    addContributorWithNameOrOrcid(contributorsRolesAndSequencesList, contributor);
                }
            } else {
                addContributorWithNameOrOrcid(contributorsRolesAndSequencesList, contributor);
            }
        } else {
            addContributorWithNameOrOrcid(contributorsRolesAndSequencesList, contributor);
        }
    }

    private void addContributorWithNameOrOrcid(List<ContributorsRolesAndSequencesV2> contributorsRolesAndSequencesList, Contributor contributor) {
        if ((contributor.getContributorOrcid() != null && !"".equals(contributor.getContributorOrcid().getPath())) ||
                (contributor.getCreditName() != null && !"".equals(contributor.getCreditName().getContent()))) {
            contributorsRolesAndSequencesList.add(addContributor(contributor));
        }
    }

    private ContributorsRolesAndSequencesV2 addContributor(Contributor contributor) {
        ContributorsRolesAndSequencesV2 crs = new ContributorsRolesAndSequencesV2();
        if(contributor == null) {
            return crs;
        }
        if (contributor.getContributorOrcid() != null) {
            crs.setContributorOrcid(contributor.getContributorOrcid());
        }
        if (contributor.getCreditName() != null) {
            crs.setCreditName(contributor.getCreditName());
        }
        if (contributor.getContributorAttributes() != null) {
            ContributorAttributes ca = new ContributorAttributes();
            if (contributor.getContributorAttributes().getContributorRole() != null) {
                ca.setContributorRole(contributor.getContributorAttributes().getContributorRole());
            }
            if (contributor.getContributorAttributes().getContributorSequence() != null) {
                ca.setContributorSequence(contributor.getContributorAttributes().getContributorSequence());
            }
            List<ContributorAttributes> rolesAndSequences = new ArrayList<>();
            rolesAndSequences.add(ca);
            crs.setRolesAndSequences(rolesAndSequences);
        }

        return crs;
    }

    private String getCachedContributorName(String orcid) {
        String cacheKey = getCacheKey(orcid);
        if(contributorsNameCache.containsKey(cacheKey)){
            return contributorsNameCache.get(cacheKey);
        }        
        
        return null;
    }
    
    private Map<String, String> getContributorNamesFromDB(Set<String> ids) {
        Iterable<List<String>> it = Iterables.partition(ids, BATCH_SIZE);
        Map<String, String> contributorNames = new HashMap<String, String>();
        for(List<String> idsList : it) {
            List<RecordNameEntity> entities = recordNameDao.getRecordNames(idsList);
            if(entities != null) {
                for(RecordNameEntity entity : entities) {
                    String orcid = entity.getOrcid();
                    String publicCreditName = cacheManager.getPublicCreditName(orcid);
                    publicCreditName = (publicCreditName == null ? "" : publicCreditName);
                    contributorNames.put(orcid, publicCreditName);
                    // Store in the cache
                    contributorsNameCache.put(getCacheKey(orcid), publicCreditName);
                }
            }
        }
        return contributorNames;
    }

    public void filterContributorPrivateData(WorkBulk works) {
        if(works != null) {
            for(BulkElement element : works.getBulk()) {
                if(Work.class.isAssignableFrom(element.getClass())) {
                    filterContributorPrivateData((Work) element);
                }
            }
        }
    }
    
    private String getCacheKey(String orcid) {
        Date lastModified = profileLastModifiedAspect.retrieveLastModifiedDate(orcid);
        return orcid + "_" + (lastModified == null ? 0 : lastModified.getTime());
    }
    
    public void setCacheManager(ActivityManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setProfileEntityManager(ProfileEntityManager profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    public void setRecordNameDao(RecordNameDao recordNameDao) {
        this.recordNameDao = recordNameDao;
    }

    public void setContributorsNameCache(Cache<String, String> contributorsNameCache) {
        this.contributorsNameCache = contributorsNameCache;
    }

    public void setProfileLastModifiedAspect(ProfileLastModifiedAspect profileLastModifiedAspect) {
        this.profileLastModifiedAspect = profileLastModifiedAspect;
    }        
}
