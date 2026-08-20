package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public class JpaJaxbKeywordAdapterImpl implements JpaJaxbKeywordAdapter {

    private Object mapperFacade;

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword) {
        if (keyword == null) {
            return null;
        }
        ProfileKeywordEntity result = map(keyword, ProfileKeywordEntity.class);
        if(result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    @Override
    public Keyword toKeyword(ProfileKeywordEntity entity) {
        if (entity == null) {
            return null;
        }
        return map(entity, Keyword.class);
    }

    @Override
    public Keywords toKeywords(Collection<ProfileKeywordEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<Keyword> keywordList = mapAsList(entities, Keyword.class);
        Keywords keywords = new Keywords();
        keywords.setKeywords(keywordList);
        return keywords;
    }

    @Override
    public ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword, ProfileKeywordEntity existing) {
        if (keyword == null) {
            return null;
        }
        map(keyword, existing);
        return existing;
    }

    private <S, D> D map(S source, Class<D> destinationClass) {
        return destinationClass.cast(invoke("map", new Class<?>[] { Object.class, Class.class }, source, destinationClass));
    }

    private <S, D> D map(S source, D destinationObject) {
        return cast(invoke("map", new Class<?>[] { Object.class, Object.class }, source, destinationObject));
    }

    private <S, D> List<D> mapAsList(Collection<S> source, Class<D> destinationClass) {
        if (source.isEmpty()) {
            return Collections.emptyList();
        }
        Object mapped = invoke("mapAsList", new Class<?>[] { Iterable.class, Class.class }, source, destinationClass);
        if (mapped instanceof List<?>) {
            return cast(mapped);
        }
        return source.stream().map(item -> map(item, destinationClass)).collect(Collectors.toList());
    }

    private Object invoke(String methodName, Class<?>[] parameterTypes, Object... args) {
        if (mapperFacade == null) {
            throw new IllegalStateException("Mapper facade has not been set");
        }
        try {
            return mapperFacade.getClass().getMethod(methodName, parameterTypes).invoke(mapperFacade, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to invoke mapper facade method: " + methodName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object value) {
        return (T) value;
    }

}
