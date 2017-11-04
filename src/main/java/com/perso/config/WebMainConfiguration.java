package com.perso.config;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.annotation.Resource;
import javax.servlet.Filter;

/**
 * Configuration type pour une application web
 *
 * @author mdenoual
 *
 */
public class WebMainConfiguration {

	@Resource
	protected WebCfgProperties webCfgProperties;


	// CXF
	@Bean
	public ServletRegistrationBean cxfServletRegistration() {
		ServletRegistrationBean cxfServletRegistration = new ServletRegistrationBean(new CXFServlet(),
				getServicesUrlPattern());
		cxfServletRegistration.setLoadOnStartup(1);
		cxfServletRegistration.setName("cxfServlet");
		return cxfServletRegistration;
	}


	// Filtres
	@Bean
	public FilterRegistrationBean springSecurityFilterRegistration() {
		return filterBean(1, new DelegatingFilterProxy(), "springSecurityFilterChain");
	}


	private FilterRegistrationBean filterBean(final int order, final Filter filter, final String name) {
		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
		filterRegistration.setFilter(filter);
		filterRegistration.setName(name);
		filterRegistration.addUrlPatterns(getServicesUrlPattern());
		filterRegistration.setOrder(order);
		return filterRegistration;
	}

	private String getServicesUrlPattern() {
		return webCfgProperties.getUrl().getServices() + "/*";
	}


}
