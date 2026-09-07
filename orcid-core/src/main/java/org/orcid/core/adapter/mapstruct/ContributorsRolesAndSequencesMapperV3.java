package org.orcid.core.adapter.mapstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Plain, Orika-free converter for serializing/deserializing ContributorsRolesAndSequences objects.
 */
public class ContributorsRolesAndSequencesMapperV3 {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContributorsRolesAndSequencesMapperV3.class);

    @Resource(name = "workContributorRoleConverter")
    private ContributorRoleConverter workContributorRoleConverter;

    public String convertTo(List<ContributorsRolesAndSequences> source) {
        return JsonUtils.convertToJsonString(source);
    }

    public List<ContributorsRolesAndSequences> convertFrom(String source) {
        return null;
    }

    public List<ContributorsRolesAndSequences> getContributorsRolesAndSequencesList(String source) {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<ContributorsRolesAndSequences> contributorsRolesAndSequencesResult = new ArrayList<>();
        try {
            contributorsRolesAndSequencesResult = objectMapper.readValue(source, new TypeReference<List<ContributorsRolesAndSequences>>(){});
            if (contributorsRolesAndSequencesResult != null) {
                for (ContributorsRolesAndSequences contributorsRolesAndSequences : contributorsRolesAndSequencesResult) {
                    if (contributorsRolesAndSequences.getRolesAndSequences() != null) {
                        for (ContributorAttributes crs : contributorsRolesAndSequences.getRolesAndSequences()) {
                            String providedRoleValue = crs.getContributorRole();
                            if (!PojoUtil.isEmpty(providedRoleValue)) {
                                CreditRole cr = CreditRole.fromUiValue(providedRoleValue);
                                if (cr != null) {
                                    providedRoleValue = cr.name();
                                }
                                crs.setContributorRole(ContributorUtils.getCreditRole(workContributorRoleConverter.toRoleValue(providedRoleValue)));
                            }
                        }
                    }
                }
            }
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return contributorsRolesAndSequencesResult;
        } catch (Exception ioe) {
            LOGGER.error("Unable to process contributors", ioe);
            throw ioe;
        }
        return contributorsRolesAndSequencesResult;
    }

}
