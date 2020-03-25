package org.orcid.core.adapter.v3;

import org.orcid.jaxb.model.v3.release.record.Spam;
import org.orcid.persistence.jpa.entities.SpamEntity;

public interface JpaJaxbSpamAdapter {
    SpamEntity toSpamEntity(Spam spam);

    Spam toSpam(SpamEntity spamEntity);

}
