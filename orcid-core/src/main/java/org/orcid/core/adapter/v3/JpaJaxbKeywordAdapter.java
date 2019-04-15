package org.orcid.core.adapter.v3;

import java.util.Collection;

import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public interface JpaJaxbKeywordAdapter {

    ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword);

    Keyword toKeyword(ProfileKeywordEntity entity);

    Keywords toKeywords(Collection<ProfileKeywordEntity> entities);        

    ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword, ProfileKeywordEntity existing);
}
