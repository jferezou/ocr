package com.perso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({
        ServicesExposesConfiguration.class,
        WebMainConfiguration.class,
        SpringSecurityConfiguration.class})
public class AppConfig {


    @Bean(name = "webProperties")
    @ConfigurationProperties(prefix = "webapp")
    public WebCfgProperties webProperties() {
        return new WebCfgProperties();
    }
}
