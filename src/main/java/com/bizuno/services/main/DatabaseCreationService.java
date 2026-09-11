package com.bizuno.services.main;

import com.bizuno.exception.TenantDatabaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            
            String sql = "CREATE DATABASE " + dbName;
            statement.execute(sql);
            System.out.println("✅ Database created successfully: " + dbName);
            
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("⚠️ Database already exists: " + dbName);
            } else {
                throw new TenantDatabaseException("Failed to create database: " + dbName, null, dbName, e);
            }
        }
    }

    public void dropTenantDatabase(String dbName) {
        String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + adminDbName;
        
        try (Connection connection = DriverManager.getConnection(url, adminUsername, adminPassword);
             Statement statement = connection.createStatement()) {
            
            String sql = "DROP DATABASE IF EXISTS " + dbName;
            statement.execute(sql);
            System.out.println("✅ Database dropped successfully: " + dbName);
            
        } catch (SQLException e) {
            throw new TenantDatabaseException("Failed to drop database: " + dbName, null, dbName, e);
        }
    }

    public boolean databaseExists(String dbName) {
        String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + adminDbName;
        
        try (Connection connection = DriverManager.getConnection(url, adminUsername, adminPassword);
             Statement statement = connection.createStatement()) {
            
            String sql = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
            var resultSet = statement.executeQuery(sql);
            return resultSet.next();
            
        } catch (SQLException e) {
            throw new TenantDatabaseException("Failed to check database existence: " + dbName, null, dbName, e);
        }
    }
}
