package com.perso.config;

import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publication automatique des services REST
 */
@Configuration
public class RESTPublisherConfiguration {
    /**
     * Préfixe des services REST (exemple d'url d'un service REST :
     * http://localhost/ContextApp/services/rest/monServiceRest)
     */
    @Bean
    public RESTPublisher getRESTPublisher(ApplicationContext context, SpringBus bus) {
        return new RESTPublisher(context, bus);
    }
}
