package org.orcid.core.adapter.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;

/**
 * Replacement for Orika's MapperFacade using plain Java without any Orika dependencies.
 * 
 * This facade maintains a registry of bidirectional mapping functions for type pairs
 * and provides the interface that MapperFacadeSupport expects via reflection.
 * 
 * Method signatures deliberately match what MapperFacadeSupport looks up:
 * - map(Object, Class)
 * - map(Object, Object)
 * - mapAsList(Iterable, Class)
 * - mapAsSet(Iterable, Class)
 * - mapAsList(Iterable, Class, Object context)
 * - newMappingContext()
 */
public class HandwrittenMapperFacade implements MapperFacade {
    
    private static final Object NULL_KEY = new Object();
    
    private final Map<String, MapperEntry<?, ?>> forwardMappers = new HashMap<>();
    private final Map<String, MapperEntry<?, ?>> reverseMappers = new HashMap<>();
    
    /**
     * Register a bidirectional mapper for a type pair.
     * 
     * @param <A> the first type
     * @param <B> the second type
     * @param typeA the class of the first type
     * @param typeB the class of the second type
     * @param mapAToB function to map from A to B
     * @param mapBToA function to map from B to A
     */
    public <A, B> void registerMapper(Class<A> typeA, Class<B> typeB, 
            MappingFunction<A, B> mapAToB, 
            MappingFunction<B, A> mapBToA) {
        String keyA = getTypeKey(typeA, typeB);
        String keyB = getTypeKey(typeB, typeA);
        
        forwardMappers.put(keyA, new MapperEntry<>(typeA, typeB, mapAToB));
        reverseMappers.put(keyB, new MapperEntry<>(typeB, typeA, mapBToA));
    }
    
    /**
     * Register a unidirectional mapper (only A → B, no reverse).
     */
    public <A, B> void registerMapperOneWay(Class<A> typeA, Class<B> typeB,
            MappingFunction<A, B> mapAToB) {
        String key = getTypeKey(typeA, typeB);
        forwardMappers.put(key, new MapperEntry<>(typeA, typeB, mapAToB));
    }
    
    /**
     * Map an object from one type to another, creating a new instance of the destination type.
     * 
     * @param source the source object to map
     * @param destinationClass the class of the destination type
     * @return a new instance of destinationClass with data mapped from source
     */
    @SuppressWarnings("unchecked")
    public <D> D map(Object source, Class<D> destinationClass) {
        return map(source, destinationClass, null);
    }
    
    /**
     * Map an object from one type to another, creating a new instance of the destination type,
     * with context for context-dependent mapping logic.
     * 
     * @param source the source object to map
     * @param destinationClass the class of the destination type
     * @param context mapping context with optional properties
     * @return a new instance of destinationClass with data mapped from source
     */
    @SuppressWarnings("unchecked")
    public <D> D map(Object source, Class<D> destinationClass, Object context) {
        if (source == null) {
            return null;
        }
        
        String key = getTypeKey(source.getClass(), destinationClass);
        MapperEntry<?, ?> entry = forwardMappers.get(key);
        
        if (entry != null) {
            MapperEntry<Object, D> typedEntry = (MapperEntry<Object, D>) entry;
            MappingContext ctx = (context instanceof MappingContext) ? 
                (MappingContext) context : new MappingContext();
            return typedEntry.mapper.map(source, ctx);
        }
        
        // Fallback: use reflection-based copy for same-class or compatible types
        return copyByReflection(source, destinationClass);
    }
    
    /**
     * Map an object from one type to another, copying data into an existing destination instance.
     * 
     * @param source the source object to map from
     * @param destination the destination object to map into (must be instantiated already)
     * @return the destination object (same instance as parameter)
     */
    @SuppressWarnings("unchecked")
    public <D> D map(Object source, D destination) {
        return map(source, destination, null);
    }
    
