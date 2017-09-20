package com.perso.config.aspects;

import com.perso.annotation.ServiceMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Définit le contexte applicatif et les services associés.
 */
public class ApplicationModule {

    public interface LogContext {
        String NAME = "service_name";
        String ARGS = "service_args";
        String RUNTIME_MS = "service_runtime_ms";
    }
    /**
     * Clé de contexte pour accéder au logger
     **/
    public static final Key<Logger> LOGGER = new Key<>();
    /**
     * Clé de contexte pour accéder au module applicatif
     **/
    public static final Key<ApplicationModule> MODULE = new Key<>();

    /**
     * L'identifiant de l'application
     */
    private final String applicationId;

    /**
     * eventLogger
     */
    private Logger eventLogger;

    /**
     * Ensemble des objets écoutant le contexte applicatif.
     */
    private List<ApplicationModuleEventListener> eventListeners = new ArrayList<>();

    /**
     * Table des contextes associés aux threads.
     */
    private static ThreadLocal<Context> thread2Context = new ThreadLocal<>();


    /**
     * Constructeur, créé aussi un logger applicatif nommé EVENT.&lt;identifiant&gt;.
     *
     * @param applicationId identifiant de l'application.
     */
    public ApplicationModule(final String applicationId) {
        this.applicationId = applicationId;
        this.eventLogger = LogManager.getLogger("EVENT." + applicationId);
    }

    /**
     * Publie un évènement métier sur le logger applicatif.
     *
     * @param marker un libellé représentant le type d'évènement.
     * @param action le message.
     * @param args   les arguments du message.
     */
    public void notify(final Marker marker, final String action, final Object... args) {
        eventLogger.info(marker, action, args);
    }

    /**
     * Prépare un contexte pour exécuter un service.
     *
     * @param logger    le logger technique associé.
     * @param idService l'identifiant du services.
     * @param params    les paramètres d'invocation du service.
     * @return un contexte associé au thread courant.
     */
    public Context inContext(final Logger logger, final String idService, ServiceMethod annotation, final Object... params) {
        return new Context(logger, idService, params, annotation);
    }

    /**
     * L'identifiant de l'application.
     *
     * @return idem.
     */
    public String getName() {
        return applicationId;
    }

    /**
     * Notifie les listeners d'un évèvenement.
     *
     * @param event l'évènement à propager.
     */
    public void fire(ApplicationModuleEvent event) {
        CollectionUtils.forAllDo(eventListeners, event);
    }

