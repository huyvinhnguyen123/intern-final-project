package com.beetech.finalproject.common;

public class LogStatus {

    private LogStatus() {}

    public static String requestCreate(String value) {
        return "Request creating " + value + "...";
    }

    public static String requestUpdate(String value) {
        return "Request updating " + value + "...";
    }

    public static String requestDelete(String value) {
        return "Request deleting " + value + "...";
    }

    public static String requestSelect(String value) {
        return "Request selecting " + value + "...";
    }

    public static String requestSearch(String value) {
        return "Request searching " + value + "...";
    }

    public static String requestImportFile(String value) {
        return "Request import " + value + " file...";
    }

    public static String requestExportFile(String value) {
        return "Request export " + value + " file...";
    }

    public static final String REQUEST_AUTHENTICATE = "Request login authenticating...";
    //=====================================================================================================================================
    // Auth Logger
    public static final String LOGIN_AUTHENTICATE_SUCCESS = "Login authenticate SUCCESS!";
    public static final String LOGIN_AUTHENTICATE_FAIL = "Login authenticate FAIL!";
    public static final String GENERATE_TOKEN_SUCCESS = "Generate token SUCCESS!";
    public static final String GENERATE_TOKEN_FAIL = "Generate token FAIL!";
    //=====================================================================================================================================
    // CRUD Logger
    public static String createSuccess(String value) {
        return "Create " + value + " SUCCESS!";
    }

    public static String createFail(String value) {
        return "Create " + value + " FAIL!";
    }

    public static String updateSuccess(String value) {
        return "Update " + value + " SUCCESS!";
    }

    public static String updateFail(String value) {
        return "Update " + value + " FAIL!";
    }

    public static String deleteSuccess(String value) {
        return "Delete " + value + " SUCCESS!";
    }

    public static String deleteFail(String value) {
        return "Delete " + value + " FAIL!";
    }

    public static String selectOneSuccess(String value) {
        return "Select " + value + " SUCCESS!";
    }

    public static String selectOneFail(String value) {
        return "Select " + value + " FAIL!";
    }

    public static String selectAllSuccess(String value) {
        return "Select all " + value + " SUCCESS!";
    }

    public static String selectAllFail(String value) {
        return "Select all " + value + " FAIL!";
    }

    public static String searchOneSuccess(String value) {
        return "Search " + value + " SUCCESS!";
    }

    public static String searchOneFail(String value) {
        return "Search " + value + " FAIL!: ";
    }

    public static String searchAllSuccess(String value) {
        return "Search all " + value + " SUCCESS!";
    }

    public static String searchAllFail(String value) {
        return "Search all " + value + " FAIL!: ";
    }

    // Upload Logger
    public static String uploadSuccess(String value) {
        return "Upload " + value + " SUCCESS!";
    }
    public static String uploadFail(String value) {
        return "Upload " + value + " FAIL!";
    }

}
