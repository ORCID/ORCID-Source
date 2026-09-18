package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.FundingMapperV2;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

public class FundingMapperV2Test {

    @Test
    public void mapFundingCustomFieldsShouldPopulateEntityValues() {
        Funding funding = mock(Funding.class, RETURNS_DEEP_STUBS);
        when(funding.getOrganizationDefinedType().getContent()).thenReturn("grant");
        when(funding.getTitle().getTranslatedTitle().getContent()).thenReturn("titulo");
        when(funding.getTitle().getTranslatedTitle().getLanguageCode()).thenReturn("es");
        when(funding.getUrl().getValue()).thenReturn("https://example.org/funding");
        when(funding.getAmount().getContent()).thenReturn("123.45");
        when(funding.getAmount().getCurrencyCode()).thenReturn("USD");

        ProfileFundingEntity entity = new ProfileFundingEntity();

        FundingMapperV2.INSTANCE.mapFundingCustomFields(funding, entity);

        assertEquals("grant", entity.getOrganizationDefinedType());
        assertEquals("https://example.org/funding", entity.getUrl());
        assertEquals("titulo", entity.getTranslatedTitle());
        assertEquals("es", entity.getTranslatedTitleLanguageCode());
        assertEquals(BigDecimal.valueOf(123.45d), entity.getNumericAmount());
        assertEquals("USD", entity.getCurrencyCode());
    }

    @Test
    public void mapFundingCustomFieldsShouldHandleNulls() {
        Funding funding = mock(Funding.class);
        ProfileFundingEntity entity = new ProfileFundingEntity();

        FundingMapperV2.INSTANCE.mapFundingCustomFields(funding, entity);

        assertNull(entity.getOrganizationDefinedType());
        assertNull(entity.getUrl());
        assertNull(entity.getTranslatedTitle());
        assertNull(entity.getTranslatedTitleLanguageCode());
        assertNull(entity.getNumericAmount());
        assertNull(entity.getCurrencyCode());
    }
}
