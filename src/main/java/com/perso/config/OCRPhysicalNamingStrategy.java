package com.perso.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

public class OCRPhysicalNamingStrategy implements PhysicalNamingStrategy, Serializable {
    private Map<Identifier, Identifier> schemaMapping;

    public OCRPhysicalNamingStrategy(Map<String, String> logSchema2phySchemas) {
        this.schemaMapping = (Map)logSchema2phySchemas.entrySet().stream().collect(Collectors.toMap((e) -> {
            return this.toId((String)e.getKey());
        }, (e) -> {
            return this.toId((String)e.getValue());
        }));
    }

    private Identifier toId(String key) {
        return Identifier.toIdentifier(key);
    }

    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment context) {
        Identifier mappedSchemaIdentifier = (Identifier)this.schemaMapping.get(identifier);
        if (mappedSchemaIdentifier != null) {
            identifier = mappedSchemaIdentifier;
        }

        return identifier;
    }

    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment context) {
        Identifier mappedSchemaIdentifier = (Identifier)this.schemaMapping.get(identifier);
        if (mappedSchemaIdentifier != null) {
            identifier = mappedSchemaIdentifier;
        }

        return identifier;
    }

    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        return name;
    }

    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return name;
    }
}
