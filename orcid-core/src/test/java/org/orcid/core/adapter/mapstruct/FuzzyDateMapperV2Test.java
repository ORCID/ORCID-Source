package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;

public class FuzzyDateMapperV2Test {

    @Test
    public void fuzzyDateToPublicationDateEntityShouldMapValues() {
        FuzzyDate fuzzyDate = new FuzzyDate();
        fuzzyDate.setYear(new Year(2020));
        fuzzyDate.setMonth(new Month(5));
        fuzzyDate.setDay(new Day(7));

        PublicationDateEntity entity = new PublicationDateEntity();
        FuzzyDateMapperV2.INSTANCE.fuzzyDateToPublicationDateEntity(fuzzyDate, entity);

        assertEquals(Integer.valueOf(2020), entity.getYear());
        assertEquals(Integer.valueOf(5), entity.getMonth());
        assertEquals(Integer.valueOf(7), entity.getDay());
    }

    @Test
    public void publicationDateEntityToFuzzyDateShouldMapNulls() {
        PublicationDateEntity entity = new PublicationDateEntity();
        FuzzyDate fuzzyDate = new FuzzyDate();

        FuzzyDateMapperV2.INSTANCE.publicationDateEntityToFuzzyDate(entity, fuzzyDate);

        assertNull(fuzzyDate.getYear());
        assertNull(fuzzyDate.getMonth());
        assertNull(fuzzyDate.getDay());
    }
}
