package org.orcid.core.version.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.version.V3VersionObjectFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class V3VersionObjectFactoryImpl implements V3VersionObjectFactory {

    private final ConcurrentHashMap<Pair<Class<?>, String>, Class<?>> classMap = new ConcurrentHashMap<>();

    @Override
    public Object createEquivalentInstance(Object originalObject, String requiredVersion) {
        Class<?> originalClass = originalObject.getClass();
        Pair<Class<?>, String> key = new ImmutablePair<>(originalClass, requiredVersion);
        Class<?> requiredClass = classMap.get(key);
        if (requiredClass == null) {
            requiredClass = createClass(originalClass, requiredVersion);
            classMap.put(key, requiredClass);
        }
        return createInstance(requiredClass);
    }

    private Class<?> createClass(Class<?> originalClass, String requiredVersion) {
        try {
            return Class.forName(calculateClassFullName(originalClass, requiredVersion));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find equivalent class for " + originalClass + " in version " + requiredVersion, e);
        }
    }

    private String calculateClassFullName(Class<?> originalClass, String requiredVersion) {
        String requiredMinorVersion = requiredVersion.substring(requiredVersion.lastIndexOf('_') + 1);
        String originalPackageName = originalClass.getPackage().getName();
        String packageNameBase = originalPackageName.substring(0, originalPackageName.lastIndexOf("v3.") + 3);
        String packageNameRemainder = originalPackageName.substring(originalPackageName.lastIndexOf("v3.") + 6); // assumption that minor version is 3 chars, eg rc1
        String originalClassName = originalClass.getSimpleName();
        String requiredClassFullName = packageNameBase + requiredMinorVersion + packageNameRemainder + '.' + originalClassName;
        return requiredClassFullName;
    }

    private Object createInstance(Class<?> requiredClass) {
        try {
            return requiredClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Problem instantiating class " + requiredClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Problem accessing class " + requiredClass.getName(), e);
        }
    }

}
