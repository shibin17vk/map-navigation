package com.example.shibin.myapplication.network;

import android.content.Context;
import android.util.Log;

import com.example.shibin.myapplication.utils.Constants;

import com.google.android.gms.common.api.Api;

import retrofit.RestAdapter;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 */

public class ApiManager {

    private static ApiManager apiManager;
    private Context mContext;
    private ApiService service;

    private ApiManager(Context context) {
        this.mContext = context;
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.API_END_POINT_GOOGLE_SERVICE)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(final String message) {
                        Log.i(Constants.APP_TAG, message);
                    }
                })
                .build();
        service = restAdapter.create(ApiService.class);

    }

    public static ApiManager getApiManager(Context context) {
        if(apiManager == null) {
            apiManager = new ApiManager(context);
        }
        return apiManager;
    }

    public ApiService getApiService() {
        return service;
    }

}