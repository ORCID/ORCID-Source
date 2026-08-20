package org.orcid.core.adapter.v3.impl;

import org.orcid.core.adapter.v3.JpaJaxbSpamAdapter;
import org.orcid.jaxb.model.v3.release.record.SourceType;
import org.orcid.jaxb.model.v3.release.record.Spam;
import org.orcid.persistence.jpa.entities.SpamEntity;

public class JpaJaxbSpamAdapterImpl implements JpaJaxbSpamAdapter {

    public void setMapperFacade(Object mapperFacade) {
        // No-op: retained for backward-compatible Spring XML wiring during incremental Orika removal.
    }

    @Override
    public SpamEntity toSpamEntity(Spam spam) {
        if (spam == null) {
            return null;
        }
        SpamEntity mapped = new SpamEntity();
        mapped.setSpamCounter(spam.getSpamCounter());
        if (spam.getSourceType() != null) {
            mapped.setSourceType(org.orcid.persistence.jpa.entities.SourceType.valueOf(spam.getSourceType().name()));
        }
        return mapped;
    }

    @Override
    public Spam toSpam(SpamEntity spamEntity) {
        if (spamEntity == null) {
            return null;
        }
        Spam mapped = new Spam();
        mapped.setSpamCounter(spamEntity.getSpamCounter());
        if (spamEntity.getSourceType() != null) {
            mapped.setSourceType(SourceType.fromValue(spamEntity.getSourceType().name()));
        }
        return mapped;
    }
    
}
