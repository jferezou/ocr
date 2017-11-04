package com.perso.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.boot.system.EmbeddedServerPortFileWriter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static com.perso.config.EnvironmentInitializer.Props.SPRING_PROFILES_ACTIVE;

public class EnvironmentInitializer {
    public static final String APPLICATION_CONF_DIR = "{application}";

    interface Props {
        String SB_LOG4J_CONFIGURATION_FILE = "logging.config";
        String LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";
        String SPRING_CONFIG_NAME = "spring.config.name";
        String SPRING_CONFIG_LOCATION = "spring.config.location";
        String OCR_DEV_DIR = "config.dir";
        String SPRING_CLOUD_BOOTSTRAP_LOCATION = "spring.cloud.bootstrap.location";
        String SPRING_CLOUD_CONFIG_ENABLED = "spring.cloud.config.enabled";
        String SPRING_CLOUD_CONFIG_FAIL_FAST = "spring.cloud.config.failFast";
        String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
    }

    interface Resources {
        String OCR_CONFIG = "config/";
    }

    static enum Mode {
        STANDALONE;
    }

    private static class FolderConfig {
        public static final String FOLDER_STRUCT = "%s%s%s/";
        final String configurationLocation;
        final String mainManagerConfigLocation;
        final String mainBootConfigLocation;

        FolderConfig(final String rootDir) {
            configurationLocation = rootDir;
            mainManagerConfigLocation = String.format(FOLDER_STRUCT, rootDir, Resources.OCR_CONFIG, "configuration");
            mainBootConfigLocation = String.format(FOLDER_STRUCT, rootDir, Resources.OCR_CONFIG, "configuration");
        }
    }

    private SortedMap<String, String> updatedProperties = new TreeMap<>();
    private List<String> errors = new ArrayList<>();

    public void setUp(final String componentId, final String appId) {
        String ocrDevDir = System.getProperty(Props.OCR_DEV_DIR);
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        // On est en environnement de développement simplifié
        if (ocrDevDir != null) {
            System.out.println("### Starting " + componentId + ", files for execution are located at : " + System.getProperty("user.dir").replace('\\', '/'));
            Mode runningMode = getRunningMode();
            String runningDesc = "";
            ocrDevDir = fixPathAsDir(ocrDevDir);
            FolderConfig config = new FolderConfig(ocrDevDir);

            setupBootstrapConfigurationFileLocations(componentId, appId, config, runningMode);
            for (SortedMap.Entry<String, String> updatedProperty : updatedProperties.entrySet()) {
                System.out.println("# " + updatedProperty.getKey() + " = " + updatedProperty.getValue());
            }
            System.out.flush();
            for (String error : errors) {
                System.err.println("!! " + error);
            }
            System.out.println("###");
        }
    }

    private Mode getRunningMode() {
        return Mode.STANDALONE;
    }

    private static String fixPathAsDir(String ocrDevDir) {
        ocrDevDir = ocrDevDir.replace('\\', '/');
        if (!ocrDevDir.endsWith("/")) {
            ocrDevDir += '/';
        }
        return ocrDevDir;
    }


    private String getSearchLocations(final FolderConfig folderConfig, final String... appId) {
        String appHolder = APPLICATION_CONF_DIR;
        String appBase = "";
        if (appId.length == 1) {
            appHolder = appId[0] + "/";
            appBase = "application.yaml";
        }
        String[] locations = {
                folderConfig.mainManagerConfigLocation + appBase,
                folderConfig.mainManagerConfigLocation + appHolder,
        };
        return StringUtils.join(locations, ",");
    }

