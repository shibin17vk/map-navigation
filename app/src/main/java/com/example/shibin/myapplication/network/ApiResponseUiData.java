package com.example.shibin.myapplication.network;

import com.example.shibin.myapplication.appexceptions.AppException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 *
 * Having api id, api request status (onSend/ onComplete/ onSuccess/ onFailure), web service response and
 * exception (if there)
 */

public class ApiResponseUiData {

    private String apiId;
    private int status;
    private AppException appException;
    private ArrayList<Object> responseParam;
    private String msg;

    public ApiResponseUiData(final String apiId, final int status, final AppException appException,
            final Object... responseParam) {
        this.apiId = apiId;
        this.status = status;
        this.appException = appException;
        this.responseParam = new ArrayList<>();

        if (responseParam != null) {
            for (Object object : responseParam) {
                this.responseParam.add(object);
            }
        }

    }

    public ApiResponseUiData(final String apiId, final int status) {
        this.apiId = apiId;
        this.status = status;
        this.responseParam = new ArrayList<>();
    }
    public ApiResponseUiData(final String apiId, final int status,String msg) {
        this.apiId = apiId;
        this.status = status;
        this.responseParam = new ArrayList<>();
        this.msg =msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ApiResponseUiData() {
        this.responseParam = new ArrayList<>();
    }

    public ApiResponseUiData build() {
        return this;
    }


    public ApiResponseUiData addApiId(final String apiId) {
        this.apiId = apiId;
        return this;
    }

    public ApiResponseUiData addStatus(final int status) {
        this.status = status;
        return this;
    }

    public ApiResponseUiData addException(final AppException appException) {
        this.appException = appException;
        return this;
    }

    public ApiResponseUiData addResponseParam(Object... responseParam) {
        for (Object object : responseParam) {
            this.responseParam.add(object);
        }
        return this;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(final String apiId) {
        this.apiId = apiId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public AppException getAppException() {
        return appException;
    }

    public void setAppException(final AppException appException) {
        this.appException = appException;
    }

    public List<Object> getResponseParam() {
        return responseParam;
    }

    public void setResponseParam(final ArrayList<Object> responseParam) {
        this.responseParam = responseParam;
    }
}
