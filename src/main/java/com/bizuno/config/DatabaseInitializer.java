package com.bizuno.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessResourceFailureException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.regex.Pattern;

@Slf4j
@Configuration
public class DatabaseInitializer {

    private static final Pattern SAFE_IDENTIFIER =  Pattern.compile("^\\w+$");

    private static final String PROP_DB_NAME = "DB_NAME";
    private static final String PROP_DB_USERNAME = "DB_USERNAME";

    @Value("${database.host}")
    private String host;

    @Value("${database.port}")
    private String port;

    @Value("${db.name}")
    private String db;

    @Value("${db.username}")
    private String user;

    @Value("${db.password}")
    private String pass;

    @Value("${admin.datasource.db}")
    private String adminDb;

    @Value("${admin.datasource.username}")
    private String adminUser;

    @Value("${admin.datasource.password}")
    private String adminPass;

    @Bean(name = "mainDataSource")
    @Primary
    public DataSource dataSource() {
        validateIdentifier(db, "DB_NAME");
        validateIdentifier(user, "DB_USERNAME");

        String adminUrl = "jdbc:postgresql://" + host + ":" + port + "/" + adminDb;

        try (Connection conn = DriverManager.getConnection(adminUrl, adminUser, adminPass)) {
            conn.setAutoCommit(true);

            log.info("Connected to PostgreSQL as admin");

            createOrUpdateRole(conn, user, pass);
            createDatabaseIfNotExists(conn, db, user);
            grantPrivileges(conn, db, user);

            log.info("PostgreSQL bootstrap completed successfully");

        } catch (SQLException ex) {
            throw new DataAccessResourceFailureException(
                    "PostgreSQL bootstrap failed: unable to initialize role/database",
                    ex
            );
        }

        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://" + host + ":" + port + "/" + db)
                .username(user)
                .password(pass)
                .build();
    }

    // ---------------------------------------------------------
    // Role Creation / Update
    // ---------------------------------------------------------
    private void createOrUpdateRole(Connection conn, String user, String pass)
            throws SQLException {

        validateIdentifier(user, PROP_DB_USERNAME);
        String safePassword = escapeLiteral(pass);

        try (PreparedStatement ps =
                     conn.prepareStatement("SELECT 1 FROM pg_roles WHERE rolname = ?")) {

            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {

                try (Statement st = conn.createStatement()) {

                    if (!rs.next()) {
                        st.execute(buildCreateRoleSql(user, safePassword));
                        log.info("Role created");
                    } else {
                        st.execute(buildAlterRoleSql(user, safePassword));
                        log.info("Role password updated");
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------
    // Database Creation
    // ---------------------------------------------------------
    private void createDatabaseIfNotExists(Connection conn, String db, String owner)
            throws SQLException {

        validateIdentifier(db, PROP_DB_NAME);
        validateIdentifier(owner, "DB_OWNER");

        try (PreparedStatement ps =
                     conn.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {

            ps.setString(1, db);
            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    try (Statement st = conn.createStatement()) {
                        st.execute(buildCreateDatabaseSql(db, owner));
                        log.info("Database created");
                    }
                } else {
                    log.info("Database already exists");
                }
            }
        }
    }


    // ---------------------------------------------------------
    // Grant Privileges
    // ---------------------------------------------------------
    private void grantPrivileges(Connection conn, String db, String user)
            throws SQLException {

        validateIdentifier(db, PROP_DB_NAME);
        validateIdentifier(user, PROP_DB_USERNAME);

        try (Statement st = conn.createStatement()) {
            st.execute(buildGrantSql(db, user));
            log.info("Privileges granted on database");
        }
    }

    // ---------------------------------------------------------
    // Safe SQL Builders
    // ---------------------------------------------------------
    private String buildCreateRoleSql(String user, String password) {
        return String.format("CREATE ROLE \"%s\" LOGIN PASSWORD '%s'", user, password);
    }

    private String buildAlterRoleSql(String user, String password) {
        return String.format("ALTER ROLE \"%s\" WITH PASSWORD '%s'", user, password);
    }

    private String buildCreateDatabaseSql(String db, String owner) {
        return String.format("CREATE DATABASE \"%s\" OWNER \"%s\"", db, owner);
    }

    private String buildGrantSql(String db, String user) {
        return String.format("GRANT ALL PRIVILEGES ON DATABASE \"%s\" TO \"%s\"", db, user);
    }

    // ---------------------------------------------------------
    // Validation Helpers
    // ---------------------------------------------------------
    private static void validateIdentifier(String value, String field) {
        if (value == null || !SAFE_IDENTIFIER.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid identifier for " + field + ": " + value
            );
        }
    }

    private static String escapeLiteral(String value) {
        return value == null ? "" : value.replace("'", "''");
    }

}
