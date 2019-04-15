package org.orcid.core.adapter.v3;

import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;

public interface JpaJaxbResearchResourceAdapter extends JpaJaxbActivityAdapter<ResearchResource,ResearchResourceEntity,ResearchResourceSummary>{
}
