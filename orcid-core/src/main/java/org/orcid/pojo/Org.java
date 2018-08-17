package org.orcid.pojo;

import org.orcid.jaxb.model.v3.rc1.common.Organization;

public class Org {

    private String name;

    private String city;

    private String region;

    private String country;

    private String orgDisambiguatedId;

    private String disambiguationSource;

    public String getOrgDisambiguatedId() {
        return orgDisambiguatedId;
    }

    public void setOrgDisambiguatedId(String orgDisambiguatedId) {
        this.orgDisambiguatedId = orgDisambiguatedId;
    }

    public String getDisambiguationSource() {
        return disambiguationSource;
    }

    public void setDisambiguationSource(String disambiguationSource) {
        this.disambiguationSource = disambiguationSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static Org valueOf(Organization organization) {
        Org org = new Org();
        org.setName(organization.getName());
        org.setCity(organization.getAddress().getCity());
        org.setRegion(organization.getAddress().getRegion());
        org.setCountry(organization.getAddress().getCountry().value());

        if (organization.getDisambiguatedOrganization() != null) {
            org.setDisambiguationSource(organization.getDisambiguatedOrganization().getDisambiguationSource());
            org.setOrgDisambiguatedId(String.valueOf(organization.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier()));
        }
        return org;
    }

}
