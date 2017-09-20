package com.perso.config.aspects;

import com.perso.annotation.ServiceMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

/**
 * Un aspect appliqué à toutes les méthodes de bean spring annotées par @ServiceMethod. Encapsule l'appel de la méthode dans le contexte du module
 * applicatif.
 *
 * @see ApplicationModule
 */
@Aspect
public class ServiceMethodAspect {
    @Resource
    private ApplicationModule app;

    @Pointcut(value = "execution(public javax.ws.rs.core.Response *(..))")
    public void anyPublicMethodThatReturnResponse() {
    }

    @Around("anyPublicMethodThatReturnResponse() && @annotation(serviceMethod)")
    public Response executeInApplicationContext(final ProceedingJoinPoint pjp, ServiceMethod serviceMethod) throws Throwable {
        Logger log = LogManager.getLogger(pjp.getTarget().getClass());
        return app.inContext(log, pjp.getSignature().getName(), serviceMethod, pjp.getArgs()).execute(pjp);
    }
}

