package org.orcid.jaxb.model.v3.rc1.common;

import java.util.List;

public interface MultipleOrganizationHolder {
    List<Organization> getOrganization();
    void setOrganization(List<Organization> organizations);
}
