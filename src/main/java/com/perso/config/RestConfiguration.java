package com.perso.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.session.web.http.SessionRepositoryFilter;

import javax.annotation.Resource;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * Config Spring Security pour les services REST.
 *
 * @author mdenoual
 *
 */
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER - 2)
public class RestConfiguration extends WebSecurityConfigurerAdapter {

    @Resource
    private WebCfgProperties webCfgProperties;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String restPrefix = webCfgProperties.getSecurity().getRestPrefix();

        // @formatter:off
        http
                .antMatcher(restPrefix + "/**")
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(restPrefix + "/traitement*").permitAll();
        // @formatter:on
    }
}
