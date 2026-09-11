package com.bizuno.exception;

import lombok.Getter;

public class TenantDatabaseException extends RuntimeException {
    @Getter
    private final String tenantId;
    @Getter
    private final String databaseName;


    public TenantDatabaseException(String message, String tenantId, String databaseName, Throwable cause) {
        super(message, cause);
        this.tenantId = tenantId;
        this.databaseName = databaseName;
    }

    public TenantDatabaseException(String message, String tenantId, String databaseName){
        super(message);
        this.tenantId = tenantId;
        this.databaseName = databaseName;
    }

}

