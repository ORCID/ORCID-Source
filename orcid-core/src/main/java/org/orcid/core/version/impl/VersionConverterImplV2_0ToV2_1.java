package org.orcid.core.version.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionObjectFactory;
import org.orcid.jaxb.model.common_v2.OrcidIdBase;
import org.orcid.jaxb.model.common_v2.SourceClientId;

public class VersionConverterImplV2_0ToV2_1 implements V2VersionConverter {

    private static final String LOWER_VERSION = "2.0";
    private static final String UPPER_VERSION = "2.1";

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private V2VersionObjectFactory v2VersionObjectFactory;

    @Override
    public String getLowerVersion() {
        return LOWER_VERSION;
    }

    @Override
    public String getUpperVersion() {
        return UPPER_VERSION;
    }

    @Override
    public V2Convertible downgrade(V2Convertible objectToDowngrade) {
        Object objectToConvert = objectToDowngrade.getObjectToConvert();
        Object targetObject = v2VersionObjectFactory.createEquivalentInstance(objectToConvert, LOWER_VERSION);
        copyInto(objectToConvert, targetObject, true);
        return new V2Convertible(targetObject, LOWER_VERSION);
    }

    @Override
    public V2Convertible upgrade(V2Convertible objectToUpgrade) {
        Object objectToConvert = objectToUpgrade.getObjectToConvert();
        Object targetObject = v2VersionObjectFactory.createEquivalentInstance(objectToConvert, UPPER_VERSION);
        copyInto(objectToConvert, targetObject, false);
        return new V2Convertible(targetObject, UPPER_VERSION);
    }

    /**
     * Deep-copies every bean property from source onto an already-constructed target of the
     * same class, recomputing OrcidIdBase.uri per direction. Handles standard properties with setters,
     * getter-only collection properties, and getter-only complex object properties.
     */
    private void copyInto(Object source, Object target, boolean downgrade) {
        if (source == null || target == null) {
            return;
        }
        if (source instanceof OrcidIdBase) {
            copyOrcidIdBase((OrcidIdBase) source, (OrcidIdBase) target, downgrade);
            return;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if ("class".equals(pd.getName())) {
                    continue;
                }
                Method readMethod = readMethodFor(pd, source.getClass());
                if (readMethod == null) {
                    continue;
                }
                if (pd.getWriteMethod() != null) {
                    Object value = readMethod.invoke(source);
                    pd.getWriteMethod().invoke(target, copyValue(value, downgrade));
                } else if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
                    Object sourceColl = readMethod.invoke(source);
                    if (sourceColl instanceof Collection) {
                        Object targetCollObj = readMethod.invoke(target);
                        if (targetCollObj instanceof Collection) {
                            @SuppressWarnings("unchecked")
                            Collection<Object> targetColl = (Collection<Object>) targetCollObj;
                            targetColl.clear();
                            @SuppressWarnings("unchecked")
                            Collection<Object> copiedColl = (Collection<Object>) copyValue(sourceColl, downgrade);
                            if (copiedColl != null) {
                                targetColl.addAll(copiedColl);
                            }
                        }
                    }
                } else {
                    // Handles getter-only non-collection complex properties
                    Object sourceObj = readMethod.invoke(source);
                    if (sourceObj != null) {
                        Object targetObj = readMethod.invoke(target);
                        if (targetObj != null) {
                            copyInto(sourceObj, targetObj, downgrade);
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException | IntrospectionException e) {
            throw new RuntimeException("Unable to copy " + source.getClass(), e);
        }
    }

    /**
     * The property's read method, falling back to an "is" accessor for boxed Boolean.
     *
     * The JavaBeans Introspector accepts an "is" prefix only for primitive boolean, so a property
     * declared as {@code Boolean isVerified()} has no read method and is skipped, dropping the
     * value from the converted object. Email is the one that bites: verified, current and primary
     * are all boxed, so all three were skipped, and verified and primary - the two that reach the
     * wire - came back null from /v2.0 and /v2.1 for an address the database has as both. Orika,
     * which this converter replaced, resolved "is" for Boolean as well, so nothing was lost
     * before the migration.
     *
     * Deliberately narrow: boxed Boolean only. Widening it would change properties nobody has
     * reported a problem with.
     */
    private Method readMethodFor(PropertyDescriptor pd, Class<?> sourceClass) {
        if (pd.getReadMethod() != null) {
            return pd.getReadMethod();
        }
        if (!Boolean.class.equals(pd.getPropertyType()) || pd.getName().isEmpty()) {
            return null;
        }
        String accessor = "is" + Character.toUpperCase(pd.getName().charAt(0)) + pd.getName().substring(1);
        try {
            Method candidate = sourceClass.getMethod(accessor);
            return Boolean.class.equals(candidate.getReturnType()) ? candidate : null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Object copyValue(Object value, boolean downgrade) {
        if (value == null || isImmutable(value)) {
            return value;
        }
        if (value instanceof List) {
            List<Object> copy = new ArrayList<>();
            for (Object item : (List<?>) value) {
                copy.add(copyValue(item, downgrade));
            }
            return copy;
        }
        if (value instanceof Set) {
            Set<Object> copy = new LinkedHashSet<>();
            for (Object item : (Set<?>) value) {
                copy.add(copyValue(item, downgrade));
            }
            return copy;
        }
        try {
            Object copy = value.getClass().getDeclaredConstructor().newInstance();
            copyInto(value, copy, downgrade);
            return copy;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to instantiate " + value.getClass(), e);
        }
    }

    private void copyOrcidIdBase(OrcidIdBase a, OrcidIdBase b, boolean downgrade) {
        b.setHost(a.getHost());
        b.setPath(a.getPath());
        String pathPrefix = (a instanceof SourceClientId) ? "/client/" : "/";
        if (downgrade) {
            b.setUri(orcidUrlManager.getBaseUriHttp() + pathPrefix + b.getPath());
        } else {
            b.setUri(orcidUrlManager.getBaseUrl() + pathPrefix + a.getPath());
        }
    }

    private boolean isImmutable(Object value) {
        return value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character || value.getClass().isEnum()
                || value instanceof java.util.Date || value instanceof javax.xml.datatype.XMLGregorianCalendar;
    }
}