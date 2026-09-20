package com.bizuno.config;

import com.bizuno.exception.TenantDatabaseException;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Configuration
public class BusinessDatabaseConfig {

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Value("${business.datasource.url}")
    private String tenantDatasourceUrl;

    private final Map<String, EntityManagerFactory> tenantEmfMap = new ConcurrentHashMap<>();

    public EntityManagerFactory getOrCreateTenantEntityManagerFactory(String tenantId, String dbName, String packagesToScan) {
        return tenantEmfMap.computeIfAbsent(tenantId, key -> {
            log.info("Creating EntityManagerFactory for tenant: {}, database: {}", tenantId, dbName);
            EntityManagerFactory emf = createTenantEntityManagerFactory(dbName, packagesToScan);
            log.info("Tenant database setup completed: {}", dbName);
            return emf;
        });
    }

    private EntityManagerFactory createTenantEntityManagerFactory(String dbName, String packagesToScan) {
        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();

        // Create data source for the school database
        DataSource schoolDataSource = createTenantDataSource(dbName);
        emfBean.setDataSource(schoolDataSource);

        // SCAN ONLY given ENTITIES - This is crucial!
        emfBean.setPackagesToScan(packagesToScan);
        emfBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Hibernate properties for automatic table creation
        Map<String, Object> properties = getStringObjectMap();
        emfBean.setJpaPropertyMap(properties);

        try {
            emfBean.afterPropertiesSet();
            EntityManagerFactory emf = emfBean.getObject();

            // Trigger schema creation by creating and closing EntityManager
            triggerSchemaCreation(emf, dbName);

            return emf;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create EntityManagerFactory for school database: " + dbName, e);
        }
    }

    /**
     * Creates HikariCP DataSource for school database
     */
    private DataSource createTenantDataSource(String dbName) {
        com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
        config.setJdbcUrl(tenantDatasourceUrl + dbName);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // Connection pooling settings optimized for tenants
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1800000);
        config.setPoolName("TenantPool-" + dbName);

        // PostgresSQL optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new com.zaxxer.hikari.HikariDataSource(config);
    }

    /**
     * Triggers Hibernate to create tables by performing a simple operation
     */
    private void triggerSchemaCreation(EntityManagerFactory emf, String dbName) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.createQuery("SELECT 1 FROM Object").setMaxResults(1).getResultList();
            log.debug("Schema creation triggered for: {}", dbName);
        } catch (Exception e) {
            log.debug("DDL execution completed for: {}", dbName);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Get existing EntityManagerFactory for a tenant
     */
    public EntityManagerFactory getTenantEntityManagerFactory(String tenantId) {
        return tenantEmfMap.get(tenantId);
    }

    /**
     * Close and remove a tenant's EntityManagerFactory
     */
    public void closeTenantEntityManagerFactory(String tenantId) {
        EntityManagerFactory emf = tenantEmfMap.remove(tenantId);
        if (emf != null && emf.isOpen()) {
            emf.close();
            log.info("Closed EntityManagerFactory for tenant: {}", tenantId);
        }
    }

    /**
     * Check if a tenant's EntityManagerFactory exists
     */
    public boolean hasTenantEntityManagerFactory(String tenantId) {
        return tenantEmfMap.containsKey(tenantId);
    }

    /**
     * Method to execute in tenant context
     */
    public <T> T executeInTenantContext(String tenantId, String dbName, Function<EntityManager, T> operation) {
        EntityManager em = null;
        EntityTransaction transaction = null;

        try {
            log.debug("Looking for EntityManagerFactory for tenantId: {}", tenantId);
            log.debug("Current tenantEmfMap keys: {}", tenantEmfMap.keySet());

            EntityManagerFactory emf = hasTenantEntityManagerFactory(tenantId) ? getTenantEntityManagerFactory(tenantId) : null;
            if(emf == null){
                log.error("EntityManagerFactory not found for tenant: {}", tenantId);
                throw new TenantDatabaseException("Invalid tenant provided", tenantId, dbName);
            }

            em = emf.createEntityManager();
            transaction = em.getTransaction();

            transaction.begin();
            T result = operation.apply(em);
            transaction.commit();

            return result;

        } catch (TenantDatabaseException e){
            throw new TenantDatabaseException(e.getMessage(), e.getTenantId(), e.getDatabaseName());
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    log.error("Rollback failed for tenant: {}", tenantId, rollbackEx);
                }
            }
            throw new TenantDatabaseException("Failed to save in school database", tenantId, dbName, e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Batch operation support for better performance
     */
    public <T> List<T> executeBatchInTenantContext(String tenantId, String dbName, List<Function<EntityManager, T>> operations) {
        return executeInTenantContext(tenantId, dbName, em -> {
            List<T> results = new ArrayList<>();
            for (Function<EntityManager, T> operation : operations) {
                results.add(operation.apply(em));
            }
            return results;
        });
    }

    /**
     * Cleanup on application shutdown
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up tenant EntityManagerFactories...");
        tenantEmfMap.forEach((tenantId, emf) -> {
            if (emf != null && emf.isOpen()) {
                emf.close();
                log.info("Closed EntityManagerFactory for tenant: {}", tenantId);
            }
        });
        tenantEmfMap.clear();
    }

    private static Map<String, Object> getStringObjectMap() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        properties.put("hibernate.implicit_naming_strategy", "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");
        properties.put("hibernate.attribute_converter_apply", "false");
        return properties;
    }
}

