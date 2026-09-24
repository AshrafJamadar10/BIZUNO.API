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

//    public enum USER_TYPE {
//        PLATFORM_USER,
//        BUSINESS_USER;
//
//        public static USER_TYPE[] getAllValues(){ return  values();}
//    }

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
        DASHBOARD,
        PACKAGE,
        ROLE,
        USER,
        SETTINGS,
        AUDIT_LOGS,
        SUBSCRIPTION;

        public static CrudPermissionScopes[] getAllValues() {
            return values();
        }
    }

    public enum CrudPermissionScopesBusiness {
        DASHBOARD,
        USERS,
        WAREHOUSE,
        CUSTOMER,
        PRODUCT,
        PRODUCT_CATEGORY,
        PURCHASE,
        INVENTORY,
        SALES_AND_INVOICE,
        PAYMENT,
        SUPPLIER,
        REPORT,
        SETTING,
        STAFF,
        ATTENDANCE,
        ROLE,
        NOTIFICATION,
        AUDIT_LOG,
        PRIORITY_SUPPORT;

        public static CrudPermissionScopesBusiness[] getAllValues() {
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

    public enum StaffStatus {
        ACTIVE,
        INACTIVE,
        ON_LEAVE,
        TERMINATED;

        public static StaffStatus[] getAllValues() {
            return values();
        }
    }

    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LATE,
        HALF_DAY;

        public static AttendanceStatus[] getAllValues() {
            return values();
        }
    }

    public enum CustomerStatus {
        ACTIVE,
        INACTIVE,
        BLOCKED;

        public static CustomerStatus[] getAllValues() {
            return values();
        }
    }

    public enum SupplierStatus {
        ACTIVE,
        INACTIVE,
        BLOCKED;

        public static SupplierStatus[] getAllValues() {
            return values();
        }
    }

    public enum ProductStatus {
        ACTIVE,
        INACTIVE,
        DISCONTINUED;

        public static ProductStatus[] getAllValues() {
            return values();
        }
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED,
        PARTIALLY_PAID;

        public static PaymentStatus[] getAllValues() {
            return values();
        }
    }

    public enum InvoiceStatus {
        DRAFT,
        SENT,
        PAID,
        OVERDUE,
        CANCELLED;

        public static InvoiceStatus[] getAllValues() {
            return values();
        }
    }

    public enum PaymentMethod {
        CASH,
        BANK_TRANSFER,
        CREDIT_CARD,
        DEBIT_CARD,
        CHEQUE,
        UPI,
        ONLINE;

        public static PaymentMethod[] getAllValues() {
            return values();
        }
    }

    public enum PurchaseOrderStatus {
        DRAFT,
        PENDING,
        APPROVED,
        ORDERED,
        RECEIVED,
        CANCELLED;

        public static PurchaseOrderStatus[] getAllValues() {
            return values();
        }
    }

    public enum MovementType {
        IN,
        OUT,
        TRANSFER,
        ADJUSTMENT;

        public static MovementType[] getAllValues() {
            return values();
        }
    }

    public enum CustomFieldDataType {
        SHORT_TEXT,
        LONG_TEXT,
        NUMBER,
        DECIMAL,
        DATE,
        DATE_TIME,
        BOOLEAN,
        SELECT,
        MULTI_SELECT,
        EMAIL,
        PHONE,
        URL;

        public static CustomFieldDataType[] getAllValues() {
            return values();
        }
    }

    public enum CustomFieldTab{
        PRODUCT,
        CATEGORY,
        CUSTOMER,
        SUPPLIER,
        INVENTORY,
        WAREHOUSE,
        STAFF,
        PURCHASE_ORDER;

        public static CustomFieldTab[] getAllValues() {
            return values();
        }
    }

    // In ModelEnums.java
    public enum CustomFieldType {
        INPUT,        // Manually entered
        CALCULATED,   // Computed from other fields
        ROLLUP;        // Aggregated from child entities

        public static CustomFieldType[] getAllValues() {
            return values();
        }
    }
}
