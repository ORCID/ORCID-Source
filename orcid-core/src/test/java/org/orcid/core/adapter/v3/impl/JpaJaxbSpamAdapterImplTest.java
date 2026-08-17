package org.orcid.core.adapter.v3.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.record.SourceType;
import org.orcid.jaxb.model.v3.release.record.Spam;
import org.orcid.persistence.jpa.entities.SpamEntity;

public class JpaJaxbSpamAdapterImplTest {

    private final JpaJaxbSpamAdapterImpl adapter = new JpaJaxbSpamAdapterImpl();

    @Test
    public void toSpamEntityShouldReturnNullForNullInput() {
        assertNull(adapter.toSpamEntity(null));
    }

    @Test
    public void toSpamEntityShouldMapFields() {
        Spam spam = new Spam();
        spam.setSpamCounter(4);
        spam.setSourceType(SourceType.USER);

        SpamEntity entity = adapter.toSpamEntity(spam);

        assertNotNull(entity);
        assertEquals(Integer.valueOf(4), entity.getSpamCounter());
        assertEquals(org.orcid.persistence.jpa.entities.SourceType.USER, entity.getSourceType());
    }

    @Test
    public void toSpamShouldReturnNullForNullInput() {
        assertNull(adapter.toSpam(null));
    }

    @Test
    public void toSpamShouldMapFields() {
        SpamEntity entity = new SpamEntity();
        entity.setId(7L);
        entity.setOrcid("0000-0000-0000-0002");
        entity.setSpamCounter(2);
        entity.setSourceType(org.orcid.persistence.jpa.entities.SourceType.USER);

        Spam spam = adapter.toSpam(entity);

        assertNotNull(spam);
        assertEquals(Integer.valueOf(2), spam.getSpamCounter());
        assertEquals(SourceType.USER, spam.getSourceType());
    }
}
