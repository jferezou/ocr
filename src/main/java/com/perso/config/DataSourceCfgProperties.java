package com.perso.config;
public class DataSourceCfgProperties {
    private String url;
    private String username;
    private String password;
    private int minPoolSize;
    private int maxPoolSize;
    private String testQuery;
    private String driverClassName;

    public DataSourceCfgProperties() {
    }

    public String getUrl() {
        return this.url;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public int getMinPoolSize() {
        return this.minPoolSize;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public String getTestQuery() {
        return this.testQuery;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setMinPoolSize(final int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public void setMaxPoolSize(final int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setTestQuery(final String testQuery) {
        this.testQuery = testQuery;
    }

    public void setDriverClassName(final String driverClassName) {
        this.driverClassName = driverClassName;
    }
}