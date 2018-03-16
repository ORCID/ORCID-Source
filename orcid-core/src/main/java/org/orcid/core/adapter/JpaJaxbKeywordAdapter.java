package org.orcid.core.adapter;

import java.util.Collection;

import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public interface JpaJaxbKeywordAdapter {

    ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword);

    Keyword toKeyword(ProfileKeywordEntity entity);

    Keywords toKeywords(Collection<ProfileKeywordEntity> entities);        

    ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword, ProfileKeywordEntity existing);
}
