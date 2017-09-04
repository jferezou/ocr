package com.perso;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {"com.perso.*"})
@PropertySource("classpath:com/perso/config.properties")
@EnableTransactionManagement
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        registerEndpoints();
    }
    private void registerEndpoints() {
        register(RestOcr.class);
    }
}
