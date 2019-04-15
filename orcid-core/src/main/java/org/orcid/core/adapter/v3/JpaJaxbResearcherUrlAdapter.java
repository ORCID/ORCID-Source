package org.orcid.core.adapter.v3;

import java.util.Collection;

import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public interface JpaJaxbResearcherUrlAdapter {
    ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl);

    ResearcherUrl toResearcherUrl(ResearcherUrlEntity entity);
    
    ResearcherUrls toResearcherUrlList(Collection<ResearcherUrlEntity> entities);
    
    ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl, ResearcherUrlEntity existing);
}
