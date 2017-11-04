package com.perso;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringRunner {
	private final Class<?> mainClass;
	private final AnnotationConfigApplicationContext ctx;

	public SpringRunner(final Class<?> mainClass, final Class<?> config) {
		this.mainClass = mainClass;
		ctx = new AnnotationConfigApplicationContext(config);
	}

	public void run(final String[] args) {
		Object bean = ctx.getBeanFactory().autowire(mainClass, AutowireCapableBeanFactory.AUTOWIRE_NO, true);
		try {
			Method method = mainClass.getMethod("run", String[].class,AnnotationConfigApplicationContext.class);
			method.invoke(bean, new Object[] {args,ctx });
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}