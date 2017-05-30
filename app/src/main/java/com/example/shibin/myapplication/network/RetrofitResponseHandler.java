package com.example.shibin.myapplication.network;

import com.example.shibin.myapplication.appexceptions.AppException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 */

public class RetrofitResponseHandler {

    public static synchronized AppException onRetrofitResponseFailue(final Throwable throwable)  {
        AppException appException = new AppException();
        boolean isNetworkException = throwable instanceof ConnectException ||
                throwable instanceof SocketTimeoutException ||  throwable instanceof IOException;
        if(isNetworkException) {
            appException.setExceptionMessage("Cannot connect to server !");
        } else {
            appException.setExceptionMessage("Something went wrong !");
        }
        return appException;
    }
}
