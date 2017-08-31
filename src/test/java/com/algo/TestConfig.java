package com.algo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan(basePackages = { "com.perso.*" })
@PropertySources({
	@PropertySource("classpath:config-test.properties"),
	@PropertySource("classpath:dictionary-test.properties")
})
public class TestConfig {

}
