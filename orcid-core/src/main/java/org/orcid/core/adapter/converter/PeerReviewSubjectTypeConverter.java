package org.orcid.core.adapter.converter;

import org.orcid.jaxb.model.record_v2.WorkType;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class PeerReviewSubjectTypeConverter  extends BidirectionalConverter<WorkType, String> {

    @Override
    public String convertTo(WorkType source, Type<String> destinationType) {
        return source.name();
    }

    @Override
    public WorkType convertFrom(String source, Type<WorkType> destinationType) {
        try {
            return WorkType.valueOf(source);
        } catch (IllegalArgumentException e) {
            if(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name().equals(source)) {
              return WorkType.DISSERTATION;  
            } 
            return WorkType.OTHER;
        }
    }

}
