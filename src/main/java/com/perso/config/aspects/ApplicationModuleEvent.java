package com.perso.config.aspects;

import org.apache.commons.collections4.Closure;


/**
 * Un évènement susceptible d'être levé par un {@link ApplicationModule#fire(ApplicationModuleEvent)}.
 */
public abstract class ApplicationModuleEvent implements Closure<ApplicationModuleEventListener> {
    public static final ApplicationModuleEvent BEFORE_SERVICE_METHOD_EXECUTION = new BeforeServiceMethodExecution();
    public static final ApplicationModuleEvent AFTER_SERVICE_METHOD_EXECUTION = new AfterServiceMethodExecution();

    @Override
    public abstract void execute(ApplicationModuleEventListener listener);

    public static class AfterServiceMethodExecution extends ApplicationModuleEvent {
        @Override
        public void execute(ApplicationModuleEventListener input) {
            input.afterServiceMethodExecution(ApplicationModule.currentContext());
        }
    }

    public static class BeforeServiceMethodExecution extends ApplicationModuleEvent {
        @Override
        public void execute(ApplicationModuleEventListener input) {
            input.beforeServiceMethodExecution(ApplicationModule.currentContext());
        }
    }
}
