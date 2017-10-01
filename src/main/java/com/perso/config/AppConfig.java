package com.perso.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.hibernate.SessionFactory;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@Import({
        OcrConfiguration.class,
        ServicesExposesConfiguration.class,
        WebMainConfiguration.class,
        SpringSecurityConfiguration.class})
public class AppConfig {
    /**
     * Hibernate valide le mapping Classe - Tables
     */
    public static final String HIBERNATE_HBM2DDL_VALUE = "validate";

    /**
     * nom JNDI de la source de donnees
     */
    public static final String DATASOURCE_OCR = "OCRDS";

    /**
     * packages contenant les entity hibernate
     */
    public static final String[] BASE_DTO_PACKAGES = { "com.perso.bdd.entity" };

    /**
     * Dialecte hibernate utilisé
     */
    public static final String JDBC_DIALECT = "org.hibernate.dialect.PostgreSQLDialect";

    static {
        // Permet de rediriger les logs hibernate vers slf4j
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }



    @Bean(name = "webProperties")
    @ConfigurationProperties(prefix = "webapp")
    public WebCfgProperties webProperties() {
        return new WebCfgProperties();
    }


    @Bean(name = "hibernateProperties")
    @ConfigurationProperties(prefix = "hibernate.configuration")
    public HibernateCfgProperties hibernateProperties() {
        HibernateCfgProperties hibernateCfgProperties = new HibernateCfgProperties();
        hibernateCfgProperties.setPackagesToScan(BASE_DTO_PACKAGES);
        hibernateCfgProperties.setDDLMode(HIBERNATE_HBM2DDL_VALUE);
        return hibernateCfgProperties;
    }

    @Bean(name = "ocrProperties")
    @ConfigurationProperties(prefix = "datasource.ocr")
    public DataSourceCfgProperties ssiProperties() {
        return new DataSourceCfgProperties();
    }

    @Bean(name = "OCRDS", initMethod = "init", destroyMethod = "close")
    @Primary
    public DataSource ocrDataSource(@Qualifier("ocrProperties") DataSourceCfgProperties ocrDsConf) {
        PGXADataSource pgDataSource = new PGXADataSource();
        pgDataSource.setUrl(ocrDsConf.getUrl());
        pgDataSource.setUser(ocrDsConf.getUsername());
        pgDataSource.setPassword(ocrDsConf.getPassword());

        return createAtomikosDS("ocrDatasource", pgDataSource, ocrDsConf);
    }


    @Bean
    @Primary
    public LocalSessionFactoryBean sessionFactoryBean(
            @Qualifier("OCRDS") final DataSource dataSource,
            @Qualifier("hibernateProperties") final HibernateCfgProperties hibernateProperty,
            UserTransaction userTransaction,
            TransactionManager atomikosTransactionManager) {
        return createLocalSessionFactoryBean(dataSource, hibernateProperty, userTransaction, atomikosTransactionManager);
    }

    @Bean(
            name = {"userTransaction"}
    )
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }

    @Bean(
            name = {"atomikosTransactionManager"},
            initMethod = "init",
            destroyMethod = "close"
    )
    public TransactionManager atomikosTransactionManager() throws Throwable {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean(
            name = {"transactionManager"}
    )
    public PlatformTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager atomikosTransactionManager) throws Throwable {
        return new JtaTransactionManager(userTransaction, atomikosTransactionManager);
    }

    @Bean
    public PersistenceExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public SessionFactory sessionFactory(LocalSessionFactoryBean sessionFactoryBean) {
        return sessionFactoryBean.getObject();
    }

    protected AtomikosDataSourceBean createAtomikosDS(String dsId, XADataSource ds, DataSourceCfgProperties config) {
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName(dsId);
        xaDataSource.setMinPoolSize(config.getMinPoolSize());
        xaDataSource.setMaxPoolSize(config.getMaxPoolSize());
        xaDataSource.setTestQuery(config.getTestQuery());
        return xaDataSource;
    }

    protected LocalSessionFactoryBean createLocalSessionFactoryBean(DataSource dataSource, HibernateCfgProperties hibernateCfgProperties, UserTransaction userTransaction, TransactionManager atomikosTransactionManager) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        AtomikosJtaPlatform.txMgr = atomikosTransactionManager;
        AtomikosJtaPlatform.userTx = userTransaction;
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan(hibernateCfgProperties.getPackagesToScan());
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", hibernateCfgProperties.getJdbcDialect());
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", hibernateCfgProperties.getDDLMode());
        hibernateProperties.setProperty("hibernate.show_sql", "false");
        hibernateProperties.setProperty("hibernate.default_schema", hibernateCfgProperties.getDefaultSchema());
        hibernateProperties.setProperty("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        hibernateProperties.setProperty("hibernate.transaction.coordinator_class", "jta");
        sessionFactoryBean.setPhysicalNamingStrategy(new OCRPhysicalNamingStrategy(hibernateCfgProperties.getSchemas()));
        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        return sessionFactoryBean;
    }
}
