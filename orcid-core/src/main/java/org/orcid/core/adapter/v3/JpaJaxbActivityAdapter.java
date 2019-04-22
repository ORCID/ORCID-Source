package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
//model, entity, summary, 
public interface JpaJaxbActivityAdapter <M,E,S>{
    E toEntity(M model);

    M toModel(E entity);
    
    S toSummary(E entity);

    List<M> toModels(Collection<E> entities);
    
    List<S> toSummaries(Collection<E> entities);
    
    E toEntity(M model, E existing);
}
