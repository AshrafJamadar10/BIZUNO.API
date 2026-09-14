package com.bizuno.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class PackageSchemaInitializer implements ApplicationRunner {

    private final DataSource mainDataSource;

    public PackageSchemaInitializer(@Qualifier("mainDataSource") DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection connection = mainDataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                CREATE TABLE IF NOT EXISTS packages (
                    package_id UUID PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    description VARCHAR(500) NOT NULL,
                    base_price NUMERIC(10,2) NOT NULL DEFAULT 0,
                    billing_period VARCHAR(30) NOT NULL,
                    package_days INTEGER NOT NULL DEFAULT 0,
                    trial_days INTEGER NOT NULL DEFAULT 0,
                    setup_fee NUMERIC(10,2) DEFAULT 0,
                    display_order INTEGER NOT NULL DEFAULT 0,
                    recommended BOOLEAN NOT NULL DEFAULT FALSE,
                    created_by VARCHAR(255),
                    created_date TIMESTAMP,
                    modified_by VARCHAR(255),
                    modified_date TIMESTAMP,
                    is_active BOOLEAN DEFAULT TRUE
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS package_features (
                    package_feature_id BIGSERIAL PRIMARY KEY,
                    package_feature_code VARCHAR(255) NOT NULL,
                    feature_name VARCHAR(255) NOT NULL,
                    description TEXT,
                    scope VARCHAR(255) NOT NULL,
                    limit_type VARCHAR(255),
                    limit_value INTEGER DEFAULT 0,
                    unit VARCHAR(255),
                    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                    display_order INTEGER,
                    package_id UUID,
                    CONSTRAINT fk_package_feature_package
                        FOREIGN KEY (package_id) REFERENCES packages(package_id)
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS package_feature_operations (
                    package_feature_id BIGINT NOT NULL,
                    operation VARCHAR(255) NOT NULL,
                    CONSTRAINT fk_package_feature_operations
                        FOREIGN KEY (package_feature_id) REFERENCES package_features(package_feature_id)
                )
                """);
        }
    }
}
