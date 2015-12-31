package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record_rc2.DelegationDetails;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;

public interface JpaJaxbGivenPermissionToAdapter {
    GivenPermissionToEntity toGivenPermissionTo(DelegationDetails details);

    DelegationDetails toDelegationDetails(GivenPermissionToEntity entity);

    List<DelegationDetails> toDelegationDetailsList(Collection<GivenPermissionToEntity> entities);
}
