/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.tree;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TreeCleaner.class);

    public static final ConcurrentHashMap<Class<?>, Class<?>> CANDIDATE_CLASSES = new ConcurrentHashMap<>();

    private boolean removeEmptyObjects = true;

    public boolean isRemoveEmptyObjects() {
        return removeEmptyObjects;
    }

    public void setRemoveEmptyObjects(boolean removeEmptyObjects) {
        this.removeEmptyObjects = removeEmptyObjects;
    }

    public void clean(Object obj, TreeCleaningStrategy decisionMaker) {
        if (obj == null) {
            return;
        }
        if (Collection.class.isAssignableFrom(obj.getClass())) {
            processCollection((Collection<?>) obj, decisionMaker);
        } else {
            processGetters(obj, decisionMaker);
        }
    }

    private void processCollection(Collection<?> coll, TreeCleaningStrategy decisionMaker) {
        Iterator<?> iterator = coll.iterator();
        while (iterator.hasNext()) {
            Object objInCollection = iterator.next();
            TreeCleaningDecision decision = decisionMaker.needsStripping(objInCollection);
            if (decision.isNeedingCleaning()) {
                iterator.remove();
            } else {
                if (decision.isNeedingPropertyChecking()) {
                    clean(objInCollection, decisionMaker);
                }
                if (removeEmptyObjects && hasNoActiveProperties(objInCollection)) {
                    iterator.remove();
                }
            }
        }
    }

    private void processGetters(Object obj, TreeCleaningStrategy decisionMaker) {
        Map<Method, Method> gettersAndSetters = getGetterAndCorrespondingSetter(obj.getClass().getMethods());
        Set<Method> getters = gettersAndSetters.keySet();
        for (Method getter : getters) {
            try {
                Object returnedObj = getter.invoke(obj);
                TreeCleaningDecision decision = decisionMaker.needsStripping(returnedObj);
                if (decision.isNeedingCleaning()) {
                    nullify(obj, gettersAndSetters, getter);
                } else {
                    if (decision.isNeedingPropertyChecking()) {
                        clean(returnedObj, decisionMaker);
                    }
                    if (removeEmptyObjects && hasNoActiveProperties(returnedObj)) {
                        nullify(obj, gettersAndSetters, getter);
                    }
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Cannot execute method", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("Cannot execute method", e);
            }
        }
    }

    private void nullify(Object obj, Map<Method, Method> gettersAndSetters, Method getter) throws IllegalAccessException, InvocationTargetException {
        Method setter = gettersAndSetters.get(getter);
        Type[] params = setter.getGenericParameterTypes();
        if (params.length > 0) {
            Object[] empty = new Object[params.length];
            setter.invoke(obj, empty);
        }
    }

    private boolean hasNoActiveProperties(Object ob) {
        if (ob == null) {
            return true;
        }
        Map<Method, Method> orcidGettersAndSetters = getGetterAndCorrespondingSetter(ob.getClass().getMethods());
        Set<Method> getters = orcidGettersAndSetters.keySet();

        int inactiveCount = 0;
        for (Method getter : getters) {
            try {
                Object returned = getter.invoke(ob);
                if (returned == null) {
                    inactiveCount++;
                } else if (Collection.class.isAssignableFrom(returned.getClass())) {
                    Collection<?> coll = (Collection<?>) returned;
                    if (coll.isEmpty()) {
                        inactiveCount++;
                    }
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Cannot execute method", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("Cannot execute method", e);
            }
        }
        return (getters != null && getters.size() > 0 && getters.size() == inactiveCount);
    }

    private Map<Method, Method> getGetterAndCorrespondingSetter(Method[] methods) {
        Map<Method, Method> methodMap = new HashMap<Method, Method>();
        for (Method m : methods) {
            if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
                if (isCandidate(m)) {
                    methodMap.put(m, getCorrespondingSetter(m.getName(), methods));
                }
            }
        }
        return methodMap;
    }

    private boolean isCandidate(Method method) {
        if (method != null && method.getName().startsWith("get")) {
            Class<?> returnType = method.getReturnType();
            if (CANDIDATE_CLASSES.contains(returnType)) {
                return true;
            }
            Package aPackage = returnType.getPackage();
            String packageName = aPackage != null ? aPackage.getName() : "";
            if (packageName.startsWith("org.orcid") || Collection.class.isAssignableFrom(returnType) || String.class.isAssignableFrom(returnType)
                    || "long".equals(returnType.getName())) {
                CANDIDATE_CLASSES.put(returnType, returnType);
                return true;
            }
        }
        return false;
    }

    private Method getCorrespondingSetter(String methodName, Method[] methods) {
        String setterName = methodName.replace("get", "set");
        for (Method m : methods) {
            if (setterName.equals(m.getName())) {
                return m;
            }
        }
        return null;
    }

}
