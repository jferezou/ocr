package com.perso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "liste")
public class ListeFleursConfig {

    private List<String> fleurs;

    ListeFleursConfig() {
        this.fleurs = new ArrayList<>();
    }

    public List<String> getFleurs() {
        return this.fleurs;
    }

}