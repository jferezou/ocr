package com.perso.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe de base pour tous les services SpringBoot SIS. Elle s'assure de démmarrer l'application en activant l'écriture des fichiers application.pid
 * et application.port. Elle ajoute une comportement spécifique à l'environnement de développement pour s'assurer d'une initialisation facile. (cf.
 * {@link EnvironmentInitializer})
 */
@SpringBootApplication
public abstract class ServerComponent {
    private static String STOPPED = "### STOPPED ###";
    public static void run(String componentId, String appId, Class<?> appClass, String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { System.out.println(STOPPED); }));
        EnvironmentInitializer environmentInitializer = new EnvironmentInitializer();
        environmentInitializer.setUp(componentId, appId);
        SpringApplication application = new SpringApplication(appClass);
        environmentInitializer.setUpProcessFiles(application);
        application.run(args);
    }

    public static void run(String componentId, Class<?> appClass, String[] args) {
        run(componentId, componentId, appClass, args);
    }
}