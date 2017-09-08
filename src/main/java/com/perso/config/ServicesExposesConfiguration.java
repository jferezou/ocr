package com.perso.config;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({
        CXFConfiguration.class,
        RESTPublisherConfiguration.class
})
@ComponentScan(basePackages = {
        "com.perso.config"
})
public class ServicesExposesConfiguration {
    /**
     * Déploiement des services REST
     *
     * @param publisher
     *            un service permettant de publier des services rest
     * @return bean Serveur REST listant les services déployés
     */
    @Bean(name = "jaxrs")
    public JAXRSServerFactoryBean getRESTPublishedServices(final RESTPublisher publisher) {
        return publisher.publish(RESTPublisher.DEFAULT_NS,RestOcr.class);
    }
}