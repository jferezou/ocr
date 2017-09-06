package com.perso.config;

import java.lang.reflect.Constructor;

import org.apache.cxf.bus.spring.Jsr250BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
public class CXFConfiguration {

    @Bean
    public org.apache.cxf.bus.spring.BusWiringBeanFactoryPostProcessor getBusWiringBeanFactoryPostProcessor() {
        return new org.apache.cxf.bus.spring.BusWiringBeanFactoryPostProcessor();
    }

    @Bean
    public org.apache.cxf.bus.spring.Jsr250BeanPostProcessor getJsr250BeanPostProcessor() throws Exception {
        Constructor<Jsr250BeanPostProcessor> constructor = Jsr250BeanPostProcessor.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        return constructor.newInstance(new Object[0]);
    }

    @Bean
    public org.apache.cxf.bus.spring.BusExtensionPostProcessor getBusExtensionPostProcessor() {
        return new org.apache.cxf.bus.spring.BusExtensionPostProcessor();
    }
}
