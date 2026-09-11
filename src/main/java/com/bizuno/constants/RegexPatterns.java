package com.bizuno.constants;

public final class RegexPatterns {
    private RegexPatterns(){}

    public static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String REGEX_PHONE = "^[6789]\\d{9}$";
    public static final String REGEX_PASSWORD = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\/\\-]).{8,20}$";
    public static final String REGEX_EMPTY = "^$";
    public static final String REGEX_VEHICLE_NUMBER = "^[A-Z]{2}[0-9]{2}([A-Z]{1,2})?[0-9]{4}$";
    public static final String REGEX_OTP = "^\\d{6}$";
    public static final String REGEX_LETTERS_AND_SPACES = "^[a-zA-Z ]+$";
    public static final String REGEX_LETTERS_NUMBERS_HYPHEN = "^[a-zA-Z0-9-]+$";
    public static final String REGEX_ONLY_LETTERS = "^[a-zA-Z]+$";
    public static final String REGEX_OFFICE_NUMBER = "^$|^\\d{10,15}$";
    public static final String REGEX_LETTERS_NUMBERS_UNDERSCORE = "^[a-zA-Z0-9_]+$";
    public static final String REGEX_IP_ADDRESS = "^$|^(?:(?:25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(?:\\.(?!$)|$)){4}$";
    public static final String REGEX_BLOOD_GROUP = "^(A|B|AB|O)[+-]$";
    public static final String REGEX_ADDRESS = "^[\\p{L}0-9\\s,./#\\-()&':;+@_]+$";
    public static final String REGEX_ONLY_NUMBERS = "^[0-9]+$";
    public static final String REGEX_AADHAAR_NUMBER = "\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\b";
    public static final String REGEX_PAN_NUMBER = "\\b[A-Z]{5}[0-9]{4}[A-Z]\\b";
    public static final String REGEX_IFSC_CODE = "^[A-Z]{4}0[A-Z0-9]{6}$";
    public static final String REGEX_LOCATION = "^[A-Za-z\\s,]+$";
    public static final String REGEX_JOB_SKILL = "^[A-Za-z0-9\\s,()#.+-]+$";
    public static final String REGEX_LETTERS_NUMBERS = "^[a-zA-Z0-9]+$";
    public static final String REGEX_LETTERS_NUMBERS_AND_SPACES = "^[a-zA-Z0-9 ]+$";
    public static final String REGEX_SESSION = "^\\d{4}-\\d{4}$";
    public static final String REGEX_TIME = "^((([01]?\\d|2[0-3]):[0-5]\\d)|(0?[1-9]|1[0-2]):[0-5]\\d\\s?(AM|PM))" + "(\\s?-\\s?((([01]?\\d|2[0-3]):[0-5]\\d)|(0?[1-9]|1[0-2]):[0-5]\\d\\s?(AM|PM)))?$";
    public static final String REGEX_DESCRIPTION = "^[A-Za-z0-9\\s.,;:'\"()!@#&%\\-_/]*$";
    public static final String REGEX_DATE = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d{2}$";
    public static final String REGEX_LINK = "^(https?:\\/\\/)?(www\\.)?[a-zA-Z0-9][a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(:\\d+)?(\\/[A-Za-z0-9._~:/?#@!$&'()*+,;=%-]*)?$";
    public static final String REGEX_DECIMAL = "^\\d+(\\.\\d+)?$";
    public static final String REGEX_GENDER = "^(?i)(male|female|other)$";
    public static final String REGEX_MARITAL_STATUS = "^(?i)(single|married|unmarried|divorced|widowed|separated)$";
    public static final String REGEX_BOOLEAN = "^(true|false)$";
    public static final String REGEX_BANK_ACCOUNT_NUMBER = "^\\d{10,16}$";
    public static final String REGEX_MONTH = "^(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)$";
    public static final String REGEX_YEAR = "^(19|20)\\d{2}$";
    public static final String REGEX_LATITUDE = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$";
    public static final String REGEX_LONGITUDE = "^[-+]?((1[0-7]\\d(\\.\\d+)?)|(180(\\.0+)?)|(\\d{1,2}(\\.\\d+)?))$";
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9._]+@[0-9]{11}$\n";
    public static final String REGEX_PHONE_EMAIL_USERNAME = "^(?:[6789]\\d{9}|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}|[A-Z0-9_]+@[0-9]{11})$\n";
    public static final String REGEX_AADHAR = "^[2-9]{1}[0-9]{11}$";
    public static final String REGEX_DEFAULT_PARENT = "^(?i)(father|mother|guardian)$";
}
