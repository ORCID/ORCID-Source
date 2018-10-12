package org.orcid.jaxb.model.v3.rc2.common;

import java.util.List;

public interface MultipleOrganizationHolder {
    List<Organization> getOrganization();
    void setOrganization(List<Organization> organizations);
}
