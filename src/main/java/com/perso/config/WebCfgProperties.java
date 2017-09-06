package com.perso.config;


import lombok.Getter;
import lombok.Setter;

/**
 * Classe encapsulant les propriétés requises pour la configuration Web des modules SIS.
 *
 * @author mdenoual
 *
 */
@Getter
@Setter
public class WebCfgProperties {

    private UrlConfig url;
    private SecurityConfig security;

    @Getter
    @Setter
    public static class UrlConfig {
        private String contextRoot;
        private String services;
    }


    @Getter
    @Setter
    public static class SecurityConfig {
        private String restPrefix;
    }
}

