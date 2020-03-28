package com.cds.mini.error;

public enum Errors {
    USER_ID_EXIST("00001", "User Id already exists"),
    USER_DATA_INVALID("00002", "User data invalid"),
    USER_ID_NOT_EXIST("00003", "User Id does not exist"),
    USER_IMPORT_ERROR("00004", "User import error")
    ;

    private final String code;
    private final String message;

    Errors(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return message + "(" + code + ")";
    }
}
