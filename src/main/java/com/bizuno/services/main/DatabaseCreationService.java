package com.bizuno.services.main;

import com.bizuno.exception.TenantDatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Service
public class DatabaseCreationService {

    @Value("${admin.datasource.db}")
    private String adminDbName;

    @Value("${admin.datasource.username}")
    private String adminUsername;

    @Value("${admin.datasource.password}")
    private String adminPassword;

    @Value("${database.host}")
    private String dbHost;

    @Value("${database.port}")
    private String dbPort;

    public void createTenantDatabase(String dbName) {
        String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + adminDbName;

        try (Connection connection = DriverManager.getConnection(url, adminUsername, adminPassword);
             Statement statement = connection.createStatement()) {

            // Escape double quotes in database name to prevent SQL injection
            String escapedDbName = dbName.replace("\"", "\"\"");
            String sql = "CREATE DATABASE \"" + escapedDbName + "\"";
            statement.execute(sql);
            log.info("Database created successfully: {}", dbName);

        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                log.warn("Database already exists: {}", dbName);
            } else {
                throw new TenantDatabaseException("Failed to create database: " + dbName, null, dbName, e);
            }
        }
    }

    public void dropTenantDatabase(String dbName) {
        String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + adminDbName;

        try (Connection connection = DriverManager.getConnection(url, adminUsername, adminPassword);
             Statement statement = connection.createStatement()) {

            // Escape double quotes in database name to prevent SQL injection
            String escapedDbName = dbName.replace("\"", "\"\"");
            String sql = "DROP DATABASE IF EXISTS \"" + escapedDbName + "\"";
            statement.execute(sql);
            log.info("Database dropped successfully: {}", dbName);

        } catch (SQLException e) {
            throw new TenantDatabaseException("Failed to drop database: " + dbName, null, dbName, e);
        }
    }

    public boolean databaseExists(String dbName) {
        String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + adminDbName;
        
        try (Connection connection = DriverManager.getConnection(url, adminUsername, adminPassword);
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT 1 FROM pg_database WHERE datname = ?")) {
            
            statement.setString(1, dbName);
            var resultSet = statement.executeQuery();
            return resultSet.next();
            
        } catch (SQLException e) {
            throw new TenantDatabaseException("Failed to check database existence: " + dbName, null, dbName, e);
        }
    }

    public boolean isDatabaseExists(String dbName) {
        return databaseExists(dbName);
    }
}
