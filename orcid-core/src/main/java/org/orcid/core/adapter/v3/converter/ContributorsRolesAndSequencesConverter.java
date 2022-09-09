package org.orcid.core.adapter.v3.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ContributorsRolesAndSequencesConverter extends BidirectionalConverter<List<ContributorsRolesAndSequences>, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContributorsRolesAndSequencesConverter.class);

    private ContributorRoleConverter roleConverter;

    public ContributorsRolesAndSequencesConverter(ContributorRoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    @Override
    public String convertTo(List<ContributorsRolesAndSequences> source, Type<String> destinationType) {
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public List<ContributorsRolesAndSequences> convertFrom(String source, Type<List<ContributorsRolesAndSequences>> destinationType) {
        return null;
    }

    public List<ContributorsRolesAndSequences> getContributorsRolesAndSequencesList(String source) {
        ContributorUtils contributorUtils = new ContributorUtils(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        List<ContributorsRolesAndSequences> contributorsRolesAndSequencesResult = new ArrayList<>();
        try {
            contributorsRolesAndSequencesResult = objectMapper.readValue(source, new TypeReference<List<ContributorsRolesAndSequences>>(){});
            for (ContributorsRolesAndSequences contributorsRolesAndSequences : contributorsRolesAndSequencesResult) {
                if (contributorsRolesAndSequences.getRolesAndSequences() != null) {
                    for (ContributorAttributes crs : contributorsRolesAndSequences.getRolesAndSequences()) {
                        String providedRoleValue = crs.getContributorRole();
                        if (!PojoUtil.isEmpty(providedRoleValue)) {
                            CreditRole cr = CreditRole.fromUiValue(providedRoleValue);
                            if (cr != null) {
                                providedRoleValue = cr.name();
                            }
                            crs.setContributorRole(contributorUtils.getCreditRole(roleConverter.toRoleValue(providedRoleValue)));
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
