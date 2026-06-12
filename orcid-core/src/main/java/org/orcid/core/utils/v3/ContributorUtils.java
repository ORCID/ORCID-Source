package org.orcid.core.utils.v3;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.FundingContributor;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.ajaxForm.PojoUtil;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContributorUtils {
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManagerV3;

    public void filterContributorPrivateData(Funding funding) {
        if (funding.getContributors() != null && funding.getContributors().getContributor() != null) {
            for (FundingContributor contributor : funding.getContributors().getContributor()) {
                contributor.setContributorEmail(null);
                if (!PojoUtil.isEmpty(contributor.getContributorOrcid())) {
                    String contributorOrcid = contributor.getContributorOrcid().getPath();
                    if (profileEntityManagerV3.orcidExists(contributorOrcid)) {
                        // contributor is an ORCID user - visibility of user's
                        // name in record must be taken into account                        
                        String publicContributorCreditName = recordNameManagerReadOnlyV3.fetchDisplayablePublicName(contributorOrcid);
                        CreditName creditName = new CreditName(publicContributorCreditName != null ? publicContributorCreditName : "");
                        contributor.setCreditName(creditName);
                    }
                }
            }
        }
    }

    public List<ContributorsRolesAndSequences> getContributorsGroupedByOrcid(List<Contributor> contributors, Integer maxContributorsForUI) {
        List<ContributorsRolesAndSequences> contributorsRolesAndSequencesList = new ArrayList<>();
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

    private void groupContributorsByOrcid(Contributor contributor, List<ContributorsRolesAndSequences> contributorsRolesAndSequencesList) {
        if (contributor.getContributorOrcid() != null) {
            String orcid = contributor.getContributorOrcid().getPath();
            if (!StringUtils.isBlank(orcid)) {
                if (contributorsRolesAndSequencesList.size() > 0) {
                    List<ContributorsRolesAndSequences> c = contributorsRolesAndSequencesList
                            .stream()
                            .filter(contr -> contr.getContributorOrcid() != null && contr.getContributorOrcid().getPath() != null && orcid.equals(contr.getContributorOrcid().getPath()))
                            .collect(Collectors.toList());
                    if (c.size() > 0) {
                        ContributorsRolesAndSequences contributorsRolesAndSequences = c.get(0);
                        ContributorAttributes ca = new ContributorAttributes();
                        if(contributor.getContributorAttributes() != null) {
                            if (contributor.getContributorAttributes().getContributorRole() != null) {
                                ca.setContributorRole(getCreditRole(contributor.getContributorAttributes().getContributorRole()));
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

    private void addContributorWithNameOrOrcid(List<ContributorsRolesAndSequences> contributorsRolesAndSequencesList, Contributor contributor) {
        if ((contributor.getContributorOrcid() != null && !"".equals(contributor.getContributorOrcid().getPath())) ||
                (contributor.getCreditName() != null && !"".equals(contributor.getCreditName().getContent()))) {
            contributorsRolesAndSequencesList.add(addContributor(contributor));
        }
    }

    private ContributorsRolesAndSequences addContributor(Contributor contributor) {
        ContributorsRolesAndSequences crs = new ContributorsRolesAndSequences();
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
                ca.setContributorRole(getCreditRole(contributor.getContributorAttributes().getContributorRole()));
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

    public static String getCreditRole(String contributorRole) {
        try {
            return CreditRole.fromValue(contributorRole).getUiValue();
        } catch(Exception e) {
            return contributorRole;
        }
    }

}
