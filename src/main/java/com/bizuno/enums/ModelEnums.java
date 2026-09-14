package com.bizuno.enums;

public class ModelEnums {

    public enum PackageBillingPeriod {
        MONTHLY, QUARTERLY, YEARLY, LIFETIME, ONE_TIME;

        public static PackageBillingPeriod[] getAllValues(){
            return values();
        }

        public int getMonths() {
            return switch (this) {
                case MONTHLY -> 1;
                case QUARTERLY -> 3;
                case YEARLY -> 12;
                case LIFETIME, ONE_TIME -> 1200;
            };
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
        DASHBOARD,
        USERS,
        WAREHOUSES,
        CUSTOMERS,
        PRODUCTS,
        PURCHASES,
        INVENTORY,
        SALES_AND_INVOICES,
        PAYMENTS,
        SUPPLIERS,
        REPORTS,
        SETTINGS,
        STAFF,
        ATTENDANCE,
        ROLES,
        NOTIFICATIONS,
        AUDIT_LOGS,
        API_ACCESS,
        CUSTOM_BRANDING,
        PRIORITY_SUPPORT;

        public static CrudPermissionScopes[] getAllValues() {
            return values();
        }
    }

    public enum FeatureLimitType {
        NONE,
        COUNT,
        AMOUNT,
        STORAGE;

        public static FeatureLimitType[] getAllValues() {
            return values();
        }
    }
}
