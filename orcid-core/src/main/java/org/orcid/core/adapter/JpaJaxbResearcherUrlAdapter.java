package org.orcid.core.adapter;

import java.util.Collection;

import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public interface JpaJaxbResearcherUrlAdapter {
    ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl);

    ResearcherUrl toResearcherUrl(ResearcherUrlEntity entity);
    
    ResearcherUrls toResearcherUrlList(Collection<ResearcherUrlEntity> entities);
    
    ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl, ResearcherUrlEntity existing);
}
