package org.orcid.pojo.summary;

import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.pojo.ajaxForm.PojoUtil;

import java.util.ArrayList;
import java.util.List;

public class ExternalIdentifiersSummary {
    private String id;
    private String commonName;
    private String reference;
    private String url;
    private boolean validated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public static List<ExternalIdentifiersSummary> valueOf(PersonExternalIdentifiers personExternalIdentifiers, String orcid) {
        List<ExternalIdentifiersSummary> externalIdentifiersSummaryList = new ArrayList<>();

        if(personExternalIdentifiers != null) {
            personExternalIdentifiers.getExternalIdentifiers().forEach(personExternalIdentifier -> {
                externalIdentifiersSummaryList.add(ExternalIdentifiersSummary.valueOf(personExternalIdentifier, orcid));
            });
        }

        return externalIdentifiersSummaryList;
    }

    public static ExternalIdentifiersSummary valueOf(PersonExternalIdentifier personExternalIdentifier, String orcid) {
        ExternalIdentifiersSummary form = new ExternalIdentifiersSummary();

        if (personExternalIdentifier != null) {
            if (!PojoUtil.isEmpty(personExternalIdentifier.getType())) {
                form.setCommonName(personExternalIdentifier.getType());
            }

            if (!PojoUtil.isEmpty(personExternalIdentifier.getValue())) {
                form.setReference(personExternalIdentifier.getValue());
            }

            if (!PojoUtil.isEmpty(personExternalIdentifier.getUrl())) {
                form.setUrl(personExternalIdentifier.getUrl().getValue());
            }

            if (personExternalIdentifier.getPutCode() != null) {
                form.setId(String.valueOf(personExternalIdentifier.getPutCode()));
            }

            if (personExternalIdentifier.getSource() != null) {
                form.setValidated(!SourceUtils.isSelfAsserted(personExternalIdentifier.getSource(), orcid));
            }
        }
        return form;
    }

}
