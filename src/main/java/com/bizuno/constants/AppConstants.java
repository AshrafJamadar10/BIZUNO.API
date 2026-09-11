package com.bizuno.constants;

public final class AppConstants {




    private AppConstants() {}

    // Status Codes
    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_NO_CONTENT = 204;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_CONFLICT = 409;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    // Success Messages
    public static final String MESSAGE_SUCCESS = "Completed successfully.";
    public static final String MESSAGE_LOGIN_SUCCESS = "Logged in successfully.";
    public static final String MESSAGE_CREATED = "%s created successfully.";
    public static final String MESSAGE_UPDATED = "Updated successfully.";
    public static final String MESSAGE_DELETED = "Deleted successfully.";
    public static final String MESSAGE_SENT = "Sent successfully.";
    public static final String MESSAGE_VERIFIED = "Verified successfully.";
    public static final String MESSAGE_UPDATED_SUCCESS = "%s updated successfully";
    public static final String MESSAGE_DELETED_SUCCESS = "%s deleted successfully.";
    public static final String MESSAGE_REGISTERED_SUCCESS = "Registered successfully";
    public static final String MESSAGE_SCHOOL_REGISTRATION_COMPLETED = "School registration is completed.";

    // Error Messages
    public static final String MESSAGE_ERROR = "An unexpected error occurred.";
    public static final String MESSAGE_UNAUTHORIZED = "Unauthorized access.";
    public static final String MESSAGE_ACCESS_DENIED_TO_SCHOOL = "Access denied to school.";
    public static final String MESSAGE_FORBIDDEN = "You do not have permission to access this resource.";
    public static final String MESSAGE_NOT_FOUND = "The requested resource was not found.";
    public static final String MESSAGE_CONFLICT = "A conflict occurred with the existing resource.";
    public static final String MESSAGE_BAD_REQUEST = "Invalid request data.";
    public static final String MESSAGE_INTERNAL_SERVER_ERROR = "An internal server error occurred.";
    public static final String MESSAGE_REQUIRED = "ALL FIELDS ARE REQUIRED";
    public static final String MESSAGE_FIELD_REQUIRED = "%s is required.";
    public static final String MESSAGE_EXISTS = "%s already exists.";
    public static final String NOT_FOUND = "%s not found.";
    public static final String MESSAGE_ERROR_CREATING = "Error creating %s";
    public static final String MESSAGE_INVALID_OTP = "Invalid OTP.";
    public static final String MESSAGE_FAILED_TO_SEND_OTP = "Failed to send OTP.";
    public static final String MESSAGE_CREATE_AN_ACCOUNT_TO_LOGIN = "Create an account to login";
    public static final String ERROR_ENTER_LATEST_OTP = "Enter latest otp.";
    public static final String MESSAGE_SCHOOL_REGISTRATION_ALREADY_COMPLETED = "School registration is already completed.";
    public static final String MESSAGE_DATABASE_UNAVAILABLE = "Database temporarily unavailable";
    public static final String MESSAGE_SCHOOL_REGISTRATION_INCOMPLETED = "Required complete registration of school.";

    // Custom Messages
    public static final String MESSAGE_USER_NOT_FOUND = "SchoolUser not found.";
    public static final String MESSAGE_INVALID_INPUT = "Invalid input provided.";
    public static final String MESSAGE_VERIFY_PHONE_AND_EMAIL = "Both phone number and email must be verified.";
    public static final String MESSAGE_INVALID_PHONE_AND_EMAIL = "Invalid Phone number or Email provided.";
    public static final String MESSAGE_INVALID_CREDENTIALS = "Invalid credentials.";
    public static final String MESSAGE_ACCESS_DENIED = "Access denied.";
    public static final String MESSAGE_DUPLICATE_ENTRY = "Duplicate entry detected.";
    public static final String MESSAGE_VALIDATION_FAILED = "Validation failed. Please check your input.";
    public static final String MESSAGE_OPERATION_FAILED = "The requested operation could not be completed.";
    public static final String MESSAGE_UPLOAD_CLEAR_DOCUMENT = "Please upload a clear document.";
    public static final String MESSAGE_PASSWORD_DO_NOT_MATCH = "Passwords do not match";
    public static final String MESSAGE_NEW_PASSWORD_DO_NOT_MATCH = "New Passwords do not match";
    public static final String MESSAGE_SAME_AS_OLD_PASS = "New Password cannot be same as old password";

    //Email Constants
    public static final String EMAIL_SUBJECT = "Your OTP Code";
    public static final String EMAIL_BODY_DEFAULT = "Your One-Time Password (OTP) is: ";
    public static final String EMAIL_PASSWORD_CHANGE_SUBJECT = "Password changed successfully";
    public static final String EMAIL_PASSWORD_CHANGE_BODY = "Your password has been changed successfully. If not done by you please contact to the support team immediately.";


    //validation messages
    public static final String VALIDATION_PASSWORD = "Password must be 8–20 characters long and contain at least one uppercase letter, one number, and one special character";
    public static final String VALIDATION_OTP = "OTP must contain only numbers";

    //bank
    public static final String ACCOUNT_NUMBER_DONT_MATCH = "Account Numbers don't match";
    public static final String ENCRYPTION_KEY_NOT_SET = "Encryption key not configured properly";

    //pagination
    public static final String PAGE_ERROR_INDEX = "Page index must not be less than zero";
    public static final String PAGE_ERROR_SIZE = "Page size must not be less than one";

}

