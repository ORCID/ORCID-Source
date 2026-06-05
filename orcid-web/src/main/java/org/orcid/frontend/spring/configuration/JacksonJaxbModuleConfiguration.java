package org.orcid.frontend.spring.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class JacksonJaxbModuleConfiguration implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MappingJackson2HttpMessageConverter) {
            MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) bean;
            ObjectMapper mapper = converter.getObjectMapper();
            mapper.registerModule(new JaxbAnnotationModule());
        }
        return bean;
    }
}