    private void setupBootstrapConfigurationFileLocations(final String componentId, final String appId, final FolderConfig folderConfig, final Mode mode) {

        if (Mode.STANDALONE.equals(mode)) {
            setPropertyAppendValues(SPRING_PROFILES_ACTIVE, "standalone");
            String configLocations = getSearchLocations(folderConfig, appId);
            setPropertyIfNotSet(Props.SPRING_CONFIG_NAME, appId);
            setPropertyIfNotSet(Props.SPRING_CONFIG_LOCATION, configLocations);
            setPropertyIfNotSet(Props.SPRING_CLOUD_CONFIG_ENABLED, "false");
            setPropertyIfNotSet(Props.SPRING_CLOUD_CONFIG_FAIL_FAST, "false");
        }
        String[] folders = {folderConfig.mainBootConfigLocation};
        String bootstrapFile = selectFile(appId + "/bootstrap.yaml", folders);
        if (bootstrapFile == null) {
            errors.add("Pas de fichier bootstrap.yaml trouvé");
        } else {
            setPropertyIfNotSet(Props.SPRING_CLOUD_BOOTSTRAP_LOCATION, bootstrapFile);
            checkBootstrapProfile(bootstrapFile);
        }

        String log4jFile = selectFile(appId + "/log4j2.yaml", folders);
        if (log4jFile == null) {
            log4jFile = selectFile(appId + "/log4j2.xml", folders);
        }
        if (log4jFile == null) {
            errors.add("Pas de fichier log4j2.yaml ou log4j2.xml trouvé");
        }
        String logFileConfiguration = "file:/" + log4jFile;
        setPropertyIfNotSet(Props.SB_LOG4J_CONFIGURATION_FILE, logFileConfiguration);
        setPropertyIfNotSet(Props.LOG4J_CONFIGURATION_FILE, logFileConfiguration);
    }

    private void checkBootstrapProfile(final String bootstrapFile) {
        Yaml yaml = new Yaml();
        try {
            Map<String, Object> bootstrapDoc = (Map<String, Object>) yaml.load(new FileReader(bootstrapFile));
            Map<String, Object> springDoc = (Map<String, Object>) bootstrapDoc.get("spring");
            if (springDoc == null)
                return;
            Map<String, Object> profileDoc = (Map<String, Object>) springDoc.get("profiles");
            if (profileDoc == null)
                return;
            String activeProfileString = (String) profileDoc.get("active");
            if (activeProfileString == null)
                return;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String selectFile(final String filename, final String... folders) {
        for (String folder : folders) {
            File file = new File(folder + filename);
            if (file.exists()) {
                return file.getAbsolutePath().replace("\\", "/");
            }
        }
        return null;
    }

    /**
     * Surcharge la propriété système que si elle n'a pas été définie explicitement avant.
     *
     * @param key   nom de la variable système.
     * @param value valeur à définir.
     */
    private void setPropertyIfNotSet(final String key, final String value) {
        String currentValue = System.getProperty(key);
        if (currentValue == null) {
            updatedProperties.put(key, value);
            if (value != null)
                System.setProperty(key, value);
        }
    }

    public void setUpProcessFiles(final SpringApplication application) {
        File logs = new File("logs");
        File applicationPid = new File(logs, "application.pid");
        File applicationPort = new File(logs, "application.port");
        application.addListeners(new ApplicationPidFileWriter(applicationPid));
        application.addListeners(new EmbeddedServerPortFileWriter(applicationPort));
    }
    private void setPropertyAppendValues(final String key, String values) {
        String currentValue = System.getProperty(key);
        if (currentValue != null) {
            Set<String> setOfValues = new LinkedHashSet<>();
            splitTrimAndAdd(currentValue, setOfValues);
            splitTrimAndAdd(values, setOfValues);
            values = StringUtils.join(setOfValues, ',');
        }
        updatedProperties.put(key, values);
        System.setProperty(key, values);
    }

    private void splitTrimAndAdd(final String currentValue, final Set<String> set) {
        String[] values = StringUtils.split(currentValue, ',');
        for (int i = 0; i < values.length; i++) {
            set.add(StringUtils.trim(values[i]));
        }
    }
}