package com.perso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import javax.annotation.Resource;

/**
 * Classe de configuration de Spring Security, en remplacement du security-context.xml
 *
 * @author mdenoual
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Import({
		RestConfiguration.class
})
public class SpringSecurityConfiguration extends GlobalMethodSecurityConfiguration {

	@Resource
	private WebCfgProperties webCfgProperties;


	@Resource
	private ApplicationContext context;


	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		// TODO voir si on peut surcharger autrement le rolePrefix
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setDefaultRolePrefix("");
		expressionHandler.setApplicationContext(context);
		return expressionHandler;
	}


}
