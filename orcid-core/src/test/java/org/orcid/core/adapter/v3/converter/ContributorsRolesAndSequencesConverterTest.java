package org.orcid.core.adapter.v3.converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.pojo.ContributorsRolesAndSequences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ContributorsRolesAndSequencesConverterTest {

    String contributorsJson = "[{\"contributorOrcid\":{\"uri\":null,\"path\":\"0000-0000-0000-000X\",\"host\":null},\"creditName\":{\"content\":\"Contributor 1\"},\"contributorEmail\":null,\"contributorAttributes\":null,\"rolesAndSequences\":[{\"contributorSequence\":null,\"contributorRole\":\"http://credit.niso.org/contributor-roles/funding-acquisition/\"},{\"contributorSequence\":null,\"contributorRole\":\"http://credit.niso.org/contributor-roles/writing-review-editing/\"}]}]";
    String contributorsJsonWithoutRoles = "[{\"contributorOrcid\":{\"uri\":null,\"path\":\"0000-0000-0000-000X\",\"host\":null},\"creditName\":{\"content\":\"Contributor 1\"},\"contributorEmail\":null,\"contributorAttributes\":null,\"rolesAndSequences\":null}]";

    @InjectMocks
    private ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void convertTo() {
        assertEquals(contributorsJson, contributorsRolesAndSequencesConverter.convertTo(getContributorsRolesAndSequences(), null));
    }

    @Test
    public void getContributorsRolesAndSequencesList() {
        assertEquals(getContributorsRolesAndSequences(), contributorsRolesAndSequencesConverter.getContributorsRolesAndSequencesList(contributorsJson));
    }

    @Test
    public void convertToContributorsWithoutRolesAndSequence() {
        List<ContributorsRolesAndSequences> contributors = getContributorsRolesAndSequences();
        contributors.get(0).setRolesAndSequences(null);
        assertEquals(contributorsJsonWithoutRoles, contributorsRolesAndSequencesConverter.convertTo(contributors, null));
    }

    @Test
    public void getContributorsWithoutRolesAndSequence() {
        List<ContributorsRolesAndSequences> contributors = getContributorsRolesAndSequences();
        contributors.get(0).setRolesAndSequences(null);
        assertEquals(contributors, contributorsRolesAndSequencesConverter.getContributorsRolesAndSequencesList(contributorsJsonWithoutRoles));
    }

    private List<ContributorsRolesAndSequences> getContributorsRolesAndSequences() {
        ContributorsRolesAndSequences crs = new ContributorsRolesAndSequences();
        crs.setContributorOrcid(new ContributorOrcid("0000-0000-0000-000X"));
        crs.setCreditName(new CreditName("Contributor 1"));
        List<ContributorAttributes> rolesAndSequences = new ArrayList<>();
        ContributorAttributes ca = new ContributorAttributes();
        ca.setContributorRole(CreditRole.FUNDING_ACQUISITION.value());
        rolesAndSequences.add(ca);
        ca = new ContributorAttributes();
        ca.setContributorRole(CreditRole.WRITING_REVIEW_EDITING.value());
        rolesAndSequences.add(ca);
        crs.setRolesAndSequences(rolesAndSequences);
        return Arrays.asList(crs);
    }
}