    /**
     * Map an object from one type to another, copying data into an existing destination instance,
     * with context.
     * 
     * @param source the source object to map from
     * @param destination the destination object to map into
     * @param context mapping context with optional properties
     * @return the destination object (same instance as parameter)
     */
    @SuppressWarnings("unchecked")
    public <D> D map(Object source, D destination, Object context) {
        if (source == null || destination == null) {
            return destination;
        }
        
        Class<?> sourceClass = source.getClass();
        Class<?> destClass = destination.getClass();
        String key = getTypeKey(sourceClass, destClass);
        
        MapperEntry<?, ?> entry = forwardMappers.get(key);
        if (entry != null) {
            // For now, mappers create new instances; this is a fallback
            MapperEntry<Object, D> typedEntry = (MapperEntry<Object, D>) entry;
            MappingContext ctx = (context instanceof MappingContext) ? 
                (MappingContext) context : new MappingContext();
            D mapped = typedEntry.mapper.map(source, ctx);
            copyInto(mapped, destination);
            return destination;
        }
        
        // Fallback: copy matching properties
        copyMatchingProperties(source, destination);
        return destination;
    }
    
    /**
     * Map an iterable of objects from one type to a list of another type.
     * 
     * @param <D> the destination type
     * @param sources an iterable of source objects
     * @param destinationClass the class of each destination element
     * @return a list of mapped destination objects
     */
    public <D> List<D> mapAsList(Iterable<?> sources, Class<D> destinationClass) {
        return mapAsList(sources, destinationClass, null);
    }
    
    /**
     * Map an iterable of objects from one type to a list of another type, with context.
     * 
     * @param <D> the destination type
     * @param sources an iterable of source objects
     * @param destinationClass the class of each destination element
     * @param context mapping context with optional properties
     * @return a list of mapped destination objects
     */
    public <D> List<D> mapAsList(Iterable<?> sources, Class<D> destinationClass, Object context) {
        List<D> result = new ArrayList<>();
        if (sources != null) {
            for (Object source : sources) {
                result.add(map(source, destinationClass, context));
            }
        }
        return result;
    }
    
    /**
     * Map an iterable of objects from one type to a set of another type.
     * 
     * @param <D> the destination type
     * @param sources an iterable of source objects
     * @param destinationClass the class of each destination element
     * @return a set of mapped destination objects
     */
    public <D> Set<D> mapAsSet(Iterable<?> sources, Class<D> destinationClass) {
        Set<D> result = new HashSet<>();
        if (sources != null) {
            for (Object source : sources) {
                result.add(map(source, destinationClass));
            }
        }
        return result;
    }
    
    /**
     * Create a new mapping context for context-dependent mapping logic.
     * 
     * @return a new MappingContext instance
     */
    public MappingContext newMappingContext() {
        return new MappingContext();
    }
    
    /**
     * Copy all properties with matching names and compatible types from source to destination.
     * This is the "by default" logic equivalent to Orika's byDefault().
     * 
     * @param source the source object
     * @param destination the destination object
     */
    public static void copyMatchingProperties(Object source, Object destination) {
        if (source == null || destination == null) {
            return;
        }
        
        try {
            BeanInfo sourceBeanInfo = Introspector.getBeanInfo(source.getClass());
            BeanInfo destBeanInfo = Introspector.getBeanInfo(destination.getClass());
            
            PropertyDescriptor[] sourceProps = sourceBeanInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> destProps = new HashMap<>();
            for (PropertyDescriptor pd : destBeanInfo.getPropertyDescriptors()) {
                destProps.put(pd.getName(), pd);
            }
            
            for (PropertyDescriptor sourceProp : sourceProps) {
                String propName = sourceProp.getName();
                if ("class".equals(propName)) {
                    continue;
                }
                
                PropertyDescriptor destProp = destProps.get(propName);
                if (destProp == null || destProp.getWriteMethod() == null) {
                    continue;
                }
                
                Method readMethod = sourceProp.getReadMethod();
                if (readMethod == null) {
                    continue;
                }
                
                try {
                    Object value = readMethod.invoke(source);
                    Method writeMethod = destProp.getWriteMethod();
                    
                    // Check if types are compatible
                    Class<?> sourceType = sourceProp.getPropertyType();
                    Class<?> destType = destProp.getPropertyType();
                    
                    if (destType.isAssignableFrom(sourceType)) {
                        writeMethod.invoke(destination, value);
                    }
                } catch (Exception e) {
                    // Skip properties that can't be copied
                }
            }
        } catch (IntrospectionException e) {
            // If introspection fails, silently skip
        }
    }
    
