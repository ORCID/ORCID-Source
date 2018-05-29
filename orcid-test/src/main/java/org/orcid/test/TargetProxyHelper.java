package org.orcid.test;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.test.util.ReflectionTestUtils;

/** Utilities for working with spring bean proxies and mockito
 * 
 */
public class TargetProxyHelper {

    @SuppressWarnings( { "unchecked" })
    public static <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        while ((AopUtils.isJdkDynamicProxy(proxy))) {
            return (T) getTargetObject(((Advised) proxy).getTargetSource().getTarget(), targetClass);
        }
        return (T) proxy; // expected to be cglib proxy then, which is simply a
        // specialized class
    }
    
    /** Inject a bean into a Spring proxy - for when we don't have a method to do so.
     *  We can't inject our mock using @InjectMock and MockitoAnnotations.initMocks(this) due to spring proxies
     *  So we'll do it manually
     *  
     * @param target
     * @param fieldname
     * @param thingToInject
     */
    public static void injectIntoProxy(Object target,String fieldname, Object thingToInject){
        ReflectionTestUtils.setField(unwrapProxy(target), fieldname, thingToInject);
    }
    
    /** Extract the bean from a Spring proxy
     * Similar to getTargetObject but you don't need to know the implementing class 
     *  see https://github.com/mockito/mockito/issues/209
     *  also see https://groups.google.com/forum/#!topic/mockito/DmkyGhmCDtY
     * @param bean
     * @return the unwrapped bean
     */
    public static Object unwrapProxy(Object bean) {
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            Advised advised = (Advised) bean;
            try {
                bean = advised.getTargetSource().getTarget();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }
}
