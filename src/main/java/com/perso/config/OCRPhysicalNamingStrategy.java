package com.perso.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

public class OCRPhysicalNamingStrategy implements PhysicalNamingStrategy, Serializable {
    private Map<Identifier, Identifier> schemaMapping;

    public OCRPhysicalNamingStrategy(final Map<String, String> logSchema2phySchemas) {
        this.schemaMapping = (Map)logSchema2phySchemas.entrySet().stream().collect(Collectors.toMap((e) -> {
            return this.toId((String)e.getKey());
        }, (e) -> {
            return this.toId((String)e.getValue());
        }));
    }

    private Identifier toId(final String key) {
        return Identifier.toIdentifier(key);
    }

    public Identifier toPhysicalCatalogName(Identifier identifier, final JdbcEnvironment context) {
        Identifier mappedSchemaIdentifier = (Identifier)this.schemaMapping.get(identifier);
        if (mappedSchemaIdentifier != null) {
            identifier = mappedSchemaIdentifier;
        }

        return identifier;
    }

    public Identifier toPhysicalSchemaName(Identifier identifier, final JdbcEnvironment context) {
        Identifier mappedSchemaIdentifier = (Identifier)this.schemaMapping.get(identifier);
        if (mappedSchemaIdentifier != null) {
            identifier = mappedSchemaIdentifier;
        }

        return identifier;
    }

    public Identifier toPhysicalTableName(final Identifier name, final JdbcEnvironment context) {
        return name;
    }

    public Identifier toPhysicalSequenceName(final Identifier name, final JdbcEnvironment context) {
        return name;
    }

    public Identifier toPhysicalColumnName(final Identifier name, final JdbcEnvironment context) {
        return name;
    }
}