    public void addEventListener(ApplicationModuleEventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Récupère le contexte d'éxécution courant
     *
     * @return contexte d'exécution courant
     */
    public static Context currentContext() {
        return thread2Context.get();
    }

    public static <T> T get(Key<T> key) {
        return currentContext().get(key);
    }

    public void prepareExecute(Logger logger, String executionName) {
        thread2Context.set(new Context(logger, executionName, new Object[]{}, null));
        fire(ApplicationModuleEvent.BEFORE_SERVICE_METHOD_EXECUTION);
    }

    private void prepareExecute(Context context) {
        thread2Context.set(context);
        fire(ApplicationModuleEvent.BEFORE_SERVICE_METHOD_EXECUTION);
    }

    public void completeExecute() {
        fire(ApplicationModuleEvent.AFTER_SERVICE_METHOD_EXECUTION);
        thread2Context.remove();
    }

    /**
     * Encapsule l'execution du call dans un call qui fait une prise de temps.
     *
     * @param context  le context d'exécution.
     * @param callable le call à encadrer.
     * @return le call encadré.
     */
    public <V> Call<V> timedCall(final Context context, final Call<V> callable) {
        return new Call<V>() {
            @Override
            public V call() throws Throwable {
                Logger log = context.techLogger;
                long depart = System.currentTimeMillis();
                ThreadContext.put(LogContext.NAME, context.id);
                ThreadContext.put(LogContext.ARGS, Arrays.toString(context.params));
                log.info("DEBUT {} {}", context.id, context.params);
                ThreadContext.remove(LogContext.ARGS);
                try {
                    return callable.call();
                } finally {
                    long runtime = System.currentTimeMillis() - depart;
                    ThreadContext.put(LogContext.RUNTIME_MS, String.valueOf(runtime));
                    log.info("FIN {} [{}ms]", context.id, runtime);
                    ThreadContext.remove(LogContext.RUNTIME_MS);
                }
            }
        };
    }

    @FunctionalInterface
    public interface Call<R> {
        R call() throws Throwable;
    }

    /**
     * Définit une clé permettant d'accèder à des données spécifiques dans le contexte.
     *
     * @param <T> type de la donnée associée à cette clé.
     */
    public static class Key<T> {
    }

    /**
     * Définit les fonctions relatives à une invocation de service dans une application.
     */
    public class Context {
        /**
         * Le nom de la méthode exécutée
         */
        private final String id;
        /**
         * Le nom fonctionnel de la méthode exécutée
         */
        private final String idFonctionnel;

        /**
         * Les paramètres de la méthode exécutée
         */
        private final Object[] params;
        /**
         * Le logger utilisé pour les entrées/sorties des méthodes
         */
        private Logger techLogger;
        /**
         * Différentes informations liées au contexte d'exécution en cours.
         */
        private Map<Key<?>, Object> elements = new HashMap<>();
        /**
         * Active le traçage des temps dans le logger.
         */
        private boolean isTraced;

        /**
         * Constructeur AppContext
         *
         * @param logger     un logger pour tracer l'entrée/sortie des méthodes
         * @param id         le nom de la méthode éxécutée
         * @param params
         * @param annotation L'annotation utilisée sur la méthode. Peut-être null dans le cas où la méthode n'est pas annotée par ServiceMethod
         */
        public Context(final Logger logger, final String id, final Object[] params,
                       final ServiceMethod annotation) {
            this.techLogger = logger;
            this.id = id;
            this.idFonctionnel = annotation != null ? annotation.value() : ServiceMethod.DEFAULT_METHOD_NAME;
            this.params = params;
            this.isTraced = annotation != null ? annotation.traceTime() : true;
            put(LOGGER, techLogger);
            put(MODULE, ApplicationModule.this);
        }

        public boolean isTraced() {
            return isTraced;
        }

        /**
         * Appel de la méthode passé en paramètre + ajout des log de monitoring
         *
         * @param callable La méthode à exécuter, la méthode appelé peut avoir des données en retour
         * @return L'objet retour de la méthode exécuté
         */
        public <V> V execute(final Call<V> callable) throws Throwable {
            Call<V> serviceCall = isTraced() && techLogger.isInfoEnabled() ? timedCall(this, callable) : callable;
            try {
                prepareExecute(this);
                    return serviceCall.call();
            } finally {
                completeExecute();
            }
        }

        public Response execute(final ProceedingJoinPoint pjp)  throws Throwable {
            Response response;
                response = execute(() -> {
                    return (Response) pjp.proceed();
                });
            return response;
        }

        @Override
        public String toString() {
            return new StringBuilder(getName()).append('.').append(id).append(Arrays.toString(params)).toString();
        }

        /**
         * Récupère un éléments du contexte
         *
         * @param key la clé avec laquelle l'élément à été enregistré dans le contexte
         * @return l'élément correspondant à la clé. Null si aucun objet ne correspond à la clé.
         */
        @SuppressWarnings("unchecked")
        public <T> T get(Key<T> key) {
            return (T) elements.get(key);
        }

        /**
         * Ajoute un élément dans le contexte
         *
         * @param key   la clé à associé à l'élément à ajouter
         * @param value L'élément à ajouter au contexte
         */
        public <T> void put(Key<?> key, T value) {
            elements.put(key, value);
        }

        /**
         * Supprime un éléméent du contexte en fonction de la clé passée en paramètre.
         *
         * @param key
         */
        public <T> void remove(Key<?> key) {
            elements.remove(key);
        }

        /**
         * Le nom du service.
         *
         * @return Le nom fonctionnel si définit, le nom technique sinon.
         */
        public String getServiceName() {
            return !StringUtils.equals(idFonctionnel, ServiceMethod.DEFAULT_METHOD_NAME) ? idFonctionnel : id;
        }
    }
}
