package com.bizuno.enums;

public class ModelEnums {

    public enum PackageBillingPeriod {
        MONTHLY, QUARTERLY, YEARLY, LIFETIME, ONE_TIME;

        public static PackageBillingPeriod[] getAllValues(){
            return values();
        }
    }

    public enum AuditAction {
        CREATE,
        UPDATE,
        DELETE,
        LOGIN,
        LOGOUT,
        APPROVE,
        REJECT,
        DOWNLOAD
    }

    public enum PlatformAuditModule {
        PACKAGES,
        SUBSCRIPTION;
        public static PlatformAuditModule[] getAllValues(){ return  values();}
    }

    public enum USER_TYPE {
        PLATFORM_USER,
        BUSINESS_USER;

        public static USER_TYPE[] getAllValues(){ return  values();}
    }

    public enum SubscriptionStatus {
        ACTIVE,
        TRIALING,
        PAST_DUE,
        CANCELED,
        EXPIRED,
        SUSPENDED;

        public static SubscriptionStatus[] getAllValues(){ return  values();}
    }

    public enum Titles {
        SUPER_ADMIN,
        ADMIN,
        EMPLOYEE;

        public static Titles[] getAllValues() {
            return values();
        }
    }

    public enum RoleType {
        PLATFORM,
        BUSINESS;

        public static RoleType[] getAllValues() {
            return values();
        }
    }

    public enum  CrudOperation {
        CREATE,
        READ,
        UPDATE,
        DELETE;

        public static CrudOperation[] getAllValues() {
            return values();
        }
    }

    public enum CrudPermissionScopes {
        BUSINESS,
        EMPLOYEE,
        INVOICE,
        INVENTORY;

        public static CrudPermissionScopes[] getAllValues() {
            return values();
        }
    }
}
