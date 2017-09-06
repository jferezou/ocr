package com.perso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "segment")
public class ListeFleursConfig {

    private List<String> list;

    ListeFleursConfig() {
        this.list = new ArrayList<>();
    }

    public List<String> getList() {
        return this.list;
    }

}