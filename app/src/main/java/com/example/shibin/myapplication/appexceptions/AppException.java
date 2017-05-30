package com.example.shibin.myapplication.appexceptions;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 */

public class AppException extends Exception {

    private String exceptionMessage;
    private int errorCode;
    NullPointerException d;

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