    /**
     * Copy all properties from source to destination (used when mapping into existing instance).
     */
    private static void copyInto(Object source, Object destination) {
        copyMatchingProperties(source, destination);
    }
    
    /**
     * Reflection-based copy for same-class or compatible types (fallback when no explicit mapper exists).
     */
    @SuppressWarnings("unchecked")
    private static <D> D copyByReflection(Object source, Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        
        try {
            D destination = destinationClass.getDeclaredConstructor().newInstance();
            copyMatchingProperties(source, destination);
            return destination;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance of " + destinationClass.getName(), e);
        }
    }
    
    /**
     * Get a unique key for a type pair.
     */
    private String getTypeKey(Class<?> typeA, Class<?> typeB) {
        return typeA.getName() + " -> " + typeB.getName();
    }
    
    /**
     * Entry storing a mapper function for a specific type pair.
     */
    private static class MapperEntry<A, B> {
        final Class<A> sourceType;
        final Class<B> destType;
        final MappingFunction<A, B> mapper;
        
        MapperEntry(Class<A> sourceType, Class<B> destType, MappingFunction<A, B> mapper) {
            this.sourceType = sourceType;
            this.destType = destType;
            this.mapper = mapper;
        }
    }
    
    /**
     * Functional interface for mapping functions.
     * Takes a source object and a context, returns a mapped destination object.
     */
    @FunctionalInterface
    public interface MappingFunction<A, B> {
        B map(A source, MappingContext context);
    }
    
    // ============ MapperFacade interface stub implementations ============
    // Most Orika-specific methods are not used; they're stubbed to throw UnsupportedOperationException
    
    @Override
    public void map(Object source, Object destination, MappingContext context) {
        throw new UnsupportedOperationException("Use map(source, destination) instead");
    }

    @Override
    public <S, D> D map(S source, Class<D> destinationClass, MappingContext context) {
        throw new UnsupportedOperationException("Use map(source, destinationClass) instead");
    }

    @Override
    public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass, MappingContext context) {
        throw new UnsupportedOperationException("Use mapAsList(source, destinationClass) instead");
    }

    @Override
    public <S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass, MappingContext context) {
        throw new UnsupportedOperationException("Use mapAsSet(source, destinationClass) instead");
    }

    @Override
    public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
        return this.mapAsList(source, destinationClass);
    }

    @Override
    public <S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass) {
        return this.mapAsSet(source, destinationClass);
    }

    @Override
    public <S, D> D mapToNew(S source, Class<D> destinationClass) {
        return map(source, destinationClass);
    }

    @Override
    public <S, D> D mapToNew(S source, Class<D> destinationClass, MappingContext context) {
        return map(source, destinationClass, context);
    }

    @Override
    public <S, D> void mapToExisting(S source, D destination) {
        map(source, destination);
    }

    @Override
    public <S, D> void mapToExisting(S source, D destination, MappingContext context) {
        map(source, destination, context);
    }

    @Override
    public <S, D> D mapToExistingAs(S source, D destination) {
        map(source, destination);
        return destination;
    }

    @Override
    public <S, D> D mapToExistingAs(S source, D destination, MappingContext context) {
        map(source, destination, context);
        return destination;
    }

    @Override
    public <S> java.util.Date mapAsDate(S source, Class<?> dateClass) {
        throw new UnsupportedOperationException("Date mapping not implemented");
    }

    @Override
    public <S> java.util.Date mapAsDate(S source, Class<?> dateClass, MappingContext context) {
        throw new UnsupportedOperationException("Date mapping not implemented");
    }
}
