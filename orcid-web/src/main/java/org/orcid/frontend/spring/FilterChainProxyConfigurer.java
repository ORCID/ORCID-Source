package org.orcid.frontend.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.stereotype.Component;

/**
 * Configures the Spring Security {@link FilterChainProxy} to use the
 * {@link NotFoundRequestRejectedHandler}, so that rejected requests receive
 * a 404 response instead of propagating as a 500 error.
 */
@Component
public class FilterChainProxyConfigurer implements BeanPostProcessor {

    private final RequestRejectedHandler requestRejectedHandler;

    @Autowired
    public FilterChainProxyConfigurer(RequestRejectedHandler requestRejectedHandler) {
        this.requestRejectedHandler = requestRejectedHandler;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof FilterChainProxy) {
            ((FilterChainProxy) bean).setRequestRejectedHandler(requestRejectedHandler);
        }
        return bean;
    }
}



