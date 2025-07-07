package com.salesanalytics.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.StandardBasicTypes; 

public class SQLiteDialect extends Dialect {

    public SQLiteDialect() {
        super(DatabaseVersion.make(3, 40));
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.NONE;
    }


    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public boolean supportsCascadeDelete() {
        return false;
    }

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();

        var typeConfig = functionContributions.getTypeConfiguration();

        functionRegistry.registerPattern(
                "lower",
                "lower(?1)",
                typeConfig.getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)
        );

        functionRegistry.registerPattern(
                "upper",
                "upper(?1)",
                typeConfig.getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)
        );

        functionRegistry.registerPattern(
                "substr",
                "substr(?1, ?2, ?3)",
                typeConfig.getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)
        );

        functionRegistry.registerPattern(
                "length",
                "length(?1)",
                typeConfig.getBasicTypeRegistry().resolve(StandardBasicTypes.INTEGER)
        );
    }
}