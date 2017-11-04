package com.perso.config;

import java.util.HashMap;
import java.util.Map;

public class HibernateCfgProperties {
    private String jdbcDialect;
    private String defaultSchema;
    private Map<String, String> schemas = new HashMap();
    private String[] packagesToScan;
    private String DDLMode;

    public HibernateCfgProperties() {
    }

    public String getJdbcDialect() {
        return this.jdbcDialect;
    }

    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    public Map<String, String> getSchemas() {
        return this.schemas;
    }

    public String[] getPackagesToScan() {
        return this.packagesToScan;
    }

    public String getDDLMode() {
        return this.DDLMode;
    }

    public void setJdbcDialect(final String jdbcDialect) {
        this.jdbcDialect = jdbcDialect;
    }

    public void setDefaultSchema(final String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public void setSchemas(final Map<String, String> schemas) {
        this.schemas = schemas;
    }

    public void setPackagesToScan(final String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setDDLMode(final String DDLMode) {
        this.DDLMode = DDLMode;
    }
}