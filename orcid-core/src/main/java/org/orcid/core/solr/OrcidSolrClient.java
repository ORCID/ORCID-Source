package org.orcid.core.solr;

public abstract class OrcidSolrClient {

    // generate or filter specified fl param to only include allowed values
    protected String getFieldList(String requestedFieldList) {
        String[] specifiedFields = new String[0];
        StringBuilder fl = new StringBuilder();
        if (requestedFieldList != null) {
            specifiedFields = requestedFieldList.split(",");
            for (String specifiedField : specifiedFields) {
                if (SolrConstants.ALLOWED_FIELDS.contains(specifiedField)) {
                    fl.append(specifiedField).append(",");
                }
            }
            return !fl.toString().isEmpty() ? fl.toString() : getDefaultFieldList();
        } else {
            return getDefaultFieldList();
        }

    }

    private String getDefaultFieldList() {
        StringBuilder defaultFieldList = new StringBuilder(SolrConstants.ALLOWED_FIELDS.get(0));
        SolrConstants.ALLOWED_FIELDS.subList(1, SolrConstants.ALLOWED_FIELDS.size()).forEach(s -> defaultFieldList.append(",").append(s));
        return defaultFieldList.toString();
    }

}
