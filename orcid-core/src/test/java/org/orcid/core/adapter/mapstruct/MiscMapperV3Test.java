package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;

public class MiscMapperV3Test {

    @Test
    public void mapExternalIdentifierAtoBShouldMapUrlValue() {
        PersonExternalIdentifier model = new PersonExternalIdentifier();
        Url url = new Url();
        url.setValue("https://example.org/ext");
        model.setUrl(url);

        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        MiscMapperV3.INSTANCE.mapExternalIdentifierAtoB(model, entity);

        assertEquals("https://example.org/ext", entity.getExternalIdUrl());
    }

    @Test
    public void mapOrgBtoAShouldPopulateAddressWhenRegionPresent() {
        OrgEntity entity = new OrgEntity();
        entity.setRegion("Scotland");

        Organization model = new Organization();
        MiscMapperV3.INSTANCE.mapOrgBtoA(entity, model);

        assertNotNull(model.getAddress());
        assertEquals("Scotland", model.getAddress().getRegion());
    }

    @Test
    public void mapResearchResourceAtoBShouldMapProposalFields() {
        ResearchResource model = mock(ResearchResource.class, RETURNS_DEEP_STUBS);
        when(model.getProposal().getTitle().getTranslatedTitle().getContent()).thenReturn("Translated");
        when(model.getProposal().getTitle().getTranslatedTitle().getLanguageCode()).thenReturn("en");
        when(model.getProposal().getUrl().getValue()).thenReturn("https://example.org/proposal");

        ResearchResourceEntity entity = new ResearchResourceEntity();
        MiscMapperV3.INSTANCE.mapResearchResourceAtoB(model, entity);

        assertEquals("Translated", entity.getTranslatedTitle());
        assertEquals("en", entity.getTranslatedTitleLanguageCode());
        assertEquals("https://example.org/proposal", entity.getUrl());
    }

    @Test
    public void mapResearchResourceAtoBShouldHandleNullProposal() {
        ResearchResource model = new ResearchResource();

        ResearchResourceEntity entity = new ResearchResourceEntity();
        MiscMapperV3.INSTANCE.mapResearchResourceAtoB(model, entity);

        assertNull(entity.getTranslatedTitle());
        assertNull(entity.getTranslatedTitleLanguageCode());
        assertNull(entity.getUrl());
    }

    @Test
    public void mapAffiliationAtoBShouldSetUrl() {
        Education model = new Education();
        Url url = new Url();
        url.setValue("https://example.org/affiliation");
        model.setUrl(url);

        OrgAffiliationRelationEntity entity = new OrgAffiliationRelationEntity();
        MiscMapperV3.INSTANCE.mapAffiliationAtoB(model, entity);

        assertEquals("https://example.org/affiliation", entity.getUrl());
    }
}
