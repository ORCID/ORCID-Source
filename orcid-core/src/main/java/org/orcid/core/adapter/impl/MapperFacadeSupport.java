package org.orcid.core.adapter.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MapperFacadeSupport {

    private Object mapperFacade;

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    public <S, D> D map(S source, Class<D> destinationClass) {
        return destinationClass.cast(invoke("map", new Class<?>[] { Object.class, Class.class }, source, destinationClass));
    }

    public <S, D> D map(S source, D destinationObject) {
        return cast(invoke("map", new Class<?>[] { Object.class, Object.class }, source, destinationObject));
    }

    public <S, D> List<D> mapAsList(Collection<S> source, Class<D> destinationClass) {
        return cast(invoke("mapAsList", new Class<?>[] { Iterable.class, Class.class }, source, destinationClass));
    }

    public <S, D> Set<D> mapAsSet(Collection<S> source, Class<D> destinationClass) {
        return cast(invoke("mapAsSet", new Class<?>[] { Iterable.class, Class.class }, source, destinationClass));
    }

    public Object newContext() {
        if (mapperFacade == null) {
            throw new IllegalStateException("Mapper facade has not been set");
        }

        try {
            Method factoryMethod = mapperFacade.getClass().getMethod("newMappingContext");
            return factoryMethod.invoke(mapperFacade);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to create mapping context", e);
        }
    }

    public void setContextProperty(Object context, String key, Object value) {
        if (context == null) {
            return;
        }

        try {
            Method method = context.getClass().getMethod("setProperty", Object.class, Object.class);
            method.invoke(context, key, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to set mapping context property", e);
        }
    }

    public <S, D> List<D> mapAsList(Collection<S> source, Class<D> destinationClass, Object context) {
        if (mapperFacade == null) {
            throw new IllegalStateException("Mapper facade has not been set");
        }

        Method candidate = null;
        for (Method method : mapperFacade.getClass().getMethods()) {
            if (!"mapAsList".equals(method.getName())) {
                continue;
            }
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 3 && Iterable.class.isAssignableFrom(params[0]) && Class.class.equals(params[1])) {
                candidate = method;
                break;
            }
        }

        if (candidate == null) {
            throw new IllegalStateException("Unable to find mapAsList method with context");
        }

        try {
            return cast(candidate.invoke(mapperFacade, source, destinationClass, context));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to map list with context", e);
        }
    }

    private Object invoke(String methodName, Class<?>[] parameterTypes, Object... args) {
        if (mapperFacade == null) {
            throw new IllegalStateException("Mapper facade has not been set");
        }
        try {
            Method method = mapperFacade.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(mapperFacade, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to invoke mapper facade method: " + methodName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object value) {
        return (T) value;
    }
}
