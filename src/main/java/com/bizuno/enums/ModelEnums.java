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
}
