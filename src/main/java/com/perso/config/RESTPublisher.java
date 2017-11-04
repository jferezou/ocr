package com.perso.config;


import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * Conteneur permettant d'enregistrer l'ensemble des services REST à publier.
 *
 * @see RESTPublisherConfiguration
 */
public class RESTPublisher {
    /**
     * Racine de publication par défaut.
     */
    public static final String DEFAULT_NS = "/rest";

    /**
     * Registre des services à publier.
     */
    private Map<String, List<Class<?>>> nsToServices = new HashMap<String, List<Class<?>>>();

    /**
     * Contexte Spring
     */
    private final ApplicationContext context;

    /**
     * Bus CXF
     */
    private final SpringBus bus;

    RESTPublisher(final ApplicationContext context, final SpringBus bus) {
        this.context = context;
        this.bus = bus;
    }

    /**
     * Associe les services passés en paramètre en temps que services associés à la racine par défaut.
     *
     * @param services  services à publier.
     */
    public JAXRSServerFactoryBean publish(final Class<?>... services) {
        return publish(DEFAULT_NS, services);
    }

    /**
     * Associe les services passés en paramètre en temps que services associés à la racine donnée en paramètre.
     *
     * @param nameSpace nom de la racine à ajouter.
     * @param services  services à publier.
     */
    public JAXRSServerFactoryBean publish(final String nameSpace, final Class<?>... services) {
        return publish(nameSpace, (factoryBean -> {
        }), services);
    }

    public JAXRSServerFactoryBean publish(final String nameSpace, final FactoryInitializer initializer, final Class<?>... services) {
        JAXRSServerFactoryBean serverFactory = new JAXRSServerFactoryBean();
        serverFactory.setAddress(nameSpace);

        List<Object> beans = getServices(context, services);
        if (!beans.isEmpty()) {
            serverFactory.setBus(bus);
            serverFactory.setServiceBeans(beans);
            serverFactory.setStart(true);
            serverFactory.getInInterceptors().add(new LoggingInInterceptor());
            serverFactory.getOutInterceptors().add(new LoggingOutInterceptor(2 * 1024));
            // configuration du provider Json
            JacksonJsonProvider provider = new JacksonJsonProvider();
            serverFactory.setProvider(provider);
            initializer.initialize(serverFactory);
            serverFactory.create();
        }
        return serverFactory;

    }

    protected List<Object> getServices(final ApplicationContext context, final Class<?>... services) {
        List<Object> beanList = new ArrayList<Object>();
        List<Class<?>> publishedServiceClass = Arrays.asList(services);
        for (Class<?> serviceClass : publishedServiceClass) {
            Object bean = null;
            try {
                bean = context.getBean(serviceClass.getName());
            } catch (BeansException e) {
                bean = context.getBean(serviceClass);
            }
            beanList.add(bean);
        }
        return beanList;
    }


    public interface FactoryInitializer {
        void initialize(JAXRSServerFactoryBean serverFactory);
    }
}
